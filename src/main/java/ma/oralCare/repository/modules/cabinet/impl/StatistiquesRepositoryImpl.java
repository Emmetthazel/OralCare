package ma.oralCare.repository.cabinet.impl;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.repository.cabinet.api.StatistiquesRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    private final Connection connection;

    public StatistiquesRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Statistiques save(Statistiques entity) {
        final String sql = """
                INSERT INTO statistiques
                    (nom, categorie, chiffre, dateCalcul)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            if (entity.getCategorie() != null) {
                ps.setString(2, entity.getCategorie().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            if (entity.getChiffre() != null) {
                ps.setDouble(3, entity.getChiffre());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            LocalDate dateCalcul = entity.getDateCalcul();
            if (dateCalcul != null) {
                ps.setDate(4, java.sql.Date.valueOf(dateCalcul));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Statistiques", e);
        }
    }

    @Override
    public Statistiques update(Statistiques entity) {
        final String sql = """
                UPDATE statistiques
                SET nom = ?, categorie = ?, chiffre = ?, dateCalcul = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            if (entity.getCategorie() != null) {
                ps.setString(2, entity.getCategorie().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            if (entity.getChiffre() != null) {
                ps.setDouble(3, entity.getChiffre());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            LocalDate dateCalcul = entity.getDateCalcul();
            if (dateCalcul != null) {
                ps.setDate(4, java.sql.Date.valueOf(dateCalcul));
            } else {
                ps.setNull(4, java.sql.Types.DATE);
            }

            ps.setLong(5, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Statistiques with id " + entity.getId(), e);
        }
    }

    @Override
    public Statistiques findById(Long id) {
        final String sql = "SELECT * FROM statistiques WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapStatistiques(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Statistiques with id " + id, e);
        }
    }

    @Override
    public List<Statistiques> findAll() {
        final String sql = "SELECT * FROM statistiques";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Statistiques> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapStatistiques(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Statistiques", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM statistiques WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Statistiques with id " + id, e);
        }
    }
}


