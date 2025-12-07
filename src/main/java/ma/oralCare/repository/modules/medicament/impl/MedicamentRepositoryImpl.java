package ma.oralCare.repository.modules.medicament.impl;

import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.repository.modules.medicament.api.MedicamentRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapMedicament

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    // --- 1. Opérations CRUD de base (Gérer Catalogue Médicament - Admin) ---

    @Override
    public List<Medicament> findAll() {
        // UC: consulter Médicament
        String sql = "SELECT * FROM Medicament ORDER BY nom ASC";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapMedicament(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les médicaments", e);
        }
        return out;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM Medicament WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedicament(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du médicament par ID", e);
        }
    }

    @Override
    public void create(Medicament newElement) {
        // UC: ajouter Médicament
        if (newElement == null) return;
        String sql = "INSERT INTO Medicament (nom, laboratoire, type, forme, remboursable, prixUnitaire, description) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getLaboratoire());
            ps.setString(3, newElement.getType());
            ps.setString(4, newElement.getForme().name());
            ps.setBoolean(5, newElement.getRemboursable());
            ps.setDouble(6, newElement.getPrixUnitaire());
            ps.setString(7, newElement.getDescription());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du médicament", e);
        }
    }

    @Override
    public void update(Medicament newValuesElement) {
        // UC: Modifier Médicament
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Medicament SET nom = ?, laboratoire = ?, type = ?, forme = ?, remboursable = ?, prixUnitaire = ?, description = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getLaboratoire());
            ps.setString(3, newValuesElement.getType());
            ps.setString(4, newValuesElement.getForme().name());
            ps.setBoolean(5, newValuesElement.getRemboursable());
            ps.setDouble(6, newValuesElement.getPrixUnitaire());
            ps.setString(7, newValuesElement.getDescription());
            ps.setLong(8, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du médicament", e);
        }
    }

    @Override
    public void delete(Medicament medicament) {
        if (medicament != null && medicament.getId() != null) deleteById(medicament.getId());
    }

    @Override
    public void deleteById(Long id) {
        // UC: Supprimer Médicament
        String sql = "DELETE FROM Medicament WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du médicament par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques (UC: rechercher Médicament) ---

    @Override
    public List<Medicament> findByNomContaining(String nomPartiel) {
        // Utilisé pour l'autocomplétion ou la recherche simple
        String sql = "SELECT * FROM Medicament WHERE nom LIKE ? ORDER BY nom ASC";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nomPartiel + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par nom partiel", e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoire) {
        String sql = "SELECT * FROM Medicament WHERE laboratoire = ? ORDER BY nom ASC";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, laboratoire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par laboratoire", e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament forme) {
        String sql = "SELECT * FROM Medicament WHERE forme = ? ORDER BY nom ASC";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, forme.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par forme", e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByRemboursable(Boolean remboursable) {
        String sql = "SELECT * FROM Medicament WHERE remboursable = ? ORDER BY nom ASC";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, remboursable);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des médicaments par statut remboursable", e);
        }
        return out;
    }
}