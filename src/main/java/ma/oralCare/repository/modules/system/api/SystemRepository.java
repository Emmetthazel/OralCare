package ma.oralCare.repository.modules.system.api;

import java.util.Map;

public interface SystemRepository {

    /**
     * Calcule la taille réelle du fichier SQLite sur le disque.
     * Utilisé pour la Carte 2 "Stock DB".
     */
    double getDatabaseSizeInMB();

    /**
     * Récupère le statut d'une configuration (ex: 'BACKUP_STATUS').
     * Utilisé pour la cloche de notification 🔔.
     */
    String getConfigStatus(String key);

    /**
     * Récupère la valeur d'une configuration (ex: la date de dernière sauvegarde).
     */
    String getConfigValue(String key);

    /**
     * Met à jour ou insère une configuration système.
     */
    void updateConfig(String key, String value, String status, String description);

    /**
     * Récupère toutes les configs (optionnel, pour un tableau de bord complet).
     */
    Map<String, String> getAllConfigs();
}