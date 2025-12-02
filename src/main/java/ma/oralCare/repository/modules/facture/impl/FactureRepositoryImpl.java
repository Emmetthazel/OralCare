package ma.oralCare.repository.facture.impl;

import ma.oralCare.entities.facture.Facture;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.facture.api.FactureRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryImpl implements FactureRepository {

    private final Connection connection;

    public FactureRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Facture save(Facture entity) {
        final String sql = """
                INSERT INTO factures
                    (totaleFacture, totalePaye, reste, statut, dateFacture)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getTotaleFacture() != null) {
                ps.setDouble(1, entity.getTotaleFacture());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }
            if (entity.getTotalePaye() != null) {
                ps.setDouble(2, entity.getTotalePaye());
            } else {
                ps.setNull(2, java.sql.Types.DOUBLE);
            }
            if (entity.getReste() != null) {
                ps.setDouble(3, entity.getReste());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            if (entity.getStatut() != null) {
                ps.setString(4, entity.getStatut().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            LocalDateTime dateFacture = entity.getDateFacture();
            if (dateFacture != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(dateFacture));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Facture", e);
        }
    }

    @Override
    public Facture update(Facture entity) {
        final String sql = """
                UPDATE factures
                SET totaleFacture = ?, totalePaye = ?, reste = ?, statut = ?, dateFacture = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getTotaleFacture() != null) {
                ps.setDouble(1, entity.getTotaleFacture());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }
            if (entity.getTotalePaye() != null) {
                ps.setDouble(2, entity.getTotalePaye());
            } else {
                ps.setNull(2, java.sql.Types.DOUBLE);
            }
            if (entity.getReste() != null) {
                ps.setDouble(3, entity.getReste());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            if (entity.getStatut() != null) {
                ps.setString(4, entity.getStatut().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            LocalDateTime dateFacture = entity.getDateFacture();
            if (dateFacture != null) {
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(dateFacture));
            } else {
                ps.setNull(5, java.sql.Types.TIMESTAMP);
            }

            ps.setLong(6, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Facture with id " + entity.getId(), e);
        }
    }

    @Override
    public Facture findById(Long id) {
        final String sql = "SELECT * FROM factures WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapFacture(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Facture with id " + id, e);
        }
    }

    @Override
    public List<Facture> findAll() {
        final String sql = "SELECT * FROM factures";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Facture> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapFacture(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Factures", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM factures WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Facture with id " + id, e);
        }
    }
}


