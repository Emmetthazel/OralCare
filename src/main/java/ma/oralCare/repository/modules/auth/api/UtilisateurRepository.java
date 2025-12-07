package ma.oralCare.repository.modules.auth.api;

import ma.oralCare.entities.staff.Utilisateur;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Utilisateur, étendant CrudRepository
 * et ajoutant des méthodes spécifiques d'authentification et de gestion.
 */
public interface UtilisateurRepository extends CrudRepository<Utilisateur, Long> {

    // --- Méthodes Héritées de CrudRepository (CRUD de Base) ---
    // Ces méthodes sont implicitement incluses et couvrent les CU "Ajouter", "Consulter", "Supprimer utilisateur".
    // List<Utilisateur> findAll();
    // Utilisateur findById(Long id);
    // void create(Utilisateur newElement);
    // void update(Utilisateur newValuesElement);
    // void delete(Utilisateur patient);
    // void deleteById(Long id);

    // --- 1. Méthodes d'Authentification et de Sécurité ---

    /**
     * Objectif : Authentification et vérification d'unicité.
     * CU Correspondant : S'authentifier.
     * @param login Le login (nom d'utilisateur) unique.
     * @return L'Utilisateur correspondant.
     */
    Optional<Utilisateur> findByLogin(String login);

    /**
     * Objectif : Changement ou réinitialisation du mot de passe.
     * CU Correspondant : Réinitialiser Mot de Passe (lié à Modifier utilisateur).
     * @param userId L'ID de l'utilisateur.
     * @param newPassword Le nouveau mot de passe (doit être haché).
     * @return L'Utilisateur mis à jour.
     */
    Utilisateur updateMotDePasse(Long userId, String newPassword);

    // --- 2. Méthodes de Gestion de Compte et de Recherche Spécifique ---

    /**
     * Objectif : Activation/désactivation de compte.
     * CU Correspondant : Activer/désactiver Compte (inclusion de Modifier utilisateur).
     * @param userId L'ID de l'utilisateur.
     * @param estActif Le nouvel état du compte.
     * @return L'Utilisateur mis à jour.
     */
    Utilisateur updateStatutCompte(Long userId, boolean estActif);

    /**
     * Objectif : Recherche par identifiant unique (CIN).
     * CU Correspondant : Supporte Consulter utilisateur (recherche).
     * @param cin Le CIN de l'utilisateur.
     * @return L'Utilisateur correspondant, s'il existe.
     */
    Optional<Utilisateur> findByCin(String cin);

    /**
     * Objectif : Filtrage des utilisateurs par rôle.
     * CU Correspondant : Supporte Consulter liste utilisateurs (filtrage).
     * @param roleLibelle Le libellé du rôle (ex: "DOCTOR").
     * @return La liste des Utilisateurs ayant ce rôle.
     */
    List<Utilisateur> findByRoleLibelle(String roleLibelle);

    // --- 3. Méthodes de Gestion des Rôles (Relation N:M) ---

    /**
     * Objectif : Attribuer un rôle à un utilisateur.
     * CU Correspondant : Attribuer Role (extension de Gérer utilisateurs).
     * @param userId L'ID de l'utilisateur à modifier.
     * @param roleId L'ID du rôle à ajouter.
     * @return L'Utilisateur mis à jour.
     */
    Utilisateur addRoleToUtilisateur(Long userId, Long roleId);

    /**
     * Objectif : Retirer un rôle à un utilisateur (gestion des permissions).
     * CU Correspondant : Modifier permissions (extension de Modifier utilisateur).
     * @param userId L'ID de l'utilisateur.
     * @param roleId L'ID du rôle à supprimer.
     * @return L'Utilisateur mis à jour.
     */
    Utilisateur removeRoleFromUtilisateur(Long userId, Long roleId);
}