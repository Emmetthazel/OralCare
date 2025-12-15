package ma.oralCare.repository.modules.notification.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.entities.users.Notification;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationRepositoryImpl implements NotificationRepository {

    // Requête de base pour sélectionner les champs de Notification et BaseEntity
    private static final String BASE_SELECT_SQL = """
        SELECT n.id_entite, n.titre, n.message, n.date, n.time, n.type, n.priorite,
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Notification n 
        JOIN BaseEntity b ON n.id_entite = b.id_entite
        """;

    // Requêtes d'insertion
    private static final String INSERT_BASE_ENTITY_SQL = "INSERT INTO BaseEntity (date_creation, cree_par) VALUES (?, ?)";
    private static final String INSERT_NOTIFICATION_SQL = "INSERT INTO Notification (id_entite, titre, message, date, time, type, priorite) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String INSERT_NOTIFICATION_UTILISATEUR_SQL = "INSERT INTO notification_utilisateur (notification_id, utilisateur_id, est_lu) VALUES (?, ?, FALSE)";

    // Requêtes de suppression
    private static final String DELETE_NOTIFICATION_SQL = "DELETE FROM Notification WHERE id_entite = ?";
    private static final String DELETE_BASE_ENTITY_SQL = "DELETE FROM BaseEntity WHERE id_entite = ?";

    // =========================================================================
    //                            MÉTHODES UTILITAIRES
    // =========================================================================

    private List<Notification> executeSelectQuery(String sql, Object... params) {
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SELECT pour Notification.", e);
        }
        return out;
    }

    // =========================================================================
    //                            CRUD
    // =========================================================================

    @Override
    public List<Notification> findAll() {
        String sql = BASE_SELECT_SQL + " ORDER BY n.date DESC, n.time DESC";
        return executeSelectQuery(sql);
    }

    @Override
    public Optional<Notification> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE n.id_entite = ?";
        List<Notification> results = executeSelectQuery(sql, id);
        return results.stream().findFirst();
    }

    @Override
    public void create(Notification notification) {
        if (notification == null) return;

        // Date de création pour BaseEntity
        LocalDateTime now = LocalDateTime.now();
        Timestamp dateCreation = Timestamp.valueOf(now);
        Long creePar = notification.getCreePar() != null ? notification.getCreePar() : 1L; // Par défaut Admin

        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // DÉBUT DE LA TRANSACTION

            Long generatedId = null;

            // 1. Insertion dans BaseEntity et récupération de l'ID généré
            try (PreparedStatement psBase = c.prepareStatement(INSERT_BASE_ENTITY_SQL, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, dateCreation);
                psBase.setLong(2, creePar);

                int rowsAffected = psBase.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("L'insertion dans BaseEntity a échoué, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = psBase.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getLong(1);
                        notification.setIdEntite(generatedId); // Rétro-injecter l'ID
                    } else {
                        throw new SQLException("L'insertion dans BaseEntity a échoué, aucun ID généré.");
                    }
                }
            }

            // 2. Insertion dans Notification (avec l'ID récupéré)
            try (PreparedStatement psNotif = c.prepareStatement(INSERT_NOTIFICATION_SQL)) {
                psNotif.setLong(1, notification.getIdEntite());
                psNotif.setString(2, notification.getTitre().name());
                psNotif.setString(3, notification.getMessage());
                psNotif.setDate(4, Date.valueOf(notification.getDate()));
                psNotif.setTime(5, Time.valueOf(notification.getTime()));
                psNotif.setString(6, notification.getType().name());
                psNotif.setString(7, notification.getPriorite().name());
                psNotif.executeUpdate();
            }

            // 3. Insertion dans notification_utilisateur (gestion des destinataires M-to-M)
            if (notification.getUtilisateurs() != null && !notification.getUtilisateurs().isEmpty()) {
                try (PreparedStatement psNotifUser = c.prepareStatement(INSERT_NOTIFICATION_UTILISATEUR_SQL)) {
                    for (Utilisateur destinataire : notification.getUtilisateurs()) {
                        if (destinataire != null && destinataire.getIdEntite() != null) {
                            psNotifUser.setLong(1, notification.getIdEntite());
                            psNotifUser.setLong(2, destinataire.getIdEntite());
                            psNotifUser.addBatch();
                        }
                    }
                    psNotifUser.executeBatch();
                }
            }

            c.commit(); // FIN DE LA TRANSACTION (succès)

        } catch (SQLException e) {
            try {
                if (c != null) {
                    c.rollback(); // ANNULATION DE LA TRANSACTION (échec)
                }
            } catch (SQLException rollbackEx) {
                // Log l'échec du rollback
                System.err.println("Échec du rollback de la transaction Notification: " + rollbackEx.getMessage());
            }
            // Propager l'exception qui a causé l'échec du CREATE
            throw new RuntimeException("Erreur critique lors de la création de la notification (transaction annulée).", e);

        } finally {
            if (c != null) {
                try {
                    c.setAutoCommit(true);
                    c.close();
                } catch (SQLException closeEx) {
                    System.err.println("Erreur lors de la fermeture de la connexion: " + closeEx.getMessage());
                }
            }
        }
    }

    @Override
    public void update(Notification notification) {
        // La mise à jour de Notification est généralement simple (message, priorité).
        // La gestion des destinataires est plus complexe et se ferait par des méthodes dédiées (addDestinataire, removeDestinataire).

        String updateSql = "UPDATE Notification SET titre=?, message=?, date=?, time=?, type=?, priorite=? WHERE id_entite=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(updateSql)) {

            ps.setString(1, notification.getTitre().name());
            ps.setString(2, notification.getMessage());
            ps.setDate(3, Date.valueOf(notification.getDate()));
            ps.setTime(4, Time.valueOf(notification.getTime()));
            ps.setString(5, notification.getType().name());
            ps.setString(6, notification.getPriorite().name());
            ps.setLong(7, notification.getIdEntite());

            ps.executeUpdate();

            // Mise à jour de la date de modification dans BaseEntity
            // Non implémenté ici pour concision, mais devrait être fait.

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la notification.", e);
        }
    }

    @Override
    public void delete(Notification notification) {
        if (notification != null && notification.getIdEntite() != null) {
            deleteById(notification.getIdEntite());
        }
    }

    @Override
    public void deleteById(Long id) {
        // La suppression du Notification va cascader sur notification_utilisateur.
        // La suppression de BaseEntity va cascader sur Notification.
        // Nous supprimons BaseEntity pour déclencher la cascade.

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(DELETE_BASE_ENTITY_SQL)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la notification avec ID " + id, e);
        }
    }

    // =========================================================================
    //                            MÉTHODES SPÉCIFIQUES
    // =========================================================================

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        String sql = BASE_SELECT_SQL + """
            JOIN notification_utilisateur nu ON n.id_entite = nu.notification_id
            WHERE nu.utilisateur_id = ?
            ORDER BY n.date DESC, n.time DESC
        """;
        return executeSelectQuery(sql, utilisateurId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateurId(Long utilisateurId) {
        String sql = BASE_SELECT_SQL + """
            JOIN notification_utilisateur nu ON n.id_entite = nu.notification_id
            WHERE nu.utilisateur_id = ? AND nu.est_lu = FALSE 
            ORDER BY n.date DESC, n.time DESC
        """;
        return executeSelectQuery(sql, utilisateurId);
    }

    @Override
    public List<Notification> findByPriorite(NotificationPriorite priorite) {
        String sql = BASE_SELECT_SQL + " WHERE n.priorite = ? ORDER BY n.date DESC, n.time DESC";
        return executeSelectQuery(sql, priorite.name());
    }

    @Override
    public List<Notification> findByTitre(NotificationTitre titre) {
        String sql = BASE_SELECT_SQL + " WHERE n.titre = ? ORDER BY n.date DESC, n.time DESC";
        return executeSelectQuery(sql, titre.name());
    }

    @Override
    public List<Notification> findByDate(LocalDate date) {
        String sql = BASE_SELECT_SQL + " WHERE n.date = ? ORDER BY n.time DESC";
        return executeSelectQuery(sql, Date.valueOf(date));
    }

    @Override
    public void markAsRead(Long notificationId, Long utilisateurId) {
        String sql = "UPDATE notification_utilisateur SET est_lu = TRUE WHERE notification_id = ? AND utilisateur_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du marquage de la notification comme lue.", e);
        }
    }

    @Override
    public void markAllAsReadByUtilisateurId(Long utilisateurId) {
        String sql = "UPDATE notification_utilisateur SET est_lu = TRUE WHERE utilisateur_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du marquage de toutes les notifications comme lues.", e);
        }
    }
}