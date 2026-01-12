package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.entities.users.Role;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByLogin(String login);
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByCin(String cin);
    List<Utilisateur> findAllByNomContaining(String nom);
    List<Utilisateur> findAllByRole(String roleLibelle);
    List<Role> findRolesByUtilisateurId(Long id);
    void addRoleToUtilisateur(Long utilisateurId, Long roleId);
    void removeRoleFromUtilisateur(Long utilisateurId, Long roleId);
    List<UserStaffDTO> findAllStaffWithCabinetDetails(String search);
    void updatePassword(String email, String encodedPassword);
    void updateStatus(String email, String status);
    void update(Utilisateur u);
    void save(Utilisateur u);
    Long findCabinetIdByName(String cabinetName);
    void updatePassword(Long userId, String encodedPassword);
    Optional<Utilisateur> findOneByColumn(String columnName, Object value);
    List<String> findAllCabinetNames();
    Long findRoleIdByName(String roleName);
}