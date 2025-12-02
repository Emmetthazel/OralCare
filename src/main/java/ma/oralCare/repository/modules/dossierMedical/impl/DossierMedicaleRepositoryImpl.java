package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.modules.dossierMedicale.api.DossierMedicaleRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DossierMedicaleRepositoryImpl implements DossierMedicaleRepository {

    // ============================================
    // ❗ À COMPLÉTER : ta méthode pour obtenir une connexion
    // ============================================
    private Connection getConnection() throws SQLException {
        // TODO : remplacer par ta future connexion JDBC
        return null;
    }

    // ==========================================================
    // SAVE
    // ==========================================================
    @Override
    public DossierMedicale save(DossierMedicale dossier) {
        String sql = """
            INSERT INTO dossiers_medicaux (date_de_creation, id_patient)
            VALUES (?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(dossier.getDateDeCreation()));
            ps.setLong(2, dossier.getPatient().getId());

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                dossier.setId(keys.getLong(1));
            }

            return dossier;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ==========================================================
    // UPDATE
    // ==========================================================
    @Override
    public DossierMedicale update(DossierMedicale dossier) {
        String sql = """
            UPDATE dossiers_medicaux 
            SET date_de_creation = ?, id_patient = ?
            WHERE id_dm = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(dossier.getDateDeCreation()));
            ps.setLong(2, dossier.getPatient().getId());
            ps.setLong(3, dossier.getId());

            ps.executeUpdate();

            return dossier;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    // ==========================================================
    // DELETE BY ID
    // ==========================================================
    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM dossiers_medicaux WHERE id_dm = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // ==========================================================
    // FIND BY ID
    // ==========================================================
    @Override
    public DossierMedicale findById(Long id) {
        String sql = "SELECT * FROM dossiers_medicaux WHERE id_dm = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapDossierMedicale(rs);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // ==========================================================
    // FIND ALL
    // ==========================================================
    @Override
    public List<DossierMedicale> findAll() {
        String sql = "SELECT * FROM dossiers_medicaux";
        List<DossierMedicale> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapDossierMedicale(rs));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // ==========================================================
    // FIND BY PATIENT ID
    // ==========================================================
    @Override
    public List<DossierMedicale> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM dossiers_medicaux WHERE id_patient = ?";

        List<DossierMedicale> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapDossierMedicale(rs));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    // ==========================================================
    // MAPPING
    // ==========================================================
    private DossierMedicale mapDossierMedicale(ResultSet rs) throws SQLException {

        DossierMedicale d = new DossierMedicale();

        d.setId(rs.getLong("id_dm"));

        Date dc = rs.getDate("date_de_creation");
        if (dc != null) d.setDateDeCreation(dc.toLocalDate());

        // Charger le patient
        Patient p = new Patient();
        p.setId(rs.getLong("id_patient"));
        d.setPatient(p);

        // Les listes seront chargées ailleurs
        d.setConsultations(null);
        d.setOrdonnances(null);
        d.setCertificats(null);
        d.setRendezVous(null);
        d.setSituationFinanciere(null);
        d.setMedecin(null);

        return d;
    }
}
