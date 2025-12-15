package ma.oralCare.repository.modules.agenda.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // Requête de base pour lire AgendaMensuel + BaseEntity
    private static final String BASE_SELECT_SQL = """
        SELECT am.mois, am.annee, am.medecin_id,
            b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM AgendaMensuel am JOIN BaseEntity b ON am.id_entite = b.id_entite
        """;

    // SQL pour insérer un jour non disponible (utilisé dans batch)
    private static final String SQL_INSERT_JOUR =
            "INSERT INTO AgendaMensuel_JourNonDisponible(agenda_id, jour_non_disponible) VALUES (?,?)";

    // =========================================================================
    //                            CRUD BASIQUE (Hérité de CrudRepository)
    // =========================================================================

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = BASE_SELECT_SQL + " ORDER BY am.annee DESC, am.mois DESC, am.medecin_id";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AgendaMensuel agenda = RowMappers.mapAgendaMensuel(rs);
                // Chargement des jours non disponibles
                agenda.setJoursNonDisponible(findJoursNonDisponiblesByAgendaId(agenda.getIdEntite()));
                out.add(agenda);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll AgendaMensuel.", e);
        }
        return out;
    }

    @Override
    public Optional<AgendaMensuel> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE am.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AgendaMensuel agenda = RowMappers.mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(findJoursNonDisponiblesByAgendaId(agenda.getIdEntite()));
                    return Optional.of(agenda);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById AgendaMensuel.", e);
        }
    }

    @Override
    public void create(AgendaMensuel am) {
        Long baseId = null;
        Connection c = null; // Maintenu pour le rollback dans le catch

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "INSERT INTO BaseEntity(date_creation, date_derniere_modification, cree_par) VALUES(?, ?, ?)";
        String sqlAgenda = "INSERT INTO AgendaMensuel(id_entite, mois, annee, medecin_id) VALUES(?, ?, ?, ?)";

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Début de transaction

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, nowTimestamp);
                psBase.setNull(2, Types.TIMESTAMP);
                if (am.getCreePar() != null) psBase.setLong(3, am.getCreePar());
                else psBase.setNull(3, Types.BIGINT);
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                am.setIdEntite(baseId);
                am.setDateCreation(now);
            }

            // 2. Insertion dans AgendaMensuel
            try (PreparedStatement psAgenda = c.prepareStatement(sqlAgenda)) {
                psAgenda.setLong(1, am.getIdEntite());
                psAgenda.setString(2, am.getMois().name());
                psAgenda.setInt(3, am.getAnnee());
                psAgenda.setLong(4, am.getMedecin().getIdEntite());
                psAgenda.executeUpdate();
            }

            // 3. Insertion des Jours Non Disponibles (BATCH insertion)
            if (am.getJoursNonDisponible() != null && !am.getJoursNonDisponible().isEmpty()) {
                try (PreparedStatement psJour = c.prepareStatement(SQL_INSERT_JOUR)) {
                    for (Jour jour : am.getJoursNonDisponible()) {
                        psJour.setLong(1, am.getIdEntite());
                        psJour.setString(2, jour.name());
                        psJour.addBatch();
                    }
                    psJour.executeBatch();
                }
            }

            c.commit();

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la création de l'Agenda Mensuel.", e);
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
    public void update(AgendaMensuel am) {
        Connection c = null;

        LocalDateTime now = LocalDateTime.now();
        Timestamp nowTimestamp = Timestamp.valueOf(now);

        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlAgenda = "UPDATE AgendaMensuel SET mois=?, annee=?, medecin_id=? WHERE id_entite=?";

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour de BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(sqlBase)) {
                psBase.setTimestamp(1, nowTimestamp);
                if (am.getModifiePar() != null) psBase.setLong(2, am.getModifiePar());
                else psBase.setNull(2, Types.BIGINT);
                psBase.setLong(3, am.getIdEntite());
                psBase.executeUpdate();
                am.setDateDerniereModification(now);
            }

            // 2. Mise à jour de AgendaMensuel
            try (PreparedStatement psAgenda = c.prepareStatement(sqlAgenda)) {
                psAgenda.setString(1, am.getMois().name());
                psAgenda.setInt(2, am.getAnnee());
                psAgenda.setLong(3, am.getMedecin().getIdEntite());
                psAgenda.setLong(4, am.getIdEntite());
                psAgenda.executeUpdate();
            }

            // 3. Mise à jour des Jours Non Disponibles (CORRIGÉE)
            if (am.getJoursNonDisponible() != null) {
                // *** CORRECTION : Appel de la méthode interne setJoursNonDisponibleInternal ***
                setJoursNonDisponibleInternal(am.getIdEntite(), am.getJoursNonDisponible(), c);
            }

            c.commit(); // Commit unique

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on update.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour de l'Agenda Mensuel.", e);
        } finally {
            if (c != null) {
                try {
                    // Rétablir l'AutoCommit avant de fermer
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    @Override
    public void delete(AgendaMensuel am) { if (am != null) deleteById(am.getIdEntite()); }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity déclenche ON DELETE CASCADE dans AgendaMensuel et AgendaMensuel_JourNonDisponible
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de l'Agenda Mensuel par ID.", e); }
    }

    // =========================================================================
    //                            MÉTHODES SPÉCIFIQUES À L'AGENDA
    // =========================================================================

    @Override
    public Optional<AgendaMensuel> findByMedecinIdAndMoisAndAnnee(Long medecinId, Mois mois, int annee) {
        String sql = BASE_SELECT_SQL + " WHERE am.medecin_id = ? AND am.mois = ? AND am.annee = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setString(2, mois.name());
            ps.setInt(3, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AgendaMensuel agenda = RowMappers.mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(findJoursNonDisponiblesByAgendaId(agenda.getIdEntite()));
                    return Optional.of(agenda);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByMedecinIdAndMoisAndAnnee.", e);
        }
    }

    @Override
    public Optional<AgendaMensuel> findCurrentAgenda(Long medecinId, LocalDate date) {
        Mois mois = Mois.valueOf(date.getMonth().name());
        int annee = date.getYear();
        return findByMedecinIdAndMoisAndAnnee(medecinId, mois, annee);
    }

    @Override
    public List<AgendaMensuel> findAllByMedecinId(Long medecinId) {
        String sql = BASE_SELECT_SQL + " WHERE am.medecin_id = ? ORDER BY am.annee DESC, am.mois DESC";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AgendaMensuel agenda = RowMappers.mapAgendaMensuel(rs);
                    agenda.setJoursNonDisponible(findJoursNonDisponiblesByAgendaId(agenda.getIdEntite()));
                    out.add(agenda);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByMedecinId.", e);
        }
        return out;
    }

    // =========================================================================
    //                    GESTION JOURS NON DISPONIBLES (MANY-TO-MANY)
    // =========================================================================

    @Override
    public void addJourNonDisponible(Long agendaId, Jour jour) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT_JOUR)) {
            ps.setLong(1, agendaId);
            ps.setString(2, jour.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du JourNonDisponible.", e);
        }
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, Jour jour) {
        String sql = "DELETE FROM AgendaMensuel_JourNonDisponible WHERE agenda_id=? AND jour_non_disponible=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setString(2, jour.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du JourNonDisponible.", e);
        }
    }

    @Override
    public List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId) {
        String sql = "SELECT jour_non_disponible FROM AgendaMensuel_JourNonDisponible WHERE agenda_id = ?";
        List<Jour> jours = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    jours.add(Jour.valueOf(rs.getString("jour_non_disponible")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des jours non disponibles.", e);
        }
        return jours;
    }

    // --- Version Publique (Conforme à l'interface, gère sa propre transaction) ---
    @Override
    public void setJoursNonDisponible(Long agendaId, List<Jour> joursNonDisponible) {
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // Appel à la version interne pour la logique
            setJoursNonDisponibleInternal(agendaId, joursNonDisponible, c);

            c.commit();
        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); }
                catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error on setJours.", rollbackEx); }
            }
            throw new RuntimeException("Erreur lors de la mise à jour des jours non disponibles.", e);
        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); }
            }
        }
    }

    // --- Version Interne (Utilisée par update(am) et la version publique, fait le travail) ---
    public void setJoursNonDisponibleInternal(Long agendaId, List<Jour> joursNonDisponible, Connection c) throws SQLException {
        String sqlDelete = "DELETE FROM AgendaMensuel_JourNonDisponible WHERE agenda_id=?";

        // 1. Supprimer tous les jours existants
        try (PreparedStatement psDelete = c.prepareStatement(sqlDelete)) {
            psDelete.setLong(1, agendaId);
            psDelete.executeUpdate();
        }

        // 2. Insérer la nouvelle liste (BATCH insertion)
        if (joursNonDisponible != null && !joursNonDisponible.isEmpty()) {
            try (PreparedStatement psJour = c.prepareStatement(SQL_INSERT_JOUR)) {
                for (Jour jour : joursNonDisponible) {
                    psJour.setLong(1, agendaId);
                    psJour.setString(2, jour.name());
                    psJour.addBatch();
                }
                psJour.executeBatch();
            }
        }
        // PAS de commit/close ici.
    }
}