package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.repository.modules.dossierMedical.api.AntecedentRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapAntecedent

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    // Hypothèse de table de jointure: patient_antecedent (patient_id, antecedent_id)

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Antecedent> findAll() {
        String sql = "SELECT * FROM Antecedent ORDER BY categorie, nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les antécédents", e);
        }
        return out;
    }

    @Override
    public Antecedent findById(Long id) {
        String sql = "SELECT * FROM Antecedent WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAntecedent(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'antécédent par ID", e);
        }
    }

    @Override
    public void create(Antecedent newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Antecedent (nom, categorie, niveauRisque) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getCategorie().name());
            ps.setString(3, newElement.getNiveauRisque().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'antécédent", e);
        }
    }

    @Override
    public void update(Antecedent newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Antecedent SET nom = ?, categorie = ?, niveauRisque = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getCategorie().name());
            ps.setString(3, newValuesElement.getNiveauRisque().name());
            ps.setLong(4, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'antécédent", e);
        }
    }

    @Override
    public void delete(Antecedent antecedent) {
        if (antecedent != null && antecedent.getId() != null) deleteById(antecedent.getId());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans Antecedent doit être en cascade sur patient_antecedent
        String sql = "DELETE FROM Antecedent WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'antécédent par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques (Antécédents génériques) ---

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        String sql = "SELECT * FROM Antecedent WHERE categorie = ? ORDER BY nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, categorie.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des antécédents par catégorie", e);
        }
        return out;
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque) {
        String sql = "SELECT * FROM Antecedent WHERE niveauRisque = ? ORDER BY nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, niveauRisque.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des antécédents par niveau de risque", e);
        }
        return out;
    }

    @Override
    public List<Antecedent> findByNomContaining(String nom) {
        String sql = "SELECT * FROM Antecedent WHERE nom LIKE ? ORDER BY nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des antécédents par nom", e);
        }
        return out;
    }

    // --- 3. Implémentation des Méthodes de Gestion de la Relation Patient-Antecedent ---

    @Override
    public List<Antecedent> findByPatientId(Long patientId) {
        // Jointure entre Antecedent et la table Many-to-Many
        String sql = "SELECT a.* FROM Antecedent a JOIN patient_antecedent pa ON a.id = pa.antecedent_id WHERE pa.patient_id = ? ORDER BY a.niveauRisque DESC, a.nom";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des antécédents par Patient ID", e);
        }
        return out;
    }

    @Override
    public void linkAntecedentToPatient(Long antecedentId, Long patientId) {
        String sql = "INSERT INTO patient_antecedent (antecedent_id, patient_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            ps.setLong(2, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // NOTE: Si la contrainte d'unicité est violée (déjà lié), on peut ignorer l'erreur ou la gérer spécifiquement
            throw new RuntimeException("Erreur lors de l'association de l'antécédent au patient", e);
        }
    }

    @Override
    public void unlinkAntecedentFromPatient(Long antecedentId, Long patientId) {
        String sql = "DELETE FROM patient_antecedent WHERE antecedent_id = ? AND patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, antecedentId);
            ps.setLong(2, patientId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la dissociation de l'antécédent du patient", e);
        }
    }
}