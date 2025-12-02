package ma.oralCare.repository.dossier.impl;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.dossier.api.DossierMedicaleRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DossierMedicaleRepositoryImpl implements DossierMedicaleRepository {

    private final Connection connection;

    public DossierMedicaleRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public DossierMedicale save(DossierMedicale entity) {
        final String sql = """
                INSERT INTO dossiers_medicales
                    (dateDeCreation)
                VALUES (?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (entity.getDateDeCreation() != null) {
                ps.setDate(1, java.sql.Date.valueOf(entity.getDateDeCreation()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving DossierMedicale", e);
        }
    }

    @Override
    public DossierMedicale update(DossierMedicale entity) {
        final String sql = """
                UPDATE dossiers_medicales
                SET dateDeCreation = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            if (entity.getDateDeCreation() != null) {
                ps.setDate(1, java.sql.Date.valueOf(entity.getDateDeCreation()));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }
            ps.setLong(2, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating DossierMedicale with id " + entity.getId(), e);
        }
    }

    @Override
    public DossierMedicale findById(Long id) {
        final String sql = "SELECT * FROM dossiers_medicales WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapDossierMedicale(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding DossierMedicale with id " + id, e);
        }
    }

    @Override
    public List<DossierMedicale> findAll() {
        final String sql = "SELECT * FROM dossiers_medicales";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<DossierMedicale> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapDossierMedicale(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all DossierMedicale", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM dossiers_medicales WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting DossierMedicale with id " + id, e);
        }
    }
}


