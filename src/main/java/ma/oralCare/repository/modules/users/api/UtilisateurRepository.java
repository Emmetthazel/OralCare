package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.entities.users.Role;
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
}