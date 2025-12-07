package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Consultation.
 * Gère le CRUD et les recherches spécifiques aux consultations médicales.
 */
public interface ConsultationRepository extends CrudRepository<Consultation, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Consulter Cs / Dossier) ---

    /**
     * Recherche toutes les consultations associées à un Dossier Médical spécifique.
     * @param dossierMedicaleId L'ID du Dossier Médical.
     * @return Une liste des consultations pour ce dossier, triées par date descendante.
     */
    List<Consultation> findByDossierMedicaleId(Long dossierMedicaleId);

    /**
     * Recherche les consultations par leur statut.
     * @param statut Le statut de la consultation (SCHEDULED, COMPLETED, CANCELLED).
     * @return Une liste des consultations correspondantes.
     */
    List<Consultation> findByStatut(StatutConsultation statut);

    /**
     * Recherche les consultations qui ont eu lieu à une date précise.
     * @param date La date de la consultation.
     * @return Une liste des consultations effectuées à cette date.
     */
    List<Consultation> findByDate(LocalDate date);

    // --- 2. Opérations Métier (Basées sur les UC : Terminer, Annuler, Ajouter observation) ---

    /**
     * Met à jour le statut d'une consultation (UC: Terminer une Cs, Annuler une Cs).
     * @param id L'ID de la consultation.
     * @param nouveauStatut Le nouveau statut à appliquer.
     */
    void updateStatut(Long id, StatutConsultation nouveauStatut);

    /**
     * Met à jour les observations du médecin (UC: ajouter observation).
     * @param id L'ID de la consultation.
     * @param observation Le texte de l'observation.
     */
    void updateObservation(Long id, String observation);

    // --- 3. Gestion des Interventions/Actes (InterventionMedecin est Many-to-One vers Consultation) ---

    /**
     * Ajoute une intervention médicale (acte + dent) à une consultation.
     * Nécessite le repository InterventionMedecin pour être complet, mais l'opération peut être centralisée ici.
     * @param consultationId L'ID de la consultation.
     * @param intervention L'objet InterventionMedecin à créer (doit contenir Acte et prixDePatient).
     */
    void addIntervention(Long consultationId, InterventionMedecin intervention);

}