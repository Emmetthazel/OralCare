package ma.oralCare.mvc.controllers.facture.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;
import ma.oralCare.service.modules.facture.dto.FactureStats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour la gestion des factures par les secrétaires
 */
public interface FactureController {

    /**
     * Crée une nouvelle facture
     */
    Facture createFacture(FactureCreateRequest request);

    /**
     * Met à jour une facture existante
     */
    Facture updateFacture(Long id, FactureUpdateRequest request);

    /**
     * Supprime une facture
     */
    void deleteFacture(Long id);

    /**
     * Récupère une facture par son ID
     */
    Optional<Facture> getFactureById(Long id);

    /**
     * Récupère toutes les factures
     */
    List<Facture> getAllFactures();

    /**
     * Récupère les factures d'un patient
     */
    List<Facture> getFacturesByPatient(Long patientId);

    /**
     * Récupère les factures par statut
     */
    List<Facture> getFacturesByStatut(String statut);

    /**
     * Récupère les factures d'une période
     */
    List<Facture> getFacturesByPeriode(LocalDate debut, LocalDate fin);

    /**
     * Enregistre un paiement sur une facture
     */
    Facture enregistrerPaiement(Long factureId, PaiementRequest paiementRequest);

    /**
     * Annule une facture
     */
    Facture annulerFacture(Long id);

    /**
     * Exporte une facture en PDF
     */
    byte[] exporterFacturePDF(Long factureId);

    /**
     * Exporte les factures en CSV
     */
    String exporterFacturesCSV(List<Facture> factures);

    /**
     * Récupère les statistiques de facturation
     */
    FactureStats getFactureStats(LocalDate debut, LocalDate fin);

    /**
     * Rafraîchit les données du panel factures
     */
    void refreshFacturePanel();

    /**
     * Définit la vue associée (pour le pattern MVC)
     */
    void setView(Object view);
    
    /**
     * Récupère la liste des patients pour les factures
     */
    List<Patient> getPatients();
}
