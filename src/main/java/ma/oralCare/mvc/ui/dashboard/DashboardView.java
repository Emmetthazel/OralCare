package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import java.util.List;

public interface DashboardView {
    void updateStatCards(String patients, String visits, String appts);

    // AJOUTER CETTE LIGNE :
    void updateAppointments(List<RDVDisplayModel> rdvs);
}