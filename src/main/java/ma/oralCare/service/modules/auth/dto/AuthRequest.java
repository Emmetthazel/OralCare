package ma.oralCare.service.modules.auth.dto;

public class AuthRequest {

    private String login;
    private String password;

    // Constructeur
    public AuthRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    // Getters
    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    // Setters (optionnel si tu veux modifier après création)
    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
