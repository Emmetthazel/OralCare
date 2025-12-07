package ma.oralCare.repository.modules.medicament.impl;

import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.modules.medicament.api.PrescriptionRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapPrescription

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    // --- 1. Opérations CRUD de base (Impliqué dans UC: Gérer les Ordonnances) ---

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM Prescription";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPrescription(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les prescriptions", e);
        }
        return out;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM Prescription WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPrescription(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la prescription par ID", e);
        }
    }

    @Override
    public void create(Prescription newElement) {
        // UC: ajouter prescription
        if (newElement == null || newElement.getOrdonnance() == null || newElement.getMedicament() == null) return;
        String sql = "INSERT INTO Prescription (quantite, frequence, dureeEnJours, ordonnance_id, medicament_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, newElement.getQuantite());
            ps.setString(2, newElement.getFrequence());
            ps.setInt(3, newElement.getDureeEnJours());
            ps.setLong(4, newElement.getOrdonnance().getId());
            ps.setLong(5, newElement.getMedicament().getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la prescription", e);
        }
    }

    @Override
    public void update(Prescription newValuesElement) {
        // Impliqué dans UC: Modifier Ordonnance
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Prescription SET quantite = ?, frequence = ?, dureeEnJours = ?, ordonnance_id = ?, medicament_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, newValuesElement.getQuantite());
            ps.setString(2, newValuesElement.getFrequence());
            ps.setInt(3, newValuesElement.getDureeEnJours());
            ps.setLong(4, newValuesElement.getOrdonnance() != null ? newValuesElement.getOrdonnance().getId() : null);
            ps.setLong(5, newValuesElement.getMedicament() != null ? newValuesElement.getMedicament().getId() : null);
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la prescription", e);
        }
    }

    @Override
    public void delete(Prescription prescription) {
        if (prescription != null && prescription.getId() != null) deleteById(prescription.getId());
    }

    @Override
    public void deleteById(Long id) {
        // Impliqué dans UC: Supprimer Ordonnance
        String sql = "DELETE FROM Prescription WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la prescription par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT * FROM Prescription WHERE ordonnance_id = ?";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, ordonnanceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPrescription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des prescriptions par Ordonnance ID", e);
        }
        return out;
    }
}