package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DossierMedicaleRepositoryImpl implements DossierMedicaleRepository {

    private static final String BASE_SELECT_SQL = """
        SELECT dm.id_entite, dm.patient_id, dm.medecin_id, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM DossierMedicale dm JOIN BaseEntity b ON dm.id_entite = b.id_entite
        """;

    // =========================================================================
    //                            MÉTHODES UTILITAIRES
    // =========================================================================

    private List<DossierMedicale> executeSelectQuery(String sql, Object... params) {
        List<DossierMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // RowMappers.mapDossierMedicale doit mapper les IDs patient et medecin
                    DossierMedicale dossier = RowMappers.mapDossierMedicale(rs);
                    out.add(dossier);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour DossierMedicale.", e);
        }
        return out;
    }

    // =========================================================================
    //                                CRUD (Hérité)
    // =========================================================================

    @Override
    public List<DossierMedicale> findAll() {
        String sql = BASE_SELECT_SQL + " ORDER BY dm.id_entite DESC";
        return executeSelectQuery(sql);
    }

    @Override
    public Optional<DossierMedicale> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE dm.id_entite = ?";
        List<DossierMedicale> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }

    @Override
    public void create(DossierMedicale dossier) {
        Long baseId = null;
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";
        String sqlDossier = """
            INSERT INTO DossierMedicale(id_entite, patient_id, medecin_id)
            VALUES(?, ?, ?)
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);
                if (dossier.getCreePar() != null) psBase.setLong(3, dossier.getCreePar());
                else psBase.setNull(3, Types.BIGINT);
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity pour DossierMedicale.");
                }
                dossier.setIdEntite(baseId);
                dossier.setDateCreation(now);
            }

            // 2. Insertion dans DossierMedicale
            try (PreparedStatement psDossier = c.prepareStatement(sqlDossier)) {
                psDossier.setLong(1, dossier.getIdEntite());

                // patient_id (NOT NULL)
                if (dossier.getPatient() == null || dossier.getPatient().getIdEntite() == null) {
                    throw new IllegalArgumentException("Patient ID est obligatoire pour la création d'un DossierMedicale.");
                }
                psDossier.setLong(2, dossier.getPatient().getIdEntite());

                // medecin_id (NULL)
                if (dossier.getMedecin() != null && dossier.getMedecin().getIdEntite() != null) {
                    psDossier.setLong(3, dossier.getMedecin().getIdEntite());
                } else {
                    psDossier.setNull(3, Types.BIGINT);
                }

                psDossier.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on create DossierMedicale.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la création du DossierMedicale.", e);
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
    public void update(DossierMedicale dossier) {
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlDossier = """
            UPDATE DossierMedicale SET patient_id=?, medecin_id=?
            WHERE id_entite=?
            """;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                if (dossier.getModifiePar() != null) psBase.setLong(2, dossier.getModifiePar());
                else psBase.setNull(2, Types.BIGINT);
                psBase.setLong(3, dossier.getIdEntite());
                psBase.executeUpdate();
                dossier.setDateDerniereModification(now);
            }

            // 2. Mise à jour de DossierMedicale
            try (PreparedStatement psDossier = c.prepareStatement(sqlDossier)) {

                // patient_id (NOT NULL)
                if (dossier.getPatient() == null || dossier.getPatient().getIdEntite() == null) {
                    throw new IllegalArgumentException("Patient ID est obligatoire pour la mise à jour d'un DossierMedicale.");
                }
                psDossier.setLong(1, dossier.getPatient().getIdEntite());

                // medecin_id (NULL)
                if (dossier.getMedecin() != null && dossier.getMedecin().getIdEntite() != null) {
                    psDossier.setLong(2, dossier.getMedecin().getIdEntite());
                } else {
                    psDossier.setNull(2, Types.BIGINT);
                }

                psDossier.setLong(3, dossier.getIdEntite());
                psDossier.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on update DossierMedicale.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour du DossierMedicale.", e);
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
    public void delete(DossierMedicale dossier) {
        if (dossier != null) deleteById(dossier.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity déclenche la cascade
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression du DossierMedicale par ID.", e); }
    }

    // =========================================================================
    //                            MÉTHODES SPÉCIFIQUES
    // =========================================================================

    @Override
    public Optional<DossierMedicale> findByPatientId(Long patientId) {
        String sql = BASE_SELECT_SQL + " WHERE dm.patient_id = ?";
        List<DossierMedicale> results = executeSelectQuery(sql, patientId);
        return results.stream().findFirst();
    }

    @Override
    public List<DossierMedicale> findByMedecinId(Long medecinId) {
        String sql = BASE_SELECT_SQL + " WHERE dm.medecin_id = ? ORDER BY dm.id_entite DESC";
        return executeSelectQuery(sql, medecinId);
    }

    // =========================================================================
    //                           NAVIGATION (Relations One-to-Many)
    // =========================================================================

    @Override
    public List<Consultation> findConsultationsByDossierId(Long dossierId) {
        String sql = """
            SELECT c.id_entite, c.date, c.statut, c.observation_medecin, c.dossier_medicale_id,
                   b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
            FROM Consultation c JOIN BaseEntity b ON c.id_entite = b.id_entite
            WHERE c.dossier_medicale_id = ?
            ORDER BY c.date DESC
        """;
        List<Consultation> consultations = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    consultations.add(RowMappers.mapConsultation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des consultations par Dossier ID.", e);
        }
        return consultations;
    }

    @Override
    public List<Ordonnance> findOrdonnancesByDossierId(Long dossierId) {
        String sql = """
            SELECT o.id_entite, o.date_ordonnance, o.consultation_id, o.dossier_medicale_id,
                   b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
            FROM Ordonnance o JOIN BaseEntity b ON o.id_entite = b.id_entite
            WHERE o.dossier_medicale_id = ?
            ORDER BY o.date_ordonnance DESC
        """;
        List<Ordonnance> ordonnances = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(RowMappers.mapOrdonnance(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des ordonnances par Dossier ID.", e);
        }
        return ordonnances;
    }

    @Override
    public List<Certificat> findCertificatsByDossierId(Long dossierId) {
        // Le schéma Certificat est lié à Consultation, qui est liée à DossierMedicale.
        // On peut faire une jointure pour récupérer les certificats liés au dossier via la consultation.
        String sql = """
            SELECT cert.id_entite, cert.date_debut, cert.date_fin, cert.duree, cert.note_medecin, cert.consultation_id,
                   b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
            FROM Certificat cert
            JOIN BaseEntity b ON cert.id_entite = b.id_entite
            JOIN Consultation c ON cert.consultation_id = c.id_entite
            WHERE c.dossier_medicale_id = ?
            ORDER BY cert.date_debut DESC
        """;
        List<Certificat> certificats = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    certificats.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des certificats par Dossier ID.", e);
        }
        return certificats;
    }
}