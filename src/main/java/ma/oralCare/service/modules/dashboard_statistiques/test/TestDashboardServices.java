package ma.oralCare.service.modules.dashboard_statistiques.test;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.service.modules.dashboard_statistiques.impl.DashboardServiceImpl;
import ma.oralCare.service.modules.dashboard_statistiques.impl.StatistiquesServiceImpl;

import java.time.LocalDate;
import java.util.List;

/**
 * Test console simple pour les services de dashboard / statistiques.
 */
public class TestDashboardServices {

    private final DashboardServiceImpl dashboardService = new DashboardServiceImpl();
    private final StatistiquesServiceImpl statistiquesService = new StatistiquesServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST DASHBOARD / STATISTIQUES SERVICES ===");
        TestDashboardServices tester = new TestDashboardServices();
        tester.run();
        System.out.println("=== FIN TEST DASHBOARD / STATISTIQUES SERVICES ===");
    }

    private void run() {
        // Lecture globale des statistiques (si des données existent déjà via seed.sql)
        List<Statistiques> all = statistiquesService.getAll();
        System.out.println("Statistiques totales: " + all.size());

        List<Statistiques> forPeriod = dashboardService.getStatistiquesForPeriod(
                LocalDate.now().minusMonths(1),
                LocalDate.now()
        );
        System.out.println("Statistiques pour la période: " + forPeriod.size());
    }
}


