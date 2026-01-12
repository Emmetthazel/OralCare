/*package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.controllers.agenda.AgendaController;
import ma.oralCare.mvc.controllers.dashboard.DashboardStatsController;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.service.modules.agenda.impl.AgendaServiceImpl;
import ma.oralCare.service.modules.dashboard_statistiques.impl.StatistiquesServiceImpl;
import javax.swing.*;


public class MedecinDashboard extends BaseDashboard {

    // Éléments de navigation définis dans la Sidebar
    private static final String[] NAV_ITEMS = {
            "Dashboard", "Rendez-vous", "Situation financière",
            "Ordonnances", "Dossiers médicaux", "Catalogue Actes", "Antécédants"
    };

    public MedecinDashboard() {
        // Initialisation de la fenêtre de base (Titre, Sidebar, Infos utilisateur)
        super("OralCare - Espace Praticien Pro", "Dr. Berrada", "Dentiste", NAV_ITEMS);

        // --- ENREGISTREMENT DES MODULES RÉELS ---
        // Chaque panel possède son propre en-tête (Titre + Bouton d'action)
        contentArea.add(createMainDashboardView(), "Dashboard");
        contentArea.add(new RendezVousPanel(), "Rendez-vous");
        contentArea.add(new SituationFinancierePanel(), "Situation financière");
        contentArea.add(new OrdonnancesPanel(), "Ordonnances");
        contentArea.add(new CatalogueActesPanel(), "Catalogue Actes");
        contentArea.add(new AntecedentsPanel(), "Antécédants");

        // --- GESTION DES PLACEHOLDERS ---
        // Ajoute automatiquement une page d'attente pour les modules non encore codés
        for (String item : NAV_ITEMS) {
            if (!isPageImplemented(item)) {
                contentArea.add(createPlaceholderPage(item), item);
            }
        }
    }

    private boolean isPageImplemented(String item) {
        return item.equals("Dashboard") ||
                item.equals("Rendez-vous") ||
                item.equals("Situation financière") ||
                item.equals("Ordonnances") ||
                item.equals("Catalogue Actes") ||
                item.equals("Antécédants");
    }



}
*/