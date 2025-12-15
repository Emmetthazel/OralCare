package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActeRepositoryImpl implements ActeRepository {

    // Requête de base pour lire Acte + BaseEntity
    private static final String BASE_SELECT_SQL = """
        SELECT a.id_entite, a.libelle, a.categorie, a.prix_de_base,
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM acte a JOIN BaseEntity b ON a.id_entite = b.id_entite
        """;

    // =========================================================================
    //                            MÉTHODES UTILITAIRES
    // =========================================================================

    private List<Acte> executeSelectQuery(String sql, Object... params) {
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                // Assurer que les BigDecimal sont correctement gérés si passés en paramètres
                if (params[i] instanceof BigDecimal) {
                    ps.setBigDecimal(i + 1, (BigDecimal) params[i]);
                } else {
                    ps.setObject(i + 1, params[i]);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Acte acte = RowMappers.mapActe(rs);
                    // NOTE: Le chargement des interventions sera fait au besoin par le Service ou findInterventionsByActeId
                    out.add(acte);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour Acte.", e);
        }
        return out;
    }

    // =========================================================================
    //                                CRUD (Hérité)
    // =========================================================================

    @Override
    public List<Acte> findAll() {
        String sql = BASE_SELECT_SQL + " ORDER BY a.libelle";
        return executeSelectQuery(sql);
    }

    @Override
    public Optional<Acte> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE a.id_entite = ?";
        List<Acte> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }

    @Override
    public void create(Acte acte) {
        Long baseId = null;
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";
        String sqlActe = "INSERT INTO acte(id_entite, libelle, categorie, prix_de_base) VALUES(?, ?, ?, ?)";

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);
                if (acte.getCreePar() != null) psBase.setLong(3, acte.getCreePar());
                else psBase.setNull(3, Types.BIGINT);
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                acte.setIdEntite(baseId);
                acte.setDateCreation(now);
            }

            // 2. Insertion dans Acte
            try (PreparedStatement psActe = c.prepareStatement(sqlActe)) {
                psActe.setLong(1, acte.getIdEntite());
                psActe.setString(2, acte.getLibelle());
                psActe.setString(3, acte.getCategorie());
                psActe.setBigDecimal(4, acte.getPrixDeBase());
                psActe.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on create Acte.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la création de l'Acte.", e);
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
    public void update(Acte acte) {
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlActe = "UPDATE acte SET libelle=?, categorie=?, prix_de_base=? WHERE id_entite=?";

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                if (acte.getModifiePar() != null) psBase.setLong(2, acte.getModifiePar());
                else psBase.setNull(2, Types.BIGINT);
                psBase.setLong(3, acte.getIdEntite());
                psBase.executeUpdate();
                acte.setDateDerniereModification(now);
            }

            // 2. Mise à jour de Acte
            try (PreparedStatement psActe = c.prepareStatement(sqlActe)) {
                psActe.setString(1, acte.getLibelle());
                psActe.setString(2, acte.getCategorie());
                psActe.setBigDecimal(3, acte.getPrixDeBase());
                psActe.setLong(4, acte.getIdEntite());
                psActe.executeUpdate();
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on update Acte.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour de l'Acte.", e);
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
    public void delete(Acte acte) {
        if (acte != null) deleteById(acte.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity déclenche ON DELETE CASCADE dans 'acte'
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de l'Acte par ID.", e); }
    }

    // =========================================================================
    //                                  MÉTHODES SPÉCIFIQUES
    // =========================================================================

    @Override
    public Optional<Acte> findByLibelle(String libelle) {
        String sql = BASE_SELECT_SQL + " WHERE a.libelle = ?";
        List<Acte> results = executeSelectQuery(sql, libelle);
        return results.stream().findFirst();
    }

    @Override
    public List<Acte> findByCategorie(String categorie) {
        String sql = BASE_SELECT_SQL + " WHERE a.categorie = ? ORDER BY a.libelle";
        return executeSelectQuery(sql, categorie);
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(id_entite) FROM acte WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la vérification de l'existence de l'Acte par ID.", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(id_entite) FROM acte";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des Actes.", e);
        }
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        String sql = BASE_SELECT_SQL + " ORDER BY a.libelle LIMIT ? OFFSET ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                List<Acte> actes = new ArrayList<>();
                while (rs.next()) {
                    actes.add(RowMappers.mapActe(rs));
                }
                return actes;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la page d'Actes.", e);
        }
    }

    // =========================================================================
    //                           NAVIGATION INVERSE (InterventionMedecin)
    // =========================================================================

    @Override
    public List<InterventionMedecin> findInterventionsByActeId(Long acteId) {
        // Supposons que InterventionsMedecin est mappé par un JOIN avec BaseEntity
        String sql = """
            SELECT im.id_entite, im.prix_de_patient, im.num_dent, im.consultation_id, im.acte_id,
                   b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
            FROM intervention_medecin im JOIN BaseEntity b ON im.id_entite = b.id_entite
            WHERE im.acte_id = ?
            ORDER BY im.num_dent
        """;
        List<InterventionMedecin> interventions = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, acteId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // NOTE: Assurez-vous que RowMappers.mapInterventionMedecin est implémenté
                    interventions.add(RowMappers.mapInterventionMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des interventions par Acte ID.", e);
        }
        return interventions;
    }

    @Override
    public Optional<Acte> findByInterventionMedecinId(Long interventionMedecinId) {
        String sql = BASE_SELECT_SQL + """
            JOIN intervention_medecin im ON a.id_entite = im.acte_id
            WHERE im.id_entite = ?
            """;
        List<Acte> results = executeSelectQuery(sql, interventionMedecinId);
        return results.stream().findFirst();
    }
}