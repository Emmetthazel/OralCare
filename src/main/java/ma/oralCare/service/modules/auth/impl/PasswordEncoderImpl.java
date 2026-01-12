package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.service.modules.auth.api.PasswordEncoder;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordEncoderImpl implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être nul");
        }
        // BCrypt attend une String, on convertit donc le CharSequence
        return BCrypt.hashpw(rawPassword.toString(), BCrypt.gensalt());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        // 1. Sécurité : Si l'un des paramètres est null, la correspondance est impossible
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        try {
            // 2. BCrypt.checkpw extrait automatiquement le sel du 'encodedPassword'
            // pour hacher le 'rawPassword' et comparer les résultats.
            return BCrypt.checkpw(rawPassword.toString(), encodedPassword);

        } catch (IllegalArgumentException e) {
            // 3. Gestion d'erreur : Si le format du hash en base est invalide
            // (par exemple si c'est du texte clair comme "Qd5$XNIeD0" au lieu d'un hash BCrypt)
            System.err.println("[BCRYPT-ERROR] Le format du mot de passe en base est invalide (pas un hash BCrypt).");
            return false;
        } catch (Exception e) {
            // 4. Capture générique pour éviter tout plantage de l'application lors de l'auth
            return false;
        }
    }
}