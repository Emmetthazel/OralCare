package ma.oralCare.repository.modules.caisse.impl;

import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.modules.cabinet.api.RevenuesRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister pour gérer les connexions
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Revenues> findAll() {
        String sql = "SELECT * FROM Revenues ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRevenues(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les recettes", e);
        }
        return out;
    }

    @Override
    public Revenues findById(Long id) {
        String sql = "SELECT * FROM Revenues WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRevenues(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la recette par ID", e);
        }
    }

    @Override
    public void create(Revenues newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Revenues (titre, description, montant, date, cabinetMedicale_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getTitre());
            ps.setString(2, newElement.getDescription());
            ps.setDouble(3, newElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newElement.getDate()));
            ps.setLong(5, newElement.getCabinetMedicale() != null ? newElement.getCabinetMedicale().getId() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la recette", e);
        }
    }

    @Override
    public void update(Revenues newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Revenues SET titre = ?, description = ?, montant = ?, date = ?, cabinetMedicale_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getTitre());
            ps.setString(2, newValuesElement.getDescription());
            ps.setDouble(3, newValuesElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newValuesElement.getDate()));
            ps.setLong(5, newValuesElement.getCabinetMedicale() != null ? newValuesElement.getCabinetMedicale().getId() : null);
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la recette", e);
        }
    }

    @Override
    public void delete(Revenues revenues) {
        if (revenues != null && revenues.getId() != null) deleteById(revenues.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Revenues WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la recette par ID", e);
        }
    }

    // --- 2. Méthodes de Recherche Spécifiques (pour Rapports et Analyses) ---

    @Override
    public List<Revenues> findByCabinetMedicaleId(Long cabinetMedicaleId) {
        String sql = "SELECT * FROM Revenues WHERE cabinetMedicale_id = ? ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des recettes par Cabinet ID", e);
        }
        return out;
    }

    @Override
    public List<Revenues> findByTitreContaining(String titre) {
        String sql = "SELECT * FROM Revenues WHERE titre LIKE ? ORDER BY date DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            // Utilisation de % pour la recherche partielle
            ps.setString(1, "%" + titre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des recettes par titre", e);
        }
        return out;
    }

    @Override
    public Double calculateTotalRevenuesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT SUM(montant) FROM Revenues WHERE date BETWEEN ? AND ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // SUM peut retourner null si aucune ligne n'est trouvée, d'où l'utilisation de getDouble
                    double sum = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : sum;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du total des recettes", e);
        }
        return 0.0;
    }

    @Override
    public List<Revenues> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Revenues ORDER BY date DESC LIMIT ? OFFSET ?";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par page", e);
        }
        return out;
    }
}