package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.oralCare.repository.common.RowMappers; // Utilisation de votre classe de mappage

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE IMPORTANTE: Nous utilisons RuntimeException pour encapsuler les SQLException
// afin de respecter la signature de l'interface CrudRepository (sans throws SQLException).

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes SQL ---
    // Les requêtes de modification/création ne changent pas
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    private static final String CREATE_SQL =
            "INSERT INTO intervention_medecin (id_entite, prix_de_patient, num_dent, consultation_id, acte_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE intervention_medecin SET prix_de_patient = ?, num_dent = ?, consultation_id = ?, acte_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM intervention_medecin WHERE id_entite = ?";

    // Requêtes SELECT avec JOIN implicite (puisque RowMappers s'attend à tous les champs de BaseEntity)
    private static final String SELECT_BASE_FIELDS =
            " i.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM intervention_medecin i JOIN BaseEntity b ON i.id_entite = b.id_entite ";
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE i.id_entite = ?";
    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ACTE_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE i.acte_id = ?";
    private static final String FIND_PAGE_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " LIMIT ? OFFSET ?";
    private static final String FIND_BY_CONSULTATION_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE i.consultation_id = ?";
    private static final String CALC_TOTAL_PRICE_SQL =
            "SELECT SUM(prix_de_patient) FROM intervention_medecin WHERE consultation_id = ?";
    private static final String APPLY_REMISE_SQL =
            "UPDATE intervention_medecin SET prix_de_patient = prix_de_patient * (1 - ? / 100) WHERE id_entite = ?";
    private static final String EXISTS_BY_CONSULTATION_ACTE_DENT_SQL =
            "SELECT COUNT(*) FROM intervention_medecin WHERE consultation_id = ? AND acte_id = ? AND num_dent = ?";
    private static final String FIND_BY_NUM_DENT_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE i.num_dent = ?";


    // --- Logique BaseEntity (gestion des transactions) ---

    // Note: createBaseEntity et updateBaseEntity sont conservées car elles gèrent la logique de la base de données
    // pour les transactions (INSERT/UPDATE/DELETE sur BaseEntity), indépendamment du RowMapper.

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


    // --- Méthodes de l'interface CrudRepository (Conformité RuntimeException) ---

    @Override
    public void create(InterventionMedecin entity) {
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

            // 2. CREATE InterventionMedecin
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setBigDecimal(2, entity.getPrixDePatient());
                stmt.setObject(3, entity.getNumDent(), Types.INTEGER);
                stmt.setObject(4, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT);
                stmt.setObject(5, entity.getActe() != null ? entity.getActe().getIdEntite() : null, Types.BIGINT);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de l'intervention a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de l'InterventionMedecin: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<InterventionMedecin> findById(Long id) {
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Utilisation de RowMappers
                    return Optional.of(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de l'InterventionMedecin par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ) : " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<InterventionMedecin> findAll() {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ALL_SQL)) {

            while (rs.next()) {
                // Utilisation de RowMappers
                interventions.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche de toutes les InterventionsMedecin: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (FIND_ALL) : " + e.getMessage(), e);
        }
        return interventions;
    }

    @Override
    public void update(InterventionMedecin entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de l'InterventionMedecin ne peuvent pas être nuls pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE InterventionMedecin
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setBigDecimal(1, entity.getPrixDePatient());
                stmt.setObject(2, entity.getNumDent(), Types.INTEGER);
                stmt.setObject(3, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT);
                stmt.setObject(4, entity.getActe() != null ? entity.getActe().getIdEntite() : null, Types.BIGINT);
                stmt.setLong(5, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de l'InterventionMedecin échouée.");
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
            System.err.println("Erreur JDBC lors de la mise à jour de l'InterventionMedecin: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(InterventionMedecin entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de l'InterventionMedecin ne peut pas être null pour la suppression.");
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

            // 1. DELETE InterventionMedecin
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
            System.err.println("Erreur lors de la suppression de l'InterventionMedecin par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface InterventionMedecinRepository ---

    @Override
    public List<InterventionMedecin> findByActeId(Long acteId) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ACTE_ID_SQL)) {
            stmt.setLong(1, acteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des InterventionsMedecin par Acte ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ custom) : " + e.getMessage(), e);
        }
        return interventions;
    }

    @Override
    public List<InterventionMedecin> findPage(int limit, int offset) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_PAGE_SQL)) {
            stmt.setInt(1, limit);
            stmt.setInt(2, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération d'une page d'InterventionsMedecin: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ page) : " + e.getMessage(), e);
        }
        return interventions;
    }

    @Override
    public List<InterventionMedecin> consulterParConsultation(Long idConsultation) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CONSULTATION_SQL)) {
            stmt.setLong(1, idConsultation);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la consultation par ID consultation: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ by consultation) : " + e.getMessage(), e);
        }
        return interventions;
    }

    @Override
    public Double calculateTotalPatientPriceByConsultationId(Long consultationId) {
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CALC_TOTAL_PRICE_SQL)) {
            stmt.setLong(1, consultationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total.doubleValue() : 0.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du calcul du prix total: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CALCULATE) : " + e.getMessage(), e);
        }
        return 0.0;
    }

    @Override
    public InterventionMedecin appliquerRemisePonctuelle(Long interventionId, Double pourcentageRemise) {
        Optional<InterventionMedecin> interventionOpt = findById(interventionId);
        if (interventionOpt.isEmpty()) {
            throw new IllegalArgumentException("InterventionMedecin avec ID " + interventionId + " introuvable.");
        }
        InterventionMedecin intervention = interventionOpt.get();

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. Appliquer la remise sur le prix
            try (PreparedStatement stmt = conn.prepareStatement(APPLY_REMISE_SQL)) {
                stmt.setDouble(1, pourcentageRemise);
                stmt.setLong(2, interventionId);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La remise ponctuelle a échoué.");
                }
            }

            // 2. Mettre à jour BaseEntity
            // Assurez-vous que le champ modifiePar a été renseigné
            if (intervention.getModifiePar() == null) intervention.setModifiePar(1L); // Fallback si nécessaire
            intervention.setDateDerniereModification(LocalDateTime.now());
            updateBaseEntity(conn, intervention);

            conn.commit();

            // Relecture de l'objet pour obtenir le prix mis à jour
            return findById(interventionId).orElseThrow(
                    () -> new RuntimeException("Échec de la relecture de l'intervention après la remise.")
            );

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur lors de l'application de la remise ponctuelle: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (Remise) : " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Acte> findActeByInterventionId(Long interventionId) {
        // findById utilise RowMappers.mapInterventionMedecin qui crée une référence Acte
        // Le repository doit être utilisé pour charger l'Acte si nécessaire, mais ici
        // on se contente de l'ActeRef créé par RowMappers, ce qui est suffisant si
        // l'application utilise des Proxies ou si ActeRef est suffisant.
        return findById(interventionId).map(InterventionMedecin::getActe);
    }

    @Override
    public boolean existsByConsultationActeAndDent(Long consultationId, Long acteId, Integer numDent) {
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(EXISTS_BY_CONSULTATION_ACTE_DENT_SQL)) {

            stmt.setLong(1, consultationId);
            stmt.setLong(2, acteId);
            stmt.setInt(3, numDent);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'existence: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (EXISTS) : " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_NUM_DENT_SQL)) {

            stmt.setInt(1, numDent);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche des InterventionsMedecin par numéro de dent: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ by dent) : " + e.getMessage(), e);
        }
        return interventions;
    }
}