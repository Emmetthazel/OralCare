package ma.oralCare.repository.modules.actes.impl;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.repository.modules.actes.api.InterventionMedecinRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.common.RowMappers; // Nécessaire pour mapper les résultats

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE: Cette classe suppose que RowMappers.mapInterventionMedecin(rs) et
// RowMappers.mapActe(rs) existent et mappent correctement les champs SQL aux entités.
public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM interventionmedecin ORDER BY dateIntervention DESC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de toutes les interventions", e); }
        return out;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM interventionmedecin WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapInterventionMedecin(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'intervention par ID", e); }
    }

    @Override
    public void create(InterventionMedecin newElement) {
        if (newElement == null || newElement.getConsultation() == null || newElement.getActe() == null) {
            throw new IllegalArgumentException("L'intervention, la consultation ou l'acte ne peut être nul.");
        }

        // CORRECTION: Extraction des IDs via les objets de relation
        Long consultationId = newElement.getConsultation().getId();
        Long acteId = newElement.getActe().getId();

        String sql = "INSERT INTO interventionmedecin (consultation_id, acte_id, numDent, prixDePatient) VALUES (?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, consultationId);
            ps.setLong(2, acteId);
            ps.setInt(3, newElement.getNumDent());
            ps.setDouble(4, newElement.getPrixDePatient());
            // ps.setTimestamp(5, Timestamp.valueOf(newElement.getDateIntervention())); // Supprimé si non requis

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de l'intervention", e); }
    }

    @Override
    public void update(InterventionMedecin newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null ||
                newValuesElement.getConsultation() == null || newValuesElement.getActe() == null) {
            throw new IllegalArgumentException("Les données de l'intervention ne sont pas complètes pour la mise à jour.");
        }

        // CORRECTION: Extraction des IDs via les objets de relation
        Long consultationId = newValuesElement.getConsultation().getId();
        Long acteId = newValuesElement.getActe().getId();

        // Note: dateIntervention est souvent gérée par la base de données ou omise ici.
        String sql = "UPDATE interventionmedecin SET consultation_id = ?, acte_id = ?, numDent = ?, prixDePatient = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            ps.setLong(2, acteId);
            ps.setInt(3, newValuesElement.getNumDent());
            ps.setDouble(4, newValuesElement.getPrixDePatient());
            // ps.setTimestamp(5, Timestamp.valueOf(newValuesElement.getDateIntervention())); // Supprimé si non requis
            ps.setLong(5, newValuesElement.getId()); // ID pour le WHERE

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de l'intervention", e); }
    }

    @Override
    public void delete(InterventionMedecin element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM interventionmedecin WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de l'intervention par ID", e); }
    }

    // --- 2. Méthodes de Recherche Spécifiques ---

    @Override
    public List<InterventionMedecin> findByActeId(Long acteId) {
        String sql = "SELECT * FROM interventionmedecin WHERE acte_id = ? ORDER BY numDent";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des interventions par Acte ID", e); }
        return out;
    }

    @Override
    public List<InterventionMedecin> findPage(int limit, int offset) {
        String sql = "SELECT * FROM interventionmedecin ORDER BY id DESC LIMIT ? OFFSET ?";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche par page", e); }
        return out;
    }

    @Override
    public List<InterventionMedecin> consulterParConsultation(Long idConsultation) {
        String sql = "SELECT * FROM interventionmedecin WHERE consultation_id = ? ORDER BY numDent";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, idConsultation);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des interventions par Consultation ID", e); }
        return out;
    }

    @Override
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        String sql = "SELECT * FROM interventionmedecin WHERE numDent = ? ORDER BY id DESC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, numDent);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des interventions par numéro de dent", e); }
        return out;
    }

    // --- 3. Logique Financière et Navigation ---

    @Override
    public Double calculateTotalPatientPriceByConsultationId(Long consultationId) {
        String sql = "SELECT SUM(prixDePatient) FROM interventionmedecin WHERE consultation_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du prix total pour la consultation " + consultationId, e);
        }
        return 0.0;
    }

    @Override
    public InterventionMedecin appliquerRemisePonctuelle(Long interventionId, Double pourcentageRemise) {
        InterventionMedecin intervention = findById(interventionId);
        if (intervention == null) return null;

        double prixDeBase = intervention.getPrixDePatient();
        double nouveauPrix = prixDeBase * (1.0 - pourcentageRemise / 100.0);

        String sql = "UPDATE interventionmedecin SET prixDePatient = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, nouveauPrix);
            ps.setLong(2, interventionId);
            ps.executeUpdate();

            intervention.setPrixDePatient(nouveauPrix);
            return intervention;
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de l'application de la remise ponctuelle", e); }
    }

    @Override
    public Optional<Acte> findActeByInterventionId(Long interventionId) {
        // Jointure pour trouver l'acte lié à une intervention
        String sql = "SELECT a.* FROM acte a JOIN interventionmedecin im ON a.id = im.acte_id WHERE im.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, interventionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapActe(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'acte par intervention ID", e); }
    }

    @Override
    public boolean existsByConsultationActeAndDent(Long consultationId, Long acteId, Integer numDent) {
        String sql = "SELECT 1 FROM interventionmedecin WHERE consultation_id = ? AND acte_id = ? AND numDent = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, consultationId);
            ps.setLong(2, acteId);
            ps.setInt(3, numDent);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la vérification de l'existence de l'intervention", e); }
    }
}