package ma.oralCare.service.modules.session.dto;

import ma.oralCare.entities.users.Secretaire;

import java.time.LocalDateTime;

public class LoginResult {
    private boolean success;
    private String message;
    private String sessionId;
    private Secretaire secretaire;
    private LocalDateTime loginTime;
    private LocalDateTime sessionExpiryTime;

    public LoginResult() {}

    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResult(boolean success, String message, String sessionId, Secretaire secretaire) {
        this.success = success;
        this.message = message;
        this.sessionId = sessionId;
        this.secretaire = secretaire;
        this.loginTime = LocalDateTime.now();
        this.sessionExpiryTime = LocalDateTime.now().plusHours(8); // Session de 8 heures
    }

    public static LoginResult success(String sessionId, Secretaire secretaire) {
        return new LoginResult(true, "Connexion réussie", sessionId, secretaire);
    }

    public static LoginResult failure(String message) {
        return new LoginResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Secretaire getSecretaire() {
        return secretaire;
    }

    public void setSecretaire(Secretaire secretaire) {
        this.secretaire = secretaire;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    public void setSessionExpiryTime(LocalDateTime sessionExpiryTime) {
        this.sessionExpiryTime = sessionExpiryTime;
    }

    @Override
    public String toString() {
        return "LoginResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", secretaire=" + (secretaire != null ? secretaire.getNom() : "null") +
                ", loginTime=" + loginTime +
                ", sessionExpiryTime=" + sessionExpiryTime +
                '}';
    }
}
