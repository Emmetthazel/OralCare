package ma.oralCare.mvc.controllers.ordonnance.impl;

import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.mvc.controllers.ordonnance.api.OrdonnanceController;
import ma.oralCare.mvc.ui1.medecin.PrescriptionView;
import ma.oralCare.service.modules.ordonnance.api.OrdonnanceService;
import ma.oralCare.service.modules.ordonnance.impl.OrdonnanceServiceImpl;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class OrdonnanceControllerImpl implements OrdonnanceController {
    
    private final PrescriptionView view;
    private final OrdonnanceService ordonnanceService;
    
    public OrdonnanceControllerImpl(PrescriptionView view) {
        this.view = view;
        this.ordonnanceService = new OrdonnanceServiceImpl();
    }
    
    @Override
    public void refreshView() {
        // Rafraîchit la vue avec les ordonnances
        // À implémenter selon PrescriptionView
    }
    
    @Override
    public void handleCreateOrdonnance(Long consultationId, Long dossierId) {
        if (consultationId == null && dossierId == null) {
            JOptionPane.showMessageDialog(view, "Consultation ou dossier requis", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Ordonnance ordonnance = Ordonnance.builder()
                    .date(LocalDate.now())
                    .build();
            
            // TODO: Assigner consultation et dossier
            
            ordonnanceService.createOrdonnance(ordonnance);
            refreshView();
            JOptionPane.showMessageDialog(view, "Ordonnance créée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la création: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSelectOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) return;
        
        try {
            Optional<Ordonnance> ordonnanceOpt = ordonnanceService.getOrdonnanceById(ordonnanceId);
            if (ordonnanceOpt.isPresent()) {
                // Mettre à jour la vue avec l'ordonnance sélectionnée
                // À implémenter selon PrescriptionView
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleDeleteOrdonnance(Long ordonnanceId) {
        if (ordonnanceId == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous vraiment supprimer cette ordonnance ?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ordonnanceService.deleteOrdonnance(ordonnanceId);
                refreshView();
                JOptionPane.showMessageDialog(view, "Ordonnance supprimée avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la suppression: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return ordonnanceService.getOrdonnancesByDossierId(dossierId);
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        return ordonnanceService.getOrdonnancesByConsultationId(consultationId);
    }
    
    @Override
    public Optional<Ordonnance> getOrdonnanceById(Long id) {
        if (id == null) return Optional.empty();
        return ordonnanceService.getOrdonnanceById(id);
    }
    
    @Override
    public void handleAddPrescription(Long ordonnanceId) {
        // Logique pour ajouter une prescription à une ordonnance
        // À implémenter selon PrescriptionView
    }
    
    @Override
    public void handleSaveOrdonnance(Ordonnance ordonnance) {
        if (ordonnance == null) return;
        
        try {
            if (ordonnance.getIdEntite() == null) {
                ordonnanceService.createOrdonnance(ordonnance);
            } else {
                ordonnanceService.updateOrdonnance(ordonnance);
            }
            refreshView();
            JOptionPane.showMessageDialog(view, "Ordonnance enregistrée avec succès",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de l'enregistrement: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
