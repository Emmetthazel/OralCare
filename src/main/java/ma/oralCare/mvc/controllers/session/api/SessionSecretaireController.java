package ma.oralCare.mvc.controllers.session.api;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.service.modules.session.dto.LoginRequest;
import ma.oralCare.service.modules.session.dto.LoginResult;
import ma.oralCare.service.modules.session.dto.SessionInfo;

import java.util.List;
import java.util.Optional;

public interface SessionSecretaireController {

    /**
     * Authentifie un secrétaire
     */
    LoginResult login(LoginRequest loginRequest);

    /**
     * Déconnecte un secrétaire
     */
    void logout(String sessionId);

    /**
     * Vérifie la validité d'une session
     */
    boolean checkSessionValidity(String sessionId);

    /**
     * Récupère les informations de la session
     */
    Optional<SessionInfo> getSessionInformation(String sessionId);

    /**
     * Récupère le secrétaire connecté
     */
    Optional<Secretaire> getCurrentSecretaire(String sessionId);

    /**
     * Rafraîchit une session
     */
    boolean refreshSession(String sessionId);

    /**
     * Change le mot de passe
     */
    boolean changePassword(String sessionId, String oldPassword, String newPassword);

    /**
     * Met à jour le profil du secrétaire
     */
    Secretaire updateProfile(String sessionId, Secretaire secretaire);

    /**
     * Récupère les statistiques du dashboard
     */
    SessionInfo getDashboardStatistics(String sessionId);

    /**
     * Nettoie les sessions expirées
     */
    void performSessionCleanup();

    /**
     * Force la déconnexion de toutes les sessions d'un secrétaire
     */
    void forceLogoutAllSecretaireSessions(Long secretaireId);

    /**
     * Récupère toutes les sessions actives (pour admin)
     */
    List<SessionInfo> getAllActiveSessions();

    /**
     * Initialise le contrôleur avec les services nécessaires
     */
    void initialize();

    /**
     * Démarre les tâches de fond (nettoyage, etc.)
     */
    void startBackgroundTasks();
}
