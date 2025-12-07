package ma.oralCare.repository.modules.dashboard.impl;

import ma.oralCare.entities.notification.Notification;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;
import ma.oralCare.conf.SessionFactory; // Supposé exister
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NotificationRepositoryImpl implements NotificationRepository {

    // --- 1. Opérations CRUD de base (Héritées de CrudRepository) ---

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM Notification ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapNotification(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les notifications", e);
        }
        return out;
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM Notification WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapNotification(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la notification par ID", e);
        }
    }

    @Override
    public void create(Notification newElement) {
        if (newElement == null) return;
        String sql = "INSERT INTO Notification (titre, message, date, time, type, priorite) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getTitre().name());
            ps.setString(2, newElement.getMessage());
            ps.setDate(3, Date.valueOf(newElement.getDate()));
            ps.setTime(4, Time.valueOf(newElement.getTime()));
            ps.setString(5, newElement.getType().name());
            ps.setString(6, newElement.getPriorite().name());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
        // NOTE: La relation Notification-Utilisateur doit être insérée séparément ici si les utilisateurs sont multiples.
    }

    @Override
    public void update(Notification newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        String sql = "UPDATE Notification SET titre = ?, message = ?, date = ?, time = ?, type = ?, priorite = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getTitre().name());
            ps.setString(2, newValuesElement.getMessage());
            ps.setDate(3, Date.valueOf(newValuesElement.getDate()));
            ps.setTime(4, Time.valueOf(newValuesElement.getTime()));
            ps.setString(5, newValuesElement.getType().name());
            ps.setString(6, newValuesElement.getPriorite().name());
            ps.setLong(7, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la notification", e);
        }
    }

    @Override
    public void delete(Notification notification) {
        if (notification != null && notification.getId() != null) deleteById(notification.getId());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans Notification doit être en cascade sur Notification_Utilisateur
        String sql = "DELETE FROM Notification WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la notification par ID", e);
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Spécifiques ---

    @Override
    public List<Notification> findByUtilisateurId(Long utilisateurId) {
        String sql = "SELECT n.* FROM Notification n JOIN notification_utilisateur nu ON n.id = nu.notification_id WHERE nu.utilisateur_id = ? ORDER BY n.date DESC, n.time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des notifications par Utilisateur ID", e);
        }
        return out;
    }

    @Override
    public List<Notification> findUnreadByUtilisateurId(Long utilisateurId) {
        // Sélectionne les notifications qui n'ont pas été marquées comme lues (is_read = FALSE ou 0)
        String sql = "SELECT n.* FROM Notification n JOIN notification_utilisateur nu ON n.id = nu.notification_id WHERE nu.utilisateur_id = ? AND nu.is_read = FALSE ORDER BY n.priorite DESC, n.date DESC, n.time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des notifications non lues par Utilisateur ID", e);
        }
        return out;
    }

    @Override
    public List<Notification> findByPriorite(NotificationPriorite priorite) {
        String sql = "SELECT * FROM Notification WHERE priorite = ? ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, priorite.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des notifications par priorité", e);
        }
        return out;
    }

    @Override
    public List<Notification> findByTitre(NotificationTitre titre) {
        String sql = "SELECT * FROM Notification WHERE titre = ? ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titre.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des notifications par titre", e);
        }
        return out;
    }

    @Override
    public List<Notification> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Notification WHERE date = ? ORDER BY time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des notifications par date", e);
        }
        return out;
    }

    // --- 3. Implémentation des Opérations Métier (Lecture/Non-Lecture) ---

    @Override
    public void markAsRead(Long notificationId, Long utilisateurId) {
        // Mise à jour de la table de jointure pour marquer la notification comme lue (is_read = TRUE ou 1)
        String sql = "UPDATE notification_utilisateur SET is_read = TRUE WHERE notification_id = ? AND utilisateur_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.setLong(2, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du marquage de la notification comme lue", e);
        }
    }

    @Override
    public void markAllAsReadByUtilisateurId(Long utilisateurId) {
        // Mise à jour de toutes les lignes non lues pour un utilisateur
        String sql = "UPDATE notification_utilisateur SET is_read = TRUE WHERE utilisateur_id = ? AND is_read = FALSE";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du marquage de toutes les notifications comme lues", e);
        }
    }
}