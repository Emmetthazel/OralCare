package ma.oralCare.repository.cabinet.impl;

import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.cabinet.api.ChargesRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryImpl implements ChargesRepository {

    private final Connection connection;

    public ChargesRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Charges save(Charges entity) {
        final String sql = """
                INSERT INTO charges
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
            throw new RuntimeException("Error while saving Charges", e);
        }
    }

    @Override
    public Charges update(Charges entity) {
        final String sql = """
                UPDATE charges
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
            throw new RuntimeException("Error while updating Charges with id " + entity.getId(), e);
        }
    }

    @Override
    public Charges findById(Long id) {
        final String sql = "SELECT * FROM charges WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapCharges(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Charges with id " + id, e);
        }
    }

    @Override
    public List<Charges> findAll() {
        final String sql = "SELECT * FROM charges";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Charges> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapCharges(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Charges", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM charges WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Charges with id " + id, e);
        }
    }
}


