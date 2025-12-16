package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException.

public class RevenuesRepositoryImpl implements RevenuesRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Revenues ---
    private static final String SELECT_BASE_FIELDS =
            " r.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Revenues r JOIN BaseEntity b ON r.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE r.id_entite = ?";

    private static final String CREATE_SQL =
            "INSERT INTO Revenues (id_entite, titre, description, montant, date, cabinet_medicale_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Revenues SET titre = ?, description = ?, montant = ?, date = ?, cabinet_medicale_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Revenues WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_CABINET_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE r.cabinet_medicale_id = ?";

    private static final String FIND_BY_TITRE_CONTAINING_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE r.titre LIKE ?";

    private static final String CALCULATE_TOTAL_REVENUES_SQL =
            "SELECT SUM(montant) FROM Revenues WHERE date BETWEEN ? AND ?";

    private static final String FIND_PAGE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " ORDER BY r.date DESC LIMIT ? OFFSET ?"; // Tri par date le plus récent en premier

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

    private List<Revenues> executeFindQuery(String sql, Object... params) {
        List<Revenues> revenuesList = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Gestion spécifique des types LocalDateTime
                if (params[i] instanceof LocalDateTime) {
                    stmt.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) params[i]));
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    revenuesList.add(RowMappers.mapRevenues(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Revenues : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Revenues) : " + e.getMessage(), e);
        }
        return revenuesList;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Revenues> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Revenues> findById(Long id) {
        List<Revenues> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Revenues entity) {
        if (entity.getCreePar() == null) {
            throw new IllegalArgumentException("Le champ creePar doit contenir l'ID de l'utilisateur créateur.");
        }
        if (entity.getCabinetMedicale() == null || entity.getCabinetMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le CabinetMedicale est obligatoire pour un Revenu.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. CREATE BaseEntity
            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            // 2. CREATE Revenues
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setString(2, entity.getTitre());
                stmt.setString(3, entity.getDescription());
                stmt.setBigDecimal(4, entity.getMontant());
                stmt.setTimestamp(5, entity.getDate() != null ? Timestamp.valueOf(entity.getDate()) : null); // Date NOT NULL dans la DB
                stmt.setLong(6, entity.getCabinetMedicale().getIdEntite()); // NOT NULL

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création du Revenu a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création du Revenu: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Revenues entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar du Revenu ne peuvent pas être nuls pour la mise à jour.");
        }
        if (entity.getCabinetMedicale() == null || entity.getCabinetMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le CabinetMedicale est obligatoire pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Revenues
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, entity.getTitre());
                stmt.setString(2, entity.getDescription());
                stmt.setBigDecimal(3, entity.getMontant());
                stmt.setTimestamp(4, entity.getDate() != null ? Timestamp.valueOf(entity.getDate()) : null);
                stmt.setLong(5, entity.getCabinetMedicale().getIdEntite());
                stmt.setLong(6, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour du Revenu échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour du Revenu: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Revenues entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID du Revenu ne peut pas être null pour la suppression.");
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

            // 1. DELETE Revenues (optionnel)
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
            System.err.println("Erreur lors de la suppression du Revenu par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface RevenuesRepository ---

    @Override
    public List<Revenues> findByCabinetMedicaleId(Long cabinetMedicaleId) {
        if (cabinetMedicaleId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_CABINET_ID_SQL, cabinetMedicaleId);
    }

    @Override
    public List<Revenues> findByTitreContaining(String titre) {
        if (titre == null || titre.trim().isEmpty()) return new ArrayList<>();

        String pattern = "%" + titre.trim() + "%";
        return executeFindQuery(FIND_BY_TITRE_CONTAINING_SQL, pattern);
    }

    @Override
    public Double calculateTotalRevenuesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return 0.0;

        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALCULATE_TOTAL_REVENUES_SQL)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // SUM retourne un Double, ou 0.0 si le résultat est NULL
                    Double total = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : total;
                }
            }
            return 0.0;
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors du calcul des revenus: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CALCULATE TOTAL) : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Revenues> findPage(int limit, int offset) {
        // Validation basique des paramètres de pagination
        if (limit <= 0 || offset < 0) {
            throw new IllegalArgumentException("Les paramètres de pagination (limit > 0, offset >= 0) sont invalides.");
        }

        return executeFindQuery(FIND_PAGE_SQL, limit, offset);
    }
}