package ma.oralCare.repository.modules.agenda.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    // --- Requêtes SQL de Base ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_DELETE_BASE = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // REQUÊTES AJUSTÉES : SEULS dossier_medicale_id et consultation_id sont utilisés
    private static final String SQL_INSERT_RDV = "INSERT INTO RDV (id_entite, dossier_medicale_id, consultation_id, date, heure, motif, statut, note_medecin) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_RDV = "UPDATE RDV SET dossier_medicale_id=?, consultation_id=?, date=?, heure=?, motif=?, statut=?, note_medecin=? WHERE id_entite=?";

    private static final String BASE_SELECT_RDV_SQL = """
        SELECT r.id_entite, r.dossier_medicale_id, r.consultation_id, r.date, r.heure, r.motif, r.statut, r.note_medecin,
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM RDV r
        JOIN BaseEntity b ON r.id_entite = b.id_entite
    """;

    // =========================================================================
    //                            CRUD
    // =========================================================================

    @Override
    public void create(RDV rdv) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        Long dossierId = (rdv.getDossierMedicale() != null) ? rdv.getDossierMedicale().getIdEntite() : null;
        Long consultationId = (rdv.getConsultation() != null) ? rdv.getConsultation().getIdEntite() : null;

        if (dossierId == null) {
            throw new IllegalArgumentException("Erreur de création RDV: DossierMedicale doit être non nul.");
        }

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, rdv.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                rdv.setIdEntite(baseId);
                rdv.setDateCreation(now);
            }

            // 2. RDV
            try (PreparedStatement psRdv = c.prepareStatement(SQL_INSERT_RDV)) {
                int i = 1;
                psRdv.setLong(i++, rdv.getIdEntite());

                // IDs de relation
                psRdv.setLong(i++, dossierId);
                psRdv.setObject(i++, consultationId); // Peut être NULL

                // Champs RDV
                psRdv.setDate(i++, Date.valueOf(rdv.getDate()));
                psRdv.setTime(i++, Time.valueOf(rdv.getHeure()));
                psRdv.setString(i++, rdv.getMotif());
                psRdv.setString(i++, rdv.getStatut().name());
                psRdv.setString(i++, rdv.getNoteMedecin());

                psRdv.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création RDV.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création du RDV.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public Optional<RDV> findById(Long id) {
        String sql = BASE_SELECT_RDV_SQL + " WHERE r.id_entite = ?";
        return executeFindQuery(sql, id);
    }

    @Override
    public List<RDV> findAll() {
        String sql = BASE_SELECT_RDV_SQL + " ORDER BY r.date, r.heure";
        return executeFindAllQuery(sql);
    }

    @Override
    public void update(RDV rdv) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        Long dossierId = (rdv.getDossierMedicale() != null) ? rdv.getDossierMedicale().getIdEntite() : null;
        Long consultationId = (rdv.getConsultation() != null) ? rdv.getConsultation().getIdEntite() : null;

        if (dossierId == null) {
            throw new IllegalArgumentException("Erreur de mise à jour RDV: DossierMedicale doit être non nul.");
        }

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity Update
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, rdv.getModifiePar());
                psBase.setLong(3, rdv.getIdEntite());
                psBase.executeUpdate();
                rdv.setDateDerniereModification(now);
            }

            // 2. RDV Update
            try (PreparedStatement psRdv = c.prepareStatement(SQL_UPDATE_RDV)) {
                int i = 1;
                // IDs de relation
                psRdv.setLong(i++, dossierId);
                psRdv.setObject(i++, consultationId); // Peut être NULL

                // Champs RDV
                psRdv.setDate(i++, Date.valueOf(rdv.getDate()));
                psRdv.setTime(i++, Time.valueOf(rdv.getHeure()));
                psRdv.setString(i++, rdv.getMotif());
                psRdv.setString(i++, rdv.getStatut().name());
                psRdv.setString(i++, rdv.getNoteMedecin());

                // WHERE clause
                psRdv.setLong(i++, rdv.getIdEntite());
                psRdv.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour RDV.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour du RDV.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    // ------------------------------------------------------------------------
    // MÉTHODE MANQUANTE AJOUTÉE POUR SATISFAIRE CrudRepository
    // ------------------------------------------------------------------------
    @Override
    public void delete(RDV rdv) {
        if (rdv.getIdEntite() == null) {
            throw new IllegalArgumentException("L'ID de l'entité RDV ne peut pas être null pour la suppression.");
        }
        deleteById(rdv.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE_BASE)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du RDV.", e);
        }
    }

    // =========================================================================
    //                            Implémentations Spécifiques
    // =========================================================================

    @Override
    public RDV updateStatut(Long rdvId, StatutRDV nouveauStatut) {
        final String SQL_UPDATE_STATUT = "UPDATE RDV SET statut = ? WHERE id_entite = ?";

        LocalDateTime now = LocalDateTime.now();

        // UTILISATION DE TRY-WITH-RESOURCES pour gérer la connexion
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);

            // 1. Update BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, 1L);
                psBase.setLong(3, rdvId);
                psBase.executeUpdate();
            }

            // 2. Update RDV
            try (PreparedStatement psRdv = c.prepareStatement(SQL_UPDATE_STATUT)) {
                psRdv.setString(1, nouveauStatut.name());
                psRdv.setLong(2, rdvId);
                psRdv.executeUpdate();
            }

            c.commit();

            // IMPORTANT: findById utilise une NOUVELLE connexion, ce qui est correct.
            // La connexion 'c' est fermée APRÈS cette ligne, grâce au try-with-resources.
            return findById(rdvId).orElse(null);

        } catch (SQLException e) {
            // Le Rollback DOIT se faire sur la connexion 'c' si l'exception est attrapée.
            // Si l'exception se produit, 'c' sera toujours disponible dans le bloc try-catch si on le déclare en dehors.
            // Si on utilise try-with-resources, on peut devoir relancer une RuntimeException.

            // Pour gérer le rollback dans le try-with-resources, nous relançons l'exception.
            throw new RuntimeException("Erreur lors de la mise à jour du statut du RDV.", e);
        }
    }

    @Override
    public List<RDV> findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = BASE_SELECT_RDV_SQL + " WHERE r.dossier_medicale_id = ? ORDER BY r.date DESC";
        return executeFindAllQuery(sql, dossierMedicaleId);
    }

    @Override
    public Optional<RDV> findByConsultationId(Long consultationId) {
        String sql = BASE_SELECT_RDV_SQL + " WHERE r.consultation_id = ?";
        return executeFindQuery(sql, consultationId);
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = BASE_SELECT_RDV_SQL + " WHERE r.date = ? ORDER BY r.heure";
        return executeFindAllQuery(sql, Date.valueOf(date));
    }

    @Override
    public boolean existsByDateAndHeureAndMedecinId(LocalDate date, LocalTime heure, Long medecinId) {
        final String SQL = """
            SELECT 1
            FROM RDV r
            JOIN DossierMedicale dm ON r.dossier_medicale_id = dm.id_entite
            WHERE r.date = ? AND r.heure = ? AND dm.medecin_id = ? LIMIT 1
        """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(heure));
            ps.setLong(3, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de disponibilité du créneau.", e);
        }
    }

    @Override
    public List<RDV> findByStatut(StatutRDV statut) {
        String sql = BASE_SELECT_RDV_SQL + " WHERE r.statut = ? ORDER BY r.date, r.heure";
        return executeFindAllQuery(sql, statut.name());
    }

    // =========================================================================
    //                            Méthodes Utilitaires
    // =========================================================================

    private Optional<RDV> executeFindQuery(String sql, Object... params) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRDV(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'exécution de la requête de recherche RDV.", e);
        }
    }

    private List<RDV> executeFindAllQuery(String sql, Object... params) {
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRDV(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur d'exécution de la requête findAll RDV.", e);
        }
        return out;
    }
}