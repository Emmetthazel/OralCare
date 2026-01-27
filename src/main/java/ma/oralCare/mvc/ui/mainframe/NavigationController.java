package ma.oralCare.mvc.ui.mainframe;

import javax.swing.JPanel;

/**
 * Interface de découplage pour les contrôleurs de navigation
 * Permet le pattern MapsTo(Controller) pour une navigation flexible
 */
public interface NavigationController {
    /**
     * Exécute la navigation vers une vue spécifique
     * @param viewId Identifiant de la vue à afficher
     * @param targetPanel Panneau cible pour le contenu de la vue
     */
    void executeNavigation(String viewId, JPanel targetPanel);
    
    /**
     * Retourne le nom du contrôleur pour le logging/debug
     * @return Nom du contrôleur
     */
    String getControllerName();
}
