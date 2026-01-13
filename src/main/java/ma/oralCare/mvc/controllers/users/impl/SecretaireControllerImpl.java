package ma.oralCare.mvc.controllers.users.impl;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.mvc.controllers.users.api.SecretaireController;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.service.modules.users.api.SecretaireService;

import java.util.List;
import java.util.Optional;

public class SecretaireControllerImpl implements SecretaireController {

    private final SecretaireService secretaireService;
    private SecretaireDashboard view;

    public SecretaireControllerImpl(SecretaireService secretaireService) {
        this.secretaireService = secretaireService;
    }

    public void setView(SecretaireDashboard view) {
        this.view = view;
    }

    @Override
    public Secretaire createSecretaire(Secretaire secretaire) {
        try {
            Secretaire createdSecretaire = secretaireService.createSecretaire(secretaire);
            refreshDashboardData();
            return createdSecretaire;
        } catch (IllegalArgumentException e) {
            showError("Erreur de création: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            showError("Erreur lors de la création du secrétaire: " + e.getMessage());
            throw new RuntimeException("Erreur création secrétaire", e);
        }
    }

    @Override
    public Secretaire updateSecretaire(Secretaire secretaire) {
        try {
            Secretaire updatedSecretaire = secretaireService.updateSecretaire(secretaire);
            refreshDashboardData();
            return updatedSecretaire;
        } catch (IllegalArgumentException e) {
            showError("Erreur de mise à jour: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            showError("Erreur lors de la mise à jour du secrétaire: " + e.getMessage());
            throw new RuntimeException("Erreur mise à jour secrétaire", e);
        }
    }

    @Override
    public void deleteSecretaire(Long id) {
        try {
            secretaireService.deleteSecretaire(id);
            refreshDashboardData();
            showInfo("Secrétaire supprimé avec succès");
        } catch (Exception e) {
            showError("Erreur lors de la suppression du secrétaire: " + e.getMessage());
            throw new RuntimeException("Erreur suppression secrétaire", e);
        }
    }

    @Override
    public Optional<Secretaire> getSecretaireById(Long id) {
        try {
            return secretaireService.findSecretaireById(id);
        } catch (Exception e) {
            showError("Erreur lors de la récupération du secrétaire: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Secretaire> getAllSecretaires() {
        try {
            return secretaireService.findAllSecretaires();
        } catch (Exception e) {
            showError("Erreur lors de la récupération des secrétaires: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public Optional<Secretaire> getSecretaireByLogin(String login) {
        try {
            return secretaireService.findSecretaireByLogin(login);
        } catch (Exception e) {
            showError("Erreur lors de la recherche par login: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Secretaire> getSecretaireByCin(String cin) {
        try {
            return secretaireService.findSecretaireByCin(cin);
        } catch (Exception e) {
            showError("Erreur lors de la recherche par CIN: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Secretaire> searchSecretairesByNom(String nom) {
        try {
            return secretaireService.findSecretairesByNomContaining(nom);
        } catch (Exception e) {
            showError("Erreur lors de la recherche par nom: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public List<Secretaire> getSecretairesByCabinet(Long cabinetId) {
        try {
            return secretaireService.findSecretairesByCabinetId(cabinetId);
        } catch (Exception e) {
            showError("Erreur lors de la récupération des secrétaires du cabinet: " + e.getMessage());
            return List.of();
        }
    }

    @Override
    public boolean checkLoginExists(String login) {
        try {
            return secretaireService.existsByLogin(login);
        } catch (Exception e) {
            showError("Erreur lors de la vérification du login: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkCinExists(String cin) {
        try {
            return secretaireService.existsByCin(cin);
        } catch (Exception e) {
            showError("Erreur lors de la vérification du CIN: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void updateLastLogin(Long secretaireId) {
        try {
            secretaireService.updateLastLoginDate(secretaireId);
        } catch (Exception e) {
            showError("Erreur lors de la mise à jour de la date de connexion: " + e.getMessage());
        }
    }

    @Override
    public void refreshDashboardData() {
        if (view != null) {
            try {
                // Rafraîchir les données du dashboard avec les informations actualisées
                List<Secretaire> secretaires = getAllSecretaires();
                // Mettre à jour les vues si nécessaire
                updateViewData(secretaires);
            } catch (Exception e) {
                showError("Erreur lors du rafraîchissement des données: " + e.getMessage());
            }
        }
    }

    /**
     * Met à jour les données de la vue
     */
    private void updateViewData(List<Secretaire> secretaires) {
        // Implémenter la logique de mise à jour de la vue
        // Par exemple, mettre à jour les tableaux, listes, etc.
        if (view != null) {
            // Appeler les méthodes de la vue pour mettre à jour les composants
            // view.updateSecretaireTable(secretaires);
            // view.updateStatistics(secretaires);
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        System.err.println("ERREUR: " + message);
        if (view != null) {
            // view.showError(message);
        }
    }

    /**
     * Affiche un message d'information
     */
    private void showInfo(String message) {
        System.out.println("INFO: " + message);
        if (view != null) {
            // view.showInfo(message);
        }
    }
}
