package ma.oralCare.service.modules.dashboard_statistiques.api;

import ma.oralCare.entities.cabinet.Statistiques;
import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

    /**
     * Récupère les données brutes ou agrégées pour une période donnée,
     * destinées à l'affichage du tableau de bord.
     * * @param start Date de début
     * @param end Date de fin
     * @return Une liste des statistiques pertinentes (ou un objet DashboardData)
     */
    List<Statistiques> getStatistiquesForPeriod(LocalDate start, LocalDate end);
}