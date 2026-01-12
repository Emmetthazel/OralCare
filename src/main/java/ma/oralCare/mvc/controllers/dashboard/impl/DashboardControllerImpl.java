package ma.oralCare.mvc.controllers.dashboard.impl;

import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.mvc.ui1.medecin.DashboardPanel;
import ma.oralCare.service.modules.dashboard.api.DashboardService;
import ma.oralCare.service.modules.dashboard.dto.DashboardDTO;

import javax.swing.SwingUtilities;
import java.time.LocalDate;

public class DashboardControllerImpl implements DashboardController {

    private final DashboardPanel view;
    private final DashboardService dashboardService;
    private final String userLogin; // ✅ Ajout du login utilisateur

    // ✅ Mise à jour du constructeur pour recevoir le login
    public DashboardControllerImpl(DashboardPanel view, DashboardService dashboardService, String userLogin) {
        this.view = view;
        this.dashboardService = dashboardService;
        this.userLogin = userLogin;

        // Lancement du premier chargement au démarrage
        refreshData();
    }

    @Override
    public void refreshData() {
        new Thread(() -> {
            try {
                // ✅ CORRECTION : Passage des deux arguments requis (Date et Login)
                DashboardDTO data = dashboardService.getDashboardData(LocalDate.now(), userLogin);

                SwingUtilities.invokeLater(() -> {
                    if (data != null) {
                        view.updateUIWithData(data);
                    }
                });
            } catch (Exception e) {
                System.err.println("Erreur lors du rafraîchissement du Dashboard : " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public DashboardPanel getView() {
        return this.view;
    }
}