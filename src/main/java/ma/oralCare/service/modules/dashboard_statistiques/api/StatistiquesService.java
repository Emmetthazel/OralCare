package ma.oralCare.service.modules.dashboard_statistiques.api;

import ma.oralCare.entities.cabinet.Statistiques;

import java.util.List;
import java.util.Map;

public interface StatistiquesService {
    List<Statistiques> getAll(); // Cette ligne doit exister
    /**
     * Récupère le nombre total de patients inscrits.
     */
    int getTotalPatientsCount();

    /**
     * Récupère le nombre de visites (rendez-vous) prévues pour aujourd'hui.
     */
    int getTodayVisitsCount();

    /**
     * Récupère le nombre total de rendez-vous enregistrés dans le système.
     */
    int getTotalApptsCount();

    /**
     * Optionnel : Récupère toutes les stats d'un coup dans une Map pour optimiser les appels.
     */
    Map<String, Integer> getDashboardSummary();
}