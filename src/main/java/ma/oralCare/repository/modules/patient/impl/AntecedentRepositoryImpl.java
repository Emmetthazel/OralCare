package ma.oralCare.repository.patient.impl;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.patient.api.AntecedentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    private final Connection connection;

    public AntecedentRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Antecedent save(Antecedent entity) {
        final String sql = """
                INSERT INTO antecedents
                    (nom, categorie, niveauRisque)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            if (entity.getCategorie() != null) {
                ps.setString(2, entity.getCategorie().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            if (entity.getNiveauRisque() != null) {
                ps.setString(3, entity.getNiveauRisque().name());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Antecedent", e);
        }
    }

    @Override
    public Antecedent update(Antecedent entity) {
        final String sql = """
                UPDATE antecedents
                SET nom = ?, categorie = ?, niveauRisque = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            if (entity.getCategorie() != null) {
                ps.setString(2, entity.getCategorie().name());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            if (entity.getNiveauRisque() != null) {
                ps.setString(3, entity.getNiveauRisque().name());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }
            ps.setLong(4, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Antecedent with id " + entity.getId(), e);
        }
    }

    @Override
    public Antecedent findById(Long id) {
        final String sql = "SELECT * FROM antecedents WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapAntecedent(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Antecedent with id " + id, e);
        }
    }

    @Override
    public List<Antecedent> findAll() {
        final String sql = "SELECT * FROM antecedents";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Antecedent> antecedents = new ArrayList<>();
            while (rs.next()) {
                antecedents.add(RowMappers.mapAntecedent(rs));
            }
            return antecedents;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Antecedents", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM antecedents WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Antecedent with id " + id, e);
        }
    }
}

package ma.oralCare.repository.patient.impl;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.patient.AntecedentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    private static final String TABLE_NAME = "antecedents";

    private final Connection connection;

    public AntecedentRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Antecedent save(Antecedent antecedent) {
        String sql = "INSERT INTO " + TABLE_NAME + " (nom, categorie, niveauRisque) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie() != null ? antecedent.getCategorie().name() : null);
            ps.setString(3, antecedent.getNiveauRisque() != null ? antecedent.getNiveauRisque().name() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating antecedent failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    antecedent.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating antecedent failed, no ID obtained.");
                }
            }

            return antecedent;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving Antecedent", e);
        }
    }

    @Override
    public Antecedent update(Antecedent antecedent) {
        String sql = "UPDATE " + TABLE_NAME + " SET nom = ?, categorie = ?, niveauRisque = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie() != null ? antecedent.getCategorie().name() : null);
            ps.setString(3, antecedent.getNiveauRisque() != null ? antecedent.getNiveauRisque().name() : null);
            ps.setLong(4, antecedent.getId());

            ps.executeUpdate();
            return antecedent;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Antecedent", e);
        }
    }

    @Override
    public Antecedent findById(Long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapAntecedent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding Antecedent by id", e);
        }

        return null;
    }

    @Override
    public List<Antecedent> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        List<Antecedent> result = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all Antecedents", e);
        }

        return result;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Antecedent by id", e);
        }
    }
}


