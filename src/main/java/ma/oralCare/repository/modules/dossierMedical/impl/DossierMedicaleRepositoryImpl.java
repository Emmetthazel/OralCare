package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapDossierMedicale, mapConsultation, etc.

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DossierMedicaleRepositoryImpl implements DossierMedicaleRepository {

    // --- 1. Opérations CRUD de base (UC: Créer, Modifier, Supprimer un Dossier) ---

    @Override
    public List<DossierMedicale> findAll() {
        String sql = "SELECT * FROM DossierMedicale ORDER BY dateDeCreation DESC";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapDossierMedicale(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les dossiers médicaux", e);
        }
        return out;
    }

    @Override
    public DossierMedicale findById(Long id) {
        // UC: Chercher et consulter un Dossier
        String sql = "SELECT * FROM DossierMedicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapDossierMedicale(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du dossier médical par ID", e);
        }
    }

    @Override
    public void create(DossierMedicale newElement) {
        // UC: créer un Dossier
        if (newElement == null || newElement.getPatient() == null) return;
        String sql = "INSERT INTO DossierMedicale (dateDeCreation, patient_id, medecin_id, situationFinanciere_id) VALUES (?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDateDeCreation()));
            ps.setLong(2, newElement.getPatient().getId());
            // Les IDs Medecin et SituationFinanciere peuvent être null au départ ou récupérés/créés
            ps.setObject(3, newElement.getMedecin() != null ? newElement.getMedecin().getId() : null, JDBCType.BIGINT);
            ps.setObject(4, newElement.getSituationFinanciere() != null ? newElement.getSituationFinanciere().getId() : null, JDBCType.BIGINT);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du dossier médical", e);
        }
    }

    @Override
    public void update(DossierMedicale newValuesElement) {
        // UC: Modifier un Dossier
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE DossierMedicale SET dateDeCreation = ?, patient_id = ?, medecin_id = ?, situationFinanciere_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDateDeCreation()));
            ps.setLong(2, newValuesElement.getPatient() != null ? newValuesElement.getPatient().getId() : null);
            ps.setObject(3, newValuesElement.getMedecin() != null ? newValuesElement.getMedecin().getId() : null, JDBCType.BIGINT);
            ps.setObject(4, newValuesElement.getSituationFinanciere() != null ? newValuesElement.getSituationFinanciere().getId() : null, JDBCType.BIGINT);
            ps.setLong(5, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du dossier médical", e);
        }
    }

    @Override
    public void delete(DossierMedicale dossier) {
        if (dossier != null && dossier.getId() != null) deleteById(dossier.getId());
    }

    @Override
    public void deleteById(Long id) {
        // UC: Supprimer un Dossier
        String sql = "DELETE FROM DossierMedicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du dossier médical par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<DossierMedicale> findByPatientId(Long patientId) {
        // Souvent utilisé dans le cadre de "Consulter Dossier Médical" (via le Patient)
        String sql = "SELECT * FROM DossierMedicale WHERE patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapDossierMedicale(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du dossier médical par Patient ID", e);
        }
    }

    @Override
    public List<DossierMedicale> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM DossierMedicale WHERE medecin_id = ? ORDER BY dateDeCreation DESC";
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedicale(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des dossiers médicaux par Médecin ID", e);
        }
        return out;
    }

    // --- 3. Implémentation des Fonctions d'Association ---

    @Override
    public List<Consultation> findConsultationsByDossierId(Long dossierId) {
        // Récupère les consultations associées au Dossier (Consultation est Many-to-One vers DossierMedicale)
        String sql = "SELECT * FROM Consultation WHERE dossierMedicale_id = ? ORDER BY date DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des consultations du dossier", e);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findOrdonnancesByDossierId(Long dossierId) {
        // Récupère les ordonnances associées au Dossier (Ordonnance est Many-to-One vers DossierMedicale)
        String sql = "SELECT * FROM Ordonnance WHERE dossierMedicale_id = ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances du dossier", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findCertificatsByDossierId(Long dossierId) {
        // Récupère les certificats associés au Dossier (Certificat est Many-to-One vers DossierMedicale)
        String sql = "SELECT * FROM Certificat WHERE dossierMedicale_id = ? ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des certificats du dossier", e);
        }
        return out;
    }
}