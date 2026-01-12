package ma.oralCare.repository.modules.system.api;

import java.util.List;

public interface LogRepository {

    /**
     * Pour la Carte 1 : Compte toutes les actions effectuées aujourd'hui
     * par l'admin ou le staff de n'importe quel cabinet.
     */
    long countTodayLogs();

    /**
     * Pour le JTextArea : Récupère les N dernières actions enregistrées
     * dans le système pour l'affichage chronologique.
     */
    List<String> getLatestLogs(int limit);

    /**
     * Pour enregistrer une nouvelle action.
     * @param adminLogin Le login de celui qui fait l'action.
     * @param cabinetId L'ID du cabinet concerné (peut être null si action globale).
     * @param description Le message à afficher (ex: "Création du cabinet Rabat").
     */
    void saveLog(String adminLogin, Long cabinetId, String description);
}