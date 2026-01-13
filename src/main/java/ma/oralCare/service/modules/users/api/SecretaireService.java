package ma.oralCare.service.modules.users.api;

import ma.oralCare.entities.users.Secretaire;

import java.util.List;
import java.util.Optional;

public interface SecretaireService {

    /**
     * Crée un nouveau secrétaire dans le système
     */
    Secretaire createSecretaire(Secretaire secretaire);

    /**
     * Met à jour les informations d'un secrétaire existant
     */
    Secretaire updateSecretaire(Secretaire secretaire);

    /**
     * Supprime un secrétaire par son ID
     */
    void deleteSecretaire(Long id);

    /**
     * Récupère un secrétaire par son ID
     */
    Optional<Secretaire> findSecretaireById(Long id);

    /**
     * Récupère tous les secrétaires
     */
    List<Secretaire> findAllSecretaires();

    /**
     * Récupère un secrétaire par son login
     */
    Optional<Secretaire> findSecretaireByLogin(String login);

    /**
     * Récupère un secrétaire par son CIN
     */
    Optional<Secretaire> findSecretaireByCin(String cin);

    /**
     * Récupère les secrétaires par nom (recherche partielle)
     */
    List<Secretaire> findSecretairesByNomContaining(String nom);

    /**
     * Récupère les secrétaires d'un cabinet spécifique
     */
    List<Secretaire> findSecretairesByCabinetId(Long cabinetId);

    /**
     * Vérifie si un login est déjà utilisé
     */
    boolean existsByLogin(String login);

    /**
     * Vérifie si un CIN est déjà utilisé
     */
    boolean existsByCin(String cin);

    /**
     * Met à jour la date de dernière connexion d'un secrétaire
     */
    void updateLastLoginDate(Long secretaireId);
}
