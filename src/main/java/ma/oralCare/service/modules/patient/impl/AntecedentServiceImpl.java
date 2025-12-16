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
 * Implémentation simple de {@link AntecedentService} basée sur {@link AntecedentRepository}.
 */
public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepository;

    public AntecedentServiceImpl() {
        this(new AntecedentRepositoryImpl());
    }

    public AntecedentServiceImpl(AntecedentRepository antecedentRepository) {
        this.antecedentRepository = Objects.requireNonNull(antecedentRepository);
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    @Override
    public Antecedent createAntecedent(Antecedent antecedent) {
        Objects.requireNonNull(antecedent, "antecedent ne doit pas être null");
        antecedentRepository.create(antecedent);
        return antecedent;
    }

    @Override
    public Antecedent updateAntecedent(Antecedent antecedent) {
        Objects.requireNonNull(antecedent, "antecedent ne doit pas être null");
        if (antecedent.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un antécédent sans idEntite");
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
        if (id == null) return;
        antecedentRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Recherches spécifiques
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
    // Liaisons Many-to-Many
    // -------------------------------------------------------------------------

    @Override
    public void linkAntecedentToPatient(Long antecedentId, Long patientId) {
        if (antecedentId == null || patientId == null) {
            throw new IllegalArgumentException("antecedentId et patientId doivent être non nuls");
        }
        antecedentRepository.linkAntecedentToPatient(antecedentId, patientId);
    }

    @Override
    public void unlinkAntecedentFromPatient(Long antecedentId, Long patientId) {
        if (antecedentId == null || patientId == null) return;
        antecedentRepository.unlinkAntecedentFromPatient(antecedentId, patientId);
    }
}


