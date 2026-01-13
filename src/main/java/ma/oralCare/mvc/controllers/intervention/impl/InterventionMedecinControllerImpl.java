package ma.oralCare.mvc.controllers.intervention.impl;

import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.mvc.controllers.intervention.api.InterventionMedecinController;
import ma.oralCare.mvc.ui1.medecin.TreatmentView;
import ma.oralCare.service.modules.intervention.api.InterventionMedecinService;
import ma.oralCare.service.modules.intervention.impl.InterventionMedecinServiceImpl;

import javax.swing.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class InterventionMedecinControllerImpl implements InterventionMedecinController {
    
    private final TreatmentView view;
    private final InterventionMedecinService interventionService;
    
    public InterventionMedecinControllerImpl(TreatmentView view) {
        this.view = view;
        this.interventionService = new InterventionMedecinServiceImpl();
    }
    
    @Override
    public void refreshView(Long consultationId) {
        if (consultationId == null) return;
        
        try {
            List<InterventionMedecin> interventions = interventionService.getInterventionsByConsultationId(consultationId);
            // Mettre à jour la vue avec les interventions
            // À implémenter selon TreatmentView
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleAddIntervention(Long consultationId, Long acteId, Integer numDent, BigDecimal prixDePatient) {
        if (consultationId == null || acteId == null) {
            JOptionPane.showMessageDialog(view, "Consultation et acte requis", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            InterventionMedecin intervention = InterventionMedecin.builder()
                    .numDent(numDent)
                    .prixDePatient(prixDePatient != null ? prixDePatient : BigDecimal.ZERO)
                    .build();
            
            // TODO: Assigner consultation et acte
            
            interventionService.createIntervention(intervention);
            refreshView(consultationId);
            JOptionPane.showMessageDialog(view, "Intervention ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de l'ajout: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleDeleteIntervention(Long interventionId) {
        if (interventionId == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer cette intervention ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                interventionService.deleteIntervention(interventionId);
                // Rafraîchir la vue
                // refreshView(consultationId);
                JOptionPane.showMessageDialog(view, "Intervention supprimée avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void handleUpdateIntervention(InterventionMedecin intervention) {
        if (intervention == null || intervention.getIdEntite() == null) return;
        
        try {
            interventionService.updateIntervention(intervention);
            // refreshView(consultationId);
            JOptionPane.showMessageDialog(view, "Intervention mise à jour avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la mise à jour: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        return interventionService.getInterventionsByConsultationId(consultationId);
    }
    
    @Override
    public Optional<InterventionMedecin> getInterventionById(Long id) {
        if (id == null) return Optional.empty();
        return interventionService.getInterventionById(id);
    }
    
    @Override
    public Double calculateTotalPriceByConsultationId(Long consultationId) {
        if (consultationId == null) return 0.0;
        return interventionService.calculateTotalPriceByConsultationId(consultationId);
    }
}
