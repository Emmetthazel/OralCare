package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapConsultation
import ma.oralCare.repository.common.RowMappers; // Supposé contenir mapInterventionMedecin

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    // --- 1. Opérations CRUD de base ---

    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM Consultation ORDER BY date DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapConsultation(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les consultations", e);
        }
        return out;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM Consultation WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapConsultation(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la consultation par ID", e);
        }
    }

    @Override
    public void create(Consultation newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Consultation (date, statut, observationMedecin, dossierMedicale_id) VALUES (?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(newElement.getDate()));
            ps.setString(2, newElement.getStatut().name());
            ps.setString(3, newElement.getObservationMedecin());
            ps.setObject(4, newElement.getDossierMedicale() != null ? newElement.getDossierMedicale().getId() : null, JDBCType.BIGINT);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la consultation", e);
        }
    }

    @Override
    public void update(Consultation newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        // UC: Modifier une Cs
        String sql = "UPDATE Consultation SET date = ?, statut = ?, observationMedecin = ?, dossierMedicale_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(newValuesElement.getDate()));
            ps.setString(2, newValuesElement.getStatut().name());
            ps.setString(3, newValuesElement.getObservationMedecin());
            ps.setObject(4, newValuesElement.getDossierMedicale() != null ? newValuesElement.getDossierMedicale().getId() : null, JDBCType.BIGINT);
            ps.setLong(5, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la consultation", e);
        }
    }

    @Override
    public void delete(Consultation consultation) {
        if (consultation != null && consultation.getId() != null) deleteById(consultation.getId());
    }

    @Override
    public void deleteById(Long id) {
        // UC: Supprimer une Cs
        String sql = "DELETE FROM Consultation WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Consultation> findByDossierMedicaleId(Long dossierMedicaleId) {
        // UC: Consulter Dossier Médical
        String sql = "SELECT * FROM Consultation WHERE dossierMedicale_id = ? ORDER BY date DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par Dossier Médical ID", e);
        }
        return out;
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        // UC: Filtrage pour Consulter Cs
        String sql = "SELECT * FROM Consultation WHERE statut = ? ORDER BY date DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par statut", e);
        }
        return out;
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        // UC: Filtrage pour Consulter Cs
        String sql = "SELECT * FROM Consultation WHERE date = ? ORDER BY statut DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des consultations par date", e);
        }
        return out;
    }

    // --- 3. Opérations Métier (Terminer/Annuler/Ajouter observation) ---

    @Override
    public void updateStatut(Long id, StatutConsultation nouveauStatut) {
        // UC: Terminer une Cs (-> COMPLETED) ou Annuler une Cs (-> CANCELLED)
        String sql = "UPDATE Consultation SET statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la consultation", e);
        }
    }

    @Override
    public void updateObservation(Long id, String observation) {
        // UC: ajouter observation
        String sql = "UPDATE Consultation SET observationMedecin = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, observation);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour des observations de la consultation", e);
        }
    }

    // --- 4. Gestion des Interventions/Actes ---

    @Override
    public void addIntervention(Long consultationId, InterventionMedecin intervention) {
        if (intervention == null || intervention.getActe() == null) return;

        // Cette méthode suppose que l'Acte et la Consultation existent.
        String sql = "INSERT INTO InterventionMedecin (prixDePatient, numDent, consultation_id, acte_id) VALUES (?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, intervention.getPrixDePatient());
            // Si numDent est null, on utilise 0 ou on vérifie si la colonne l'accepte. Ici, on suppose 0 si null.
            ps.setInt(2, intervention.getNumDent() != null ? intervention.getNumDent() : 0);
            ps.setLong(3, consultationId);
            ps.setLong(4, intervention.getActe().getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        intervention.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'intervention à la consultation", e);
        }
    }
}