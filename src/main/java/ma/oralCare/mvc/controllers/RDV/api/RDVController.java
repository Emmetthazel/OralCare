package ma.oralCare.mvc.controllers.RDV.api;

import java.time.LocalDate;

/**
 * Interface définissant les actions possibles depuis l'interface des Rendez-vous.
 * Fait le pont entre l'UI (RDVPanel) et la couche métier (RDVService).
 */
public interface RDVController {

    /**
     * Rafraîchit la vue pour la date actuellement sélectionnée (généralement aujourd'hui).
     */
    void refreshView();

    /**
     * Filtre les rendez-vous pour la semaine en cours.
     */
    void handleFilterWeek();

    /**
     * Filtre les rendez-vous pour le mois en cours.
     */
    void handleFilterMonth();

    /**
     * Filtre les données affichées selon le statut (CONFIRMED, PENDING, etc.).
     * @param statut Le libellé du statut sélectionné dans la JComboBox.
     */
    void handleFilterStatut(String statut);

    /**
     * Déclenche le passage d'un rendez-vous à l'état "En cours" et crée la consultation.
     * @param rdvId L'identifiant unique du rendez-vous.
     */
    void handleDemarrerConsultation(Long rdvId);

    /**
     * Annule un rendez-vous sélectionné.
     * @param rdvId L'identifiant unique du rendez-vous.
     */
    void handleAnnulerRDV(Long rdvId);

    /**
     * Change la date de filtrage via un sélecteur de date (si implémenté).
     * @param date La nouvelle date choisie.
     */
    void handleDateChange(LocalDate date);

    void handleOuvrirConsultation(Long rdvId);
}