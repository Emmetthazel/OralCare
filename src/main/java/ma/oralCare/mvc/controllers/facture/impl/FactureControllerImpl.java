package ma.oralCare.mvc.controllers.facture.impl;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.controllers.facture.api.FactureController;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;
import ma.oralCare.service.modules.facture.dto.FactureStats;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du contrôleur pour la gestion des factures
 */
public class FactureControllerImpl implements FactureController {

    private final ma.oralCare.service.modules.facture.api.FactureService factureService;
    private Object view;

    public FactureControllerImpl(ma.oralCare.service.modules.facture.api.FactureService factureService) {
        this.factureService = factureService;
    }

    @Override
    public Facture createFacture(FactureCreateRequest request) {
        try {
            return factureService.createFacture(request);
        } catch (Exception e) {
            showError("Erreur lors de la création de la facture: " + e.getMessage());
            throw new RuntimeException("Erreur création facture", e);
        }
    }

    @Override
    public Facture updateFacture(Long id, FactureUpdateRequest request) {
        try {
            return factureService.updateFacture(id, request);
        } catch (Exception e) {
            showError("Erreur lors de la mise à jour de la facture: " + e.getMessage());
            throw new RuntimeException("Erreur mise à jour facture", e);
        }
    }

    @Override
    public void deleteFacture(Long id) {
        try {
            factureService.deleteFacture(id);
        } catch (Exception e) {
            showError("Erreur lors de la suppression de la facture: " + e.getMessage());
            throw new RuntimeException("Erreur suppression facture", e);
        }
    }

    @Override
    public Optional<Facture> getFactureById(Long id) {
        try {
            return factureService.findFactureById(id);
        } catch (Exception e) {
            showError("Erreur lors de la récupération de la facture: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Facture> getAllFactures() {
        try {
            return factureService.findAllFactures();
        } catch (Exception e) {
            showError("Erreur lors de la récupération des factures: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Facture> getFacturesByPatient(Long patientId) {
        try {
            return factureService.findFacturesByPatient(patientId);
        } catch (Exception e) {
            showError("Erreur lors de la récupération des factures du patient: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Facture> getFacturesByStatut(String statut) {
        try {
            return factureService.findFacturesByStatut(statut);
        } catch (Exception e) {
            showError("Erreur lors de la récupération des factures par statut: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Facture> getFacturesByPeriode(LocalDate debut, LocalDate fin) {
        try {
            return factureService.findFacturesByPeriode(debut, fin);
        } catch (Exception e) {
            showError("Erreur lors de la récupération des factures de la période: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Facture enregistrerPaiement(Long factureId, PaiementRequest paiementRequest) {
        try {
            return factureService.enregistrerPaiement(factureId, paiementRequest);
        } catch (Exception e) {
            showError("Erreur lors de l'enregistrement du paiement: " + e.getMessage());
            throw new RuntimeException("Erreur enregistrement paiement", e);
        }
    }

    @Override
    public Facture annulerFacture(Long id) {
        try {
            return factureService.annulerFacture(id);
        } catch (Exception e) {
            showError("Erreur lors de l'annulation de la facture: " + e.getMessage());
            throw new RuntimeException("Erreur annulation facture", e);
        }
    }

    @Override
    public byte[] exporterFacturePDF(Long factureId) {
        try {
            return factureService.exporterFacturePDF(factureId);
        } catch (Exception e) {
            showError("Erreur lors de l'exportation PDF: " + e.getMessage());
            return new byte[0];
        }
    }

    @Override
    public String exporterFacturesCSV(List<Facture> factures) {
        try {
            return factureService.exporterFacturesCSV(factures);
        } catch (Exception e) {
            showError("Erreur lors de l'exportation CSV: " + e.getMessage());
            return "";
        }
    }

    @Override
    public FactureStats getFactureStats(LocalDate debut, LocalDate fin) {
        try {
            return factureService.getFactureStats(debut, fin);
        } catch (Exception e) {
            showError("Erreur lors de la récupération des statistiques: " + e.getMessage());
            return new FactureStats();
        }
    }

    @Override
    public void refreshFacturePanel() {
        if (view != null) {
            // Implémenter la logique de rafraîchissement de la vue
            System.out.println("[FACTURE-CONTROLLER] Rafraîchissement du panel factures demandé");
        }
    }

    @Override
    public void setView(Object view) {
        this.view = view;
    }
    
    @Override
    public List<Patient> getPatients() {
        try {
            // Pour l'instant, retourner une liste vide
            // TODO: Implémenter la récupération des patients via le service approprié
            return List.of();
        } catch (Exception e) {
            showError("Erreur lors de la récupération des patients: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        System.err.println("[FACTURE-CONTROLLER] ERROR: " + message);
        if (view != null) {
            // Implémenter l'affichage dans la vue si nécessaire
        }
    }

    /**
     * Affiche un message d'information
     */
    private void showInfo(String message) {
        System.out.println("[FACTURE-CONTROLLER] INFO: " + message);
        if (view != null) {
            // Implémenter l'affichage dans la vue si nécessaire
        }
    }
}
