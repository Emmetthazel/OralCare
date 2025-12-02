package ma.oralCare.repository.staff.impl;

import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.staff.api.MedecinRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    private final Connection connection;

    public MedecinRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Medecin save(Medecin entity) {
        final String sql = """
                INSERT INTO medecins
                    (nom, email, cin, tel, sexe, login, motDePass, lastLoginDate, dateNaissance,
                     salaire, prime, dateRecrutement, soldeConge, specialite)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            if (entity.getSalaire() != null) {
                ps.setDouble(10, entity.getSalaire());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
            }
            if (entity.getPrime() != null) {
                ps.setDouble(11, entity.getPrime());
            } else {
                ps.setNull(11, java.sql.Types.DOUBLE);
            }
            if (entity.getDateRecrutement() != null) {
                ps.setDate(12, java.sql.Date.valueOf(entity.getDateRecrutement()));
            } else {
                ps.setNull(12, java.sql.Types.DATE);
            }
            if (entity.getSoldeConge() != null) {
                ps.setInt(13, entity.getSoldeConge());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }

            ps.setString(14, entity.getSpecialite());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Medecin", e);
        }
    }

    @Override
    public Medecin update(Medecin entity) {
        final String sql = """
                UPDATE medecins
                SET nom = ?, email = ?, cin = ?, tel = ?, sexe = ?, login = ?, motDePass = ?,
                    lastLoginDate = ?, dateNaissance = ?,
                    salaire = ?, prime = ?, dateRecrutement = ?, soldeConge = ?,
                    specialite = ?
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

            if (entity.getSalaire() != null) {
                ps.setDouble(10, entity.getSalaire());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
            }
            if (entity.getPrime() != null) {
                ps.setDouble(11, entity.getPrime());
            } else {
                ps.setNull(11, java.sql.Types.DOUBLE);
            }
            if (entity.getDateRecrutement() != null) {
                ps.setDate(12, java.sql.Date.valueOf(entity.getDateRecrutement()));
            } else {
                ps.setNull(12, java.sql.Types.DATE);
            }
            if (entity.getSoldeConge() != null) {
                ps.setInt(13, entity.getSoldeConge());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }

            ps.setString(14, entity.getSpecialite());
            ps.setLong(15, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Medecin with id " + entity.getId(), e);
        }
    }

    @Override
    public Medecin findById(Long id) {
        final String sql = "SELECT * FROM medecins WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapMedecin(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Medecin with id " + id, e);
        }
    }

    @Override
    public List<Medecin> findAll() {
        final String sql = "SELECT * FROM medecins";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Medecin> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapMedecin(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Medecins", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM medecins WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Medecin with id " + id, e);
        }
    }
}


