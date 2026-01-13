package ma.oralCare.mvc.controllers.situation.impl;

import ma.oralCare.entities.dossierMedical.SituationFinanciere;
import ma.oralCare.mvc.controllers.situation.api.SituationFinanciereController;
import ma.oralCare.mvc.ui1.medecin.FinancialSituationView;
import ma.oralCare.service.modules.caisse.api.SituationFinanciereService;
import ma.oralCare.service.modules.caisse.impl.SituationFinanciereServiceImpl;

import javax.swing.*;
import java.util.Optional;

public class SituationFinanciereControllerImpl implements SituationFinanciereController {
    
    private final FinancialSituationView view;
    private final SituationFinanciereService situationService;
    
    public SituationFinanciereControllerImpl(FinancialSituationView view) {
        this.view = view;
        this.situationService = new SituationFinanciereServiceImpl();
    }
    
    @Override
    public void refreshView(Long dossierId) {
        if (dossierId == null) return;
        
        try {
            // Récupérer la situation financière via le dossier
            // À implémenter selon FinancialSituationView
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public Optional<SituationFinanciere> getSituationByDossierId(Long dossierId) {
        // À implémenter selon la structure de données
        return Optional.empty();
    }
    
    @Override
    public Double calculerSoldePatient(Long patientId) {
        if (patientId == null) return 0.0;
        return situationService.calculerSoldePatient(patientId);
    }
    
    @Override
    public void handleUpdateSituation(SituationFinanciere situation) {
        if (situation == null) return;
        
        try {
            situationService.sauverSituationFinanciere(situation);
            JOptionPane.showMessageDialog(view, "Situation financière mise à jour avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la mise à jour: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleRefreshSituation(Long dossierId) {
        refreshView(dossierId);
    }
}
