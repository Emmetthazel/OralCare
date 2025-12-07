package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapPatient, mapAntecedent

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    // --- 1. Opérations CRUD de base (UC: Ajouter, Modifier, Supprimer Patient) ---

    @Override
    public List<Patient> findAll() {
        // UC: Consulter liste patients
        String sql = "SELECT * FROM Patient ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les patients", e);
        }
        return out;
    }

    @Override
    public Patient findById(Long id) {
        // UC: Consulter Patient
        String sql = "SELECT * FROM Patient WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPatient(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du patient par ID", e);
        }
    }

    @Override
    public void create(Patient newElement) {
        // UC: Ajouter Patient
        if (newElement == null) return;
        String sql = "INSERT INTO Patient (nom, prenom, adresse, telephone, email, dateNaissance, dateCreation, sexe, assurance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getPrenom());
            ps.setString(3, newElement.getAdresse()); // L'adresse est stockée comme String (selon l'entité Patient fournie)
            ps.setString(4, newElement.getTelephone());
            ps.setString(5, newElement.getEmail());
            ps.setDate(6, newElement.getDateNaissance() != null ? Date.valueOf(newElement.getDateNaissance()) : null);
            ps.setTimestamp(7, newElement.getDateCreation() != null ? Timestamp.valueOf(newElement.getDateCreation()) : Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, newElement.getSexe() != null ? newElement.getSexe().name() : null);
            ps.setString(9, newElement.getAssurance() != null ? newElement.getAssurance().name() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du patient", e);
        }
    }

    @Override
    public void update(Patient newValuesElement) {
        // UC: Modifier Patient
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Patient SET nom = ?, prenom = ?, adresse = ?, telephone = ?, email = ?, dateNaissance = ?, sexe = ?, assurance = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getPrenom());
            ps.setString(3, newValuesElement.getAdresse());
            ps.setString(4, newValuesElement.getTelephone());
            ps.setString(5, newValuesElement.getEmail());
            ps.setDate(6, newValuesElement.getDateNaissance() != null ? Date.valueOf(newValuesElement.getDateNaissance()) : null);
            ps.setString(7, newValuesElement.getSexe() != null ? newValuesElement.getSexe().name() : null);
            ps.setString(8, newValuesElement.getAssurance() != null ? newValuesElement.getAssurance().name() : null);
            ps.setLong(9, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du patient", e);
        }
    }

    @Override
    public void delete(Patient patient) {
        if (patient != null && patient.getId() != null) deleteById(patient.getId());
    }

    @Override
    public void deleteById(Long id) {
        // UC: Supprimer Patient
        String sql = "DELETE FROM Patient WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du patient par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Patient> findByNomAndPrenom(String nom, String prenom) {
        String sql = "SELECT * FROM Patient WHERE nom LIKE ? AND prenom LIKE ? ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + prenom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par nom et prénom", e);
        }
        return out;
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = "SELECT * FROM Patient WHERE telephone = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, telephone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par téléphone", e);
        }
    }

    @Override
    public List<Patient> findByDateNaissanceBefore(LocalDate dateNaissance) {
        String sql = "SELECT * FROM Patient WHERE dateNaissance < ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateNaissance));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par date de naissance (avant)", e);
        }
        return out;
    }

    @Override
    public List<Patient> findByAssurance(Assurance assurance) {
        String sql = "SELECT * FROM Patient WHERE assurance = ?";
        List<Patient> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, assurance.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par assurance", e);
        }
        return out;
    }

    // --- 3. Implémentation des Méthodes d'Association (Antécédents) ---

    @Override
    public List<Antecedent> findAntecedentsByPatientId(Long patientId) {
        // Supposons une table de jointure Patient_Antecedent (many-to-many)
        String sql = "SELECT A.* FROM Antecedent A JOIN Patient_Antecedent PA ON A.id = PA.antecedent_id WHERE PA.patient_id = ?";
        List<Antecedent> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des antécédents pour le patient ID: " + patientId, e);
        }
        return out;
    }

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        // UC: Affecter Antécédent
        String sql = "INSERT INTO Patient_Antecedent (patient_id, antecedent_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Ignorer si l'association existe déjà (contrainte d'unicité)
            if (!e.getMessage().contains("duplicate key")) {
                throw new RuntimeException("Erreur lors de l'ajout de l'antécédent au patient", e);
            }
        }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM Patient_Antecedent WHERE patient_id = ? AND antecedent_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'antécédent du patient", e);
        }
    }
}