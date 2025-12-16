package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException
// pour respecter la signature de l'interface CrudRepository (sans 'throws SQLException').

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Prescription ---
    private static final String SELECT_BASE_FIELDS =
            " p.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Prescription p JOIN BaseEntity b ON p.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE p.id_entite = ?";
    private static final String CREATE_SQL =
            "INSERT INTO Prescription (id_entite, quantite, frequence, duree_en_jours, ordonnance_id, medicament_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Prescription SET quantite = ?, frequence = ?, duree_en_jours = ?, ordonnance_id = ?, medicament_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Prescription WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_ORDONNANCE_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE p.ordonnance_id = ?";


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

    // --- Méthode privée d'aide pour le Mappage et l'Exécution ---

    private List<Prescription> executeFindQuery(String sql, Object... params) {
        List<Prescription> prescriptions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Utilisation de RowMappers.mapPrescription
                    prescriptions.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Prescription : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Prescription) : " + e.getMessage(), e);
        }
        return prescriptions;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Prescription> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Prescription> findById(Long id) {
        List<Prescription> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Prescription entity) {
        if (entity.getCreePar() == null) {
            throw new IllegalArgumentException("Le champ creePar doit contenir l'ID de l'utilisateur créateur.");
        }
        if (entity.getOrdonnance() == null || entity.getOrdonnance().getIdEntite() == null || entity.getMedicament() == null || entity.getMedicament().getIdEntite() == null) {
            throw new IllegalArgumentException("Ordonnance et Medicament doivent être renseignés.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. CREATE BaseEntity
            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            // 2. CREATE Prescription
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setInt(2, entity.getQuantite());
                stmt.setString(3, entity.getFrequence());
                // Utilise setInt pour les entiers primitifs, ou setObject pour gérer les valeurs null si l'entité supporte un Integer
                // Ici, on suppose que le DureeEnJours n'est pas NULL en DB (ou est 0), en se basant sur la table SQL fournie.
                stmt.setInt(4, entity.getDureeEnJours());
                stmt.setLong(5, entity.getOrdonnance().getIdEntite()); // NOT NULL
                stmt.setLong(6, entity.getMedicament().getIdEntite()); // NOT NULL

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la Prescription a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de la Prescription: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Prescription entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de la Prescription ne peuvent pas être nuls pour la mise à jour.");
        }
        if (entity.getOrdonnance() == null || entity.getOrdonnance().getIdEntite() == null || entity.getMedicament() == null || entity.getMedicament().getIdEntite() == null) {
            throw new IllegalArgumentException("Ordonnance et Medicament doivent être renseignés pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Prescription
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setInt(1, entity.getQuantite());
                stmt.setString(2, entity.getFrequence());
                stmt.setInt(3, entity.getDureeEnJours());
                stmt.setLong(4, entity.getOrdonnance().getIdEntite());
                stmt.setLong(5, entity.getMedicament().getIdEntite());
                stmt.setLong(6, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de la Prescription échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour de la Prescription: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Prescription entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de la Prescription ne peut pas être null pour la suppression.");
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

            // 1. DELETE Prescription (Optionnel car DELETE CASCADE est souvent défini sur BaseEntity)
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }

            // 2. DELETE BaseEntity (Déclenche le DELETE CASCADE sur Prescription)
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
            System.err.println("Erreur lors de la suppression de la Prescription par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface PrescriptionRepository ---

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_ORDONNANCE_ID_SQL, ordonnanceId);
    }
}