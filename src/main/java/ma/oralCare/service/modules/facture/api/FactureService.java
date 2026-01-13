package ma.oralCare.service.modules.facture.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureStats;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service de gestion des factures pour les secrétaires
 */
public interface FactureService {

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
    Optional<Facture> findFactureById(Long id);

    /**
     * Récupère toutes les factures
     */
    List<Facture> findAllFactures();

    /**
     * Récupère les factures d'un patient
     */
    List<Facture> findFacturesByPatient(Long patientId);

    /**
     * Récupère les factures par statut
     */
    List<Facture> findFacturesByStatut(String statut);

    /**
     * Récupère les factures par date
     */
    List<Facture> findFacturesByDate(LocalDate date);

    /**
     * Récupère les factures dans une période
     */
    List<Facture> findFacturesByPeriode(LocalDate debut, LocalDate fin);

    /**
     * Récupère les factures impayées
     */
    List<Facture> findFacturesImpayees();

    /**
     * Récupère les factures payées partiellement
     */
    List<Facture> findFacturesPayeesPartiellement();

    /**
     * Récupère les factures payées complètement
     */
    List<Facture> findFacturesPayeesComplement();

    /**
     * Enregistre un paiement sur une facture
     */
    Facture enregistrerPaiement(Long factureId, PaiementRequest paiementRequest);

    /**
     * Annule une facture
     */
    Facture annulerFacture(Long id);

    /**
     * Valide une facture
     */
    Facture validerFacture(Long id);

    /**
     * Calcule le total des factures d'une période
     */
    BigDecimal calculerTotalFactures(LocalDate debut, LocalDate fin);

    /**
     * Calcule le total des paiements d'une période
     */
    BigDecimal calculerTotalPaiements(LocalDate debut, LocalDate fin);

    /**
     * Calcule le total des créances (factures impayées)
     */
    BigDecimal calculerTotalCreances();

    /**
     * Génère le numéro de facture automatiquement
     */
    String genererNumeroFacture();

    /**
     * Exporte une facture en PDF
     */
    byte[] exporterFacturePDF(Long factureId);

    /**
     * Exporte les factures en CSV
     */
    String exporterFacturesCSV(List<Facture> factures);

    /**
     * Recherche des factures par critères
     */
    List<Facture> rechercherFactures(String numero, Long patientId, String statut, LocalDate debut, LocalDate fin);

    /**
     * Compte les factures par statut
     */
    long compterFacturesParStatut(String statut);

    /**
     * Applique une remise sur une facture
     */
    Facture appliquerRemise(Long factureId, BigDecimal remise, String motif);

    /**
     * Récupère les statistiques de facturation
     */
    FactureStats getFactureStats(LocalDate debut, LocalDate fin);
}
