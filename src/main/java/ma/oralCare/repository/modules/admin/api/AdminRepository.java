package ma.oralCare.repository.modules.admin.api;

import ma.oralCare.entities.staff.Admin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Long> {

    // ---- Recherche d'admin ----
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByTelephone(String telephone);
    boolean existsById(Long id);
    long count();
    List<Admin> findPage(int limit, int offset);

    // ---- Permissions (One-to-Many / élément simple) ----
    void addPermission(Long adminId, String permission);
    void removePermission(Long adminId, String permission);
    void removeAllPermissions(Long adminId);
    List<String> getPermissions(Long adminId);

    // ---- Relations héritage ----
    // Admin → Staff (id_user est FK + PK)
    Optional<Admin> findAdminDetails(Long adminId); // jointure staff + admin
}
