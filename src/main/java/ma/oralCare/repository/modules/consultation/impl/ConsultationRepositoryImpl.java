package ma.oralCare.repository.consultation.impl;

import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.consultation.api.ConsultationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    private final Connection connection;

    public ConsultationRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Consultation save(Consultation entity) {
        final String sql = """
                INSERT INTO consultations
                    (date, statut, observationMedecin)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate date = entity.getDate();
            if (date != null) {
                ps.setDate(1, java.sql.Date.valueOf(date));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }

            if (entity.getStatut() != null) {
                ps.setString(2, entity.getStatut().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            ps.setString(3, entity.getObservationMedecin());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Consultation", e);
        }
    }

    @Override
    public Consultation update(Consultation entity) {
        final String sql = """
                UPDATE consultations
                SET date = ?, statut = ?, observationMedecin = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            LocalDate date = entity.getDate();
            if (date != null) {
                ps.setDate(1, java.sql.Date.valueOf(date));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }

            if (entity.getStatut() != null) {
                ps.setString(2, entity.getStatut().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            ps.setString(3, entity.getObservationMedecin());
            ps.setLong(4, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Consultation with id " + entity.getId(), e);
        }
    }

    @Override
    public Consultation findById(Long id) {
        final String sql = "SELECT * FROM consultations WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapConsultation(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Consultation with id " + id, e);
        }
    }

    @Override
    public List<Consultation> findAll() {
        final String sql = "SELECT * FROM consultations";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Consultation> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapConsultation(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Consultations", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM consultations WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Consultation with id " + id, e);
        }
    }
}


