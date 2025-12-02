package ma.oralCare.repository.rdv.impl;

import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.rdv.api.RDVRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class RDVRepositoryImpl implements RDVRepository {

    private final Connection connection;

    public RDVRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public RDV save(RDV entity) {
        final String sql = """
                INSERT INTO rdvs
                    (date, heure, motif, statut, noteMedecin)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setCommonParameters(ps, entity);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving RDV", e);
        }
    }

    @Override
    public RDV update(RDV entity) {
        final String sql = """
                UPDATE rdvs
                SET date = ?, heure = ?, motif = ?, statut = ?, noteMedecin = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setCommonParameters(ps, entity);
            ps.setLong(6, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating RDV with id " + entity.getId(), e);
        }
    }

    @Override
    public RDV findById(Long id) {
        final String sql = "SELECT * FROM rdvs WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRDV(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding RDV with id " + id, e);
        }
    }

    @Override
    public List<RDV> findAll() {
        final String sql = "SELECT * FROM rdvs";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<RDV> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapRDV(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all RDVs", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM rdvs WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting RDV with id " + id, e);
        }
    }

    private void setCommonParameters(PreparedStatement ps, RDV entity) throws SQLException {
        LocalDate date = entity.getDate();
        LocalTime heure = entity.getHeure();

        if (date != null) {
            ps.setDate(1, java.sql.Date.valueOf(date));
        } else {
            ps.setNull(1, java.sql.Types.DATE);
        }

        if (heure != null) {
            ps.setTime(2, java.sql.Time.valueOf(heure));
        } else {
            ps.setNull(2, java.sql.Types.TIME);
        }

        ps.setString(3, entity.getMotif());

        if (entity.getStatut() != null) {
            ps.setString(4, entity.getStatut().name());
        } else {
            ps.setNull(4, java.sql.Types.VARCHAR);
        }

        ps.setString(5, entity.getNoteMedecin());
    }
}


