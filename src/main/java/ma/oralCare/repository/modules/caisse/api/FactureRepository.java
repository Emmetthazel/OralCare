package ma.oralCare.repository.modules.caisse.api;

import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Facture.
 * Gère les opérations de CRUD et les recherches spécifiques aux transactions financières.
 */
public interface FactureRepository extends CrudRepository<Facture, Long> {

    // --- 1. Méthodes de Recherche (Consultation / Lister) ---

    /**
     * Recherche toutes les factures associées à une Situation Financière spécifique.
     * @param situationFinanciereId L'ID de la situation financière.
     * @return Une liste des factures associées.
     */
    List<Facture> findBySituationFinanciereId(Long situationFinanciereId);

    /**
     * Recherche toutes les factures associées à une Consultation spécifique.
     * @param consultationId L'ID de la consultation.
     * @return Une liste des factures associées.
     */
    List<Facture> findByConsultationId(Long consultationId);

    /**
     * Recherche les factures par leur statut (Payée, En attente, En retard).
     * @param statut Le statut de la facture.
     * @return Une liste des factures ayant ce statut.
     */
    List<Facture> findByStatut(StatutFacture statut);

    /**
     * Recherche les factures émises dans une période donnée.
     * @param startDate Date et heure de début de la période.
     * @param endDate Date et heure de fin de la période.
     * @return Une liste des factures émises pendant cette période.
     */
    List<Facture> findByDateFactureBetween(LocalDateTime startDate, LocalDateTime endDate);

    // --- 2. Méthodes de Gestion Transactionnelle (Paiement / Modification) ---

    /**
     * Enregistre un nouveau paiement partiel ou total pour une facture.
     * Met à jour 'totalePaye', 'reste' et potentiellement 'statut'.
     * @param factureId L'ID de la facture à mettre à jour.
     * @param montantPaye Le montant du paiement à ajouter.
     */
    Facture enregistrerPaiement(Long factureId, Double montantPaye);

    /**
     * Annule une facture en mettant son statut à un état annulé/supprimé et met à jour les montants.
     * @param factureId L'ID de la facture à annuler.
     */
    void annulerFacture(Long factureId);

    /**
     * Met à jour le total de la facture et le reste à payer (utile après une modification d'acte).
     * @param factureId L'ID de la facture.
     * @param nouveauTotalFacture Le nouveau total brut de la facture.
     * @param nouveauReste Le nouveau reste à payer après recalcul.
     */
    void updateTotaux(Long factureId, Double nouveauTotalFacture, Double nouveauReste);
}