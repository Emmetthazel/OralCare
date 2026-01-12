package ma.oralCare.service.modules.patient.impl;

import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;
import ma.oralCare.repository.modules.patient.impl.AntecedentRepositoryImpl;
import ma.oralCare.service.modules.patient.api.AntecedentService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implémentation de {@link AntecedentService}.
 * Cette version est corrigée pour fonctionner avec le Repository autonome.
 */
public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepository;

    /**
     * ✅ Constructeur par défaut.
     * Initialise le repository autonome qui gère sa propre connexion via SessionFactory.
     */
    public AntecedentServiceImpl() {
        this.antecedentRepository = new AntecedentRepositoryImpl();
    }

    /**
     * ✅ Constructeur par injection.
     * Utile pour les tests ou si l'on souhaite passer une instance spécifique.
     * @param antecedentRepository l'instance du repository à utiliser.
     */
    public AntecedentServiceImpl(AntecedentRepository antecedentRepository) {
        this.antecedentRepository = Objects.requireNonNull(antecedentRepository, "Le repository ne peut pas être null");
    }

    // -------------------------------------------------------------------------
    // Opérations CRUD
    // -------------------------------------------------------------------------

    @Override
    public Antecedent createAntecedent(Antecedent antecedent) {
        Objects.requireNonNull(antecedent, "L'antécédent ne doit pas être null");
        antecedentRepository.create(antecedent);
        return antecedent;
    }

    @Override
    public Antecedent updateAntecedent(Antecedent antecedent) {
        Objects.requireNonNull(antecedent, "L'antécédent ne doit pas être null");
        if (antecedent.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un antécédent sans ID");
        }
        antecedentRepository.update(antecedent);
        return antecedent;
    }

    @Override
    public Optional<Antecedent> getAntecedentById(Long id) {
        if (id == null) return Optional.empty();
        return antecedentRepository.findById(id);
    }

    @Override
    public List<Antecedent> getAllAntecedents() {
        return antecedentRepository.findAll();
    }

    @Override
    public void deleteAntecedent(Long id) {
        if (id != null) {
            antecedentRepository.deleteById(id);
        }
    }

    // -------------------------------------------------------------------------
    // Méthodes de recherche
    // -------------------------------------------------------------------------

    @Override
    public List<Antecedent> findByCategorie(CategorieAntecedent categorie) {
        if (categorie == null) return List.of();
        return antecedentRepository.findByCategorie(categorie);
    }

    @Override
    public List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque) {
        if (niveauRisque == null) return List.of();
        return antecedentRepository.findByNiveauRisque(niveauRisque);
    }

    @Override
    public List<Antecedent> findByNomContaining(String nom) {
        if (nom == null || nom.isBlank()) return List.of();
        return antecedentRepository.findByNomContaining(nom);
    }

    @Override
    public List<Antecedent> findByPatientId(Long patientId) {
        if (patientId == null) return List.of();
        return antecedentRepository.findByPatientId(patientId);
    }

    // -------------------------------------------------------------------------
    // Liaisons Patient-Antécédent
    // -------------------------------------------------------------------------

    @Override
    public void linkAntecedentToPatient(Long antecedentId, Long patientId) {
        if (antecedentId == null || patientId == null) {
            throw new IllegalArgumentException("Les IDs de l'antécédent et du patient doivent être fournis");
        }
        antecedentRepository.linkAntecedentToPatient(antecedentId, patientId);
    }

    @Override
    public void unlinkAntecedentFromPatient(Long antecedentId, Long patientId) {
        if (antecedentId != null && patientId != null) {
            antecedentRepository.unlinkAntecedentFromPatient(antecedentId, patientId);
        }
    }
}