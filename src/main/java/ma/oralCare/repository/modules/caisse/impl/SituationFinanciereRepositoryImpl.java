package ma.oralCare.repository.modules.caisse.impl;

import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.entities.enums.StatutSituationFinanciere;
import ma.oralCare.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister pour gérer les connexions
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SituationFinanciereRepositoryImpl implements SituationFinanciereRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = "SELECT * FROM SituationFinanciere ORDER BY id DESC";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les situations financières", e);
        }
        return out;
    }

    @Override
    public SituationFinanciere findById(Long id) {
        String sql = "SELECT * FROM SituationFinanciere WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapSituationFinanciere(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la situation financière par ID", e);
        }
    }

    @Override
    public void create(SituationFinanciere newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO SituationFinanciere (totaleDesActes, totalePaye, credit, statut, enPromo, dossierMedicale_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, newElement.getTotaleDesActes());
            ps.setDouble(2, newElement.getTotalePaye());
            ps.setDouble(3, newElement.getCredit());
            ps.setString(4, newElement.getStatut().name());
            ps.setString(5, newElement.getEnPromo().name());
            ps.setLong(6, newElement.getDossierMedicale() != null ? newElement.getDossierMedicale().getId() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la situation financière", e);
        }
    }

    @Override
    public void update(SituationFinanciere newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE SituationFinanciere SET totaleDesActes = ?, totalePaye = ?, credit = ?, statut = ?, enPromo = ?, dossierMedicale_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, newValuesElement.getTotaleDesActes());
            ps.setDouble(2, newValuesElement.getTotalePaye());
            ps.setDouble(3, newValuesElement.getCredit());
            ps.setString(4, newValuesElement.getStatut().name());
            ps.setString(5, newValuesElement.getEnPromo().name());
            ps.setLong(6, newValuesElement.getDossierMedicale() != null ? newValuesElement.getDossierMedicale().getId() : null);
            ps.setLong(7, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la situation financière", e);
        }
    }

    @Override
    public void delete(SituationFinanciere sf) {
        if (sf != null && sf.getId() != null) deleteById(sf.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM SituationFinanciere WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la situation financière par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<SituationFinanciere> findByPatientId(Long patientId) {
        // La jointure DossierMedicale pour trouver la SF associée au Patient
        String sql = "SELECT sf.* FROM SituationFinanciere sf JOIN DossierMedicale dm ON sf.dossierMedicale_id = dm.id WHERE dm.patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSituationFinanciere(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la situation financière par Patient ID", e);
        }
    }

    @Override
    public List<SituationFinanciere> findActiveSituations() {
        // Une situation est active si elle n'est pas 'CLOSED' ou si le crédit est > 0.
        String sql = "SELECT * FROM SituationFinanciere WHERE statut = ? OR credit > 0.0 ORDER BY credit DESC";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, StatutSituationFinanciere.ACTIVE.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des situations financières actives", e);
        }
        return out;
    }

    @Override
    public List<SituationFinanciere> findAllByPatientId(Long patientId) {
        // Requête similaire à findByPatientId, mais qui retourne une liste (au cas où il y aurait plusieurs SF historiques)
        String sql = "SELECT sf.* FROM SituationFinanciere sf JOIN DossierMedicale dm ON sf.dossierMedicale_id = dm.id WHERE dm.patient_id = ? ORDER BY sf.id DESC";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSituationFinanciere(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de toutes les situations financières du patient", e);
        }
        return out;
    }

    // --- 3. Implémentation des Opérations Métier et de Relation ---

    @Override
    public void reinitialiserSF(Long id) {
        String sql = "UPDATE SituationFinanciere SET credit = 0.0, statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, StatutSituationFinanciere.CLOSED.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la réinitialisation (clôture) de la Situation Financière", e);
        }
    }

    @Override
    public void updateTotaux(Long sfId, Double montantTotalActes, Double montantTotalPaye, Double nouveauCredit) {
        // Détermination du statut basé sur le nouveau crédit
        StatutSituationFinanciere nouveauStatut = (nouveauCredit <= 0.0) ? StatutSituationFinanciere.CLOSED : StatutSituationFinanciere.ACTIVE;

        String sql = "UPDATE SituationFinanciere SET totaleDesActes = ?, totalePaye = ?, credit = ?, statut = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, montantTotalActes);
            ps.setDouble(2, montantTotalPaye);
            ps.setDouble(3, nouveauCredit);
            ps.setString(4, nouveauStatut.name());
            ps.setLong(5, sfId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour des totaux de la Situation Financière", e);
        }
    }

    @Override
    public List<Facture> findFacturesBySituationFinanciereId(Long sfId) {
        String sql = "SELECT * FROM Facture WHERE situationFinanciere_id = ? ORDER BY dateFacture DESC";
        List<Facture> out = new ArrayList<>();
        // ATTENTION: Nécessite que RowMappers.mapFacture(rs) existe et soit précis
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, sfId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des factures associées à la SF ID: " + sfId, e);
        }
        return out;
    }
}