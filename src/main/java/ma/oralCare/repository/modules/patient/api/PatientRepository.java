package ma.oralCare.repository.modules.patient.api;

import ma.oralCare.repository.common.CrudRepository;
import ma.oralCare.entities.patient.Patient;

import java.time.LocalDate;
import java.util.List;

public interface PatientRepository extends CrudRepository<Patient, Long> {

    List<Patient> findByNom(String nom);
    List<Patient> findByTelephone(String telephone);
    List<Patient> findByDateNaissance(LocalDate date);
    List<Patient> findBySexe(ma.oralCare.entities.enums.Sexe sexe);
    List<Patient> findByAssurance(ma.oralCare.entities.enums.Assurance assurance);
    List<Patient> findByAdresseContaining(String adresse);
    List<Patient> findByDateNaissanceBetween(LocalDate debut, LocalDate fin);
    List<Patient> searchPatients(String keyword);
}
