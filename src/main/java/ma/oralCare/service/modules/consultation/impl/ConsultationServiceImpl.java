package ma.oralCare.service.modules.consultation.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import ma.oralCare.service.modules.consultation.api.ConsultationService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConsultationServiceImpl implements ConsultationService {
    
    private final DossierMedicaleRepository dossierRepository;
    
    public ConsultationServiceImpl() {
        this(new DossierMedicaleRepositoryImpl());
    }
    
    public ConsultationServiceImpl(DossierMedicaleRepository dossierRepository) {
        this.dossierRepository = Objects.requireNonNull(dossierRepository);
    }
    
    private static final String BASE_SELECT_SQL = """
        SELECT c.*, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Consultation c 
        JOIN BaseEntity b ON c.id_entite = b.id_entite
        """;
    
    @Override
    public Consultation createConsultation(Consultation consultation) {
        Objects.requireNonNull(consultation, "consultation ne doit pas être null");
        Objects.requireNonNull(consultation.getDossierMedicale(), "dossierMedicale ne doit pas être null");
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                LocalDateTime now = LocalDateTime.now();
                String sqlBase = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
                String sqlConsultation = """
                    INSERT INTO Consultation(id_entite, date, statut, observation_medecin, dossier_medicale_id, libelle)
                    VALUES(?, ?, ?, ?, ?, ?)
                    """;
                
                // 1. Insertion BaseEntity
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(now));
                    psBase.setObject(2, consultation.getCreePar() != null ? consultation.getCreePar() : 1L, Types.BIGINT);
                    psBase.executeUpdate();
                    try (ResultSet keys = psBase.getGeneratedKeys()) {
                        if (keys.next()) {
                            consultation.setIdEntite(keys.getLong(1));
                            consultation.setDateCreation(now);
                        }
                    }
                }
                
                // 2. Insertion Consultation
                try (PreparedStatement psCons = connection.prepareStatement(sqlConsultation)) {
                    psCons.setLong(1, consultation.getIdEntite());
                    psCons.setDate(2, consultation.getDate() != null ? Date.valueOf(consultation.getDate()) : Date.valueOf(LocalDate.now()));
                    psCons.setString(3, consultation.getStatut() != null ? consultation.getStatut().name() : StatutConsultation.SCHEDULED.name());
                    psCons.setString(4, consultation.getObservationMedecin());
                    psCons.setLong(5, consultation.getDossierMedicale().getIdEntite());
                    psCons.setString(6, consultation.getLibelle());
                    psCons.executeUpdate();
                }
                
                connection.commit();
                return consultation;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la création de la consultation", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public Optional<Consultation> getConsultationById(Long id) {
        if (id == null) return Optional.empty();
        String sql = BASE_SELECT_SQL + " WHERE c.id_entite = ?";
        List<Consultation> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }
    
    @Override
    public List<Consultation> getAllConsultations() {
        return executeSelectQuery(BASE_SELECT_SQL + " ORDER BY c.date DESC");
    }
    
    @Override
    public List<Consultation> getConsultationsByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return dossierRepository.findConsultationsByDossierId(dossierId);
    }
    
    @Override
    public List<Consultation> getConsultationsByStatut(StatutConsultation statut) {
        if (statut == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE c.statut = ? ORDER BY c.date DESC";
        return executeSelectQuery(sql, statut.name());
    }
    
    @Override
    public List<Consultation> getConsultationsByDate(LocalDate date) {
        if (date == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE c.date = ? ORDER BY c.date DESC";
        return executeSelectQuery(sql, Date.valueOf(date));
    }
    
    @Override
    public Consultation updateConsultation(Consultation consultation) {
        Objects.requireNonNull(consultation, "consultation ne doit pas être null");
        if (consultation.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une consultation sans idEntite");
        }
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
                String sqlConsultation = """
                    UPDATE Consultation SET date=?, statut=?, observation_medecin=?, dossier_medicale_id=?, libelle=?
                    WHERE id_entite=?
                    """;
                
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    psBase.setObject(2, consultation.getModifiePar() != null ? consultation.getModifiePar() : 1L, Types.BIGINT);
                    psBase.setLong(3, consultation.getIdEntite());
                    psBase.executeUpdate();
                }
                
                try (PreparedStatement psCons = connection.prepareStatement(sqlConsultation)) {
                    psCons.setDate(1, consultation.getDate() != null ? Date.valueOf(consultation.getDate()) : Date.valueOf(LocalDate.now()));
                    psCons.setString(2, consultation.getStatut() != null ? consultation.getStatut().name() : StatutConsultation.SCHEDULED.name());
                    psCons.setString(3, consultation.getObservationMedecin());
                    psCons.setLong(4, consultation.getDossierMedicale().getIdEntite());
                    psCons.setString(5, consultation.getLibelle());
                    psCons.setLong(6, consultation.getIdEntite());
                    psCons.executeUpdate();
                }
                
                connection.commit();
                return consultation;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la mise à jour de la consultation", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public void updateConsultationStatut(Long id, StatutConsultation statut) {
        if (id == null || statut == null) return;
        Optional<Consultation> consultationOpt = getConsultationById(id);
        if (consultationOpt.isPresent()) {
            Consultation consultation = consultationOpt.get();
            consultation.setStatut(statut);
            updateConsultation(consultation);
        }
    }
    
    @Override
    public void updateConsultationObservation(Long id, String observation) {
        if (id == null) return;
        Optional<Consultation> consultationOpt = getConsultationById(id);
        if (consultationOpt.isPresent()) {
            Consultation consultation = consultationOpt.get();
            consultation.setObservationMedecin(observation);
            updateConsultation(consultation);
        }
    }
    
    @Override
    public void deleteConsultation(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation", e);
        }
    }
    
    @Override
    public Consultation terminerConsultation(Long consultationId, String observations) {
        if (consultationId == null) {
            throw new IllegalArgumentException("consultationId ne doit pas être null");
        }
        Optional<Consultation> consultationOpt = getConsultationById(consultationId);
        if (consultationOpt.isEmpty()) {
            throw new RuntimeException("Consultation #" + consultationId + " introuvable");
        }
        Consultation consultation = consultationOpt.get();
        consultation.setStatut(StatutConsultation.COMPLETED);
        consultation.setObservationMedecin(observations);
        return updateConsultation(consultation);
    }
    
    private List<Consultation> executeSelectQuery(String sql, Object... params) {
        List<Consultation> out = new ArrayList<>();
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour Consultation", e);
        }
        return out;
    }
}
