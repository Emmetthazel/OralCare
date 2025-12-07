package ma.oralCare.repository.modules.certificat.impl;

import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.modules.certificat.api.CertificatRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister pour gérer les connexions
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CertificatRepositoryImpl implements CertificatRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM Certificat ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCertificat(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les certificats", e);
        }
        return out;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM Certificat WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCertificat(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du certificat par ID", e);
        }
    }

    @Override
    public void create(Certificat newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Certificat (dateDebut, dateFin, duree, noteMedecin, dossierMedicale_id, consultation_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDateDebut()));
            ps.setDate(2, Date.valueOf(newElement.getDateFin()));
            ps.setInt(3, newElement.getDuree());
            ps.setString(4, newElement.getNoteMedecin());
            ps.setLong(5, newElement.getDossierMedicale() != null ? newElement.getDossierMedicale().getId() : null);
            ps.setLong(6, newElement.getConsultation() != null ? newElement.getConsultation().getId() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du certificat", e);
        }
    }

    @Override
    public void update(Certificat newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Certificat SET dateDebut = ?, dateFin = ?, duree = ?, noteMedecin = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDateDebut()));
            ps.setDate(2, Date.valueOf(newValuesElement.getDateFin()));
            ps.setInt(3, newValuesElement.getDuree());
            ps.setString(4, newValuesElement.getNoteMedecin());
            ps.setLong(5, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du certificat", e);
        }
    }

    @Override
    public void delete(Certificat certificat) {
        if (certificat != null && certificat.getId() != null) deleteById(certificat.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Certificat WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du certificat par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Certificat> findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT * FROM Certificat WHERE dossierMedicale_id = ? ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par Dossier Médical ID", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findByDateDebut(LocalDate date) {
        String sql = "SELECT * FROM Certificat WHERE dateDebut = ? ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par date de début", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findValidCertificates(LocalDate currentDate) {
        // Sélectionne tous les certificats dont la dateFin est supérieure ou égale à la date actuelle
        String sql = "SELECT * FROM Certificat WHERE dateFin >= ? ORDER BY dateFin ASC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(currentDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats valides", e);
        }
        return out;
    }

    @Override
    public List<Certificat> findByNoteMedecinContaining(String noteFragment) {
        String sql = "SELECT * FROM Certificat WHERE noteMedecin LIKE ? ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            // Utilisation de % pour la recherche partielle
            ps.setString(1, "%" + noteFragment + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des certificats par note du médecin", e);
        }
        return out;
    }
}