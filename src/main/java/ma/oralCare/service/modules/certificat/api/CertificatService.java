package ma.oralCare.service.modules.certificat.api;

import ma.oralCare.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificatService {
    
    Certificat createCertificat(Certificat certificat);
    
    Optional<Certificat> getCertificatById(Long id);
    
    List<Certificat> getAllCertificats();
    
    List<Certificat> getCertificatsByDossierId(Long dossierId);
    
    List<Certificat> getCertificatsByDateDebut(LocalDate date);
    
    List<Certificat> getValidCertificates(LocalDate currentDate);
    
    List<Certificat> getCertificatsByNoteContaining(String noteFragment);
    
    Certificat updateCertificat(Certificat certificat);
    
    void deleteCertificat(Long id);
}
