package ma.oralCare.mvc.ui.mainframe;

import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController;
import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;
import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.mvc.controllers.patient.api.PatientController;
import ma.oralCare.mvc.controllers.RDV.api.RDVController;

import javax.swing.*;
import java.awt.*;

/**
 * Exemple d'utilisation et d'intégration de l'AdminDashboard refactorisé
 * avec les contrôleurs existants du projet OralCare
 */
public class AdminDashboardExample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Création de la fenêtre principale
            JFrame frame = new JFrame("OralCare Admin Dashboard - Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 950);
            frame.setLocationRelativeTo(null);
            
            // 2. Création de l'instance du Dashboard
            AdminDashboard dashboard = new AdminDashboard();
            
            // 3. Simulation de l'injection des dépendances (à adapter avec votre ApplicationContext)
            // Dans votre projet, vous récupérerez ces instances depuis votre conteneur DI
            AdminDashboardController adminController = getAdminDashboardController();
            UserManagementController userController = getUserManagementController();
            SystemReferentielController systemController = getSystemReferentielController();
            DashboardController dashboardCtrl = getDashboardController();
            PatientController patientController = getPatientController();
            RDVController rdvController = getRDVController();
            
            // 4. Injection des contrôleurs dans le dashboard
            dashboard.setControllers(
                adminController,
                userController, 
                systemController,
                dashboardCtrl,
                patientController,
                rdvController
            );
            
            // 5. Ajout du dashboard à la fenêtre
            frame.add(dashboard);
            
            // 6. Affichage de la fenêtre
            frame.setVisible(true);
            
            // 7. Exemple de navigation programmatique
            Timer timer = new Timer(3000, e -> {
                // Naviguer vers la vue Patients après 3 secondes
                dashboard.navigateToView("PATIENTS");
                
                // Après 3 secondes supplémentaires, naviguer vers les Rendez-vous
                Timer timer2 = new Timer(3000, e2 -> {
                    dashboard.navigateToView("RDV");
                });
                timer2.setRepeats(false);
                timer2.start();
            });
            timer.setRepeats(false);
            timer.start();
        });
    }
    
    // Méthodes de simulation - à remplacer par vos vraies injections
    private static AdminDashboardController getAdminDashboardController() {
        // Dans votre projet: 
        // return ApplicationContext.getBean(AdminDashboardController.class);
        return null; // Placeholder
    }
    
    private static UserManagementController getUserManagementController() {
        // return ApplicationContext.getBean(UserManagementController.class);
        return null; // Placeholder
    }
    
    private static SystemReferentielController getSystemReferentielController() {
        // return ApplicationContext.getBean(SystemReferentielController.class);
        return null; // Placeholder
    }
    
    private static DashboardController getDashboardController() {
        // return ApplicationContext.getBean(DashboardController.class);
        return null; // Placeholder
    }
    
    private static PatientController getPatientController() {
        // return ApplicationContext.getBean(PatientController.class);
        return null; // Placeholder
    }
    
    private static RDVController getRDVController() {
        // return ApplicationContext.getBean(RDVController.class);
        return null; // Placeholder
    }
}
