package ma.oralCare.mvc.controllers.RDV.impl;

import ma.oralCare.mvc.controllers.RDV.api.RDVController;
import ma.oralCare.mvc.ui1.medecin.MainFrame;
import ma.oralCare.mvc.ui1.medecin.RDVPanel;
import ma.oralCare.service.modules.RDV.api.RDVService;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

public class RDVControllerImpl implements RDVController {

    private final RDVService rdvService;
    private final RDVPanel view;
    private final Long medecinId;
    private LocalDate currentDateFilter;
    private List<RDVPanelDTO> lastLoadedData; // Cache pour le filtrage par statut

    public RDVControllerImpl(RDVService rdvService, RDVPanel view, Long medecinId) {
        this.rdvService = rdvService;
        this.view = view;
        this.medecinId = medecinId;
        this.currentDateFilter = LocalDate.now();
        refreshView();
    }

    @Override
    public void refreshView() {
        this.currentDateFilter = LocalDate.now(); // Reset à aujourd'hui
        loadDataAndNotify(currentDateFilter);
        view.updateDateLabel(currentDateFilter);
    }

    @Override
    public void handleFilterWeek() {
        // Calcul du premier jour de la semaine (Lundi)
        LocalDate startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        System.out.println("[CONTROLLER] Filtrage semaine à partir du : " + startOfWeek);

        // On charge les données (Votre service peut être adapté pour gérer une plage de dates)
        loadDataAndNotify(startOfWeek);
        view.updateDateLabel(startOfWeek);
    }

    @Override
    public void handleFilterMonth() {
        LocalDate startOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        System.out.println("[CONTROLLER] Filtrage mois à partir du : " + startOfMonth);

        loadDataAndNotify(startOfMonth);
        view.updateDateLabel(startOfMonth);
    }

    @Override
    public void handleFilterStatut(String statut) {
        if (lastLoadedData == null) return;

        System.out.println("[CONTROLLER] Filtrage par statut : " + statut);

        if ("Tous".equalsIgnoreCase(statut)) {
            view.updateTable(lastLoadedData);
        } else {
            List<RDVPanelDTO> filtered = lastLoadedData.stream()
                    .filter(rdv -> rdv.getStatut().equalsIgnoreCase(statut))
                    .collect(Collectors.toList());
            view.updateTable(filtered);
        }
    }

    @Override
    public void handleDemarrerConsultation(Long rdvId) {
        if (rdvId == null) return;

        try {
            // 1. Chercher le DTO correspondant dans les données déjà chargées
            RDVPanelDTO selectedRDV = lastLoadedData.stream()
                    .filter(rdv -> rdv.getRdvId().equals(rdvId))
                    .findFirst()
                    .orElse(null);

            // 2. Action Métier : Mise à jour en base de données
            rdvService.demarrerSeance(rdvId);

            // 3. Navigation et Remplissage de l'UI
            Window parentWindow = SwingUtilities.getWindowAncestor(view);
            if (parentWindow instanceof MainFrame) {
                MainFrame main = (MainFrame) parentWindow;

                SwingUtilities.invokeLater(() -> {
                    // ✅ ACTION CRUCIALE : On envoie les données au panel AVANT de l'afficher
                    if (selectedRDV != null) {
                        main.getConsultationPanel().chargerRendezVousActif(selectedRDV);
                    }

                    // Affichage de la vue
                    main.showView("Consultation");
                });

            }
        } catch (Exception e) {
            System.err.println("[ERREUR] " + e.getMessage());
            JOptionPane.showMessageDialog(view, "Erreur : " + e.getMessage());
        }
    }

    @Override
    public void handleAnnulerRDV(Long rdvId) {
        if (rdvId == null) return;
        int confirm = JOptionPane.showConfirmDialog(view, "Annuler ce rendez-vous ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                rdvService.annulerRendezVous(rdvId);
                refreshView();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, "Erreur lors de l'annulation.");
            }
        }
    }

    @Override
    public void handleDateChange(LocalDate date) {
        if (date != null) {
            this.currentDateFilter = date;
            loadDataAndNotify(date);
            view.updateDateLabel(date);
        }
    }

    /**
     * Méthode privée utilitaire pour centraliser le chargement et la mise à jour UI
     */
    private void loadDataAndNotify(LocalDate date) {
        try {
            if (medecinId == null) return;

            // Récupération via le service
            this.lastLoadedData = rdvService.chargerPlanning(date, medecinId);

            SwingUtilities.invokeLater(() -> {
                view.updateTable(lastLoadedData);
            });
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Erreur de chargement : " + e.getMessage());
        }
    }

    @Override
    public void handleOuvrirConsultation(Long rdvId) {
        if (rdvId == null) return;

        // 1. Trouver les données dans le cache
        RDVPanelDTO selectedRDV = lastLoadedData.stream()
                .filter(rdv -> rdv.getRdvId().equals(rdvId))
                .findFirst()
                .orElse(null);

        // 2. Accéder à la MainFrame
        Window parentWindow = SwingUtilities.getWindowAncestor(view);
        if (parentWindow instanceof MainFrame && selectedRDV != null) {
            MainFrame main = (MainFrame) parentWindow;

            // 3. Charger les données et afficher
            main.getConsultationPanel().chargerRendezVousActif(selectedRDV);
            main.showView("Consultation");
        }
    }
}