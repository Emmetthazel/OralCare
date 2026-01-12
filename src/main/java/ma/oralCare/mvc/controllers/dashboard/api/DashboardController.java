package ma.oralCare.mvc.controllers.dashboard.api;

import ma.oralCare.mvc.ui1.medecin.DashboardPanel;

public interface DashboardController {
    DashboardPanel getView();
    void refreshData();
}