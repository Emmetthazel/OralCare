package ma.oralCare.repository.staff.impl;

import ma.oralCare.entities.staff.Admin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.staff.api.AdminRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AdminRepositoryImpl implements AdminRepository {

    private final Connection connection;

    public AdminRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Admin save(Admin entity) {
        final String sql = """
                INSERT INTO admins
                    (nom, email, cin, tel, sexe, login, motDePass, lastLoginDate, dateNaissance)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getCin());
            ps.setString(4, entity.getTel());
            if (entity.getSexe() != null) {
                ps.setString(5, entity.getSexe().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.setString(6, entity.getLogin());
            ps.setString(7, entity.getMotDePass());
            if (entity.getLastLoginDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(entity.getLastLoginDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            if (entity.getDateNaissance() != null) {
                ps.setDate(9, java.sql.Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Admin", e);
        }
    }

    @Override
    public Admin update(Admin entity) {
        final String sql = """
                UPDATE admins
                SET nom = ?, email = ?, cin = ?, tel = ?, sexe = ?, login = ?, motDePass = ?,
                    lastLoginDate = ?, dateNaissance = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getCin());
            ps.setString(4, entity.getTel());
            if (entity.getSexe() != null) {
                ps.setString(5, entity.getSexe().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }
            ps.setString(6, entity.getLogin());
            ps.setString(7, entity.getMotDePass());
            if (entity.getLastLoginDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(entity.getLastLoginDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            if (entity.getDateNaissance() != null) {
                ps.setDate(9, java.sql.Date.valueOf(entity.getDateNaissance()));
            } else {
                ps.setNull(9, java.sql.Types.DATE);
            }
            ps.setLong(10, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Admin with id " + entity.getId(), e);
        }
    }

    @Override
    public Admin findById(Long id) {
        final String sql = "SELECT * FROM admins WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapAdmin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Admin with id " + id, e);
        }
    }

    @Override
    public List<Admin> findAll() {
        final String sql = "SELECT * FROM admins";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Admin> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapAdmin(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Admins", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM admins WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Admin with id " + id, e);
        }
    }
}


