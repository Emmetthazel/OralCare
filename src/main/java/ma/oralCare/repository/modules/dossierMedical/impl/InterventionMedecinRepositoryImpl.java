/*package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.modules.dossierMedical.api.InterventionMedecinRepository;
import ma.oralCare.repository.common.RowMappers;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    private final SessionFactory sessionFactory = SessionFactory.getInstance();

    // Requêtes SQL
    private static final String CREATE_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String UPDATE_BASE_ENTITY_SQL = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";
    private static final String CREATE_SQL = "INSERT INTO intervention_medecin (id_entite, prix_de_patient, num_dent, consultation_id, acte_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE intervention_medecin SET prix_de_patient = ?, num_dent = ?, consultation_id = ?, acte_id = ? WHERE id_entite = ?";
    private static final String DELETE_SQL = "DELETE FROM intervention_medecin WHERE id_entite = ?";
    private static final String SELECT_BASE_FIELDS = " i.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par FROM intervention_medecin i JOIN BaseEntity b ON i.id_entite = b.id_entite ";
    private static final String FIND_BY_ID_SQL = "SELECT " + SELECT_BASE_FIELDS + " WHERE i.id_entite = ?";
    private static final String FIND_BY_CONSULTATION_SQL = "SELECT " + SELECT_BASE_FIELDS + " WHERE i.consultation_id = ?";
    private static final String CALC_TOTAL_PRICE_SQL = "SELECT SUM(prix_de_patient) FROM intervention_medecin WHERE consultation_id = ?";

    // Supprimer les champs repository et consultationService d'ici !
    public InterventionMedecinRepositoryImpl() {
        // Constructeur vide pour le Repository
    }

    @Override
    public void create(InterventionMedecin entity) {
        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);

            entity.setDateCreation(LocalDateTime.now());
            Long newId = createBaseEntity(conn, entity);
            entity.setIdEntite(newId);

            try (PreparedStatement stmt = conn.prepareStatement(CREATE_SQL)) {
                stmt.setLong(1, entity.getIdEntite());
                stmt.setBigDecimal(2, entity.getPrixDePatient());
                stmt.setObject(3, entity.getNumDent(), Types.INTEGER);
                stmt.setObject(4, entity.getConsultation() != null ? entity.getConsultation().getIdEntite() : null, Types.BIGINT);
                stmt.setObject(5, entity.getActe() != null ? entity.getActe().getIdEntite() : null, Types.BIGINT);
                stmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException("Erreur de création", e);
        }
    }

    @Override
    public Optional<InterventionMedecin> findById(Long id) {
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<InterventionMedecin> consulterParConsultation(Long idConsultation) {
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection conn = sessionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(FIND_BY_CONSULTATION_SQL)) {
            stmt.setLong(1, idConsultation);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) interventions.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
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
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0.0;
    }

    @Override
    public void deleteById(Long id) {
        Connection conn = null;
        try {
            conn = sessionFactory.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) {
                stmt.setLong(1, id);
                stmt.executeUpdate();
            }
            deleteBaseEntity(conn, id);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            throw new RuntimeException(e);
        }
    }

    // Méthodes privées utilitaires pour BaseEntity
    private Long createBaseEntity(Connection conn, BaseEntity entity) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(CREATE_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(entity.getDateCreation()));
            stmt.setLong(2, entity.getCreePar() != null ? entity.getCreePar() : 1L);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
            throw new SQLException("ID non généré");
        }
    }

    private void deleteBaseEntity(Connection conn, Long id) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(DELETE_BASE_ENTITY_SQL)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }


    @Override public List<InterventionMedecin> findAll() { return new ArrayList<>(); }
    @Override public void delete(InterventionMedecin entity) { deleteById(entity.getIdEntite()); }
    @Override public List<InterventionMedecin> findByActeId(Long id) { return null; }
    @Override public List<InterventionMedecin> findPage(int l, int o) { return null; }
    @Override public InterventionMedecin appliquerRemisePonctuelle(Long id, Double p) { return null; }
    @Override public Optional<Acte> findActeByInterventionId(Long id) { return Optional.empty(); }
    @Override public boolean existsByConsultationActeAndDent(Long c, Long a, Integer d) { return false; }
    @Override public List<InterventionMedecin> findByNumDent(Integer n) { return null; }
}*/