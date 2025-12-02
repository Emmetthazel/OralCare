package ma.oralCare.repository.cabinet.impl;

import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.cabinet.api.RevenuesRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenuesRepositoryImpl implements RevenuesRepository {

    private final Connection connection;

    public RevenuesRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Revenues save(Revenues entity) {
        final String sql = """
                INSERT INTO revenues
                    (titre, description, montant, date)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getTitre());
            ps.setString(2, entity.getDescription());
            if (entity.getMontant() != null) {
                ps.setDouble(3, entity.getMontant());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            LocalDateTime date = entity.getDate();
            if (date != null) {
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(date));
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Revenues", e);
        }
    }

    @Override
    public Revenues update(Revenues entity) {
        final String sql = """
                UPDATE revenues
                SET titre = ?, description = ?, montant = ?, date = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getTitre());
            ps.setString(2, entity.getDescription());
            if (entity.getMontant() != null) {
                ps.setDouble(3, entity.getMontant());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            LocalDateTime date = entity.getDate();
            if (date != null) {
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(date));
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }

            ps.setLong(5, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Revenues with id " + entity.getId(), e);
        }
    }

    @Override
    public Revenues findById(Long id) {
        final String sql = "SELECT * FROM revenues WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRevenues(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Revenues with id " + id, e);
        }
    }

    @Override
    public List<Revenues> findAll() {
        final String sql = "SELECT * FROM revenues";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Revenues> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapRevenues(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Revenues", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM revenues WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Revenues with id " + id, e);
        }
    }
}


