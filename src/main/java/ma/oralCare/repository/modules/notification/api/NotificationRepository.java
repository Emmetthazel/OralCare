package ma.oralCare.repository.modules.notification.api;

import ma.oralCare.entities.users.Notification;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;


public interface NotificationRepository extends CrudRepository<Notification, Long> {


    List<Notification> findByUtilisateurId(Long utilisateurId);

    List<Notification> findUnreadByUtilisateurId(Long utilisateurId);

    List<Notification> findByPriorite(NotificationPriorite priorite);

    List<Notification> findByTitre(NotificationTitre titre);

    List<Notification> findByDate(LocalDate date);

    void markAsRead(Long notificationId, Long utilisateurId);

    void markAllAsReadByUtilisateurId(Long utilisateurId);

    boolean isNotificationReadByUser(Long notificationId, Long utilisateurId);
}