package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;


public interface CertificatRepository extends CrudRepository<Certificat, Long> {

    List<Certificat> findByDossierMedicaleId(Long dossierMedicaleId);


    List<Certificat> findByDateDebut(LocalDate date);


    List<Certificat> findValidCertificates(LocalDate currentDate);


    List<Certificat> findByNoteMedecinContaining(String noteFragment);
}