package ma.oralCare.repository.modules.userManager.api;

import ma.oralCare.entities.staff.Staff;
import ma.oralCare.repository.common.CrudRepository; // Utilisation directe pour la généricité

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt pour l'entité Staff.
 * Elle gère les opérations CRUD génériques via CrudRepository et ajoute les fonctionnalités spécifiques au personnel.
 * * NOTE TECHNIQUE : Étant donné que UtilisateurRepository n'est pas générique,
 * StaffRepository hérite directement de CrudRepository pour maintenir la typage fort <Staff, Long>.
 */
public interface StaffRepository extends CrudRepository<Staff, Long> {

    // --- 1. Méthodes Héritées implicitement / Par délégation (Authentification et Utilisateur) ---
    // Ces méthodes devront être implémentées dans StaffRepositoryImpl pour appeler la logique
    // d'accès à la base de données basée sur le login/CIN/email de l'Utilisateur.

    /**
     * Recherche le personnel par leur login.
     * Bien que cette méthode soit dans l'implémentation de UtilisateurRepository, elle est nécessaire
     * pour l'authentification et doit être exposée ici pour retourner un objet Staff typé.
     */
    Optional<Staff> findByLogin(String login);

    /**
     * Recherche le personnel par son CIN.
     * @param cin Le CIN du membre du personnel.
     * @return Le Staff correspondant, s'il existe.
     */
    Optional<Staff> findByCin(String cin);


    // --- 2. Méthodes de Recherche Spécifiques au Staff (Données de Salaire/Congés) ---

    /**
     * Recherche le personnel par leur date de recrutement.
     * Utile pour la gestion administrative et les rapports.
     */
    List<Staff> findByDateRecrutement(LocalDate dateRecrutement);

    /**
     * Recherche le personnel dont le solde de congés est inférieur ou égal à un certain seuil.
     */
    List<Staff> findBySoldeCongeLessThanEqual(Integer maxSolde);

    /**
     * Recherche le personnel par son salaire, dans une fourchette spécifique.
     */
    List<Staff> findBySalaireBetween(Double minSalaire, Double maxSalaire);

    // --- 3. Méthodes de Gestion Administrative (Mises à jour) ---

    /**
     * Met à jour le salaire et/ou la prime d'un membre du personnel.
     * @param staffId L'identifiant du membre du personnel.
     * @param nouveauSalaire Le nouveau salaire (peut être null si inchangé).
     * @param nouvellePrime La nouvelle prime (peut être null si inchangée).
     */
    void updateSalaireAndPrime(Long staffId, Double nouveauSalaire, Double nouvellePrime);

    /**
     * Met à jour le solde de congés d'un membre du personnel.
     * @param staffId L'identifiant du membre du personnel.
     * @param nouveauSolde Le nouveau solde de congés.
     */
    void updateSoldeConge(Long staffId, Integer nouveauSolde);
}