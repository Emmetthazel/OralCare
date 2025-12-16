package ma.oralCare.repository.modules.cabinet.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.cabinet.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException.

public class ChargesRepositoryImpl implements ChargesRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Charges ---
    private static final String SELECT_BASE_FIELDS =
            " c.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Charges c JOIN BaseEntity b ON c.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE c.id_entite = ?";

    private static final String CREATE_SQL =
            "INSERT INTO Charges (id_entite, titre, description, montant, date, cabinet_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Charges SET titre = ?, description = ?, montant = ?, date = ?, cabinet_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Charges WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_DATE_BETWEEN_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE c.date BETWEEN ? AND ?";

    private static final String CALCULATE_TOTAL_CHARGES_SQL =
            "SELECT SUM(montant) FROM Charges WHERE date BETWEEN ? AND ?";

    private static final String FIND_BY_KEYWORD_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE c.titre LIKE ? OR c.description LIKE ?";

    private static final String FIND_BY_CABINET_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE c.cabinet_id = ?";


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

    private List<Charges> executeFindQuery(String sql, Object... params) {
        List<Charges> chargesList = new ArrayList<>();
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
                    chargesList.add(RowMappers.mapCharges(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Charges : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Charges) : " + e.getMessage(), e);
        }
        return chargesList;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Charges> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Charges> findById(Long id) {
        List<Charges> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Charges entity) {
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

            // 2. CREATE Charges
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setString(2, entity.getTitre());
                stmt.setString(3, entity.getDescription());
                stmt.setBigDecimal(4, entity.getMontant());
                stmt.setTimestamp(5, entity.getDate() != null ? Timestamp.valueOf(entity.getDate()) : null);

                stmt.setObject(6, entity.getCabinetMedicale() != null ? entity.getCabinetMedicale().getIdEntite() : null, Types.BIGINT); // NULLABLE

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la Charge a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de la Charge: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Charges entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de la Charge ne peuvent pas être nuls pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Charges
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setString(1, entity.getTitre());
                stmt.setString(2, entity.getDescription());
                stmt.setBigDecimal(3, entity.getMontant());
                stmt.setTimestamp(4, entity.getDate() != null ? Timestamp.valueOf(entity.getDate()) : null);
                stmt.setObject(5, entity.getCabinetMedicale() != null ? entity.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                stmt.setLong(6, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de la Charge échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour de la Charge: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Charges entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de la Charge ne peut pas être null pour la suppression.");
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

            // 1. DELETE Charges (optionnel car BaseEntity ON DELETE CASCADE gère normalement la suppression)
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
            System.err.println("Erreur lors de la suppression de la Charge par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface ChargesRepository ---

    @Override
    public List<Charges> findByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut == null || dateFin == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_DATE_BETWEEN_SQL, dateDebut, dateFin);
    }

    @Override
    public Double calculateTotalChargesByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        if (dateDebut == null || dateFin == null) return 0.0;

        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALCULATE_TOTAL_CHARGES_SQL)) {

            stmt.setTimestamp(1, Timestamp.valueOf(dateDebut));
            stmt.setTimestamp(2, Timestamp.valueOf(dateFin));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // SUM retourne un Double (ou BigDecimal dans certains SGBD, on utilise getDouble)
                    // Il peut être NULL si aucune ligne n'est trouvée, getDouble gère souvent le NULL en 0.0
                    Double total = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : total;
                }
            }
            return 0.0;
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors du calcul des charges: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CALCULATE TOTAL) : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Charges> findByTitreOrDescriptionContaining(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();

        String pattern = "%" + keyword.trim() + "%";
        // La méthode executeFindQuery prend en charge le pattern LIKE
        return executeFindQuery(FIND_BY_KEYWORD_SQL, pattern, pattern);
    }

    @Override
    public List<Charges> findByCabinetMedicaleId(Long cabinetId) {
        if (cabinetId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_CABINET_ID_SQL, cabinetId);
    }
}