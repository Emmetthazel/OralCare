package ma.oralCare.service.modules.auth.test;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.service.modules.auth.api.AuthorizationService;
import ma.oralCare.service.modules.auth.api.CredentialsValidator;
import ma.oralCare.service.modules.auth.api.PasswordEncoder;
import ma.oralCare.service.modules.auth.dto.AuthRequest;
import ma.oralCare.service.modules.auth.dto.UserPrincipal;
import ma.oralCare.service.modules.auth.impl.AuthorizationServiceImpl;
import ma.oralCare.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.oralCare.service.modules.auth.impl.PasswordEncoderImpl;

import java.util.Set;

/**
 * Petit test console pour valider le bon fonctionnement des services de support d'authentification.
 *
 * Ce n'est pas un test unitaire JUnit, mais un exécutable dans le même esprit que TestRepo.
 */
public class TestAuthSupportServices {

    public static void main(String[] args) {
        System.out.println("=== TEST AUTH SUPPORT SERVICES ===");

        PasswordEncoder encoder = new PasswordEncoderImpl();
        CredentialsValidator validator = new CredentialsValidatorImpl();
        AuthorizationService authorizationService = new AuthorizationServiceImpl();

        // 1. Validation des credentials
        AuthRequest request = new AuthRequest("demoLogin", "secret123");
        try {
            validator.validate(request);
            validator.validateNewPassword("newSecret123");
            System.out.println("✅ CredentialsValidator OK");
        } catch (Exception e) {
            System.err.println("❌ CredentialsValidator Échec: " + e.getMessage());
        }

        // 2. Encodage de mot de passe
        String encoded = encoder.encode(request.password());
        boolean matches = encoder.matches("secret123", encoded);
        System.out.println(matches
                ? "✅ PasswordEncoder OK"
                : "❌ PasswordEncoder Échec");

        // 3. Vérification d'autorisation basique
        UserPrincipal principal = new UserPrincipal(
                1L,
                "demoLogin",
                "Demo User",
                Set.of(RoleLibelle.ADMIN),
                Set.of("DASHBOARD_VIEW")
        );

        boolean hasAdmin = authorizationService.hasRole(principal, RoleLibelle.ADMIN);
        boolean hasAny = authorizationService.hasAnyRole(principal, RoleLibelle.ADMIN, RoleLibelle.DOCTOR);
        boolean hasPriv = authorizationService.hasPrivilege(principal, "DASHBOARD_VIEW");

        System.out.println(hasAdmin && hasAny && hasPriv
                ? "✅ AuthorizationService OK"
                : "❌ AuthorizationService Échec");

        System.out.println("=== FIN TEST AUTH SUPPORT SERVICES ===");
    }
}


