package ma.oralCare.service.modules.auth.dto;

/**
 * Requête d'authentification de base : login + mot de passe.
 */
public record AuthRequest(
        String login,
        String password
) {}


