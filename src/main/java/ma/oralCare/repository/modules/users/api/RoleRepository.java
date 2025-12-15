package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Role;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {

    Optional<Role> findByLibelle(String libelle);
    List<String> findPrivilegesByRoleId(Long roleId);
    void addPrivilegeToRole(Long roleId, String privilege);
    void removePrivilegeFromRole(Long roleId, String privilege);
}