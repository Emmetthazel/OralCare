package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;


public interface OrdonnanceRepository extends CrudRepository<Ordonnance, Long> {


    List<Ordonnance> findByDossierMedicaleId(Long dossierId);


    List<Ordonnance> findByConsultationId(Long consultationId);

    List<Ordonnance> findByDate(LocalDate date);

    List<Prescription> findPrescriptionsByOrdonnanceId(Long ordonnanceId);
}