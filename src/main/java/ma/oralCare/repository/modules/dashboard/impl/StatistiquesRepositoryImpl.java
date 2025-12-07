package ma.oralCare.repository.modules.dashboard.impl;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.modules.dashboard.api.StatistiquesRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapStatistiques

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Statistiques> findAll() {
        String sql = "SELECT * FROM Statistiques ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les statistiques", e);
        }
        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = "SELECT * FROM Statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistiques(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la statistique par ID", e);
        }
    }

    @Override
    public void create(Statistiques newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Statistiques (nom, categorie, chiffre, dateCalcul, cabinetMedicale_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getCategorie().name());
            ps.setDouble(3, newElement.getChiffre());
            ps.setDate(4, Date.valueOf(newElement.getDateCalcul()));
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
            throw new RuntimeException("Erreur lors de la création de la statistique", e);
        }
    }

    @Override
    public void update(Statistiques newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Statistiques SET nom = ?, categorie = ?, chiffre = ?, dateCalcul = ?, cabinetMedicale_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getCategorie().name());
            ps.setDouble(3, newValuesElement.getChiffre());
            ps.setDate(4, Date.valueOf(newValuesElement.getDateCalcul()));
            ps.setLong(5, newValuesElement.getCabinetMedicale() != null ? newValuesElement.getCabinetMedicale().getId() : null);
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la statistique", e);
        }
    }

    @Override
    public void delete(Statistiques statistiques) {
        if (statistiques != null && statistiques.getId() != null) deleteById(statistiques.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la statistique par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Statistiques> findByCabinetMedicaleId(Long cabinetMedicaleId) {
        String sql = "SELECT * FROM Statistiques WHERE cabinetMedicale_id = ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par Cabinet ID", e);
        }
        return out;
    }

    @Override
    public List<Statistiques> findByCategorie(StatistiqueCategorie categorie) {
        String sql = "SELECT * FROM Statistiques WHERE categorie = ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par catégorie", e);
        }
        return out;
    }

    @Override
    public List<Statistiques> findByDateCalcul(LocalDate dateCalcul) {
        String sql = "SELECT * FROM Statistiques WHERE dateCalcul = ? ORDER BY nom ASC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateCalcul));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par date", e);
        }
        return out;
    }

    @Override
    public List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT * FROM Statistiques WHERE dateCalcul BETWEEN ? AND ? ORDER BY dateCalcul ASC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des statistiques par période", e);
        }
        return out;
    }

    @Override
    public Optional<Statistiques> findLatestByCategorieAndCabinet(StatistiqueCategorie categorie, Long cabinetMedicaleId) {
        // Sélectionne la statistique la plus récente pour une catégorie et un cabinet
        String sql = "SELECT * FROM Statistiques WHERE categorie = ? AND cabinetMedicale_id = ? ORDER BY dateCalcul DESC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            ps.setLong(2, cabinetMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapStatistiques(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la dernière statistique par catégorie", e);
        }
    }
}