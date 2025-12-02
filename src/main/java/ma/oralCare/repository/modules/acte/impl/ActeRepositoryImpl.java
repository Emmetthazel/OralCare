package ma.oralCare.repository.acte.impl;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.repository.acte.api.ActeRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ActeRepositoryImpl implements ActeRepository {

    private final Connection connection;

    public ActeRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Acte save(Acte entity) {
        final String sql = """
                INSERT INTO actes
                    (libelle, categorie, prixDeBase)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getLibelle());
            ps.setString(2, entity.getCategorie());
            if (entity.getPrixDeBase() != null) {
                ps.setDouble(3, entity.getPrixDeBase());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Acte", e);
        }
    }

    @Override
    public Acte update(Acte entity) {
        final String sql = """
                UPDATE actes
                SET libelle = ?, categorie = ?, prixDeBase = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getLibelle());
            ps.setString(2, entity.getCategorie());
            if (entity.getPrixDeBase() != null) {
                ps.setDouble(3, entity.getPrixDeBase());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }
            ps.setLong(4, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Acte with id " + entity.getId(), e);
        }
    }

    @Override
    public Acte findById(Long id) {
        final String sql = "SELECT * FROM actes WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapActe(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Acte with id " + id, e);
        }
    }

    @Override
    public List<Acte> findAll() {
        final String sql = "SELECT * FROM actes";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Acte> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapActe(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Actes", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM actes WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Acte with id " + id, e);
        }
    }
}


