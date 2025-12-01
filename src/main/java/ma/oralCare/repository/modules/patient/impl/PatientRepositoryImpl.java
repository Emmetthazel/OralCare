package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    private final Connection connection;

    public PatientRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient save(Patient patient) {
        String sql = "INSERT INTO patient " +
                "(nom, prenom, adresse, telephone, email, dateNaissance, dateCreation, sexe, assurance) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getAdresse());
            ps.setString(4, patient.getTelephone());
            ps.setString(5, patient.getEmail());

            if (patient.getDateNaissance() != null) {
                ps.setDate(6, Date.valueOf(patient.getDateNaissance()));
            } else {
                ps.setNull(6, Types.DATE);
            }

            ps.setTimestamp(7, patient.getDateCreation() != null ?
                    Timestamp.valueOf(patient.getDateCreation()) :
                    Timestamp.valueOf(java.time.LocalDateTime.now()));

            ps.setString(8, patient.getSexe() != null ? patient.getSexe().name() : null);
            ps.setString(9, patient.getAssurance() != null ? patient.getAssurance().name() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Créer patient a échoué, aucune ligne affectée.");

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) patient.setId(generatedKeys.getLong(1));
                else throw new SQLException("Créer patient a échoué, aucun ID généré.");
            }
            return patient;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM patient WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return RowMappers.mapPatient(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patient";
        List<Patient> patients = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) patients.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE patient SET nom=?, prenom=?, adresse=?, telephone=?, email=?, dateNaissance=?, sexe=?, assurance=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getAdresse());
            ps.setString(4, patient.getTelephone());
            ps.setString(5, patient.getEmail());
            ps.setDate(6, patient.getDateNaissance() != null ? Date.valueOf(patient.getDateNaissance()) : null);
            ps.setString(7, patient.getSexe() != null ? patient.getSexe().name() : null);
            ps.setString(8, patient.getAssurance() != null ? patient.getAssurance().name() : null);
            ps.setLong(9, patient.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Patient patient) {
        deleteById(patient.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM patient WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM patient WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM patient";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM patient";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implémentation des méthodes spécifiques à PatientRepository
    @Override
    public List<Patient> findByNom(String nom) {
        return searchByField("nom", nom);
    }

    @Override
    public List<Patient> findByTelephone(String telephone) {
        return searchByField("telephone", telephone);
    }

    @Override
    public List<Patient> findByDateNaissance(LocalDate date) {
        return searchByField("dateNaissance", date);
    }

    @Override
    public List<Patient> findBySexe(ma.oralCare.entities.enums.Sexe sexe) {
        return searchByField("sexe", sexe != null ? sexe.name() : null);
    }

    @Override
    public List<Patient> findByAssurance(ma.oralCare.entities.enums.Assurance assurance) {
        return searchByField("assurance", assurance != null ? assurance.name() : null);
    }

    @Override
    public List<Patient> findByAdresseContaining(String adresse) {
        String sql = "SELECT * FROM patient WHERE adresse LIKE ?";
        List<Patient> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + adresse + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Patient> findByDateNaissanceBetween(LocalDate debut, LocalDate fin) {
        String sql = "SELECT * FROM patient WHERE dateNaissance BETWEEN ? AND ?";
        List<Patient> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(debut));
            ps.setDate(2, Date.valueOf(fin));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<Patient> searchPatients(String keyword) {
        String sql = "SELECT * FROM patient WHERE nom LIKE ? OR prenom LIKE ? OR adresse LIKE ?";
        List<Patient> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<Patient> searchByField(String field, Object value) {
        String sql = "SELECT * FROM patient WHERE " + field + " = ?";
        List<Patient> result = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (value instanceof LocalDate) ps.setDate(1, Date.valueOf((LocalDate) value));
            else ps.setObject(1, value);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) result.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
