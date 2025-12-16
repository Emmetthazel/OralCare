package ma.oralCare.service.modules.notifications.api;

import ma.oralCare.entities.users.Notification;

import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion des notifications (création, lecture, suppression).
 */
public interface NotificationService {

    Notification createNotification(Notification notification);

    Optional<Notification> getNotificationById(Long id);

    List<Notification> getNotificationsForUser(Long utilisateurId);

    void deleteNotification(Long id);
}


