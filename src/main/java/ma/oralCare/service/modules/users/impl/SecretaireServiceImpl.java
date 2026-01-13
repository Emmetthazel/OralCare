package ma.oralCare.service.modules.users.impl;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.repository.modules.users.api.SecretaireRepository;
import ma.oralCare.service.modules.users.api.SecretaireService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SecretaireServiceImpl implements SecretaireService {

    private final SecretaireRepository secretaireRepository;

    public SecretaireServiceImpl(SecretaireRepository secretaireRepository) {
        this.secretaireRepository = secretaireRepository;
    }

    @Override
    public Secretaire createSecretaire(Secretaire secretaire) {
        if (secretaire == null) {
            throw new IllegalArgumentException("Le secrétaire ne peut pas être null");
        }
        
        // Validation des champs obligatoires
        validateSecretaire(secretaire);
        
        // Vérification de l'unicité du login et CIN
        if (existsByLogin(secretaire.getLogin())) {
            throw new IllegalArgumentException("Le login '" + secretaire.getLogin() + "' est déjà utilisé");
        }
        
        if (existsByCin(secretaire.getCin())) {
            throw new IllegalArgumentException("Le CIN '" + secretaire.getCin() + "' est déjà utilisé");
        }
        
        secretaireRepository.create(secretaire);
        return secretaire;
    }

    @Override
    public Secretaire updateSecretaire(Secretaire secretaire) {
        if (secretaire == null || secretaire.getIdEntite() == null) {
            throw new IllegalArgumentException("Le secrétaire et son ID ne peuvent pas être null");
        }
        
        // Vérifier que le secrétaire existe
        Optional<Secretaire> existingSecretaire = secretaireRepository.findById(secretaire.getIdEntite());
        if (existingSecretaire.isEmpty()) {
            throw new IllegalArgumentException("Aucun secrétaire trouvé avec l'ID: " + secretaire.getIdEntite());
        }
        
        // Validation des champs
        validateSecretaire(secretaire);
        
        // Vérifier l'unicité si le login ou CIN a changé
        Secretaire existing = existingSecretaire.get();
        if (!existing.getLogin().equals(secretaire.getLogin()) && existsByLogin(secretaire.getLogin())) {
            throw new IllegalArgumentException("Le login '" + secretaire.getLogin() + "' est déjà utilisé");
        }
        
        if (!existing.getCin().equals(secretaire.getCin()) && existsByCin(secretaire.getCin())) {
            throw new IllegalArgumentException("Le CIN '" + secretaire.getCin() + "' est déjà utilisé");
        }
        
        secretaireRepository.update(secretaire);
        return secretaire;
    }

    @Override
    public void deleteSecretaire(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du secrétaire ne peut pas être null");
        }
        
        secretaireRepository.deleteById(id);
    }

    @Override
    public Optional<Secretaire> findSecretaireById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return secretaireRepository.findById(id);
    }

    @Override
    public List<Secretaire> findAllSecretaires() {
        return secretaireRepository.findAll();
    }

    @Override
    public Optional<Secretaire> findSecretaireByLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            return Optional.empty();
        }
        return secretaireRepository.findByLogin(login.trim());
    }

    @Override
    public Optional<Secretaire> findSecretaireByCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            return Optional.empty();
        }
        return secretaireRepository.findByCin(cin.trim());
    }

    @Override
    public List<Secretaire> findSecretairesByNomContaining(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            return secretaireRepository.findAll();
        }
        return secretaireRepository.findAllByNomContaining(nom.trim());
    }

    @Override
    public List<Secretaire> findSecretairesByCabinetId(Long cabinetId) {
        if (cabinetId == null) {
            return List.of();
        }
        return secretaireRepository.findAllByCabinetId(cabinetId);
    }

    @Override
    public boolean existsByLogin(String login) {
        if (login == null || login.trim().isEmpty()) {
            return false;
        }
        return secretaireRepository.findByLogin(login.trim()).isPresent();
    }

    @Override
    public boolean existsByCin(String cin) {
        if (cin == null || cin.trim().isEmpty()) {
            return false;
        }
        return secretaireRepository.findByCin(cin.trim()).isPresent();
    }

    @Override
    public void updateLastLoginDate(Long secretaireId) {
        if (secretaireId == null) {
            throw new IllegalArgumentException("L'ID du secrétaire ne peut pas être null");
        }
        
        Optional<Secretaire> secretaireOpt = secretaireRepository.findById(secretaireId);
        if (secretaireOpt.isPresent()) {
            Secretaire secretaire = secretaireOpt.get();
            secretaire.setLastLoginDate(LocalDate.now());
            secretaireRepository.update(secretaire);
            // No return needed for void method
        }
    }

    /**
     * Valide les champs obligatoires d'un secrétaire
     */
    private void validateSecretaire(Secretaire secretaire) {
        if (secretaire.getNom() == null || secretaire.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        
        if (secretaire.getLogin() == null || secretaire.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Le login est obligatoire");
        }
        
        if (secretaire.getMotDePass() == null || secretaire.getMotDePass().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }
        
        if (secretaire.getCin() == null || secretaire.getCin().trim().isEmpty()) {
            throw new IllegalArgumentException("Le CIN est obligatoire");
        }
        
        if (secretaire.getNumCNSS() == null || secretaire.getNumCNSS().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro CNSS est obligatoire");
        }
        
        if (secretaire.getCommission() == null || secretaire.getCommission().doubleValue() < 0) {
            throw new IllegalArgumentException("La commission doit être positive ou nulle");
        }
    }
}
