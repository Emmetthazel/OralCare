package ma.oralCare.repository.consultation.impl;

import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.consultation.api.CertificatRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepository {

    private final Connection connection;

    public CertificatRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Certificat save(Certificat entity) {
        final String sql = """
                INSERT INTO certificats
                    (dateDebut, dateFin, duree, noteMedecin)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            LocalDate dateDebut = entity.getDateDebut();
            LocalDate dateFin = entity.getDateFin();

            if (dateDebut != null) {
                ps.setDate(1, java.sql.Date.valueOf(dateDebut));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }

            if (dateFin != null) {
                ps.setDate(2, java.sql.Date.valueOf(dateFin));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setInt(3, entity.getDuree());
            ps.setString(4, entity.getNoteMedecin());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving Certificat", e);
        }
    }

    @Override
    public Certificat update(Certificat entity) {
        final String sql = """
                UPDATE certificats
                SET dateDebut = ?, dateFin = ?, duree = ?, noteMedecin = ?
                WHERE id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            LocalDate dateDebut = entity.getDateDebut();
            LocalDate dateFin = entity.getDateFin();

            if (dateDebut != null) {
                ps.setDate(1, java.sql.Date.valueOf(dateDebut));
            } else {
                ps.setNull(1, java.sql.Types.DATE);
            }

            if (dateFin != null) {
                ps.setDate(2, java.sql.Date.valueOf(dateFin));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }

            ps.setInt(3, entity.getDuree());
            ps.setString(4, entity.getNoteMedecin());
            ps.setLong(5, entity.getId());

            ps.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating Certificat with id " + entity.getId(), e);
        }
    }

    @Override
    public Certificat findById(Long id) {
        final String sql = "SELECT * FROM certificats WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapCertificat(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding Certificat with id " + id, e);
        }
    }

    @Override
    public List<Certificat> findAll() {
        final String sql = "SELECT * FROM certificats";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Certificat> result = new ArrayList<>();
            while (rs.next()) {
                result.add(RowMappers.mapCertificat(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Error while finding all Certificats", e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        final String sql = "DELETE FROM certificats WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting Certificat with id " + id, e);
        }
    }
}


