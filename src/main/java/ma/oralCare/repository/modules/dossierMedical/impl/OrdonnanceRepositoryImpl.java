package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException
// pour respecter la signature de l'interface CrudRepository (sans 'throws SQLException').

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Ordonnance ---
    private static final String SELECT_BASE_FIELDS =
            " o.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Ordonnance o JOIN BaseEntity b ON o.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE o.id_entite = ?";
    private static final String CREATE_SQL =
            "INSERT INTO Ordonnance (id_entite, date_ordonnance, dossier_medicale_id, consultation_id) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Ordonnance SET date_ordonnance = ?, dossier_medicale_id = ?, consultation_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Ordonnance WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_DOSSIER_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE o.dossier_medicale_id = ?";
    private static final String FIND_BY_CONSULTATION_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE o.consultation_id = ?";
    private static final String FIND_BY_DATE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE o.date_ordonnance = ?";

    // Requête pour les Prescriptions
    private static final String SELECT_PRESCRIPTION_BASE_FIELDS =
            " p.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Prescription p JOIN BaseEntity b ON p.id_entite = b.id_entite ";
    private static final String FIND_PRESCRIPTIONS_BY_ORDONNANCE_ID_SQL =
            "SELECT " + SELECT_PRESCRIPTION_BASE_FIELDS + " WHERE p.ordonnance_id = ?";


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

    // --- Méthode privée d'aide pour le Mappage et l'Exécution (Ordonnance) ---

    private List<Ordonnance> executeFindOrdonnanceQuery(String sql, Object... params) {
        List<Ordonnance> ordonnances = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Pour LocalDate, utiliser java.sql.Date
                if (params[i] instanceof LocalDate) {
                    stmt.setDate(i + 1, Date.valueOf((LocalDate) params[i]));
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Ordonnance : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Ordonnance) : " + e.getMessage(), e);
        }
        return ordonnances;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Ordonnance> findAll() {
        return executeFindOrdonnanceQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Ordonnance> findById(Long id) {
        List<Ordonnance> results = executeFindOrdonnanceQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Ordonnance entity) {
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

            // 2. CREATE Ordonnance
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setDate(2, entity.getDate() != null ? Date.valueOf(entity.getDate()) : null);
                stmt.setLong(3, entity.getDossierMedicale().getIdEntite()); // NOT NULL
                stmt.setObject(4, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT); // NULLABLE

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de l'Ordonnance a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de l'Ordonnance: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Ordonnance entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null || entity.getDossierMedicale() == null) {
            throw new IllegalArgumentException("L'ID, ModifiePar et DossierMedicale de l'Ordonnance ne peuvent pas être nuls pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Ordonnance
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setDate(1, entity.getDate() != null ? Date.valueOf(entity.getDate()) : null);
                stmt.setLong(2, entity.getDossierMedicale().getIdEntite());
                stmt.setObject(3, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT);
                stmt.setLong(4, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de l'Ordonnance échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour de l'Ordonnance: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Ordonnance entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de l'Ordonnance ne peut pas être null pour la suppression.");
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

            // 1. DELETE Ordonnance (La suppression de BaseEntity gère la suppression en cascade)
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
            System.err.println("Erreur lors de la suppression de l'Ordonnance par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface OrdonnanceRepository ---

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        if (dossierId == null) return new ArrayList<>();
        return executeFindOrdonnanceQuery(FIND_BY_DOSSIER_ID_SQL, dossierId);
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        if (consultationId == null) return new ArrayList<>();
        return executeFindOrdonnanceQuery(FIND_BY_CONSULTATION_ID_SQL, consultationId);
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        if (date == null) return new ArrayList<>();
        // executeFindOrdonnanceQuery gère le mappage LocalDate -> java.sql.Date
        return executeFindOrdonnanceQuery(FIND_BY_DATE_SQL, date);
    }

    @Override
    public List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        List<Prescription> prescriptions = new ArrayList<>();
        if (ordonnanceId == null) return prescriptions;

        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_PRESCRIPTIONS_BY_ORDONNANCE_ID_SQL)) {

            stmt.setLong(1, ordonnanceId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Utilisation du RowMapper pour Prescription
                    prescriptions.add(RowMappers.mapPrescription(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de la recherche des Prescriptions: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Prescription) : " + e.getMessage(), e);
        }
        return prescriptions;
    }
}