package ma.oralCare.repository.modules.patient.api;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;


public interface AntecedentRepository extends CrudRepository<Antecedent, Long> {

    void save(Antecedent antecedent); // Ajout indispensable pour le Service

    List<Antecedent> findByCategorie(CategorieAntecedent categorie);


    List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque);


    List<Antecedent> findByNomContaining(String nom);


    List<Antecedent> findByPatientId(Long patientId);

    void linkAntecedentToPatient(Long antecedentId, Long patientId);

    void unlinkAntecedentFromPatient(Long antecedentId, Long patientId);
}