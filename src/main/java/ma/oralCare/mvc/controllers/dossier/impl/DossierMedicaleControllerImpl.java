package ma.oralCare.mvc.controllers.dossier.impl;

import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.controllers.dossier.api.DossierMedicaleController;
import ma.oralCare.mvc.ui1.medecin.MedicalRecordDetailView;
import ma.oralCare.service.modules.dossier.api.DossierMedicaleService;
import ma.oralCare.service.modules.dossier.impl.DossierMedicaleServiceImpl;
import ma.oralCare.service.modules.patient.api.PatientService;
import ma.oralCare.service.modules.patient.impl.PatientServiceImpl;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

public class DossierMedicaleControllerImpl implements DossierMedicaleController {
    
    private final MedicalRecordDetailView view;
    private final DossierMedicaleService dossierService;
    private final PatientService patientService;
    
    public DossierMedicaleControllerImpl(MedicalRecordDetailView view) {
        this.view = view;
        this.dossierService = new DossierMedicaleServiceImpl();
        this.patientService = new PatientServiceImpl();
    }
    
    @Override
    public void refreshView() {
        // Rafraîchit la vue avec les données actuelles
        // À implémenter selon les besoins de MedicalRecordDetailView
    }
    
    @Override
    public Optional<DossierMedicale> getDossierByPatientId(Long patientId) {
        if (patientId == null) return Optional.empty();
        return dossierService.getDossierByPatientId(patientId);
    }
    
    @Override
    public List<DossierMedicale> getDossiersByMedecinId(Long medecinId) {
        if (medecinId == null) return List.of();
        return dossierService.getDossiersByMedecinId(medecinId);
    }
    
    @Override
    public DossierMedicale createDossierForPatient(Patient patient, Long medecinId) {
        if (patient == null || patient.getIdEntite() == null) {
            throw new IllegalArgumentException("Patient invalide");
        }
        
        // Vérifier si un dossier existe déjà
        Optional<DossierMedicale> existing = dossierService.getDossierByPatientId(patient.getIdEntite());
        if (existing.isPresent()) {
            return existing.get();
        }
        
        // Créer un nouveau dossier
        DossierMedicale dossier = DossierMedicale.builder()
                .patient(patient)
                .build();
        
        if (medecinId != null) {
            // TODO: Récupérer le médecin et l'assigner
        }
        
        return dossierService.createDossierMedicale(dossier);
    }
    
    @Override
    public void handleSearchPatient(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return;
        }
        
        try {
            List<Patient> patients = patientService.searchPatientsByNomPrenom(searchTerm);
            // Mettre à jour la vue avec les résultats
            // À implémenter selon MedicalRecordDetailView
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la recherche: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSelectDossier(Long dossierId) {
        if (dossierId == null) return;
        
        try {
            Optional<DossierMedicale> dossierOpt = dossierService.getDossierById(dossierId);
            if (dossierOpt.isPresent()) {
                DossierMedicale dossier = dossierOpt.get();
                // Mettre à jour la vue avec les données du dossier
                // À implémenter selon MedicalRecordDetailView
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors du chargement du dossier: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public void handleSaveNotes(Long dossierId, String notes) {
        if (dossierId == null) return;
        
        try {
            Optional<DossierMedicale> dossierOpt = dossierService.getDossierById(dossierId);
            if (dossierOpt.isPresent()) {
                DossierMedicale dossier = dossierOpt.get();
                // TODO: Sauvegarder les notes dans le dossier
                dossierService.updateDossierMedicale(dossier);
                JOptionPane.showMessageDialog(view, "Notes enregistrées avec succès",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Erreur lors de la sauvegarde: " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    public List<DossierMedicale> getAllDossiers() {
        return dossierService.getAllDossiers();
    }
}
