package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.service.modules.auth.api.AuthorizationService;
import ma.oralCare.service.modules.auth.dto.UserPrincipal;

import java.util.Arrays;

/**
 * Vérification simple des rôles / privilèges à partir d'un UserPrincipal.
 */
public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipal principal, RoleLibelle role) {
        if (principal == null || role == null) return false;
        return principal.roles().contains(role);
    }

    @Override
    public boolean hasAnyRole(UserPrincipal principal, RoleLibelle... roles) {
        if (principal == null || roles == null) return false;
        return Arrays.stream(roles).anyMatch(r -> principal.roles().contains(r));
    }

    @Override
    public boolean hasPrivilege(UserPrincipal principal, String privilege) {
        if (principal == null || privilege == null) return false;
        return principal.privileges().contains(privilege);
    }

    @Override
    public void checkRole(UserPrincipal principal, RoleLibelle role) {
        if (!hasRole(principal, role)) {
            throw new SecurityException("Accès refusé : rôle " + role + " requis");
        }
    }

    @Override
    public void checkAnyRole(UserPrincipal principal, RoleLibelle... roles) {
        if (!hasAnyRole(principal, roles)) {
            throw new SecurityException("Accès refusé : aucun des rôles requis n'est présent");
        }
    }

    @Override
    public void checkPrivilege(UserPrincipal principal, String privilege) {
        if (!hasPrivilege(principal, privilege)) {
            throw new SecurityException("Accès refusé : privilège requis manquant");
        }
    }
}


