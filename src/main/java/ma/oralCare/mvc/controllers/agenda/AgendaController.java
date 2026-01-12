package ma.oralCare.mvc.controllers.agenda;

import ma.oralCare.mvc.ui.dashboard.DashboardView;
import ma.oralCare.service.modules.agenda.api.AgendaService;
import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import java.util.List;

public class AgendaController {
    private final AgendaService agendaService;
    private DashboardView view;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    // Permet de lier la vue après l'instanciation
    public void setView(DashboardView view) {
        this.view = view;
    }

    /**
     * Méthode qui récupère les données et ordonne à la vue de se mettre à jour
     */
    public void refreshDashboardData() {
        List<RDVDisplayModel> appointments = agendaService.getTodayAppointments();
        if (view != null) {
            view.updateAppointments(appointments);
        }
    }

    public List<RDVDisplayModel> getTodayAppointmentsForDisplay() {
        return agendaService.getTodayAppointments();
    }
}