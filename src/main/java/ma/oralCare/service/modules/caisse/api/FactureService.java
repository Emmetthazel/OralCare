package ma.oralCare.service.modules.caisse.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.enums.StatutFacture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FactureService {

    // Opération de création simple
    Facture createFacture(Facture facture);

    // Récupération par identifiant
    Optional<Facture> getFactureById(Long id);

    // Requêtes de recherche métier
    List<Facture> getFacturesBySituationFinanciere(Long situationId);
    List<Facture> getFacturesByConsultation(Long consultationId);
    List<Facture> getFacturesByStatut(StatutFacture statut);
    List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end);

    // Opérations métier
    Facture enregistrerPaiement(Long factureId, Double montantPaye);
    void annulerFacture(Long factureId);

    /**
     * Met à jour les totaux d'une facture.
     * Cette méthode est souvent appelée par d'autres services après une modification.
     */
    void updateTotaux(Long factureId, Double total, Double reste);
}