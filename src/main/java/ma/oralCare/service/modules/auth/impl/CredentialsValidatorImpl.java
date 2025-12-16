package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.service.modules.auth.api.CredentialsValidator;
import ma.oralCare.service.modules.auth.dto.AuthRequest;

/**
 * Validation basique des credentials (non null, longueur minimale, etc.).
 */
public class CredentialsValidatorImpl implements CredentialsValidator {

    @Override
    public void validate(AuthRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("AuthRequest ne doit pas être null");
        }
        if (request.login() == null || request.login().isBlank()) {
            throw new IllegalArgumentException("Login obligatoire");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("Mot de passe obligatoire");
        }
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Nouveau mot de passe obligatoire");
        }
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        }
    }
}


