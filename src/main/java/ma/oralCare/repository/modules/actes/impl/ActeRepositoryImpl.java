package ma.oralCare.repository.modules.actes.impl;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.repository.modules.actes.api.ActeRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister pour gérer les connexions
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActeRepositoryImpl implements ActeRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Acte> findAll() {
        String sql = "SELECT * FROM acte ORDER BY categorie, prixDeBase";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapActe(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de tous les actes", e); }
        return out;
    }

    @Override
    public Acte findById(Long id) {
        String sql = "SELECT * FROM acte WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapActe(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'acte par ID", e); }
    }

    @Override
    public void create(Acte newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO acte (libelle, categorie, prixDeBase) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getLibelle());
            ps.setString(2, newElement.getCategorie());
            ps.setDouble(3, newElement.getPrixDeBase());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de l'acte", e); }
    }

    @Override
    public void update(Acte newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE acte SET libelle = ?, categorie = ?, prixDeBase = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getLibelle());
            ps.setString(2, newValuesElement.getCategorie());
            ps.setDouble(3, newValuesElement.getPrixDeBase());
            ps.setLong(4, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de l'acte", e); }
    }

    @Override
    public void delete(Acte acte) {
        if (acte != null && acte.getId() != null) deleteById(acte.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM acte WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de l'acte par ID", e); }
    }

    // --- 2. Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<Acte> findByLibelle(String libelle) {
        String sql = "SELECT * FROM acte WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapActe(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'acte par libellé", e); }
    }

    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = "SELECT * FROM acte WHERE categorie = ?";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapActe(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par catégorie", e); }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM acte WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la vérification de l'existence de l'acte", e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM acte";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors du comptage des actes", e); }
        return 0;
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        String sql = "SELECT * FROM acte LIMIT ? OFFSET ?";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapActe(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par page", e); }
        return out;
    }

    // --- 3. Méthodes de Logique Métier et de Relation (Navigation Inverse) ---

    @Override
    public List<InterventionMedecin> findInterventionsByActeId(Long acteId) {
        // Cette méthode nécessite le RowMapper pour InterventionMedecin.
        String sql = "SELECT im.* FROM interventionmedecin im WHERE im.acte_id = ?";
        List<InterventionMedecin> out = new ArrayList<>();

        // ATTENTION: Assurez-vous d'avoir une méthode RowMappers.mapInterventionMedecin(rs)
        /*
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des interventions par Acte ID", e); }
        */
        return out; // Retourne vide en l'absence de RowMapper complet
    }

    @Override
    public Optional<Acte> findByInterventionMedecinId(Long interventionMedecinId) {
        // Jointure pour trouver l'acte lié à une intervention
        String sql = "SELECT a.* FROM acte a JOIN interventionmedecin im ON a.id = im.acte_id WHERE im.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, interventionMedecinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapActe(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'acte par intervention", e); }
    }

}