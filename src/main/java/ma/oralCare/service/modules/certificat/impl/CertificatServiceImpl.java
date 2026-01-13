package ma.oralCare.service.modules.certificat.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.CertificatRepository;
import ma.oralCare.service.modules.certificat.api.CertificatService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CertificatServiceImpl implements CertificatService {
    
    private final CertificatRepository certificatRepository;
    
    public CertificatServiceImpl() {
        // Note: CertificatRepositoryImpl nécessite une Connection dans son constructeur
        // On utilisera directement les méthodes de repository via SessionFactory si nécessaire
        this.certificatRepository = null; // Sera géré différemment
    }
    
    private static final String BASE_SELECT_SQL = """
        SELECT c.*, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par,
               co.dossier_medicale_id
        FROM Certificat c
        JOIN BaseEntity b ON c.id_entite = b.id_entite
        JOIN Consultation co ON c.consultation_id = co.id_entite
        """;
    
    @Override
    public Certificat createCertificat(Certificat certificat) {
        Objects.requireNonNull(certificat, "certificat ne doit pas être null");
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlBase = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
                String sqlCert = """
                    INSERT INTO Certificat (id_entite, date_debut, date_fin, duree, note_medecin, consultation_id) 
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
                
                // 1. BaseEntity
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    psBase.setLong(2, certificat.getCreePar() != null ? certificat.getCreePar() : 1L);
                    psBase.executeUpdate();
                    try (ResultSet keys = psBase.getGeneratedKeys()) {
                        if (keys.next()) {
                            certificat.setIdEntite(keys.getLong(1));
                            certificat.setDateCreation(LocalDateTime.now());
                        }
                    }
                }
                
                // 2. Certificat
                try (PreparedStatement psCert = connection.prepareStatement(sqlCert)) {
                    psCert.setLong(1, certificat.getIdEntite());
                    psCert.setDate(2, certificat.getDateDebut() != null ? Date.valueOf(certificat.getDateDebut()) : null);
                    psCert.setDate(3, certificat.getDateFin() != null ? Date.valueOf(certificat.getDateFin()) : null);
                    psCert.setInt(4, certificat.getDuree());
                    psCert.setString(5, certificat.getNoteMedecin());
                    psCert.setLong(6, certificat.getConsultation().getIdEntite());
                    psCert.executeUpdate();
                }
                
                connection.commit();
                return certificat;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la création du certificat", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public Optional<Certificat> getCertificatById(Long id) {
        if (id == null) return Optional.empty();
        String sql = BASE_SELECT_SQL + " WHERE c.id_entite = ?";
        List<Certificat> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }
    
    @Override
    public List<Certificat> getAllCertificats() {
        return executeSelectQuery(BASE_SELECT_SQL + " ORDER BY c.date_debut DESC");
    }
    
    @Override
    public List<Certificat> getCertificatsByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE co.dossier_medicale_id = ? ORDER BY c.date_debut DESC";
        return executeSelectQuery(sql, dossierId);
    }
    
    @Override
    public List<Certificat> getCertificatsByDateDebut(LocalDate date) {
        if (date == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE c.date_debut = ? ORDER BY c.date_debut DESC";
        return executeSelectQuery(sql, Date.valueOf(date));
    }
    
    @Override
    public List<Certificat> getValidCertificates(LocalDate currentDate) {
        if (currentDate == null) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE c.date_debut <= ? AND c.date_fin >= ? ORDER BY c.date_debut DESC";
        return executeSelectQuery(sql, Date.valueOf(currentDate), Date.valueOf(currentDate));
    }
    
    @Override
    public List<Certificat> getCertificatsByNoteContaining(String noteFragment) {
        if (noteFragment == null || noteFragment.isBlank()) return List.of();
        String sql = BASE_SELECT_SQL + " WHERE c.note_medecin LIKE ? ORDER BY c.date_debut DESC";
        return executeSelectQuery(sql, "%" + noteFragment + "%");
    }
    
    @Override
    public Certificat updateCertificat(Certificat certificat) {
        Objects.requireNonNull(certificat, "certificat ne doit pas être null");
        if (certificat.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un certificat sans idEntite");
        }
        
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
                String sqlCert = """
                    UPDATE Certificat SET date_debut=?, date_fin=?, duree=?, note_medecin=?, consultation_id=?
                    WHERE id_entite=?
                    """;
                
                try (PreparedStatement psBase = connection.prepareStatement(sqlBase)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    psBase.setObject(2, certificat.getModifiePar() != null ? certificat.getModifiePar() : 1L, Types.BIGINT);
                    psBase.setLong(3, certificat.getIdEntite());
                    psBase.executeUpdate();
                }
                
                try (PreparedStatement psCert = connection.prepareStatement(sqlCert)) {
                    psCert.setDate(1, certificat.getDateDebut() != null ? Date.valueOf(certificat.getDateDebut()) : null);
                    psCert.setDate(2, certificat.getDateFin() != null ? Date.valueOf(certificat.getDateFin()) : null);
                    psCert.setInt(3, certificat.getDuree());
                    psCert.setString(4, certificat.getNoteMedecin());
                    psCert.setLong(5, certificat.getConsultation().getIdEntite());
                    psCert.setLong(6, certificat.getIdEntite());
                    psCert.executeUpdate();
                }
                
                connection.commit();
                return certificat;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erreur lors de la mise à jour du certificat", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }
    
    @Override
    public void deleteCertificat(Long id) {
        if (id == null) return;
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du certificat", e);
        }
    }
    
    private List<Certificat> executeSelectQuery(String sql, Object... params) {
        List<Certificat> out = new ArrayList<>();
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour Certificat", e);
        }
        return out;
    }
}
