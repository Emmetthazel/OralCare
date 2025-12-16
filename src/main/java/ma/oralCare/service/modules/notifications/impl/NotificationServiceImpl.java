package ma.oralCare.service.modules.notifications.impl;

import ma.oralCare.entities.users.Notification;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;
import ma.oralCare.repository.modules.notification.impl.NotificationRepositoryImpl;
import ma.oralCare.service.modules.notifications.api.NotificationService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implémentation simple de {@link NotificationService} basée sur {@link NotificationRepository}.
 */
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl() {
        this(new NotificationRepositoryImpl());
    }

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = Objects.requireNonNull(notificationRepository);
    }

    @Override
    public Notification createNotification(Notification notification) {
        Objects.requireNonNull(notification, "notification ne doit pas être null");
        notificationRepository.create(notification);
        return notification;
    }

    @Override
    public Optional<Notification> getNotificationById(Long id) {
        if (id == null) return Optional.empty();
        return notificationRepository.findById(id);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long utilisateurId) {
        if (utilisateurId == null) return List.of();
        return notificationRepository.findByUtilisateurId(utilisateurId);
    }

    @Override
    public void deleteNotification(Long id) {
        if (id == null) return;
        notificationRepository.deleteById(id);
    }
}


