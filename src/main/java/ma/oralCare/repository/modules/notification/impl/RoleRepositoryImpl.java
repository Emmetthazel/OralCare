package ma.oralCare.repository.notification.impl;

import ma.oralCare.entities.notification.Role;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.notification.api.RoleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoleRepositoryImpl implements RoleRepository {

    private final Connection connection;

    public RoleRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Role save(Role entity) {
        final String sql = """
                INSERT INTO roles
                    (libelle)
                VALUES (?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getLibelle() != null) {
                ps.setString(1, entity.getLibelle().name());
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
            throw new RuntimeException("Error while saving Role", e);
        }
    }

    @Override
    public Role update(Role entity) {
        final String sql = """
                UPDATE roles
                SET libelle = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getLibelle() != null) {
                ps.setString(1, entity.getLibelle().name());
            } else {
                ps.setNull(1, java.sql.Types.VARCHAR);
            }
            ps.setLong(2, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Role with id " + entity.getId(), e);
        }
    }

    @Override
    public Role findById(Long id) {
        final String sql = "SELECT * FROM roles WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRole(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Role with id " + id, e);
        }
    }

    @Override
    public List<Role> findAll() {
        final String sql = "SELECT * FROM roles";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Role> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapRole(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Roles", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM roles WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Role with id " + id, e);
        }
    }
}


