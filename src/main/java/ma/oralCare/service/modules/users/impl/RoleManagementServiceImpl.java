package ma.oralCare.service.modules.users.impl;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.users.Role;
import ma.oralCare.repository.modules.users.api.RoleRepository;
import ma.oralCare.repository.modules.users.impl.RoleRepositoryImpl;
import ma.oralCare.service.modules.users.api.RoleManagementService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service pour la gestion des rôles, basé sur {@link RoleRepository}.
 */
public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepository;

    public RoleManagementServiceImpl() {
        this(new RoleRepositoryImpl());
    }

    public RoleManagementServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    @Override
    public Role createRole(Role role) {
        Objects.requireNonNull(role, "role ne doit pas être null");
        roleRepository.create(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        Objects.requireNonNull(role, "role ne doit pas être null");
        roleRepository.update(role);
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        if (roleId == null) return;
        roleRepository.deleteById(roleId);
    }

    @Override
    public Role getRoleById(Long id) {
        if (id == null) return null;
        Optional<Role> opt = roleRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public Role getRoleByType(RoleLibelle type) {
        if (type == null) return null;
        return roleRepository.findByLibelle(type.name()).orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Role updateRolePrivileges(Long roleId, List<String> privileges) {
        // Si le modèle Role n'a pas de champ "privileges", cette méthode peut être étendue plus tard.
        // Pour l'instant, on retourne simplement le rôle chargé.
        return getRoleById(roleId);
    }
}


