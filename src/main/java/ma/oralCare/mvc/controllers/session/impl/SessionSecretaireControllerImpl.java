package ma.oralCare.mvc.controllers.session.impl;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.mvc.controllers.session.api.SessionSecretaireController;
import ma.oralCare.service.modules.session.api.SessionSecretaireService;
import ma.oralCare.service.modules.session.dto.LoginRequest;
import ma.oralCare.service.modules.session.dto.LoginResult;
import ma.oralCare.service.modules.session.dto.SessionInfo;

import java.util.List;
import java.util.Optional;

public class SessionSecretaireControllerImpl implements SessionSecretaireController {

    private final SessionSecretaireService sessionSecretaireService;
    private boolean initialized = false;

    public SessionSecretaireControllerImpl(SessionSecretaireService sessionSecretaireService) {
        this.sessionSecretaireService = sessionSecretaireService;
    }

    @Override
    public void initialize() {
        if (!initialized) {
            startBackgroundTasks();
            initialized = true;
        }
    }

    @Override
    public LoginResult login(LoginRequest loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getLogin() == null || loginRequest.getPassword() == null) {
                return LoginResult.failure("Login et mot de passe sont obligatoires");
            }

            LoginResult result = sessionSecretaireService.loginSecretaire(loginRequest);
            
            if (result.isSuccess()) {
                logInfo("Secrétaire connecté: " + result.getSecretaire().getLogin() + 
                       " (Session: " + result.getSessionId() + ")");
            } else {
                logWarning("Échec de connexion pour: " + loginRequest.getLogin() + 
                          " - " + result.getMessage());
            }
            
            return result;

        } catch (Exception e) {
            logError("Erreur lors de la connexion: " + e.getMessage());
            return LoginResult.failure("Erreur système lors de la connexion");
        }
    }

    @Override
    public void logout(String sessionId) {
        try {
            if (sessionId != null) {
                Optional<SessionInfo> sessionInfo = sessionSecretaireService.getSessionInfo(sessionId);
                if (sessionInfo.isPresent()) {
                    logInfo("Déconnexion du secrétaire: " + sessionInfo.get().getSecretaireLogin());
                }
                sessionSecretaireService.logoutSecretaire(sessionId);
            }
        } catch (Exception e) {
            logError("Erreur lors de la déconnexion: " + e.getMessage());
        }
    }

    @Override
    public boolean checkSessionValidity(String sessionId) {
        try {
            boolean isValid = sessionSecretaireService.isSessionValid(sessionId);
            if (!isValid && sessionId != null) {
                logInfo("Session invalide ou expirée: " + sessionId);
            }
            return isValid;
        } catch (Exception e) {
            logError("Erreur lors de la vérification de session: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Optional<SessionInfo> getSessionInformation(String sessionId) {
        try {
            return sessionSecretaireService.getSessionInfo(sessionId);
        } catch (Exception e) {
            logError("Erreur lors de la récupération des informations de session: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Secretaire> getCurrentSecretaire(String sessionId) {
        try {
            return sessionSecretaireService.getConnectedSecretaire(sessionId);
        } catch (Exception e) {
            logError("Erreur lors de la récupération du secrétaire connecté: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean refreshSession(String sessionId) {
        try {
            boolean refreshed = sessionSecretaireService.refreshSession(sessionId);
            if (refreshed) {
                logDebug("Session rafraîchie: " + sessionId);
            }
            return refreshed;
        } catch (Exception e) {
            logError("Erreur lors du rafraîchissement de session: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean changePassword(String sessionId, String oldPassword, String newPassword) {
        try {
            if (sessionId == null || oldPassword == null || newPassword == null) {
                logWarning("Paramètres invalides pour le changement de mot de passe");
                return false;
            }

            boolean changed = sessionSecretaireService.changePassword(sessionId, oldPassword, newPassword);
            if (changed) {
                logInfo("Mot de passe changé avec succès pour la session: " + sessionId);
            } else {
                logWarning("Échec du changement de mot de passe pour la session: " + sessionId);
            }
            return changed;

        } catch (Exception e) {
            logError("Erreur lors du changement de mot de passe: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Secretaire updateProfile(String sessionId, Secretaire secretaire) {
        try {
            if (sessionId == null || secretaire == null) {
                logWarning("Paramètres invalides pour la mise à jour du profil");
                throw new IllegalArgumentException("Session ou secrétaire invalide");
            }

            Secretaire updated = sessionSecretaireService.updateProfile(sessionId, secretaire);
            logInfo("Profil mis à jour pour le secrétaire: " + updated.getLogin());
            return updated;

        } catch (Exception e) {
            logError("Erreur lors de la mise à jour du profil: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public SessionInfo getDashboardStatistics(String sessionId) {
        try {
            return sessionSecretaireService.getDashboardStats(sessionId);
        } catch (Exception e) {
            logError("Erreur lors de la récupération des statistiques: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void performSessionCleanup() {
        try {
            sessionSecretaireService.cleanupExpiredSessions();
            logDebug("Nettoyage des sessions expirées effectué");
        } catch (Exception e) {
            logError("Erreur lors du nettoyage des sessions: " + e.getMessage());
        }
    }

    @Override
    public void forceLogoutAllSecretaireSessions(Long secretaireId) {
        try {
            if (secretaireId != null) {
                sessionSecretaireService.forceLogoutAllSessions(secretaireId);
                logInfo("Forcé la déconnexion de toutes les sessions du secrétaire ID: " + secretaireId);
            }
        } catch (Exception e) {
            logError("Erreur lors de la déconnexion forcée: " + e.getMessage());
        }
    }

    @Override
    public List<SessionInfo> getAllActiveSessions() {
        try {
            List<SessionInfo> sessions = sessionSecretaireService.getAllActiveSessions();
            logDebug("Nombre de sessions actives: " + sessions.size());
            return sessions;
        } catch (Exception e) {
            logError("Erreur lors de la récupération des sessions actives: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public void startBackgroundTasks() {
        try {
            if (sessionSecretaireService instanceof ma.oralCare.service.modules.session.impl.SessionSecretaireServiceImpl) {
                ((ma.oralCare.service.modules.session.impl.SessionSecretaireServiceImpl) sessionSecretaireService)
                    .startSessionCleanupTask();
                logInfo("Tâche de fond de nettoyage des sessions démarrée");
            }
        } catch (Exception e) {
            logError("Erreur lors du démarrage des tâches de fond: " + e.getMessage());
        }
    }

    /**
     * Vérifie si le contrôleur est initialisé
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Méthodes de logging
     */
    private void logInfo(String message) {
        System.out.println("[SESSION-CONTROLLER] INFO: " + message);
    }

    private void logWarning(String message) {
        System.out.println("[SESSION-CONTROLLER] WARNING: " + message);
    }

    private void logError(String message) {
        System.err.println("[SESSION-CONTROLLER] ERROR: " + message);
    }

    private void logDebug(String message) {
        System.out.println("[SESSION-CONTROLLER] DEBUG: " + message);
    }
}
