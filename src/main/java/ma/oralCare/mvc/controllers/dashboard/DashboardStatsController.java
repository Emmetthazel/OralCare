package ma.oralCare.mvc.controllers.dashboard;

import ma.oralCare.service.modules.dashboard_statistiques.api.StatistiquesService;
import ma.oralCare.mvc.ui.dashboard.DashboardView; // On importe l'interface
import javax.swing.SwingUtilities;

public class DashboardStatsController {

    private final StatistiquesService statsService;
    private final DashboardView view; // CHANGEMENT : On utilise l'interface ici

    // Le constructeur accepte maintenant n'importe quelle classe qui implémente DashboardView
    public DashboardStatsController(DashboardView view, StatistiquesService statsService) {
        this.view = view;
        this.statsService = statsService;

        refreshStats();
    }

    public void refreshStats() {
        new Thread(() -> {
            try {
                int totalPatients = statsService.getTotalPatientsCount();
                int todayVisits = statsService.getTodayVisitsCount();
                int totalAppts = statsService.getTotalApptsCount();

                SwingUtilities.invokeLater(() -> {
                    // Cette méthode est garantie par l'interface DashboardView
                    view.updateStatCards(
                            String.valueOf(totalPatients),
                            String.valueOf(todayVisits),
                            String.valueOf(totalAppts)
                    );
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}