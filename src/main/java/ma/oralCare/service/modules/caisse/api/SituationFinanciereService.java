package ma.oralCare.service.modules.caisse.api;

import ma.oralCare.entities.dossierMedical.SituationFinanciere;

import java.util.Optional;

public interface SituationFinanciereService {

    Optional<SituationFinanciere> getSituationFinanciereById(Long id);

    SituationFinanciere sauverSituationFinanciere(SituationFinanciere situation);

    /**
     * Calcule le solde total dû par un patient.
     * @param patientId L'ID du patient.
     * @return Le montant restant à payer.
     */
    Double calculerSoldePatient(Long patientId);

    /**
     * Met à jour le solde d'une SituationFinanciere après un événement (paiement, nouvelle facture).
     * @param situationId L'ID de la situation à mettre à jour.
     */
    void mettreAJourSolde(Long situationId);
}