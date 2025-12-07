package ma.oralCare.repository.modules.ordonnance.impl;

import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.modules.consultation.api.OrdonnanceRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapOrdonnance, mapPrescription

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // --- 1. Opérations CRUD de base (UC: créer, modifier, supprimer ordonnance) ---

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM Ordonnance ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les ordonnances", e);
        }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        // UC: consulter ordonnance
        String sql = "SELECT * FROM Ordonnance WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapOrdonnance(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'ordonnance par ID", e);
        }
    }

    @Override
    public void create(Ordonnance newElement) {
        // UC: créer ordonnance
        if (newElement == null || newElement.getDossierMedicale() == null) return;
        String sql = "INSERT INTO Ordonnance (date, dossierMedicale_id, consultation_id) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDate()));
            ps.setLong(2, newElement.getDossierMedicale().getId());
            ps.setObject(3, newElement.getConsultation() != null ? newElement.getConsultation().getId() : null, JDBCType.BIGINT);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'ordonnance", e);
        }
    }

    @Override
    public void update(Ordonnance newValuesElement) {
        // UC: modifier ordonnance
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Ordonnance SET date = ?, dossierMedicale_id = ?, consultation_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDate()));
            ps.setLong(2, newValuesElement.getDossierMedicale() != null ? newValuesElement.getDossierMedicale().getId() : null);
            ps.setObject(3, newValuesElement.getConsultation() != null ? newValuesElement.getConsultation().getId() : null, JDBCType.BIGINT);
            ps.setLong(4, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'ordonnance", e);
        }
    }

    @Override
    public void delete(Ordonnance ordonnance) {
        if (ordonnance != null && ordonnance.getId() != null) deleteById(ordonnance.getId());
    }

    @Override
    public void deleteById(Long id) {
        // UC: supprimer ordonnance
        String sql = "DELETE FROM Ordonnance WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ordonnance par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Ordonnance> findByDossierMedicaleId(Long dossierId) {
        String sql = "SELECT * FROM Ordonnance WHERE dossierMedicale_id = ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par Dossier Médical ID", e);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM Ordonnance WHERE consultation_id = ? ORDER BY date DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par Consultation ID", e);
        }
        return out;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Ordonnance WHERE date = ? ORDER BY id DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des ordonnances par date", e);
        }
        return out;
    }

    // --- 3. Implémentation des Fonctions d'Association ---

    @Override
    public List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        // Bien que géré par PrescriptionRepository, cette méthode offre une cohérence d'accès.
        String sql = "SELECT * FROM Prescription WHERE ordonnance_id = ?";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des prescriptions de l'ordonnance", e);
        }
        return out;
    }
}