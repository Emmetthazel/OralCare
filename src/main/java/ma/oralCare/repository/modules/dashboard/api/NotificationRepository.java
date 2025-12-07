package ma.oralCare.repository.modules.notification.api;

import ma.oralCare.entities.notification.Notification;
import ma.oralCare.entities.enums.NotificationPriorite;
import ma.oralCare.entities.enums.NotificationTitre;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Notification.
 * Gère les opérations de CRUD et les requêtes spécifiques au statut de lecture par utilisateur.
 */
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    // --- 1. Méthodes de Recherche de Notifications (Typiques) ---

    /**
     * Récupère toutes les notifications destinées à un utilisateur spécifique.
     * Cette méthode doit joindre la table Utilisateur_Notification.
     * @param utilisateurId L'ID de l'utilisateur destinataire.
     * @return La liste des notifications pour cet utilisateur.
     */
    List<Notification> findByUtilisateurId(Long utilisateurId);

    /**
     * Récupère toutes les notifications non lues destinées à un utilisateur spécifique.
     * Ceci est directement lié à l'UC "Consulter notifications".
     * @param utilisateurId L'ID de l'utilisateur.
     * @return La liste des notifications non lues.
     */
    List<Notification> findUnreadByUtilisateurId(Long utilisateurId);

    /**
     * Recherche les notifications par leur priorité.
     * @param priorite La priorité (HAUTE, MOYENNE, BASSE).
     * @return La liste des notifications ayant cette priorité.
     */
    List<Notification> findByPriorite(NotificationPriorite priorite);

    /**
     * Recherche les notifications par titre (ex: Rappel de rendez-vous).
     * @param titre Le titre de l'énumération.
     * @return La liste des notifications.
     */
    List<Notification> findByTitre(NotificationTitre titre);

    /**
     * Recherche les notifications pour une date donnée.
     * @param date La date d'émission.
     * @return La liste des notifications de cette date.
     */
    List<Notification> findByDate(LocalDate date);

    // --- 2. Opérations Métier (Basées sur l'état de lecture) ---

    /**
     * Marque une notification spécifique comme lue pour un utilisateur donné.
     * Ceci est lié à l'UC "Marquer notification lue".
     * @param notificationId L'ID de la notification.
     * @param utilisateurId L'ID de l'utilisateur.
     */
    void markAsRead(Long notificationId, Long utilisateurId);

    /**
     * Marque toutes les notifications non lues d'un utilisateur comme lues.
     * @param utilisateurId L'ID de l'utilisateur.
     */
    void markAllAsReadByUtilisateurId(Long utilisateurId);
}