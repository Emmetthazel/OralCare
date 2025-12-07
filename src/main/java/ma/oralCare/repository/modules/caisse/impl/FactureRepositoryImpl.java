package ma.oralCare.repository.modules.caisse.impl;

import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryImpl implements FactureRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT * FROM Facture ORDER BY dateFacture DESC";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapFacture(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de toutes les factures", e); }
        return out;
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM Facture WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapFacture(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de la facture par ID", e); }
    }

    @Override
    public void create(Facture newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Facture (totaleFacture, totalePaye, reste, statut, dateFacture, situationFinanciere_id, consultation_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, newElement.getTotaleFacture());
            ps.setDouble(2, newElement.getTotalePaye());
            ps.setDouble(3, newElement.getReste());
            ps.setString(4, newElement.getStatut().name());
            ps.setTimestamp(5, Timestamp.valueOf(newElement.getDateFacture()));
            ps.setLong(6, newElement.getSituationFinanciere() != null ? newElement.getSituationFinanciere().getId() : null);
            ps.setLong(7, newElement.getConsultation() != null ? newElement.getConsultation().getId() : null);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de la facture", e); }
    }

    @Override
    public void update(Facture newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Facture SET totaleFacture = ?, totalePaye = ?, reste = ?, statut = ?, dateFacture = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, newValuesElement.getTotaleFacture());
            ps.setDouble(2, newValuesElement.getTotalePaye());
            ps.setDouble(3, newValuesElement.getReste());
            ps.setString(4, newValuesElement.getStatut().name());
            ps.setTimestamp(5, Timestamp.valueOf(newValuesElement.getDateFacture()));
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de la facture", e); }
    }

    @Override
    public void delete(Facture facture) {
        if (facture != null && facture.getId() != null) deleteById(facture.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Facture WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de la facture par ID", e); }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Facture> findBySituationFinanciereId(Long situationFinanciereId) {
        String sql = "SELECT * FROM Facture WHERE situationFinanciere_id = ?";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, situationFinanciereId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par Situation Financière ID", e); }
        return out;
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM Facture WHERE consultation_id = ?";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par Consultation ID", e); }
        return out;
    }

    @Override
    public List<Facture> findByStatut(StatutFacture statut) {
        String sql = "SELECT * FROM Facture WHERE statut = ?";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par statut", e); }
        return out;
    }

    @Override
    public List<Facture> findByDateFactureBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM Facture WHERE dateFacture BETWEEN ? AND ?";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par période", e); }
        return out;
    }

    // --- 3. Implémentation des Méthodes de Gestion Transactionnelle ---

    @Override
    public Facture enregistrerPaiement(Long factureId, Double montantPaye) {
        Facture currentFacture = findById(factureId);
        if (currentFacture == null) {
            throw new IllegalArgumentException("Facture non trouvée: " + factureId);
        }

        double newTotalePaye = currentFacture.getTotalePaye() + montantPaye;
        double newReste = currentFacture.getTotaleFacture() - newTotalePaye;
        StatutFacture newStatut = (newReste <= 0.0) ? StatutFacture.PAID : StatutFacture.PENDING;

        String sql = "UPDATE Facture SET totalePaye = ?, reste = ?, statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, newTotalePaye);
            ps.setDouble(2, newReste);
            ps.setString(3, newStatut.name());
            ps.setLong(4, factureId);

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de l'enregistrement du paiement", e); }

        // Re-lecture pour retourner l'objet mis à jour
        return findById(factureId);
    }

    @Override
    public void annulerFacture(Long factureId) {
        // L'annulation met les montants payés/restants à zéro et le statut à OVERDUE (ou ANNULE si ce statut existait).
        String sql = "UPDATE Facture SET statut = ?, reste = ?, totalePaye = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, StatutFacture.OVERDUE.name());
            ps.setDouble(2, 0.0);
            ps.setDouble(3, 0.0);
            ps.setLong(4, factureId);

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de l'annulation de la facture", e); }
    }

    @Override
    public void updateTotaux(Long factureId, Double nouveauTotalFacture, Double nouveauReste) {
        StatutFacture nouveauStatut = (nouveauReste <= 0.0) ? StatutFacture.PAID : StatutFacture.PENDING;

        String sql = "UPDATE Facture SET totaleFacture = ?, reste = ?, statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, nouveauTotalFacture);
            ps.setDouble(2, nouveauReste);
            ps.setString(3, nouveauStatut.name());
            ps.setLong(4, factureId);

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour des totaux de la facture", e); }
    }
}