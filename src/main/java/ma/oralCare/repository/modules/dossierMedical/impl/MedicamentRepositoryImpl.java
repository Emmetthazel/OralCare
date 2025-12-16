package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.repository.modules.dossierMedical.api.MedicamentRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE IMPORTANTE: Toutes les méthodes de modification (create, update, delete...)
// encapsulent les SQLException dans des RuntimeException pour respecter la signature
// du CrudRepository fourni (sans 'throws SQLException').

public class MedicamentRepositoryImpl implements MedicamentRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Medicament ---
    private static final String SELECT_BASE_FIELDS =
            " m.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Medicament m JOIN BaseEntity b ON m.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE m.id_entite = ?";
    private static final String CREATE_SQL =
            "INSERT INTO Medicament (id_entite, nom, laboratoire, type, forme, remboursable, prix_unitaire, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Medicament SET nom = ?, laboratoire = ?, type = ?, forme = ?, remboursable = ?, prix_unitaire = ?, description = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Medicament WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_NOM_CONTAINING_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE m.nom LIKE ?";
    private static final String FIND_BY_LABORATOIRE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE m.laboratoire = ?";
    private static final String FIND_BY_FORME_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE m.forme = ?";
    private static final String FIND_BY_REMBOURSABLE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE m.remboursable = ?";


    // --- Logique BaseEntity (gestion des transactions) ---

    private Long createBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(CREATE_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
            stmt.setLong(2, entity.getCreePar());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new SQLException("Échec de la création de BaseEntity, aucun ID généré.");
        }
    }

    private void updateBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_BASE_ENTITY_SQL)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateDerniereModification()));
            stmt.setLong(2, entity.getModifiePar());
            stmt.setLong(3, entity.getIdEntite());

            stmt.executeUpdate();
        }
    }

    private void deleteBaseEntity(Connection conn, Long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BASE_ENTITY_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // --- Méthodes privées d'aide pour le Mappage et l'Exécution ---

    private List<Medicament> executeFindQuery(String sql, Object... params) {
        List<Medicament> medicaments = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    medicaments.add(RowMappers.mapMedicament(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ) : " + e.getMessage(), e);
        }
        return medicaments;
    }

    // --- Méthodes de l'interface CrudRepository (Conformité RuntimeException) ---

    @Override
    public List<Medicament> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Medicament> findById(Long id) {
        List<Medicament> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Medicament entity) {
        if (entity.getCreePar() == null) {
            throw new IllegalArgumentException("Le champ creePar doit contenir l'ID de l'utilisateur créateur.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. CREATE BaseEntity
            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            // 2. CREATE Medicament
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setString(2, entity.getNom());
                stmt.setString(3, entity.getLaboratoire());
                stmt.setString(4, entity.getType());
                stmt.setString(5, entity.getForme().name());
                stmt.setBoolean(6, entity.getRemboursable());
                stmt.setBigDecimal(7, entity.getPrixUnitaire());
                stmt.setString(8, entity.getDescription());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création du Medicament a échoué, aucune ligne affectée.");
                }
            }
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur JDBC lors de la création du Medicament: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Medicament entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar du Medicament ne peuvent pas être nuls pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Medicament
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, entity.getNom());
                stmt.setString(2, entity.getLaboratoire());
                stmt.setString(3, entity.getType());
                stmt.setString(4, entity.getForme().name());
                stmt.setBoolean(5, entity.getRemboursable());
                stmt.setBigDecimal(6, entity.getPrixUnitaire());
                stmt.setString(7, entity.getDescription());
                stmt.setLong(8, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour du Medicament échouée. ID: " + entity.getIdEntite());
                }
            }

            // 2. UPDATE BaseEntity
            entity.setDateDerniereModification(LocalDateTime.now());
            updateBaseEntity(conn, entity);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur JDBC lors de la mise à jour du Medicament: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Medicament entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID du Medicament ne peut pas être null pour la suppression.");
        }
        deleteById(entity.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID pour la suppression ne peut pas être null.");
        }
        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. DELETE Medicament
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            // 2. DELETE BaseEntity
            deleteBaseEntity(conn, id);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur lors de la suppression du Medicament par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface MedicamentRepository ---

    @Override
    public List<Medicament> findByNomContaining(String nomPartiel) {
        if (nomPartiel == null) return findAll();
        // Utilisation de LIKE %nomPartiel% pour la recherche partielle
        return executeFindQuery(FIND_BY_NOM_CONTAINING_SQL, "%" + nomPartiel + "%");
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        if (laboratoire == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_LABORATOIRE_SQL, laboratoire);
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament forme) {
        if (forme == null) return new ArrayList<>();
        // Utilisation de forme.name() pour mapper l'énumération Java à la colonne VARCHAR de la base de données
        return executeFindQuery(FIND_BY_FORME_SQL, forme.name());
    }

    @Override
    public List<Medicament> findByRemboursable(Boolean remboursable) {
        if (remboursable == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_REMBOURSABLE_SQL, remboursable);
    }
}