package ma.oralCare.repository.medicament.impl;

import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.medicament.api.MedicamentRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    private final Connection connection;

    public MedicamentRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Medicament save(Medicament entity) {
        final String sql = """
                INSERT INTO medicaments
                    (nom, laboratoire, type, forme, remboursable, prixUnitaire, description)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getLaboratoire());
            ps.setString(3, entity.getType());
            if (entity.getForme() != null) {
                ps.setString(4, entity.getForme().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            if (entity.getRemboursable() != null) {
                ps.setBoolean(5, entity.getRemboursable());
            } else {
                ps.setNull(5, java.sql.Types.BOOLEAN);
            }

            if (entity.getPrixUnitaire() != null) {
                ps.setDouble(6, entity.getPrixUnitaire());
            } else {
                ps.setNull(6, java.sql.Types.DOUBLE);
            }

            ps.setString(7, entity.getDescription());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Medicament", e);
        }
    }

    @Override
    public Medicament update(Medicament entity) {
        final String sql = """
                UPDATE medicaments
                SET nom = ?, laboratoire = ?, type = ?, forme = ?, remboursable = ?,
                    prixUnitaire = ?, description = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getLaboratoire());
            ps.setString(3, entity.getType());
            if (entity.getForme() != null) {
                ps.setString(4, entity.getForme().name());
            } else {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }

            if (entity.getRemboursable() != null) {
                ps.setBoolean(5, entity.getRemboursable());
            } else {
                ps.setNull(5, java.sql.Types.BOOLEAN);
            }

            if (entity.getPrixUnitaire() != null) {
                ps.setDouble(6, entity.getPrixUnitaire());
            } else {
                ps.setNull(6, java.sql.Types.DOUBLE);
            }

            ps.setString(7, entity.getDescription());
            ps.setLong(8, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Medicament with id " + entity.getId(), e);
        }
    }

    @Override
    public Medicament findById(Long id) {
        final String sql = "SELECT * FROM medicaments WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapMedicament(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Medicament with id " + id, e);
        }
    }

    @Override
    public List<Medicament> findAll() {
        final String sql = "SELECT * FROM medicaments";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Medicament> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapMedicament(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Medicaments", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM medicaments WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Medicament with id " + id, e);
        }
    }
}


