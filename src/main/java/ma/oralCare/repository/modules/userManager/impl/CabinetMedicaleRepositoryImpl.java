package ma.oralCare.repository.modules.userManager.impl;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.entities.staff.Staff;
import ma.oralCare.repository.modules.userManager.api.CabinetMedicaleRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Fourni précédemment

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du CabinetMedicaleRepository en utilisant JDBC.
 * Gère les opérations CRUD et l'accès aux collections associées (Charges, Revenues, Staff).
 */
public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {

    // --- 1. Implémentation des méthodes CRUD de CrudRepository ---

    @Override
    public List<CabinetMedicale> findAll() {
        String sql = "SELECT * FROM CabinetMedicale";
        List<CabinetMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapCabinetMedicale(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les cabinets médicaux", e);
        }
        return out;
    }

    @Override
    public CabinetMedicale findById(Long id) {
        String sql = "SELECT * FROM CabinetMedicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedicale(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du CabinetMedicale par ID", e);
        }
    }

    @Override
    public void create(CabinetMedicale newElement) {
        if (newElement == null) return;
        // Supposons que l'adresse (Value Object) est gérée par une référence (adresseId) ou une colonne intégrée
        String sql = "INSERT INTO CabinetMedicale (nom, email, logo, cin, tel1, tel2, siteWeb, instagram, facebook, description, adresseId) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getEmail());
            ps.setString(3, newElement.getLogo());
            ps.setString(4, newElement.getCin());
            ps.setString(5, newElement.getTel1());
            ps.setString(6, newElement.getTel2());
            ps.setString(7, newElement.getSiteWeb());
            ps.setString(8, newElement.getInstagram());
            ps.setString(9, newElement.getFacebook());
            ps.setString(10, newElement.getDescription());
            // L'adresse n'a pas d'ID dans votre entité, supposons null ici,
            // ou une gestion spécifique dans le service si elle doit être persistée séparément.
            ps.setObject(11, null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du CabinetMedicale", e);
        }
    }

    @Override
    public void update(CabinetMedicale newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE CabinetMedicale SET nom = ?, email = ?, logo = ?, cin = ?, tel1 = ?, tel2 = ?, siteWeb = ?, instagram = ?, facebook = ?, description = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getEmail());
            ps.setString(3, newValuesElement.getLogo());
            ps.setString(4, newValuesElement.getCin());
            ps.setString(5, newValuesElement.getTel1());
            ps.setString(6, newValuesElement.getTel2());
            ps.setString(7, newValuesElement.getSiteWeb());
            ps.setString(8, newValuesElement.getInstagram());
            ps.setString(9, newValuesElement.getFacebook());
            ps.setString(10, newValuesElement.getDescription());
            ps.setLong(11, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du CabinetMedicale", e);
        }
    }

    @Override
    public void delete(CabinetMedicale cabinetMedicale) {
        if (cabinetMedicale != null && cabinetMedicale.getId() != null) deleteById(cabinetMedicale.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM CabinetMedicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du CabinetMedicale par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<CabinetMedicale> findByCin(String cin) {
        String sql = "SELECT * FROM CabinetMedicale WHERE cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du CabinetMedicale par CIN", e);
        }
    }

    @Override
    public Optional<CabinetMedicale> findByEmail(String email) {
        String sql = "SELECT * FROM CabinetMedicale WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedicale(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du CabinetMedicale par email", e);
        }
    }

    // --- 3. Implémentation des Méthodes d'Accès aux Relations (Collections) ---

    @Override
    public List<Charges> findAllCharges(Long cabinetId) {
        // Supposons que la table Charges a une colonne cabinetMedicale_id
        String sql = "SELECT * FROM Charges WHERE cabinetMedicale_id = ?";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des charges pour le Cabinet ID: " + cabinetId, e);
        }
        return out;
    }

    @Override
    public List<Revenues> findAllRevenues(Long cabinetId) {
        // Supposons que la table Revenues a une colonne cabinetMedicale_id
        String sql = "SELECT * FROM Revenues WHERE cabinetMedicale_id = ?";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRevenues(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des revenus pour le Cabinet ID: " + cabinetId, e);
        }
        return out;
    }

    @Override
    public List<Staff> findAllStaff(Long cabinetId) {
        // La jointure est nécessaire car Staff hérite de Utilisateur et Staff est lié au Cabinet.
        String sql = "SELECT U.*, S.* FROM Utilisateur U JOIN Staff S ON U.id = S.id WHERE S.cabinetMedicaleId = ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                // Utilise le mapper Staff pour récupérer les données de Staff et Utilisateur
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du personnel pour le Cabinet ID: " + cabinetId, e);
        }
        return out;
    }
}