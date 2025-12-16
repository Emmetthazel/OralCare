package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.service.modules.auth.api.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Implémentation très simple de PasswordEncoder basée sur SHA-256.
 *
 * NOTE: pour un vrai système en production, il faudrait utiliser un algorithme
 * de hachage adapté aux mots de passe (BCrypt, Argon2, ...). Ici on reste simple
 * car l'objectif est pédagogique.
 */
public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non supporté dans cet environnement", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) return false;
        return encode(rawPassword).equals(encodedPassword);
    }
}


