package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface DossierMedicaleRepository extends CrudRepository<DossierMedicale, Long> {


    Optional<DossierMedicale> findByPatientId(Long patientId);


    List<DossierMedicale> findByMedecinId(Long medecinId);

    List<Consultation> findConsultationsByDossierId(Long dossierId);


    List<Ordonnance> findOrdonnancesByDossierId(Long dossierId);


    List<Certificat> findCertificatsByDossierId(Long dossierId);

}