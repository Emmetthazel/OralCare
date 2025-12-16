package ma.oralCare.service.modules.caisse.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.dossierMedical.SituationFinanciere;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CaisseService {

    // --- Opérations liées aux Factures (Pass-through ou Orchestration) ---
    Facture enregistrerFacture(Facture facture);

    /**
     * Récupère les factures pour une période donnée.
     * Souvent utilisé pour le reporting de la caisse journalière.
     */
    List<Facture> getFacturesBetween(LocalDateTime debut, LocalDateTime fin);

    Optional<Facture> getFactureById(Long id);

    /**
     * Enregistre un paiement et doit potentiellement déclencher la mise à jour
     * de la situation financière du patient (Logique d'Orchestration).
     */
    Facture enregistrerPaiement(Long factureId, Double montantPaye);

    // --- Opérations liées à la Situation Financière ---
    Optional<SituationFinanciere> getSituationFinanciereById(Long id);
    SituationFinanciere sauverSituationFinanciere(SituationFinanciere situation);

    // (Ajoutez ici toute autre méthode qui coordonne Facture + SituationFinanciere)
}