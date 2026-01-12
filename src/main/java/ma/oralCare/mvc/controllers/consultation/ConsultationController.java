package ma.oralCare.mvc.controllers.consultation;

import ma.oralCare.mvc.ui1.medecin.MainFrame;
import ma.oralCare.mvc.ui1.medecin.ConsultationPanel;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ConsultationController {

    private final ConsultationPanel view;
    private final MainFrame mainFrame;

    // État de la session active
    private Long currentConsultationId;
    private RDVPanelDTO currentRDV;

    public ConsultationController(ConsultationPanel view, MainFrame mainFrame) {
        this.view = view;
        this.mainFrame = mainFrame;
        initEventHandlers();
    }

    private void initEventHandlers() {
        // Logique pour ajouter une intervention (redirige vers l'onglet Actes & Soins)
        // car la table des interventions est plus complète dans l'interface dédiée
        view.getBtnAddIntervention().addActionListener(e -> {
            if (ensureConsultationActive()) {
                mainFrame.showView("Actes et Soins");
            }
        });

        // Navigation vers les Ordonnances
        view.getBtnOrdonnance().addActionListener(e -> {
            if (ensureConsultationActive()) {
                mainFrame.showView("Ordonnances");
            }
        });

        // Navigation vers les Certificats
        view.getBtnCertificat().addActionListener(e -> {
            if (ensureConsultationActive()) {
                mainFrame.showView("Certificats");
            }
        });

        // Navigation vers la Facture
        view.getBtnFacture().addActionListener(e -> {
            if (ensureConsultationActive()) {
                mainFrame.showView("Situations Financières");
            }
        });

        // Terminer la Consultation
        view.getBtnFinish().addActionListener(this::handleFinishConsultation);
    }

    /**
     * Appelé lorsqu'un médecin clique sur "Consulter" depuis la liste des RDV.
     * C'est ici que la logique métier démarre vraiment.
     */
    public void preparerNouvelleConsultation(RDVPanelDTO rdv) {
        this.currentRDV = rdv;

        // 1. Mise à jour de l'UI
        view.chargerRendezVousActif(rdv);

        // 2. Logique Métier : Création de la consultation en BDD
        // Dans un cas réel, on appellerait un service :
        // this.currentConsultationId = consultationService.creerDepuisRDV(rdv.getId());

        this.currentConsultationId = System.currentTimeMillis(); // Simulation d'ID

        System.out.println("[CONTROLLER-CS] Consultation n°" + currentConsultationId + " ouverte pour le dossier " + rdv.getDossierId());
    }

    private boolean ensureConsultationActive() {
        if (currentConsultationId == null) {
            JOptionPane.showMessageDialog(view, "Aucune consultation n'est active.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleFinishConsultation(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(view,
                "Voulez-vous terminer cette consultation ? \nCela enregistrera les observations et clôturera les actes.",
                "Clôture de consultation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Logique métier : UPDATE Consultation SET statut = 'COMPLETED', observation_medecin = ...
            String observations = view.getTxtObservations().getText();

            System.out.println("[BDD] Clôture de la consultation " + currentConsultationId);

            // On réinitialise et on retourne au Dashboard
            this.currentConsultationId = null;
            mainFrame.showView("Dashboard");
        }
    }

    // Getters pour l'ID actif (utilisé par MainFrame pour synchroniser les autres vues)
    public Long getCurrentConsultationId() { return currentConsultationId; }
}