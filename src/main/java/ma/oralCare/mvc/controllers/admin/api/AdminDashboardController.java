package ma.oralCare.mvc.controllers.admin.api;

import ma.oralCare.mvc.controllers.admin.dto.AdminDashboardDTO;

public interface AdminDashboardController {
    /**
     * Orchestre la récupération de toutes les données
     * pour remplir le Dashboard de l'Admin Global.
     */
    AdminDashboardDTO getDashboardData();
}