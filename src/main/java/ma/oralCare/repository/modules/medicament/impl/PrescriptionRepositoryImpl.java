package ma.oralCare.repository.medicament.impl;

import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.medicament.api.PrescriptionRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private final Connection connection;

    public PrescriptionRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Prescription save(Prescription entity) {
        final String sql = """
                INSERT INTO prescriptions
                    (quantite, frequence, dureeEnJours)
                VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entity.getQuantite());
            ps.setString(2, entity.getFrequence());
            ps.setInt(3, entity.getDureeEnJours());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Prescription", e);
        }
    }

    @Override
    public Prescription update(Prescription entity) {
        final String sql = """
                UPDATE prescriptions
                SET quantite = ?, frequence = ?, dureeEnJours = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entity.getQuantite());
            ps.setString(2, entity.getFrequence());
            ps.setInt(3, entity.getDureeEnJours());
            ps.setLong(4, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Prescription with id " + entity.getId(), e);
        }
    }

    @Override
    public Prescription findById(Long id) {
        final String sql = "SELECT * FROM prescriptions WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapPrescription(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Prescription with id " + id, e);
        }
    }

    @Override
    public List<Prescription> findAll() {
        final String sql = "SELECT * FROM prescriptions";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Prescription> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapPrescription(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Prescriptions", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM prescriptions WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Prescription with id " + id, e);
        }
    }
}


