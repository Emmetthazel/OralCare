package ma.oralCare.service.modules.session.dto;

import java.time.LocalDateTime;

public class LoginRequest {
    private String login;
    private String password;
    private String sessionId;

    public LoginRequest() {}

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public LoginRequest(String login, String password, String sessionId) {
        this.login = login;
        this.password = password;
        this.sessionId = sessionId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "login='" + login + '\'' +
                ", password='[PROTECTED]'" +
                ", sessionId='" + sessionId + '\'' +
                '}';
    }
}
