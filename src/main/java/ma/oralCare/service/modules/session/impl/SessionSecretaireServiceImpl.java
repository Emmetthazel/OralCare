package ma.oralCare.service.modules.session.impl;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.service.modules.session.api.SessionSecretaireService;
import ma.oralCare.service.modules.session.dto.LoginRequest;
import ma.oralCare.service.modules.session.dto.LoginResult;
import ma.oralCare.service.modules.session.dto.SessionInfo;
import ma.oralCare.service.modules.users.api.SecretaireService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SessionSecretaireServiceImpl implements SessionSecretaireService {

    private final SecretaireService secretaireService;
    private final Map<String, SessionInfo> activeSessions;
    private final Map<Long, Set<String>> secretaireSessions;

    public SessionSecretaireServiceImpl(SecretaireService secretaireService) {
        this.secretaireService = secretaireService;
        this.activeSessions = new ConcurrentHashMap<>();
        this.secretaireSessions = new ConcurrentHashMap<>();
    }

    @Override
    public LoginResult loginSecretaire(LoginRequest loginRequest) {
        try {
            // Validation des entrées
            if (loginRequest == null || loginRequest.getLogin() == null || loginRequest.getPassword() == null) {
                return LoginResult.failure("Login et mot de passe sont obligatoires");
            }

            // Recherche du secrétaire par login
            Optional<Secretaire> secretaireOpt = secretaireService.findSecretaireByLogin(loginRequest.getLogin().trim());
            if (secretaireOpt.isEmpty()) {
                return LoginResult.failure("Login ou mot de passe incorrect");
            }

            Secretaire secretaire = secretaireOpt.get();

            // Vérification du mot de passe (en pratique, utiliser un hash)
            if (!secretaire.getMotDePass().equals(loginRequest.getPassword())) {
                return LoginResult.failure("Login ou mot de passe incorrect");
            }

            // Génération de l'ID de session
            String sessionId = generateSessionId();

            // Création de la session
            SessionInfo sessionInfo = new SessionInfo(sessionId, secretaire, "localhost", "OralCare-Desktop");
            activeSessions.put(sessionId, sessionInfo);

            // Ajout de la session au secrétaire
            secretaireSessions.computeIfAbsent(secretaire.getIdEntite(), k -> new HashSet<>()).add(sessionId);

            // Mise à jour de la date de dernière connexion
            secretaireService.updateLastLoginDate(secretaire.getIdEntite());

            return LoginResult.success(sessionId, secretaire);

        } catch (Exception e) {
            return LoginResult.failure("Erreur lors de la connexion: " + e.getMessage());
        }
    }

    @Override
    public void logoutSecretaire(String sessionId) {
        if (sessionId == null) return;

        SessionInfo sessionInfo = activeSessions.remove(sessionId);
        if (sessionInfo != null) {
            // Retrait de la session du secrétaire
            Set<String> sessions = secretaireSessions.get(sessionInfo.getSecretaireId());
            if (sessions != null) {
                sessions.remove(sessionId);
                if (sessions.isEmpty()) {
                    secretaireSessions.remove(sessionInfo.getSecretaireId());
                }
            }
        }
    }

    @Override
    public boolean isSessionValid(String sessionId) {
        if (sessionId == null) return false;

        SessionInfo sessionInfo = activeSessions.get(sessionId);
        return sessionInfo != null && sessionInfo.isActive() && !sessionInfo.isExpired();
    }

    @Override
    public Optional<SessionInfo> getSessionInfo(String sessionId) {
        if (sessionId == null) return Optional.empty();

        SessionInfo sessionInfo = activeSessions.get(sessionId);
        if (sessionInfo != null && sessionInfo.isActive() && !sessionInfo.isExpired()) {
            return Optional.of(sessionInfo);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Secretaire> getConnectedSecretaire(String sessionId) {
        if (!isSessionValid(sessionId)) return Optional.empty();

        SessionInfo sessionInfo = activeSessions.get(sessionId);
        return secretaireService.findSecretaireById(sessionInfo.getSecretaireId());
    }

    @Override
    public boolean refreshSession(String sessionId) {
        SessionInfo sessionInfo = activeSessions.get(sessionId);
        if (sessionInfo != null && sessionInfo.isActive() && !sessionInfo.isExpired()) {
            sessionInfo.refreshActivity();
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String sessionId, String oldPassword, String newPassword) {
        Optional<Secretaire> secretaireOpt = getConnectedSecretaire(sessionId);
        if (secretaireOpt.isEmpty()) {
            return false;
        }

        Secretaire secretaire = secretaireOpt.get();
        
        // Vérification de l'ancien mot de passe
        if (!secretaire.getMotDePass().equals(oldPassword)) {
            return false;
        }

        // Mise à jour du mot de passe
        secretaire.setMotDePass(newPassword);
        secretaireService.updateSecretaire(secretaire);
        
        return true;
    }

    @Override
    public Secretaire updateProfile(String sessionId, Secretaire secretaire) {
        if (!isSessionValid(sessionId)) {
            throw new IllegalArgumentException("Session invalide");
        }

        SessionInfo sessionInfo = activeSessions.get(sessionId);
        if (!secretaire.getIdEntite().equals(sessionInfo.getSecretaireId())) {
            throw new IllegalArgumentException("Accès non autorisé");
        }

        return secretaireService.updateSecretaire(secretaire);
    }

    @Override
    public SessionInfo getDashboardStats(String sessionId) {
        return getSessionInfo(sessionId).orElse(null);
    }

    @Override
    public void cleanupExpiredSessions() {
        List<String> expiredSessions = new ArrayList<>();
        
        for (Map.Entry<String, SessionInfo> entry : activeSessions.entrySet()) {
            SessionInfo sessionInfo = entry.getValue();
            if (sessionInfo.isExpired()) {
                expiredSessions.add(entry.getKey());
            }
        }

        for (String sessionId : expiredSessions) {
            logoutSecretaire(sessionId);
        }
    }

    @Override
    public void forceLogoutAllSessions(Long secretaireId) {
        Set<String> sessions = secretaireSessions.get(secretaireId);
        if (sessions != null) {
            for (String sessionId : new ArrayList<>(sessions)) {
                logoutSecretaire(sessionId);
            }
        }
    }

    @Override
    public List<SessionInfo> getAllActiveSessions() {
        List<SessionInfo> activeSessionList = new ArrayList<>();
        
        for (SessionInfo sessionInfo : activeSessions.values()) {
            if (sessionInfo.isActive() && !sessionInfo.isExpired()) {
                activeSessionList.add(sessionInfo);
            }
        }
        
        return activeSessionList;
    }

    /**
     * Génère un ID de session unique
     */
    private String generateSessionId() {
        return "SEC_" + UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis();
    }

    /**
     * Démarre le thread de nettoyage des sessions expirées
     */
    public void startSessionCleanupTask() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(300000); // Nettoyage toutes les 5 minutes
                    cleanupExpiredSessions();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
}
