package ma.oralCare.service.modules.notifications.test;

import ma.oralCare.entities.users.Notification;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.test.DbTestUtils;
import ma.oralCare.repository.test.DbTestUtils.Module;
import ma.oralCare.service.modules.notifications.api.NotificationService;
import ma.oralCare.service.modules.notifications.impl.NotificationServiceImpl;

import java.util.List;

/**
 * Test console simple pour le service de notifications.
 */
public class TestNotificationService {

    private final DbTestUtils dbUtils = DbTestUtils.getInstance();
    private final NotificationService notificationService = new NotificationServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST NOTIFICATION SERVICE ===");
        TestNotificationService tester = new TestNotificationService();
        tester.run();
        System.out.println("=== FIN TEST NOTIFICATION SERVICE ===");
    }

    private void run() {
        dbUtils.cleanUp(Module.NOTIFICATION_ONLY);

        // Création d'un utilisateur destinataire minimal via DbTestUtils
        Utilisateur destinataire = dbUtils.createAdminObject();
        // On ne persiste pas forcément l'utilisateur ici, le but est de tester le service.

        Notification notif = dbUtils.createNotificationObject(List.of(destinataire));
        notificationService.createNotification(notif);
        System.out.println("✅ Notification créée via service. ID = " + notif.getIdEntite());

        List<Notification> notifs = notificationService.getNotificationsForUser(destinataire.getIdEntite());
        System.out.println("Notifications pour utilisateur (id=" + destinataire.getIdEntite() + "): " + notifs.size());
    }
}


