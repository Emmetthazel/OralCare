package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.service.modules.auth.api.CredentialsValidator;
import ma.oralCare.service.modules.auth.dto.AuthRequest;

public class CredentialsValidatorImpl implements CredentialsValidator {

    @Override
    public void validate(AuthRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête d'authentification est nulle.");
        }

        // Vérification du Login
        if (request.getLogin() == null || request.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur est obligatoire.");
        }

        // Vérification du Mot de passe
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire.");
        }

        // Optionnel : Limiter la longueur pour éviter des attaques par déni de service (DoS) sur BCrypt
        if (request.getPassword().length() > 72) {
            throw new IllegalArgumentException("Le mot de passe est trop long.");
        }
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 6 caractères.");
        }

        // On peut ajouter ici des règles complexes (Majuscule, chiffre, etc.)
        if (!newPassword.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins un chiffre.");
        }
    }
}