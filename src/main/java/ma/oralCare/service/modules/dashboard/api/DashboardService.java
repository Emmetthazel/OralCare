package ma.oralCare.service.modules.dashboard.api;

import ma.oralCare.service.modules.dashboard.dto.DashboardDTO;
import java.time.LocalDate;

/**
 * Interface définissant les opérations métier pour le tableau de bord.
 * Mise à jour pour supporter la personnalisation par utilisateur.
 */
public interface DashboardService {

    /**
     * Récupère toutes les statistiques et la liste des rendez-vous
     * pour alimenter l'interface, incluant le nom de l'utilisateur connecté.
     *
     * @param date La date cible (généralement la date du jour).
     * @param userLogin Le login de l'utilisateur pour récupérer ses infos de profil.
     * @return Un objet DashboardDTO contenant les compteurs, les listes et le nom de l'admin.
     */
    DashboardDTO getDashboardData(LocalDate date, String userLogin);

    /**
     * Méthode utilitaire pour récupérer les données d'aujourd'hui pour un utilisateur précis.
     * * @param userLogin Le login de l'utilisateur connecté.
     */
    default DashboardDTO getTodayDashboardData(String userLogin) {
        return getDashboardData(LocalDate.now(), userLogin);
    }
}