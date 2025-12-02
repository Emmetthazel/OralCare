package ma.oralCare.repository.agenda.impl;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.repository.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    private final Connection connection;

    public AgendaMensuelRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AgendaMensuel save(AgendaMensuel entity) {
        final String sql = """
                INSERT INTO agenda_mensuels
                    (mois)
                VALUES (?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getMois() != null) {
                ps.setString(1, entity.getMois().name());
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving AgendaMensuel", e);
        }
    }

    @Override
    public AgendaMensuel update(AgendaMensuel entity) {
        final String sql = """
                UPDATE agenda_mensuels
                SET mois = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getMois() != null) {
                ps.setString(1, entity.getMois().name());
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }
            ps.setLong(2, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating AgendaMensuel with id " + entity.getId(), e);
        }
    }

    @Override
    public AgendaMensuel findById(Long id) {
        final String sql = "SELECT * FROM agenda_mensuels WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapAgendaMensuel(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding AgendaMensuel with id " + id, e);
        }
    }

    @Override
    public List<AgendaMensuel> findAll() {
        final String sql = "SELECT * FROM agenda_mensuels";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<AgendaMensuel> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapAgendaMensuel(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all AgendaMensuels", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM agenda_mensuels WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting AgendaMensuel with id " + id, e);
        }
    }
}


