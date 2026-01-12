package ma.oralCare.service.modules.auth.impl;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.service.modules.auth.api.AuthorizationService;
import ma.oralCare.service.modules.auth.dto.UserPrincipal;

import java.util.Arrays;

public class AuthorizationServiceImpl implements AuthorizationService {

    @Override
    public boolean hasRole(UserPrincipal principal, RoleLibelle role) {
        return principal != null && principal.getRoles().contains(role.name());
    }

    @Override
    public boolean hasAnyRole(UserPrincipal principal, RoleLibelle... roles) {
        return principal != null && Arrays.stream(roles).anyMatch(r -> hasRole(principal, r));
    }

    @Override
    public boolean hasPrivilege(UserPrincipal principal, String privilege) {
        return principal != null && principal.getPrivileges().contains(privilege);
    }

    @Override
    public void checkRole(UserPrincipal principal, RoleLibelle role) {
        if (!hasRole(principal, role)) throw new RuntimeException("Accès refusé : Rôle " + role + " requis.");
    }

    @Override
    public void checkAnyRole(UserPrincipal principal, RoleLibelle... roles) {
        if (!hasAnyRole(principal, roles)) throw new RuntimeException("Accès refusé.");
    }

    @Override
    public void checkPrivilege(UserPrincipal principal, String privilege) {
        if (!hasPrivilege(principal, privilege)) throw new RuntimeException("Accès refusé : Privilège manquant.");
    }
}