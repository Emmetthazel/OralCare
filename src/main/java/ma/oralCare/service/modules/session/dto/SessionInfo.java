package ma.oralCare.service.modules.session.dto;

import ma.oralCare.entities.users.Secretaire;

import java.time.LocalDateTime;

public class SessionInfo {
    private String sessionId;
    private Long secretaireId;
    private String secretaireNom;
    private String secretaireLogin;
    private LocalDateTime sessionStartTime;
    private LocalDateTime lastActivityTime;
    private LocalDateTime sessionExpiryTime;
    private String ipAddress;
    private String userAgent;
    private boolean isActive;

    public SessionInfo() {}

    public SessionInfo(String sessionId, Secretaire secretaire, String ipAddress, String userAgent) {
        this.sessionId = sessionId;
        this.secretaireId = secretaire.getIdEntite();
        this.secretaireNom = secretaire.getNom() + " " + secretaire.getPrenom();
        this.secretaireLogin = secretaire.getLogin();
        this.sessionStartTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();
        this.sessionExpiryTime = LocalDateTime.now().plusHours(8);
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.isActive = true;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getSecretaireId() {
        return secretaireId;
    }

    public void setSecretaireId(Long secretaireId) {
        this.secretaireId = secretaireId;
    }

    public String getSecretaireNom() {
        return secretaireNom;
    }

    public void setSecretaireNom(String secretaireNom) {
        this.secretaireNom = secretaireNom;
    }

    public String getSecretaireLogin() {
        return secretaireLogin;
    }

    public void setSecretaireLogin(String secretaireLogin) {
        this.secretaireLogin = secretaireLogin;
    }

    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    public void setSessionStartTime(LocalDateTime sessionStartTime) {
        this.sessionStartTime = sessionStartTime;
    }

    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    public void setLastActivityTime(LocalDateTime lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }

    public LocalDateTime getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    public void setSessionExpiryTime(LocalDateTime sessionExpiryTime) {
        this.sessionExpiryTime = sessionExpiryTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Vérifie si la session est expirée
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(sessionExpiryTime);
    }

    /**
     * Met à jour le temps de dernière activité et prolonge la session
     */
    public void refreshActivity() {
        this.lastActivityTime = LocalDateTime.now();
        this.sessionExpiryTime = LocalDateTime.now().plusHours(8);
    }

    @Override
    public String toString() {
        return "SessionInfo{" +
                "sessionId='" + sessionId + '\'' +
                ", secretaireId=" + secretaireId +
                ", secretaireNom='" + secretaireNom + '\'' +
                ", secretaireLogin='" + secretaireLogin + '\'' +
                ", sessionStartTime=" + sessionStartTime +
                ", lastActivityTime=" + lastActivityTime +
                ", sessionExpiryTime=" + sessionExpiryTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
