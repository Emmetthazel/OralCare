package ma.oralCare.repository.consultation.impl;

import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.consultation.api.InterventionMedecinRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    private final Connection connection;

    public InterventionMedecinRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public InterventionMedecin save(InterventionMedecin entity) {
        final String sql = """
                INSERT INTO interventions_medecin
                    (prixDePatient, numDent)
                VALUES (?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getPrixDePatient() != null) {
                ps.setDouble(1, entity.getPrixDePatient());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }

            if (entity.getNumDent() != null) {
                ps.setInt(2, entity.getNumDent());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving InterventionMedecin", e);
        }
    }

    @Override
    public InterventionMedecin update(InterventionMedecin entity) {
        final String sql = """
                UPDATE interventions_medecin
                SET prixDePatient = ?, numDent = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getPrixDePatient() != null) {
                ps.setDouble(1, entity.getPrixDePatient());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }

            if (entity.getNumDent() != null) {
                ps.setInt(2, entity.getNumDent());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }

            ps.setLong(3, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating InterventionMedecin with id " + entity.getId(), e);
        }
    }

    @Override
    public InterventionMedecin findById(Long id) {
        final String sql = "SELECT * FROM interventions_medecin WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapInterventionMedecin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding InterventionMedecin with id " + id, e);
        }
    }

    @Override
    public List<InterventionMedecin> findAll() {
        final String sql = "SELECT * FROM interventions_medecin";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<InterventionMedecin> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapInterventionMedecin(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all InterventionMedecin", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM interventions_medecin WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting InterventionMedecin with id " + id, e);
        }
    }
}


