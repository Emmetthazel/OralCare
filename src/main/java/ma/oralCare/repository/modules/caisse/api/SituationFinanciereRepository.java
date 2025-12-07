package ma.oralCare.repository.modules.caisse.api;

import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité SituationFinanciere (SF).
 * Gère les opérations de CRUD et les recherches spécifiques au suivi financier des patients.
 */
public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Basées sur le Patient) ---

    /**
     * Recherche la Situation Financière principale associée à un patient.
     * @param patientId L'ID du patient.
     * @return L'objet SituationFinanciere correspondant, s'il existe.
     */
    Optional<SituationFinanciere> findByPatientId(Long patientId);

    /**
     * Liste toutes les situations financières actives (non soldées ou avec crédit > 0).
     * @return Une liste des SituationFinanciere ayant un solde à payer.
     */
    List<SituationFinanciere> findActiveSituations();

    /**
     * Liste toutes les situations financières d'un patient.
     * (Bien que le diagramme de classes montre 1-1, en pratique, un patient pourrait avoir un historique)
     * @param patientId L'ID du patient.
     * @return Une liste de toutes les SituationFinanciere du patient.
     */
    List<SituationFinanciere> findAllByPatientId(Long patientId);

    // --- 2. Opérations Métier (Basées sur les Cas d'Utilisation) ---

    /**
     * Réinitialise (solde) une Situation Financière, mettant le crédit à 0 et le statut à 'Soldée'.
     * Cette opération correspond à l'UC "Réinitialiser un SF" dans le diagramme.
     * @param id L'ID de la SituationFinanciere à réinitialiser.
     */
    void reinitialiserSF(Long id);

    /**
     * Met à jour les totaux (totaleDesActes, totalePayé, crédit) d'une Situation Financière
     * après l'ajout/modification/suppression d'actes ou l'enregistrement d'un paiement.
     * Cette méthode sert de base pour la synchronisation après les événements de caisse.
     * @param sfId L'ID de la SituationFinanciere à mettre à jour.
     * @param montantTotalActes Le nouveau total des actes.
     * @param montantTotalPaye Le nouveau total payé.
     * @param nouveauCredit Le nouveau crédit ou solde dû.
     */
    void updateTotaux(Long sfId, Double montantTotalActes, Double montantTotalPaye, Double nouveauCredit);

    // --- 3. Méthodes de Relation (Navigation) ---

    /**
     * Récupère la liste des factures associées à cette Situation Financière.
     * @param sfId L'ID de la SituationFinanciere.
     * @return La liste des factures liées.
     */
    List<Facture> findFacturesBySituationFinanciereId(Long sfId);
}