package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.dossierMedical.SituationFinanciere;
import ma.oralCare.entities.enums.StatutSituationFinanciere;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.SituationFinanciereRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE : Toutes les méthodes gèrent les SQLException en interne et relancent des RuntimeException
// pour respecter la signature de l'interface CrudRepository (sans 'throws SQLException').

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes SituationFinanciere ---
    private static final String SELECT_BASE_FIELDS =
            " sf.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM SituationFinanciere sf JOIN BaseEntity b ON sf.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE sf.id_entite = ?";

    // Les deux premières colonnes sont obligatoires (id_entite, dossier_medicale_id)
    private static final String CREATE_SQL =
            "INSERT INTO SituationFinanciere (id_entite, totale_des_actes, totale_paye, credit, statut, en_promo, dossier_medicale_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE SituationFinanciere SET totale_des_actes = ?, totale_paye = ?, credit = ?, statut = ?, en_promo = ?, dossier_medicale_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM SituationFinanciere WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_PATIENT_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS +
                    " JOIN DossierMedicale dm ON sf.dossier_medicale_id = dm.id_entite " +
                    " WHERE dm.patient_id = ?";

    // Recherche par Statut 'ACTIVE' (supposé)
    private static final String FIND_ACTIVE_SQL =
            "SELECT " + SELECT_BASE_FIELDS +
                    " WHERE sf.statut = 'ACTIVE'";

    // Permet de trouver toutes les situations (anciennes et actives) d'un patient
    private static final String FIND_ALL_BY_PATIENT_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS +
                    " JOIN DossierMedicale dm ON sf.dossier_medicale_id = dm.id_entite " +
                    " WHERE dm.patient_id = ?";

    // Réinitialisation : met les totaux à zéro et le statut à 'COMPLETED' (supposé)
    private static final String REINITIALISER_SF_SQL =
            "UPDATE SituationFinanciere SET totale_des_actes = 0.0, totale_paye = 0.0, credit = 0.0, statut = 'COMPLETED' WHERE id_entite = ?";

    // Mise à jour des totaux
    private static final String UPDATE_TOTAUX_SQL =
            "UPDATE SituationFinanciere SET totale_des_actes = ?, totale_paye = ?, credit = ? WHERE id_entite = ?";

    // Recherche des factures associées
    private static final String FIND_FACTURES_BY_SF_ID_SQL =
            "SELECT f.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Facture f JOIN BaseEntity b ON f.id_entite = b.id_entite " +
                    " WHERE f.situation_financiere_id = ?";


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

    // --- Méthode privée d'aide pour le Mappage et l'Exécution (SituationFinanciere) ---

    private List<SituationFinanciere> executeFindSFQuery(String sql, Object... params) {
        List<SituationFinanciere> results = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(RowMappers.mapSituationFinanciere(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND SF : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ SituationFinanciere) : " + e.getMessage(), e);
        }
        return results;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<SituationFinanciere> findAll() {
        return executeFindSFQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<SituationFinanciere> findById(Long id) {
        List<SituationFinanciere> results = executeFindSFQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(SituationFinanciere entity) {
        if (entity.getCreePar() == null) {
            throw new IllegalArgumentException("Le champ creePar doit contenir l'ID de l'utilisateur créateur.");
        }
        if (entity.getDossierMedicale() == null || entity.getDossierMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le DossierMedicale est obligatoire pour une SituationFinanciere.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. CREATE BaseEntity
            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            // 2. CREATE SituationFinanciere
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setBigDecimal(2, entity.getTotaleDesActes());
                stmt.setBigDecimal(3, entity.getTotalePaye());
                stmt.setBigDecimal(4, entity.getCredit());
                stmt.setString(5, entity.getStatut() != null ? entity.getStatut().name() : StatutSituationFinanciere.ACTIVE.name()); // Par défaut ACTIF
                stmt.setString(6, entity.getEnPromo() != null ? entity.getEnPromo().name() : null);
                stmt.setLong(7, entity.getDossierMedicale().getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la SituationFinanciere a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de la SituationFinanciere: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(SituationFinanciere entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de la SituationFinanciere ne peuvent pas être nuls pour la mise à jour.");
        }
        if (entity.getDossierMedicale() == null || entity.getDossierMedicale().getIdEntite() == null) {
            throw new IllegalArgumentException("Le DossierMedicale est obligatoire pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE SituationFinanciere
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setBigDecimal(1, entity.getTotaleDesActes());
                stmt.setBigDecimal(2, entity.getTotalePaye());
                stmt.setBigDecimal(3, entity.getCredit());
                stmt.setString(4, entity.getStatut() != null ? entity.getStatut().name() : null);
                stmt.setString(5, entity.getEnPromo() != null ? entity.getEnPromo().name() : null);
                stmt.setLong(6, entity.getDossierMedicale().getIdEntite());
                stmt.setLong(7, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de la SituationFinanciere échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour de la SituationFinanciere: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(SituationFinanciere entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de la SituationFinanciere ne peut pas être null pour la suppression.");
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

            // 1. DELETE SituationFinanciere
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
            System.err.println("Erreur lors de la suppression de la SituationFinanciere par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface SituationFinanciereRepository ---

    @Override
    public Optional<SituationFinanciere> findByPatientId(Long patientId) {
        if (patientId == null) return Optional.empty();
        // Ici, on cherche la SF ACTIVE, s'il y en a plusieurs, on prend la première (logique métier)
        List<SituationFinanciere> results = executeFindSFQuery(FIND_BY_PATIENT_ID_SQL + " AND sf.statut = 'ACTIVE' LIMIT 1", patientId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<SituationFinanciere> findActiveSituations() {
        return executeFindSFQuery(FIND_ACTIVE_SQL);
    }

    @Override
    public List<SituationFinanciere> findAllByPatientId(Long patientId) {
        if (patientId == null) return new ArrayList<>();
        // Recherche toutes les situations (actives ou terminées)
        return executeFindSFQuery(FIND_ALL_BY_PATIENT_ID_SQL, patientId);
    }

    @Override
    public void reinitialiserSF(Long id) {
        if (id == null) return;
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(REINITIALISER_SF_SQL)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de la réinitialisation de la SituationFinanciere: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (REINITIALISATION) : " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTotaux(Long sfId, Double montantTotalActes, Double montantTotalPaye, Double nouveauCredit) {
        if (sfId == null) return;
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_TOTAUX_SQL)) {

            stmt.setDouble(1, montantTotalActes);
            stmt.setDouble(2, montantTotalPaye);
            stmt.setDouble(3, nouveauCredit);
            stmt.setLong(4, sfId);

            if (stmt.executeUpdate() == 0) {
                throw new RuntimeException("Mise à jour des totaux échouée. ID: " + sfId);
            }

        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de la mise à jour des totaux: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE TOTAUX) : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesBySituationFinanciereId(Long sfId) {
        List<Facture> factures = new ArrayList<>();
        if (sfId == null) return factures;

        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_FACTURES_BY_SF_ID_SQL)) {

            stmt.setLong(1, sfId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Utilisation du RowMapper pour Facture
                    factures.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de la recherche des Factures: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Facture) : " + e.getMessage(), e);
        }
        return factures;
    }
}