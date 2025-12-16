package ma.oralCare.service.modules.auth.api;


import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.service.modules.auth.dto.*;

public interface AuthorizationService {

    boolean hasRole(UserPrincipal principal, RoleLibelle role);

    boolean hasAnyRole(UserPrincipal principal, RoleLibelle... roles);

    boolean hasPrivilege(UserPrincipal principal, String privilege);

    /**
     * Lève AuthorizationException si le rôle / privilège est absent.
     */
    void checkRole(UserPrincipal principal, RoleLibelle role);

    void checkAnyRole(UserPrincipal principal, RoleLibelle... roles);

    void checkPrivilege(UserPrincipal principal, String privilege);
}
