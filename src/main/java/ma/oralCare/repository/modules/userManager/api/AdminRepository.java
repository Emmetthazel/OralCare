package ma.oralCare.repository.modules.userManager.api;

import ma.oralCare.entities.staff.Admin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.Optional;

/**
 * Interface de dépôt pour l'entité Admin.
 * Gère les opérations de base de l'entité Admin (qui hérite de Staff/Utilisateur).
 */
public interface AdminRepository extends CrudRepository<Admin, Long> {

    // --- 1. Méthodes de Recherche Spécifiques à l'Authentification (Héritées de l'Utilisateur) ---

    /**
     * Recherche un administrateur par son login. Essentiel pour l'authentification.
     * @param login Le login (nom d'utilisateur) unique.
     * @return L'Admin correspondant, s'il existe.
     */
    Optional<Admin> findByLogin(String login);

    /**
     * Recherche un administrateur par son CIN.
     * @param cin Le CIN de l'administrateur.
     * @return L'Admin correspondant, s'il existe.
     */
    Optional<Admin> findByCin(String cin);

    // --- 2. Méthodes de Gestion de Compte Spécifiques (Basées sur les CU de l'Admin) ---
    // Ces méthodes sont déjà définies dans UtilisateurRepository, mais si AdminRepository
    // doit les exposer pour maintenir le typage fort Admin, elles peuvent être ré-exposées ici,
    // ou gérées par une couche de service utilisant UtilisateurRepository.

    /**
     * Recherche l'historique des sessions actives pour cet administrateur.
     * CU Correspondant : Gérer sessions actives.
     * NOTE: Cette méthode est souvent implémentée dans un service de sécurité plutôt que dans le dépôt.
     * Nous la laissons ici si les sessions sont stockées directement via l'entité Utilisateur.
     */
    // List<Session> findActiveSessions(Long adminId);

    // Les méthodes de modification (updateMotDePasse, updateStatutCompte) seront implémentées
    // en utilisant AdminRepository.update() ou en déléguant à UtilisateurRepository pour la cohérence
    // du CU "Modifier utilisateur".

}