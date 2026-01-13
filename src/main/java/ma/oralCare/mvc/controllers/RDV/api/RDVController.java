package ma.oralCare.mvc.controllers.RDV.api;

import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.service.modules.RDV.dto.RDVCreateRequest;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;
import ma.oralCare.service.modules.RDV.dto.RDVUpdateRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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
     * Confirme un rendez-vous sélectionné.
     * @param rdvId L'identifiant unique du rendez-vous.
     */
    void handleConfirmerRDV(Long rdvId);

    /**
     * Supprime un rendez-vous sélectionné.
     * @param rdvId L'identifiant unique du rendez-vous.
     */
    void deleteRDV(Long rdvId);

    List<RDVPanelDTO> chargerPlanning(LocalDate date, Long medecinId);

    void demarrerSeance(Long rdvId);

    void confirmerRendezVous(Long rdvId);

    void annulerRendezVous(Long rdvId);
    /**
     * Change la date de filtrage via un sélecteur de date (si implémenté).
     * @param date La nouvelle date choisie.
     */
    void handleDateChange(LocalDate date);

    void handleOuvrirConsultation(Long rdvId);

    // CRUD methods for RendezVousPanel
    
    /**
     * Récupère les rendez-vous pour une période donnée.
     * @param debut Date de début
     * @param fin Date de fin
     * @return Liste des rendez-vous
     */
    List<RDV> getRDVsByPeriode(LocalDate debut, LocalDate fin);

    /**
     * Crée un nouveau rendez-vous.
     * @param request Données du rendez-vous à créer
     * @return Le rendez-vous créé
     */
    RDV createRDV(RDVCreateRequest request);

    /**
     * Met à jour un rendez-vous existant.
     * @param rdvId Identifiant du rendez-vous
     * @param request Données de mise à jour
     * @return Le rendez-vous mis à jour
     */
    RDV updateRDV(Long rdvId, RDVUpdateRequest request);

    /**
     * Annule un rendez-vous.
     * @param rdvId Identifiant du rendez-vous à annuler
     */
    void cancelRDV(Long rdvId);

    /**
     * Confirme un rendez-vous.
     * @param rdvId Identifiant du rendez-vous à confirmer
     */
    void confirmRDV(Long rdvId);

    /**
     * Récupère les créneaux horaires disponibles pour une date et un médecin donnés.
     * @param date Date pour laquelle on veut les créneaux
     * @param medecinId Identifiant du médecin
     * @return Liste des créneaux horaires disponibles
     */
    List<LocalTime> getAvailableTimeSlots(LocalDate date, Long medecinId);
}