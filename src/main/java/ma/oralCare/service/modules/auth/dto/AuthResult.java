package ma.oralCare.service.modules.auth.dto;

public class AuthResult {

    private final boolean authenticated;
    private final String message;
    private UserPrincipal principal; // Ajout du principal pour porter les rôles
    private Long userId;
    private String login;

    // Constructeur pour l'échec
    public AuthResult(boolean authenticated, String message) {
        this.authenticated = authenticated;
        this.message = message;
    }

    // ✅ NOUVEAU : Constructeur complet avec le Principal (indispensable pour le Controller)
    public AuthResult(boolean authenticated, String message, UserPrincipal principal) {
        this.authenticated = authenticated;
        this.message = message;
        this.principal = principal;
        if (principal != null) {
            this.userId = principal.getId();
            this.login = principal.getLogin();
        }
    }

    // Getters
    public boolean isAuthenticated() { return authenticated; }
    public String getMessage() { return message; }
    public UserPrincipal getPrincipal() { return principal; } // Ajout du getter
    public Long getUserId() { return userId; }
    public String getLogin() { return login; }
}