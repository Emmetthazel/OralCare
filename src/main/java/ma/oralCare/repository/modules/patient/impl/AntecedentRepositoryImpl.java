package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.patient.Antecedent; // Utilisation directe
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque; // Utilisation directe de l'Enum
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AntecedentRepositoryImpl implements AntecedentRepository {


    // NOTE: Si Antecedent hérite de BaseEntity, la colonne ID devrait être 'id_entite'
    // Je corrige toutes les références 'id' en 'id_entite' pour respecter la convention BaseEntity.

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
    public Optional <Antecedent> findById(Long id) {
        // La requête SQL est correcte
        String sql = "SELECT * FROM Antecedent WHERE id_entite = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // CORRECTION 1: Envelopper l'objet trouvé dans Optional.of()
                    Antecedent antecedent = RowMappers.mapAntecedent(rs);
                    return Optional.of(antecedent);
                }
                // CORRECTION 2: Retourner Optional.empty() si aucun résultat
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de l'antécédent par ID", e);
        }
    }

    @Override
    public void create(Antecedent newElement) {
        if (newElement == null) return;

        // CORRECTION: Si Antecedent hérite de BaseEntity, la création doit aussi gérer l'insertion BaseEntity
        // POUR SIMPLIFIER, je conserve l'insertion simple mais je signale que c'est une simplification.
        // Si BaseEntity doit être inséré, il faut copier la logique de transaction du PatientRepositoryImpl.

        // CORRECTION: Utilisation de la colonne 'niveau_de_risque' si le getter est 'getNiveauDeRisque'
        String sql = "INSERT INTO Antecedent (nom, categorie, niveau_de_risque) VALUES (?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getCategorie().name());
            ps.setString(3, newElement.getNiveauDeRisque().name()); // <-- On suppose que le getter est getNiveauDeRisque()

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // CORRECTION: Utilisation de setIdEntite si héritage BaseEntity
                        newElement.setIdEntite(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de l'antécédent", e);
        }
    }

    @Override
    public void update(Antecedent newValuesElement) {
        if (newValuesElement == null || newValuesElement.getIdEntite() == null) return;

        // CORRECTION: Colonne DB 'niveau_de_risque' et ID 'id_entite'
        String sql = "UPDATE Antecedent SET nom = ?, categorie = ?, niveau_de_risque = ? WHERE id_entite = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getCategorie().name());

            // CORRECTION: Utilisation du getter supposé 'getNiveauDeRisque()'
            ps.setString(3, newValuesElement.getNiveauDeRisque().name());

            // CORRECTION: Utilisation de getIdEntite()
            ps.setLong(4, newValuesElement.getIdEntite());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'antécédent", e);
        }
    }

    @Override
    public void delete(Antecedent antecedent) {
        // CORRECTION: Utilisation de getIdEntite()
        if (antecedent != null && antecedent.getIdEntite() != null) deleteById(antecedent.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // CORRECTION: Utilisation de id_entite (ou BaseEntity si la suppression est centralisée)
        // Je supprime directement Antecedent, en supposant que la DB gère la cascade sur patient_antecedent.
        String sql = "DELETE FROM Antecedent WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'antécédent par ID", e);
        }
    }


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
        // CORRECTION: Colonne DB 'niveau_de_risque'
        String sql = "SELECT * FROM Antecedent WHERE niveau_de_risque = ? ORDER BY nom";
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
        // CORRECTION: id_entite pour l'Antecedent, et les noms de colonnes dans la table d'association
        String sql = "SELECT a.* FROM Antecedent a JOIN patient_antecedent pa ON a.id_entite = pa.antecedent_id WHERE pa.patient_id = ? ORDER BY a.niveau_de_risque DESC, a.nom";
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