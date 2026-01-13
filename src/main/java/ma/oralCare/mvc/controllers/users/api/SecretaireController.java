package ma.oralCare.mvc.controllers.users.api;

import ma.oralCare.entities.users.Secretaire;

import java.util.List;
import java.util.Optional;

public interface SecretaireController {

    /**
     * Crée un nouveau secrétaire
     */
    Secretaire createSecretaire(Secretaire secretaire);

    /**
     * Met à jour un secrétaire existant
     */
    Secretaire updateSecretaire(Secretaire secretaire);

    /**
     * Supprime un secrétaire par son ID
     */
    void deleteSecretaire(Long id);

    /**
     * Récupère un secrétaire par son ID
     */
    Optional<Secretaire> getSecretaireById(Long id);

    /**
     * Récupère tous les secrétaires
     */
    List<Secretaire> getAllSecretaires();

    /**
     * Récupère un secrétaire par son login
     */
    Optional<Secretaire> getSecretaireByLogin(String login);

    /**
     * Récupère un secrétaire par son CIN
     */
    Optional<Secretaire> getSecretaireByCin(String cin);

    /**
     * Recherche des secrétaires par nom
     */
    List<Secretaire> searchSecretairesByNom(String nom);

    /**
     * Récupère les secrétaires d'un cabinet
     */
    List<Secretaire> getSecretairesByCabinet(Long cabinetId);

    /**
     * Vérifie si un login existe
     */
    boolean checkLoginExists(String login);

    /**
     * Vérifie si un CIN existe
     */
    boolean checkCinExists(String cin);

    /**
     * Met à jour la date de dernière connexion
     */
    void updateLastLogin(Long secretaireId);

    /**
     * Rafraîchit les données du dashboard secrétaire
     */
    void refreshDashboardData();
}
