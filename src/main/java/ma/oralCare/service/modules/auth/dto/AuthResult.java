package ma.oralCare.service.modules.auth.dto;

/**
 * Résultat d'authentification minimal (succès + principal).
 */
public record AuthResult(
        boolean success,
        UserPrincipal principal,
        String message
) {}


