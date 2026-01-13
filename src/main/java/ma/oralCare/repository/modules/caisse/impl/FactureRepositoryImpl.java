package ma.oralCare.repository.modules.caisse.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FactureRepositoryImpl implements FactureRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // --- Requêtes BaseEntity (pour gestion des transactions) ---
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // --- Requêtes Facture ---
    private static final String SELECT_BASE_FIELDS =
            " f.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par " +
                    " FROM Facture f JOIN BaseEntity b ON f.id_entite = b.id_entite ";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_BASE_FIELDS;
    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE f.id_entite = ?";

    private static final String CREATE_SQL =
            "INSERT INTO Facture (id_entite, totale_facture, totale_paye, reste, statut, date_facture, consultation_id, situation_financiere_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE Facture SET totale_facture = ?, totale_paye = ?, reste = ?, statut = ?, date_facture = ?, consultation_id = ?, situation_financiere_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL =
            "DELETE FROM Facture WHERE id_entite = ?";

    // Requêtes spécifiques
    private static final String FIND_BY_SF_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE f.situation_financiere_id = ?";
    private static final String FIND_BY_CONSULTATION_ID_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE f.consultation_id = ?";
    private static final String FIND_BY_STATUT_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE f.statut = ?";
    private static final String FIND_BY_DATE_BETWEEN_SQL =
            "SELECT " + SELECT_BASE_FIELDS + " WHERE f.date_facture BETWEEN ? AND ?";

    // Logique métier (Enregistrer Paiement/Annuler/Update Totaux)
    private static final String ENREGISTRER_PAIEMENT_SQL =
            "UPDATE Facture SET totale_paye = totale_paye + ?, reste = totale_facture - (totale_paye + ?), statut = ? WHERE id_entite = ?";
    private static final String ANNULER_FACTURE_SQL =
            "UPDATE Facture SET statut = 'CANCELLED' WHERE id_entite = ?";
    private static final String UPDATE_TOTAUX_SQL =
            "UPDATE Facture SET totale_facture = ?, reste = ?, statut = ? WHERE id_entite = ?";

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

    private List<Facture> executeFindQuery(String sql, Object... params) {
        List<Facture> factures = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Gestion spécifique des types LocalDateTime et StatutFacture
                if (params[i] instanceof LocalDateTime) {
                    stmt.setTimestamp(i + 1, Timestamp.valueOf((LocalDateTime) params[i]));
                } else if (params[i] instanceof StatutFacture) {
                    stmt.setString(i + 1, ((StatutFacture) params[i]).name());
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    factures.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'exécution de la requête FIND Facture : " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (READ Facture) : " + e.getMessage(), e);
        }
        return factures;
    }

    // --- Méthodes de l'interface CrudRepository ---

    @Override
    public List<Facture> findAll() {
        return executeFindQuery(FIND_ALL_SQL);
    }

    @Override
    public Optional<Facture> findById(Long id) {
        List<Facture> results = executeFindQuery(FIND_BY_ID_SQL, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public void create(Facture entity) {
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

            // 2. CREATE Facture
            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setBigDecimal(2, entity.getTotaleFacture());
                stmt.setBigDecimal(3, entity.getTotalePaye());
                stmt.setBigDecimal(4, entity.getReste());
                stmt.setString(5, entity.getStatut()); // Statut NOT NULL (déjà un String grâce à getStatut())
                stmt.setTimestamp(6, Timestamp.valueOf(entity.getDateFacture())); // Date NOT NULL

                stmt.setObject(7, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT); // NULLABLE UNIQUE
                stmt.setObject(8, entity.getSituationFinanciere() != null ? entity.getSituationFinanciere().getIdEntite() : null, Types.BIGINT); // NULLABLE UNIQUE

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("La création de la Facture a échoué, aucune ligne affectée.");
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
            System.err.println("Erreur JDBC lors de la création de la Facture: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (CREATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Facture entity) {
        if (entity.getIdEntite() == null || entity.getModifiePar() == null) {
            throw new IllegalArgumentException("L'ID et le ModifiePar de la Facture ne peuvent pas être nuls pour la mise à jour.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. UPDATE Facture
            try (PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) {
                stmt.setBigDecimal(1, entity.getTotaleFacture());
                stmt.setBigDecimal(2, entity.getTotalePaye());
                stmt.setBigDecimal(3, entity.getReste());
                stmt.setString(4, entity.getStatut());
                stmt.setTimestamp(5, Timestamp.valueOf(entity.getDateFacture()));

                stmt.setObject(6, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT);
                stmt.setObject(7, entity.getSituationFinanciere() != null ? entity.getSituationFinanciere().getIdEntite() : null, Types.BIGINT);
                stmt.setLong(8, entity.getIdEntite());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Mise à jour de la Facture échouée. ID: " + entity.getIdEntite());
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
            System.err.println("Erreur JDBC lors de la mise à jour de la Facture: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE) : " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(Facture entity) {
        if (entity.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de la Facture ne peut pas être null pour la suppression.");
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

            // 1. DELETE Facture
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
            System.err.println("Erreur lors de la suppression de la Facture par ID: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (DELETE) : " + e.getMessage(), e);
        }
    }

    // --- Méthodes de l'interface FactureRepository ---

    @Override
    public List<Facture> findBySituationFinanciereId(Long situationFinanciereId) {
        if (situationFinanciereId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_SF_ID_SQL, situationFinanciereId);
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        if (consultationId == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_CONSULTATION_ID_SQL, consultationId);
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        if (statut == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_STATUT_SQL, statut);
    }

    @Override
    public List<Facture> findByDateFactureBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) return new ArrayList<>();
        return executeFindQuery(FIND_BY_DATE_BETWEEN_SQL, startDate, endDate);
    }

    @Override
    public Facture enregistrerPaiement(Long factureId, Double montantPaye) {
        if (factureId == null || montantPaye == null || montantPaye <= 0) {
            throw new IllegalArgumentException("L'ID de la facture et le montant payé doivent être valides.");
        }

        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            // Récupérer la facture existante
            Facture facture = findById(factureId)
                    .orElseThrow(() -> new RuntimeException("Facture non trouvée pour l'ID: " + factureId));

            // Calcul du nouveau statut
            BigDecimal montant = BigDecimal.valueOf(montantPaye);
            BigDecimal nouveauRestePrevu = facture.getReste().subtract(montant);

            StatutFacture nouveauStatut = StatutFacture.PENDING;
            if (nouveauRestePrevu.compareTo(BigDecimal.ZERO) <= 0) {
                nouveauStatut = StatutFacture.PAID;
            }

            // Mise à jour de la Facture
            try (PreparedStatement stmt = conn.prepareStatement(ENREGISTRER_PAIEMENT_SQL)) {
                stmt.setBigDecimal(1, montant);
                stmt.setBigDecimal(2, montant);
                stmt.setString(3, nouveauStatut.name());
                stmt.setLong(4, factureId);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException("Échec de l'enregistrement du paiement.");
                }
            }

            conn.commit();
            // Retourner la facture mise à jour pour confirmation
            return findById(factureId).get();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Erreur de rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("Erreur JDBC lors de l'enregistrement du paiement: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (PAIEMENT) : " + e.getMessage(), e);
        }
    }

    @Override
    public void annulerFacture(Long factureId) {
        if (factureId == null) {
            throw new IllegalArgumentException("L'ID de la facture ne peut pas être null pour l'annulation.");
        }
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(ANNULER_FACTURE_SQL)) {

            stmt.setLong(1, factureId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de l'annulation de la Facture: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (ANNULATION) : " + e.getMessage(), e);
        }
    }

    @Override
    public void updateTotaux(Long factureId, Double nouveauTotalFacture, Double nouveauReste) {
        if (factureId == null) {
            throw new IllegalArgumentException("L'ID de la facture ne peut pas être null pour la mise à jour des totaux.");
        }

        // Détermination du statut basé sur le nouveau reste
        StatutFacture nouveauStatut = StatutFacture.PENDING;
        if (BigDecimal.valueOf(nouveauReste).compareTo(BigDecimal.ZERO) <= 0) {
            nouveauStatut = StatutFacture.PAID;
        }

        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_TOTAUX_SQL)) {

            stmt.setBigDecimal(1, BigDecimal.valueOf(nouveauTotalFacture));
            stmt.setBigDecimal(2, BigDecimal.valueOf(nouveauReste));
            stmt.setString(3, nouveauStatut.name());
            stmt.setLong(4, factureId);

            if (stmt.executeUpdate() == 0) {
                throw new RuntimeException("Mise à jour des totaux de la facture échouée. ID: " + factureId);
            }

        } catch (SQLException e) {
            System.err.println("Erreur JDBC lors de la mise à jour des totaux de la Facture: " + e.getMessage());
            throw new RuntimeException("Erreur de persistance (UPDATE TOTAUX) : " + e.getMessage(), e);
        }
    }
}