package ma.oralCare.repository.staff.impl;

import ma.oralCare.entities.staff.Utilisateur;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.staff.api.UtilisateurRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    private final Connection connection;

    public UtilisateurRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Utilisateur save(Utilisateur entity) {
        final String sql = """
                INSERT INTO utilisateurs
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
            throw new RuntimeException("Error while saving Utilisateur", e);
        }
    }

    @Override
    public Utilisateur update(Utilisateur entity) {
        final String sql = """
                UPDATE utilisateurs
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
            throw new RuntimeException("Error while updating Utilisateur with id " + entity.getId(), e);
        }
    }

    @Override
    public Utilisateur findById(Long id) {
        final String sql = "SELECT * FROM utilisateurs WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapUtilisateur(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Utilisateur with id " + id, e);
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        final String sql = "SELECT * FROM utilisateurs";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Utilisateur> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapUtilisateur(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Utilisateurs", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Utilisateur with id " + id, e);
        }
    }
}


