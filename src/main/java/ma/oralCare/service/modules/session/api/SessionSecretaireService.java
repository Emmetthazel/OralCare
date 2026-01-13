package ma.oralCare.service.modules.session.api;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.service.modules.session.dto.SessionInfo;
import ma.oralCare.service.modules.session.dto.LoginRequest;
import ma.oralCare.service.modules.session.dto.LoginResult;

import java.util.Optional;

public interface SessionSecretaireService {

    /**
     * Authentifie un secrétaire et crée une session
     */
    LoginResult loginSecretaire(LoginRequest loginRequest);

    /**
     * Déconnecte un secrétaire et détruit sa session
     */
    void logoutSecretaire(String sessionId);

    /**
     * Vérifie si une session est valide
     */
    boolean isSessionValid(String sessionId);

    /**
     * Récupère les informations de la session
     */
    Optional<SessionInfo> getSessionInfo(String sessionId);

    /**
     * Récupère le secrétaire connecté à partir de la session
     */
    Optional<Secretaire> getConnectedSecretaire(String sessionId);

    /**
     * Rafraîchit la session (prolonge la durée de vie)
     */
    boolean refreshSession(String sessionId);

    /**
     * Change le mot de passe du secrétaire connecté
     */
    boolean changePassword(String sessionId, String oldPassword, String newPassword);

    /**
     * Met à jour le profil du secrétaire connecté
     */
    Secretaire updateProfile(String sessionId, Secretaire secretaire);

    /**
     * Récupère les statistiques du tableau de bord pour le secrétaire
     */
    SessionInfo getDashboardStats(String sessionId);

    /**
     * Nettoie les sessions expirées
     */
    void cleanupExpiredSessions();

    /**
     * Force la déconnexion de toutes les sessions d'un secrétaire
     */
    void forceLogoutAllSessions(Long secretaireId);

    /**
     * Récupère toutes les sessions actives
     */
    java.util.List<SessionInfo> getAllActiveSessions();
}
