package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.CertificatRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificatRepositoryImpl implements CertificatRepository {

    private final Connection connection;

    // ✅ SQL avec jointures pour récupérer les infos de BaseEntity et le lien Dossier Médical
    private static final String BASE_SELECT_SQL = """
        SELECT c.*, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par,
               co.dossier_medicale_id
        FROM Certificat c
        JOIN BaseEntity b ON c.id_entite = b.id_entite
        JOIN Consultation co ON c.consultation_id = co.id_entite
        """;

    public CertificatRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    // =========================================================================
    //                                CRUD
    // =========================================================================

    @Override
    public void create(Certificat certificat) {
        String sqlBase = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
        String sqlCert = """
            INSERT INTO Certificat (id_entite, date_debut, date_fin, duree, note_medecin, consultation_id) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try {
            this.connection.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                psBase.setLong(2, certificat.getCreePar() != null ? certificat.getCreePar() : 1L);
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) certificat.setIdEntite(keys.getLong(1));
                }
            }

            // 2. Certificat
            try (PreparedStatement psCert = connection.prepareStatement(sqlCert)) {
                psCert.setLong(1, certificat.getIdEntite());
                psCert.setDate(2, Date.valueOf(certificat.getDateDebut()));
                psCert.setDate(3, Date.valueOf(certificat.getDateFin()));
                psCert.setInt(4, certificat.getDuree());
                psCert.setString(5, certificat.getNoteMedecin());
                psCert.setLong(6, certificat.getConsultation().getIdEntite());
                psCert.executeUpdate();
            }
            this.connection.commit();
        } catch (SQLException e) {
            try { this.connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }
    }

    @Override
    public void update(Certificat certificat) {
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification = ?, modifie_par = ? WHERE id_entite = ?";
        String sqlCert = """
            UPDATE Certificat SET date_debut = ?, date_fin = ?, duree = ?, note_medecin = ? 
            WHERE id_entite = ?
            """;
        try {
            this.connection.setAutoCommit(false);

            try (PreparedStatement psBase = connection.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                psBase.setLong(2, certificat.getModifiePar() != null ? certificat.getModifiePar() : 1L);
                psBase.setLong(3, certificat.getIdEntite());
                psBase.executeUpdate();
            }

            try (PreparedStatement psCert = connection.prepareStatement(sqlCert)) {
                psCert.setDate(1, Date.valueOf(certificat.getDateDebut()));
                psCert.setDate(2, Date.valueOf(certificat.getDateFin()));
                psCert.setInt(3, certificat.getDuree());
                psCert.setString(4, certificat.getNoteMedecin());
                psCert.setLong(5, certificat.getIdEntite());
                psCert.executeUpdate();
            }
            this.connection.commit();
        } catch (SQLException e) {
            try { this.connection.rollback(); } catch (SQLException ignored) {}
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Certificat> findById(Long id) {
        return executeQuery(BASE_SELECT_SQL + " WHERE c.id_entite = ?", id).stream().findFirst();
    }

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // =========================================================================
    //                         MÉTHODES SPÉCIFIQUES
    // =========================================================================

    @Override
    public List<Certificat> findByDossierMedicaleId(Long dossierId) {
        return executeQuery(BASE_SELECT_SQL + " WHERE co.dossier_medicale_id = ?", dossierId);
    }

    @Override
    public List<Certificat> findValidCertificates(LocalDate currentDate) {
        return executeQuery(BASE_SELECT_SQL + " WHERE c.date_fin >= ?", Date.valueOf(currentDate));
    }

    // =========================================================================
    //                        UTILITAIRES TECHNIQUES
    // =========================================================================

    private List<Certificat> executeQuery(String sql, Object... params) {
        List<Certificat> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public List<Certificat> findAll() { return executeQuery(BASE_SELECT_SQL); }
    @Override public void delete(Certificat c) { if(c != null) deleteById(c.getIdEntite()); }
    @Override public List<Certificat> findByDateDebut(LocalDate date) { return executeQuery(BASE_SELECT_SQL + " WHERE c.date_debut = ?", Date.valueOf(date)); }
    @Override public List<Certificat> findByNoteMedecinContaining(String fragment) { return executeQuery(BASE_SELECT_SQL + " WHERE c.note_medecin LIKE ?", "%" + fragment + "%"); }
}