package ma.oralCare.service.modules.patient.api;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;

import java.util.List;
import java.util.Optional;

/**
 * Couche service pour la gestion des patients.
 *
 * L'objectif est de fournir une API métier claire, qui encapsule
 * l'accès aux repositories et prépare l'utilisation future dans les
 * contrôleurs MVC.
 */
public interface PatientService {

    // --- CRUD de base ---

    List<Patient> getAllPatients();

    Optional<Patient> getPatientById(Long id);

    Patient createPatient(Patient patient);

    Patient updatePatient(Patient patient);

    void deletePatient(Long id);

    // --- Recherches spécifiques ---

    Optional<Patient> getPatientByEmail(String email);

    Optional<Patient> getPatientByTelephone(String telephone);

    List<Patient> searchPatientsByNomPrenom(String keyword);

    long countPatients();

    List<Patient> getPatientsPage(int limit, int offset);

    // --- Gestion des antécédents liés au patient ---

    void addAntecedentToPatient(Long patientId, Long antecedentId);

    void removeAntecedentFromPatient(Long patientId, Long antecedentId);

    void removeAllAntecedentsFromPatient(Long patientId);

    List<Antecedent> getAntecedentsOfPatient(Long patientId);

    List<Patient> getPatientsByAntecedent(Long antecedentId);
}

