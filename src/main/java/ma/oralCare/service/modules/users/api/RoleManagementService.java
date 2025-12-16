package ma.oralCare.service.modules.users.api;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.users.Role;

import java.util.List;

public interface RoleManagementService {

    Role createRole(Role role);

    Role updateRole(Role role);

    void deleteRole(Long roleId);

    Role getRoleById(Long id);

    Role getRoleByType(RoleLibelle type);

    List<Role> getAllRoles();

    Role updateRolePrivileges(Long roleId, List<String> privileges);
}
