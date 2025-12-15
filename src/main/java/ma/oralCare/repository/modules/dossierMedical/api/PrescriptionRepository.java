package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;


public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {


    List<Prescription> findByOrdonnanceId(Long ordonnanceId);

}