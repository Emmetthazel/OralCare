package ma.oralCare.service.modules.intervention.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.service.modules.intervention.api.InterventionMedecinService;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class InterventionMedecinServiceImpl implements InterventionMedecinService {
    
    private static final String BASE_SELECT_SQL = """
        SELECT i.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM intervention_medecin i
        JOIN BaseEntity b ON i.id_entite = b.id_entite
        """;
    
    @Override
    public InterventionMedecin createIntervention(InterventionMedecin intervention) {
        Objects.requireNonNull(intervention, "intervention ne doit pas être null");
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlBase = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
                String sqlIntervention = """
                    INSERT INTO intervention_medecin (id_entite, prix_de_patient, num_dent, consultation_id, acte_id)
                    VALUES (?, ?, ?, ?, ?)
                    """;
                
                // 1. BaseEntity
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    psBase.setLong(2, intervention.getCreePar() != null ? intervention.getCreePar() : 1L);
                    psBase.executeUpdate();
                    try (ResultSet keys = psBase.getGeneratedKeys()) {
                        if (keys.next()) {
                            intervention.setIdEntite(keys.getLong(1));
                            intervention.setDateCreation(LocalDateTime.now());
                        }
                    }
                }
                
                // 2. InterventionMedecin
                try (PreparedStatement psInter = connection.prepareStatement(sqlIntervention)) {
                    psInter.setLong(1, intervention.getIdEntite());
                    psInter.setBigDecimal(2, intervention.getPrixDePatient());
                    psInter.setObject(3, intervention.getNumDent(), Types.INTEGER);
                    psInter.setObject(4, intervention.getConsultation() != null ? intervention.getConsultation().getIdEntite() : null, Types.BIGINT);
                    psInter.setObject(5, intervention.getActe() != null ? intervention.getActe().getIdEntite() : null, Types.BIGINT);
                    psInter.executeUpdate();
                }
                
                connection.commit();
                return intervention;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la création de l'intervention", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public Optional<InterventionMedecin> getInterventionById(Long id) {
        if (id == null) return Optional.empty();
        String sql = BASE_SELECT_SQL + " WHERE i.id_entite = ?";
        List<InterventionMedecin> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }
    
    @Override
    public List<InterventionMedecin> getAllInterventions() {
        return executeSelectQuery(BASE_SELECT_SQL + " ORDER BY i.id_entite DESC");
    }
    
    @Override
    public List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE i.consultation_id = ? ORDER BY i.id_entite DESC";
        return executeSelectQuery(sql, consultationId);
    }
    
    @Override
    public List<InterventionMedecin> getInterventionsByActeId(Long acteId) {
        if (acteId == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE i.acte_id = ? ORDER BY i.id_entite DESC";
        return executeSelectQuery(sql, acteId);
    }
    
    @Override
    public InterventionMedecin updateIntervention(InterventionMedecin intervention) {
        Objects.requireNonNull(intervention, "intervention ne doit pas être null");
        if (intervention.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une intervention sans idEntite");
        }
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
                String sqlIntervention = """
                    UPDATE intervention_medecin SET prix_de_patient=?, num_dent=?, consultation_id=?, acte_id=?
                    WHERE id_entite=?
                    """;
                
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    psBase.setObject(2, intervention.getModifiePar() != null ? intervention.getModifiePar() : 1L, Types.BIGINT);
                    psBase.setLong(3, intervention.getIdEntite());
                    psBase.executeUpdate();
                }
                
                try (PreparedStatement psInter = connection.prepareStatement(sqlIntervention)) {
                    psInter.setBigDecimal(1, intervention.getPrixDePatient());
                    psInter.setObject(2, intervention.getNumDent(), Types.INTEGER);
                    psInter.setObject(3, intervention.getConsultation() != null ? intervention.getConsultation().getIdEntite() : null, Types.BIGINT);
                    psInter.setObject(4, intervention.getActe() != null ? intervention.getActe().getIdEntite() : null, Types.BIGINT);
                    psInter.setLong(5, intervention.getIdEntite());
                    psInter.executeUpdate();
                }
                
                connection.commit();
                return intervention;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la mise à jour de l'intervention", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public void deleteIntervention(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'intervention", e);
        }
    }
    
    @Override
    public Double calculateTotalPriceByConsultationId(Long consultationId) {
        if (consultationId == null) return 0.0;
        String sql = "SELECT SUM(prix_de_patient) FROM intervention_medecin WHERE consultation_id = ?";
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total.doubleValue() : 0.0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total", e);
        }
        return 0.0;
    }
    
    private List<InterventionMedecin> executeSelectQuery(String sql, Object... params) {
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour InterventionMedecin", e);
        }
        return out;
    }
}
