package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    private static final String BASE_SELECT_SQL = """
        SELECT c.id_entite, c.date, c.statut, c.observation_medecin, c.dossier_medicale_id,
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Consultation c JOIN BaseEntity b ON c.id_entite = b.id_entite
        """;

    // =========================================================================
    //                            MÉTHODES UTILITAIRES
    // =========================================================================

    private List<Consultation> executeSelectQuery(String sql, Object... params) {
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Utiliser setObject pour gérer différents types, mais setLong est mieux pour les IDs
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour Consultation.", e);
        }
        return out;
    }

    // =========================================================================
    //                                CRUD (Hérité)
    // =========================================================================

    @Override
    public List<Consultation> findAll() {
        String sql = BASE_SELECT_SQL + " ORDER BY c.date DESC";
        return executeSelectQuery(sql);
    }

    @Override
    public Optional<Consultation> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE c.id_entite = ?";
        List<Consultation> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }

    @Override
    public void create(Consultation consultation) {
        Long baseId = null;
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";
        String sqlConsultation = """
            INSERT INTO Consultation(id_entite, date, statut, observation_medecin, dossier_medicale_id)
            VALUES(?, ?, ?, ?, ?)
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);
                // Utilisation correcte de setLong pour le BIGINT cree_par
                if (consultation.getCreePar() != null) psBase.setLong(3, consultation.getCreePar());
                else psBase.setNull(3, Types.BIGINT);
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity pour Consultation.");
                }
                consultation.setIdEntite(baseId);
                consultation.setDateCreation(now);
            }

            // 2. Insertion dans Consultation
            try (PreparedStatement psConsultation = c.prepareStatement(sqlConsultation)) {
                psConsultation.setLong(1, consultation.getIdEntite()); // OK : setLong pour l'ID
                psConsultation.setDate(2, Date.valueOf(consultation.getDate())); // OK : setDate pour LocalDate
                psConsultation.setString(3, consultation.getStatut().name()); // OK : setString pour l'ENUM
                psConsultation.setString(4, consultation.getObservationMedecin()); // OK : setString pour TEXT

                // Clé étrangère vers DossierMedicale (doit être NOT NULL selon le schéma fourni)
                DossierMedicale dm = consultation.getDossierMedicale();
                if (dm != null && dm.getIdEntite() != null) {
                    psConsultation.setLong(5, dm.getIdEntite()); // OK : setLong pour le BIGINT
                } else {
                    throw new IllegalArgumentException("DossierMedicale ID est obligatoire pour la création d'une Consultation.");
                }

                psConsultation.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on create Consultation.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la création de la Consultation.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    @Override
    public void update(Consultation consultation) {
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlConsultation = """
            UPDATE Consultation SET date=?, statut=?, observation_medecin=?, dossier_medicale_id=?
            WHERE id_entite=?
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                // Utilisation correcte de setLong pour le BIGINT modifie_par
                if (consultation.getModifiePar() != null) psBase.setLong(2, consultation.getModifiePar());
                else psBase.setNull(2, Types.BIGINT);
                psBase.setLong(3, consultation.getIdEntite());
                psBase.executeUpdate();
                consultation.setDateDerniereModification(now);
            }

            // 2. Mise à jour de Consultation
            try (PreparedStatement psConsultation = c.prepareStatement(sqlConsultation)) {
                psConsultation.setDate(1, Date.valueOf(consultation.getDate()));
                psConsultation.setString(2, consultation.getStatut().name());
                psConsultation.setString(3, consultation.getObservationMedecin());

                // Clé étrangère vers DossierMedicale
                DossierMedicale dm = consultation.getDossierMedicale();
                // Utilisation correcte de setLong pour le BIGINT dossier_medicale_id
                if (dm != null && dm.getIdEntite() != null) {
                    psConsultation.setLong(4, dm.getIdEntite());
                } else {
                    // Note: Le schéma indique NOT NULL, donc cette ligne devrait en théorie toujours être Long
                    psConsultation.setNull(4, Types.BIGINT);
                }

                psConsultation.setLong(5, consultation.getIdEntite());
                psConsultation.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on update Consultation.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour de la Consultation.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    @Override
    public void delete(Consultation consultation) {
        if (consultation != null) deleteById(consultation.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id); // OK : setLong pour l'ID
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de la Consultation par ID.", e); }
    }

    // =========================================================================
    //                            MÉTHODES SPÉCIFIQUES
    // =========================================================================

    @Override
    public List<Consultation> findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = BASE_SELECT_SQL + " WHERE c.dossier_medicale_id = ? ORDER BY c.date DESC";
        return executeSelectQuery(sql, dossierMedicaleId); // L'utilitaire utilise setObject, qui gère le Long
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        String sql = BASE_SELECT_SQL + " WHERE c.statut = ? ORDER BY c.date DESC";
        return executeSelectQuery(sql, statut.name());
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        String sql = BASE_SELECT_SQL + " WHERE c.date = ? ORDER BY c.date DESC";
        return executeSelectQuery(sql, Date.valueOf(date));
    }

    @Override
    public void updateStatut(Long id, StatutConsultation nouveauStatut) {
        String sqlConsultation = "UPDATE Consultation SET statut=? WHERE id_entite=?";
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=? WHERE id_entite=?";
        Connection c = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);
            Timestamp nowTimestamp = Timestamp.valueOf(LocalDateTime.now());

            // 1. Mise à jour du statut
            try (PreparedStatement psConsultation = c.prepareStatement(sqlConsultation)) {
                psConsultation.setString(1, nouveauStatut.name());
                psConsultation.setLong(2, id); // OK : setLong pour l'ID
                psConsultation.executeUpdate();
            }

            // 2. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setLong(2, id); // OK : setLong pour l'ID
                psBase.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on updateStatut.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la Consultation.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    @Override
    public void updateObservation(Long id, String observation) {
        String sqlConsultation = "UPDATE Consultation SET observation_medecin=? WHERE id_entite=?";
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=? WHERE id_entite=?";
        Connection c = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);
            Timestamp nowTimestamp = Timestamp.valueOf(LocalDateTime.now());

            // 1. Mise à jour de l'observation
            try (PreparedStatement psConsultation = c.prepareStatement(sqlConsultation)) {
                psConsultation.setString(1, observation);
                psConsultation.setLong(2, id); // OK : setLong pour l'ID
                psConsultation.executeUpdate();
            }

            // 2. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setLong(2, id); // OK : setLong pour l'ID
                psBase.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on updateObservation.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour de l'observation de la Consultation.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    @Override
    public void addIntervention(Long consultationId, InterventionMedecin intervention) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";
        String sqlIntervention = """
            INSERT INTO InterventionMedecin(id_entite, consultation_id, acte_id, num_dent, prix_de_patient)
            VALUES(?, ?, ?, ?, ?)
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);
                if (intervention.getCreePar() != null) psBase.setLong(3, intervention.getCreePar());
                else psBase.setNull(3, Types.BIGINT);
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity pour InterventionMedecin.");
                }
                intervention.setIdEntite(baseId);
            }

            // 2. Insertion dans InterventionMedecin
            try (PreparedStatement psIntervention = c.prepareStatement(sqlIntervention)) {
                psIntervention.setLong(1, intervention.getIdEntite());
                psIntervention.setLong(2, consultationId);

                // Champs de InterventionMedecin
                psIntervention.setLong(3, intervention.getActe() != null ? intervention.getActe().getIdEntite() : Types.NULL);

                // CORRECTION ICI : Utilisation de setInt() au lieu de setString() pour num_dent
                Integer numDent = intervention.getNumDent();
                if (numDent != null) {
                    psIntervention.setInt(4, numDent);
                } else {
                    psIntervention.setNull(4, Types.INTEGER); // Supposons que num_dent est de type INTEGER
                }

                psIntervention.setBigDecimal(5, intervention.getPrixDePatient());

                psIntervention.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on addIntervention.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de l'ajout de l'Intervention à la Consultation.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }
}