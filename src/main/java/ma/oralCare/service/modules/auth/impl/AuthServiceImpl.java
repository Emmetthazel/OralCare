package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;
import ma.oralCare.service.modules.auth.api.AuthService;
import ma.oralCare.service.modules.auth.api.CredentialsValidator;
import ma.oralCare.service.modules.auth.api.PasswordEncoder;
import ma.oralCare.service.modules.auth.dto.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder encoder;
    private final CredentialsValidator validator;
    private final UtilisateurRepository utilisateurRepository;

    // Correction : On utilise l'instance injectée au lieu d'ignorer le paramètre
    public AuthServiceImpl(UtilisateurRepository repository, PasswordEncoder encoder, CredentialsValidator validator) {
        this.encoder = encoder;
        this.validator = validator;
        this.utilisateurRepository = repository;
    }

    @Override
    public AuthResult authenticate(AuthRequest request) {
        System.out.println("\n[AUTH-LOG] Tentative de connexion pour le login : " + request.getLogin());

        try {
            // 1. Validation syntaxique (via CredentialsValidatorImpl)
            validator.validate(request);

            // 2. Recherche de l'utilisateur
            Optional<Utilisateur> userOpt = utilisateurRepository.findByLogin(request.getLogin());

            if (userOpt.isEmpty()) {
                System.out.println("[AUTH-ERROR] Login non trouvé : " + request.getLogin());
                return new AuthResult(false, "Identifiants incorrects (Utilisateur introuvable)");
            }

            Utilisateur user = userOpt.get();

            // 3. Vérification du mot de passe (via PasswordEncoderImpl / BCrypt)
            // Rappel : encodedPassword doit être un hash $2a$... pour que matches() soit vrai
            if (encoder.matches(request.getPassword(), user.getMotDePass())) {

                // 4. Récupération des noms de rôles (Enum.name())
                List<String> roleNames = user.getRoles().stream()
                        .map(r -> r.getLibelle().name()) // Récupère "DOCTOR", "ADMIN", etc.
                        .collect(Collectors.toList());

                System.out.println("[AUTH-SUCCESS] Mot de passe correct pour : " + user.getLogin());
                System.out.println("[AUTH-LOG] Rôles détectés : " + roleNames);

                if (roleNames.isEmpty()) {
                    return new AuthResult(false, "Accès refusé : Aucun rôle assigné.");
                }

                UserPrincipal principal = new UserPrincipal(user.getIdEntite(), user.getLogin(), roleNames);
                return new AuthResult(true, "Connexion réussie", principal);
            } else {
                System.out.println("[AUTH-ERROR] Mot de passe incorrect pour : " + request.getLogin());
                return new AuthResult(false, "Identifiants incorrects (Mot de passe erroné)");
            }
        } catch (Exception e) {
            System.err.println("[AUTH-FATAL] Erreur système : " + e.getMessage());
            return new AuthResult(false, "Erreur système : " + e.getMessage());
        }
    }

    @Override
    public UserPrincipal loadUserPrincipalByLogin(String login) {
        Utilisateur user = utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé : " + login));

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getLibelle().name())
                .collect(Collectors.toList());

        return new UserPrincipal(user.getIdEntite(), user.getLogin(), roles);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        validator.validateNewPassword(newPassword);

        Utilisateur user = utilisateurRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

        if (!encoder.matches(oldPassword, user.getMotDePass())) {
            throw new RuntimeException("L'ancien mot de passe est incorrect.");
        }

        // Hachage du nouveau mot de passe avant mise à jour
        String encodedNewPassword = encoder.encode(newPassword);
        utilisateurRepository.updatePassword(userId, encodedNewPassword);
    }
}