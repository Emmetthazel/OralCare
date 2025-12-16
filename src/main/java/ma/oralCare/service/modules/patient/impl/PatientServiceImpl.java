package ma.oralCare.service.modules.patient.impl;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.service.modules.patient.api.PatientService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implémentation de {@link PatientService} qui délègue la logique
 * métier simple aux repositories Patient / Antecedent.
 *
 * Cette classe respecte le même style que la couche repository :
 * - utilisation directe des implémentations via new XxxRepositoryImpl()
 * - aucune dépendance à un framework d'injection.
 */
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    public PatientServiceImpl() {
        this(new PatientRepositoryImpl());
    }

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = Objects.requireNonNull(patientRepository);
    }

    // -------------------------------------------------------------------------
    // CRUD de base
    // -------------------------------------------------------------------------

    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getPatientById(Long id) {
        if (id == null) return Optional.empty();
        return patientRepository.findById(id);
    }

    @Override
    public Patient createPatient(Patient patient) {
        Objects.requireNonNull(patient, "patient ne doit pas être null");
        patientRepository.create(patient);
        return patient;
    }

    @Override
    public Patient updatePatient(Patient patient) {
        Objects.requireNonNull(patient, "patient ne doit pas être null");
        if (patient.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un patient sans idEntite");
        }
        patientRepository.update(patient);
        return patient;
    }

    @Override
    public void deletePatient(Long id) {
        if (id == null) return;
        patientRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Recherches spécifiques
    // -------------------------------------------------------------------------

    @Override
    public Optional<Patient> getPatientByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return patientRepository.findByEmail(email);
    }

    @Override
    public Optional<Patient> getPatientByTelephone(String telephone) {
        if (telephone == null || telephone.isBlank()) return Optional.empty();
        return patientRepository.findByTelephone(telephone);
    }

    @Override
    public List<Patient> searchPatientsByNomPrenom(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return patientRepository.searchByNomPrenom(keyword);
    }

    @Override
    public long countPatients() {
        return patientRepository.count();
    }

    @Override
    public List<Patient> getPatientsPage(int limit, int offset) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit doit être > 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset ne peut pas être négatif");
        }
        return patientRepository.findPage(limit, offset);
    }

    // -------------------------------------------------------------------------
    // Gestion des antécédents d'un patient (Many-to-Many)
    // -------------------------------------------------------------------------

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        if (patientId == null || antecedentId == null) {
            throw new IllegalArgumentException("patientId et antecedentId doivent être non nuls");
        }
        patientRepository.addAntecedentToPatient(patientId, antecedentId);
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        if (patientId == null || antecedentId == null) {
            return;
        }
        patientRepository.removeAntecedentFromPatient(patientId, antecedentId);
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        if (patientId == null) return;
        patientRepository.removeAllAntecedentsFromPatient(patientId);
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        if (patientId == null) return List.of();
        return patientRepository.getAntecedentsOfPatient(patientId);
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        if (antecedentId == null) return List.of();
        return patientRepository.getPatientsByAntecedent(antecedentId);
    }
}


