package ma.oralCare.repository.cabinet.impl;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.repository.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {

    private final Connection connection;

    public CabinetMedicaleRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CabinetMedicale save(CabinetMedicale entity) {
        final String sql = """
                INSERT INTO cabinets_medicales
                    (nom, email, logo, cin, tel1, tel2, siteWeb, instagram, facebook, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getLogo());
            ps.setString(4, entity.getCin());
            ps.setString(5, entity.getTel1());
            ps.setString(6, entity.getTel2());
            ps.setString(7, entity.getSiteWeb());
            ps.setString(8, entity.getInstagram());
            ps.setString(9, entity.getFacebook());
            ps.setString(10, entity.getDescription());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving CabinetMedicale", e);
        }
    }

    @Override
    public CabinetMedicale update(CabinetMedicale entity) {
        final String sql = """
                UPDATE cabinets_medicales
                SET nom = ?, email = ?, logo = ?, cin = ?, tel1 = ?, tel2 = ?,
                    siteWeb = ?, instagram = ?, facebook = ?, description = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, entity.getNom());
            ps.setString(2, entity.getEmail());
            ps.setString(3, entity.getLogo());
            ps.setString(4, entity.getCin());
            ps.setString(5, entity.getTel1());
            ps.setString(6, entity.getTel2());
            ps.setString(7, entity.getSiteWeb());
            ps.setString(8, entity.getInstagram());
            ps.setString(9, entity.getFacebook());
            ps.setString(10, entity.getDescription());
            ps.setLong(11, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating CabinetMedicale with id " + entity.getId(), e);
        }
    }

    @Override
    public CabinetMedicale findById(Long id) {
        final String sql = "SELECT * FROM cabinets_medicales WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapCabinetMedicale(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding CabinetMedicale with id " + id, e);
        }
    }

    @Override
    public List<CabinetMedicale> findAll() {
        final String sql = "SELECT * FROM cabinets_medicales";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<CabinetMedicale> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapCabinetMedicale(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all CabinetsMedicales", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM cabinets_medicales WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting CabinetMedicale with id " + id, e);
        }
    }
}


