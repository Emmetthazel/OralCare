/*package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.controllers.agenda.AgendaController;
import ma.oralCare.mvc.controllers.dashboard.DashboardStatsController;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.service.modules.agenda.impl.AgendaServiceImpl;
import ma.oralCare.service.modules.dashboard_statistiques.impl.StatistiquesServiceImpl;

import javax.swing.*;

public class SecretaireDashboard extends BaseDashboard {

    private static final String[] NAV_ITEMS = {
            "Dashboard", "Rendez-vous", "Patients", "Agenda Médecin", "Factures"
    };

    public SecretaireDashboard() {
        super("OralCare - Espace Secrétariat", "Salwa", "Secrétaire", NAV_ITEMS);

        // Enregistrement des modules réels
        contentArea.add(createMainDashboardView(), "Dashboard");
        contentArea.add(new RendezVousPanel(), "Rendez-vous");
        contentArea.add(new PatientsPanel(), "Patients"); // Nouveau module
        contentArea.add(new AgendaMedecinPanel(), "Agenda Médecin");
        contentArea.add(new FacturesPanel(), "Factures");

        // Placeholders pour les éventuels modules futurs
        for (String item : NAV_ITEMS) {
            if (!isPageImplemented(item)) {
                contentArea.add(createPlaceholderPage(item), item);
            }
        }
    }

    private boolean isPageImplemented(String item) {
        return item.equals("Dashboard") ||
                item.equals("Rendez-vous") ||
                item.equals("Patients") ||
                item.equals("Agenda Médecin") ||
                item.equals("Factures");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception e) { e.printStackTrace(); }

        SecretaireDashboard view = new SecretaireDashboard();

        // Montage MVC standard
        AgendaServiceImpl agendaService = new AgendaServiceImpl();
        RDVRepositoryImpl rdvRepo = new RDVRepositoryImpl();
        PatientRepositoryImpl patientRepo = new PatientRepositoryImpl();
        StatistiquesServiceImpl statsService = new StatistiquesServiceImpl(patientRepo, rdvRepo);

        AgendaController agendaController = new AgendaController(agendaService);
        agendaController.setView(view);
        new DashboardStatsController(view, statsService);

        agendaController.refreshDashboardData();
        SwingUtilities.invokeLater(() -> view.setVisible(true));
    }
}
*/