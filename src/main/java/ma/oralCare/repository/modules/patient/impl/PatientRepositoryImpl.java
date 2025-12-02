package ma.oralCare.repository.patient.impl;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.patient.api.PatientRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    private final Connection connection;

    public PatientRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient save(Patient entity) {
        final String sql = """
                INSERT INTO patients
                    (nom, prenom, adresse, telephone, email, dateNaissance, dateCreation, sexe, assurance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getPrenom());
            ps.setString(3, entity.getAdresse());
            ps.setString(4, entity.getTelephone());
            ps.setString(5, entity.getEmail());
            if (entity.getDateNaissance() != null) {
                ps.setDate(6, java.sql.Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            if (entity.getDateCreation() != null) {
                ps.setTimestamp(7, java.sql.Timestamp.valueOf(entity.getDateCreation()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            if (entity.getSexe() != null) {
                ps.setString(8, entity.getSexe().name());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }
            if (entity.getAssurance() != null) {
                ps.setString(9, entity.getAssurance().name());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Patient", e);
        }
    }

    @Override
    public Patient update(Patient entity) {
        final String sql = """
                UPDATE patients
                SET nom = ?, prenom = ?, adresse = ?, telephone = ?, email = ?,
                    dateNaissance = ?, dateCreation = ?, sexe = ?, assurance = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getPrenom());
            ps.setString(3, entity.getAdresse());
            ps.setString(4, entity.getTelephone());
            ps.setString(5, entity.getEmail());
            if (entity.getDateNaissance() != null) {
                ps.setDate(6, java.sql.Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(6, java.sql.Types.DATE);
            }
            if (entity.getDateCreation() != null) {
                ps.setTimestamp(7, java.sql.Timestamp.valueOf(entity.getDateCreation()));
            } else {
                ps.setNull(7, java.sql.Types.TIMESTAMP);
            }
            if (entity.getSexe() != null) {
                ps.setString(8, entity.getSexe().name());
            } else {
                ps.setNull(8, java.sql.Types.VARCHAR);
            }
            if (entity.getAssurance() != null) {
                ps.setString(9, entity.getAssurance().name());
            } else {
                ps.setNull(9, java.sql.Types.VARCHAR);
            }
            ps.setLong(10, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Patient with id " + entity.getId(), e);
        }
    }

    @Override
    public Patient findById(Long id) {
        final String sql = "SELECT * FROM patients WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapPatient(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Patient with id " + id, e);
        }
    }

    @Override
    public List<Patient> findAll() {
        final String sql = "SELECT * FROM patients";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Patient> patients = new ArrayList<>();
            while (rs.next()) {
                patients.add(RowMappers.mapPatient(rs));
            }
            return patients;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Patients", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM patients WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Patient with id " + id, e);
        }
    }
}

package ma.oralCare.repository.patient.impl;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.patient.PatientRepository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PatientRepositoryImpl implements PatientRepository {

    private static final String TABLE_NAME = "patients";

    private final Connection connection;

    public PatientRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient save(Patient patient) {
        String sql = "INSERT INTO " + TABLE_NAME + " " +
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

            if (patient.getDateCreation() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(patient.getDateCreation()));
            } else {
                ps.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));
            }

            ps.setString(8, patient.getSexe() != null ? patient.getSexe().name() : null);
            ps.setString(9, patient.getAssurance() != null ? patient.getAssurance().name() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating patient failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }

            return patient;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving Patient", e);
        }
    }

    @Override
    public Patient update(Patient patient) {
        String sql = "UPDATE " + TABLE_NAME + " SET " +
                "nom = ?, prenom = ?, adresse = ?, telephone = ?, email = ?, " +
                "dateNaissance = ?, sexe = ?, assurance = ? " +
                "WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
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

            ps.setString(7, patient.getSexe() != null ? patient.getSexe().name() : null);
            ps.setString(8, patient.getAssurance() != null ? patient.getAssurance().name() : null);
            ps.setLong(9, patient.getId());

            ps.executeUpdate();
            return patient;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Patient", e);
        }
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapPatient(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Patient by id", e);
        }
        return null;
    }

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<Patient> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all Patients", e);
        }

        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Patient by id", e);
        }
    }
}


