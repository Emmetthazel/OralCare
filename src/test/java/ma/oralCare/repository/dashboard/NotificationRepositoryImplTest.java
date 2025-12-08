package ma.oralCare.repository.dashboard;

import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.entities.enums.NotificationType;
import ma.oralCare.entities.notification.Notification;
import ma.oralCare.entities.staff.Utilisateur;
import ma.oralCare.repository.modules.dashboard.impl.NotificationRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationRepositoryImplTest {

    private NotificationRepositoryImpl repository;
    private Utilisateur testUtilisateur;

    @BeforeAll
    void setup() {
        repository = new NotificationRepositoryImpl();

        // Création d'un utilisateur test
        testUtilisateur = Utilisateur.builder()
                .id(1L) // Si vous avez un repository pour créer l'utilisateur, créez-le d'abord
                .build();
    }

    @Test
    void testCreateAndFindById() {
        Notification notif = Notification.builder()
                .titre(NotificationTitre.NEW_MESSAGE)
                .message("Message test")
                .date(LocalDate.now())
                .time(LocalTime.now())
                .type(NotificationType.INFO)
                .priorite(NotificationPriorite.HIGH)
                .build();

        repository.create(notif);
        assertNotNull(notif.getId(), "L'ID doit être généré");

        Notification fetched = repository.findById(notif.getId());
        assertNotNull(fetched);
        assertEquals("Message test", fetched.getMessage());
        assertEquals(NotificationTitre.NEW_MESSAGE, fetched.getTitre());
    }

    @Test
    void testUpdate() {
        Notification notif = repository.findAll().get(0);
        notif.setMessage("Message mis à jour");
        repository.update(notif);

        Notification updated = repository.findById(notif.getId());
        assertEquals("Message mis à jour", updated.getMessage());
    }

    @Test
    void testDelete() {
        Notification notif = repository.findAll().get(0);
        Long id = notif.getId();
        repository.delete(notif);

        Notification deleted = repository.findById(id);
        assertNull(deleted, "La notification doit être supprimée");
    }

    @Test
    void testFindByPriorite() {
        List<Notification> list = repository.findByPriorite(NotificationPriorite.HIGH);
        assertNotNull(list);
        for (Notification n : list) {
            assertEquals(NotificationPriorite.HIGH, n.getPriorite());
        }
    }

    @Test
    void testFindByTitre() {
        List<Notification> list = repository.findByTitre(NotificationTitre.NEW_MESSAGE);
        assertNotNull(list);
        for (Notification n : list) {
            assertEquals(NotificationTitre.NEW_MESSAGE, n.getTitre());
        }
    }

    @Test
    void testFindByDate() {
        LocalDate today = LocalDate.now();
        List<Notification> list = repository.findByDate(today);
        assertNotNull(list);
        for (Notification n : list) {
            assertEquals(today, n.getDate());
        }
    }

    @Test
    void testFindByUtilisateurId() {
        List<Notification> list = repository.findByUtilisateurId(testUtilisateur.getId());
        assertNotNull(list);
        // Chaque notification doit appartenir à l'utilisateur testé
        for (Notification n : list) {
            assertNotNull(n);
        }
    }

    @Test
    void testMarkAsReadAndMarkAllAsRead() {
        Notification notif = repository.findAll().get(0);
        repository.markAsRead(notif.getId(), testUtilisateur.getId());

        List<Notification> unread = repository.findUnreadByUtilisateurId(testUtilisateur.getId());
        assertTrue(unread.stream().noneMatch(n -> n.getId().equals(notif.getId())));

        // Tester markAllAsRead
        repository.markAllAsReadByUtilisateurId(testUtilisateur.getId());
        List<Notification> stillUnread = repository.findUnreadByUtilisateurId(testUtilisateur.getId());
        assertTrue(stillUnread.isEmpty());
    }
}
