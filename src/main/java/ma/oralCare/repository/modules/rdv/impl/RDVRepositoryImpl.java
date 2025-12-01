package ma.oralCare.repository.modules.rdv.impl;

import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.repository.common.CrudRepository;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class RDVRepositoryImpl implements CrudRepository<RDV, Long> {

    private final Connection connection;

    public RDVRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public RDV save(RDV rdv) {
        String sql = "INSERT INTO rdv (date, heure, motif, statut, note_medecin) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, Date.valueOf(rdv.getDate()));
            ps.setTime(2, Time.valueOf(rdv.getHeure()));
            ps.setString(3, rdv.getMotif());
            ps.setString(4, rdv.getStatut() != null ? rdv.getStatut().name() : null);
            ps.setString(5, rdv.getNoteMedecin());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) throw new SQLException("Créer RDV a échoué, aucune ligne affectée.");

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) rdv.setId(generatedKeys.getLong(1));
                else throw new SQLException("Créer RDV a échoué, aucun ID généré.");
            }

            return rdv;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM rdv WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return RowMappers.mapRDV(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv";
        List<RDV> rdvs = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) rdvs.add(RowMappers.mapRDV(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rdvs;
    }

    @Override
    public void update(RDV rdv) {
        String sql = "UPDATE rdv SET date=?, heure=?, motif=?, statut=?, note_medecin=? WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(rdv.getDate()));
            ps.setTime(2, Time.valueOf(rdv.getHeure()));
            ps.setString(3, rdv.getMotif());
            ps.setString(4, rdv.getStatut() != null ? rdv.getStatut().name() : null);
            ps.setString(5, rdv.getNoteMedecin());
            ps.setLong(6, rdv.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(RDV rdv) {
        deleteById(rdv.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM rdv WHERE id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM rdv";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM rdv";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
