package ma.oralCare.mvc.controllers.consultation.impl;

import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.mvc.controllers.consultation.api.ConsultationController;
import ma.oralCare.mvc.ui1.medecin.MainFrame;
import ma.oralCare.mvc.ui1.medecin.ConsultationPanel;
import ma.oralCare.service.modules.consultation.api.ConsultationService;
import ma.oralCare.service.modules.consultation.impl.ConsultationServiceImpl;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Optional;

public class ConsultationControllerImpl implements ConsultationController {
    
    private final ConsultationPanel view;
    private final MainFrame mainFrame;
    private final ConsultationService consultationService;
    
    private Long currentConsultationId;
    private RDVPanelDTO currentRDV;
    
    public ConsultationControllerImpl(ConsultationPanel view, MainFrame mainFrame) {
        this.view = view;
        this.mainFrame = mainFrame;
        this.consultationService = new ConsultationServiceImpl();
        initEventHandlers();
    }
    
    private void initEventHandlers() {
        view.getBtnAddIntervention().addActionListener(e -> handleAddIntervention());
        view.getBtnOrdonnance().addActionListener(e -> handleNavigateToOrdonnances());
        view.getBtnCertificat().addActionListener(e -> handleNavigateToCertificats());
        view.getBtnFacture().addActionListener(e -> handleNavigateToFactures());
        view.getBtnFinish().addActionListener(e -> {
            String observations = view.getTxtObservations().getText();
            handleFinishConsultation(observations);
        });
    }
    
    @Override
    public void preparerNouvelleConsultation(RDVPanelDTO rdv) {
        this.currentRDV = rdv;
        view.chargerRendezVousActif(rdv);
        
        // Créer la consultation si nécessaire
        if (rdv.getConsultationId() == null && rdv.getDossierId() != null) {
            try {
                // TODO: Récupérer le dossier médical depuis le service
                Consultation consultation = Consultation.builder()
                        .date(LocalDate.now())
                        .statut(StatutConsultation.IN_PROGRESS)
                        .libelle("Consultation : " + rdv.getMotif())
                        .build();
                
                // consultation = consultationService.createConsultation(consultation);
                // this.currentConsultationId = consultation.getIdEntite();
                
                // Simulation pour l'instant
                this.currentConsultationId = System.currentTimeMillis();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la création de la consultation: " + e.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            this.currentConsultationId = rdv.getConsultationId();
        }
    }
    
    @Override
    public void handleFinishConsultation(String observations) {
        if (currentConsultationId == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous terminer cette consultation ?",
                "Clôture de consultation", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                consultationService.terminerConsultation(currentConsultationId, observations);
                this.currentConsultationId = null;
                mainFrame.showView("Dashboard");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de la clôture: " + e.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    public void handleAddIntervention() {
        if (ensureConsultationActive()) {
            mainFrame.showView("Actes et Soins");
        }
    }
    
    @Override
    public void handleNavigateToOrdonnances() {
        if (ensureConsultationActive()) {
            mainFrame.showView("Ordonnances");
        }
    }
    
    @Override
    public void handleNavigateToCertificats() {
        if (ensureConsultationActive()) {
            mainFrame.showView("Certificats Médicaux");
        }
    }
    
    @Override
    public void handleNavigateToFactures() {
        if (ensureConsultationActive()) {
            mainFrame.showView("Situations Financières");
        }
    }
    
    @Override
    public Long getCurrentConsultationId() {
        return currentConsultationId;
    }
    
    @Override
    public Consultation getCurrentConsultation() {
        if (currentConsultationId == null) return null;
        return consultationService.getConsultationById(currentConsultationId).orElse(null);
    }
    
    private boolean ensureConsultationActive() {
        if (currentConsultationId == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation n'est active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
