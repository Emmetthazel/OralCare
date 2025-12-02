package ma.oralCare.repository.facture.impl;

import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.facture.api.SituationFinanciereRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    private final Connection connection;

    public SituationFinanciereRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SituationFinanciere save(SituationFinanciere entity) {
        final String sql = """
                INSERT INTO situations_financieres
                    (totaleDesActes, totalePaye, credit, statut, enPromo)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getTotaleDesActes() != null) {
                ps.setDouble(1, entity.getTotaleDesActes());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }
            if (entity.getTotalePaye() != null) {
                ps.setDouble(2, entity.getTotalePaye());
            } else {
                ps.setNull(2, java.sql.Types.DOUBLE);
            }
            if (entity.getCredit() != null) {
                ps.setDouble(3, entity.getCredit());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            if (entity.getStatut() != null) {
                ps.setString(4, entity.getStatut().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            if (entity.getEnPromo() != null) {
                ps.setString(5, entity.getEnPromo().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving SituationFinanciere", e);
        }
    }

    @Override
    public SituationFinanciere update(SituationFinanciere entity) {
        final String sql = """
                UPDATE situations_financieres
                SET totaleDesActes = ?, totalePaye = ?, credit = ?, statut = ?, enPromo = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getTotaleDesActes() != null) {
                ps.setDouble(1, entity.getTotaleDesActes());
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }
            if (entity.getTotalePaye() != null) {
                ps.setDouble(2, entity.getTotalePaye());
            } else {
                ps.setNull(2, java.sql.Types.DOUBLE);
            }
            if (entity.getCredit() != null) {
                ps.setDouble(3, entity.getCredit());
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            if (entity.getStatut() != null) {
                ps.setString(4, entity.getStatut().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            if (entity.getEnPromo() != null) {
                ps.setString(5, entity.getEnPromo().name());
            } else {
                ps.setNull(5, java.sql.Types.VARCHAR);
            }

            ps.setLong(6, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating SituationFinanciere with id " + entity.getId(), e);
        }
    }

    @Override
    public SituationFinanciere findById(Long id) {
        final String sql = "SELECT * FROM situations_financieres WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapSituationFinanciere(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding SituationFinanciere with id " + id, e);
        }
    }

    @Override
    public List<SituationFinanciere> findAll() {
        final String sql = "SELECT * FROM situations_financieres";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<SituationFinanciere> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapSituationFinanciere(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all SituationsFinancieres", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM situations_financieres WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting SituationFinanciere with id " + id, e);
        }
    }
}


