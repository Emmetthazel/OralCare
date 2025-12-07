package ma.oralCare.repository.modules.auth.api;

import ma.oralCare.entities.notification.Role; // Entité Role supposée dans ce package
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Role.
 * Gère la création, la modification, la consultation et la suppression des rôles et de leurs privilèges.
 */
public interface RoleRepository extends CrudRepository<Role, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Basées sur le CU "Gérer Rôles" -> Consulter) ---

    /**
     * Recherche un rôle par son libellé unique.
     * @param libelle Le libellé (nom) du rôle.
     * @return Le rôle correspondant, s'il existe.
     */
    Optional<Role> findByLibelle(String libelle);

    /**
     * Recherche tous les rôles qui possèdent un privilège spécifique.
     * @param privilege Le privilège à rechercher (par exemple, "CONSULTER_PATIENT").
     * @return La liste des rôles contenant ce privilège.
     */
    List<Role> findByPrivilege(String privilege);

    // --- 2. Méthodes de Gestion Spécifiques (Basées sur le CU "Gérer Rôles" -> Modifier/Ajouter/Supprimer) ---

    /**
     * Ajoute un nouveau privilège à un rôle existant.
     * @param roleId L'ID du rôle à modifier.
     * @param newPrivilege Le privilège à ajouter.
     * @return Le rôle mis à jour.
     */
    Role addPrivilegeToRole(Long roleId, String newPrivilege);

    /**
     * Supprime un privilège spécifique d'un rôle existant.
     * @param roleId L'ID du rôle à modifier.
     * @param privilegeToRemove Le privilège à supprimer.
     * @return Le rôle mis à jour.
     */
    Role removePrivilegeFromRole(Long roleId, String privilegeToRemove);

}