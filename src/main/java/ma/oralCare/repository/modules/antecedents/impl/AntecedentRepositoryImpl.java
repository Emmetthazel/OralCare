package ma.oralCare.repository.modules.antecedents.impl;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.repository.modules.antecedents.api.AntecedentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AntecedentRepositoryImpl implements AntecedentRepository {

    private final Connection connection;

    public AntecedentRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    private Antecedent mapAntecedent(ResultSet rs) throws SQLException {
        Antecedent antecedent = new Antecedent();
        antecedent.setId(rs.getLong("id"));
        antecedent.setNom(rs.getString("nom"));

        String catValue = rs.getString("categorie");
        if (catValue != null) {
            antecedent.setCategorie(CategorieAntecedent.valueOf(catValue));
        }

        String risqueValue = rs.getString("niveauRisque");
        if (risqueValue != null) {
            antecedent.setNiveauRisque(NiveauDeRisque.valueOf(risqueValue));
        }

        antecedent.setPatients(null); // chargement plus tard si nécessaire

        return antecedent;
    }

    @Override
    public Antecedent findById(Long id) {
        String sql = "SELECT * FROM antecedents WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapAntecedent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Antecedent> findAll() {
        List<Antecedent> list = new ArrayList<>();
        String sql = "SELECT * FROM antecedents";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapAntecedent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Antecedent> findByPatientId(Long patientId) {
        List<Antecedent> list = new ArrayList<>();
        String sql = "SELECT a.* FROM antecedents a " +
                "JOIN patient_antecedent pa ON a.id = pa.antecedent_id " +
                "WHERE pa.patient_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapAntecedent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Antecedent save(Antecedent antecedent) {
        String sql = "INSERT INTO antecedents (nom, categorie, niveauRisque) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie().name());
            ps.setString(3, antecedent.getNiveauRisque().name());
            int affected = ps.executeUpdate();
            if (affected == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    antecedent.setId(rs.getLong(1));
                    return antecedent;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Antecedent update(Antecedent antecedent) {
        String sql = "UPDATE antecedents SET nom = ?, categorie = ?, niveauRisque = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, antecedent.getNom());
            ps.setString(2, antecedent.getCategorie().name());
            ps.setString(3, antecedent.getNiveauRisque().name());
            ps.setLong(4, antecedent.getId());
            int affected = ps.executeUpdate();
            return affected == 1 ? antecedent : null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM antecedents WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
