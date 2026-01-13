package ma.oralCare.mvc.controllers.certificat.api;

import ma.oralCare.entities.dossierMedical.Certificat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CertificatController {
    
    void refreshView();
    
    void handleCreateCertificat(Long consultationId, LocalDate dateDebut, int duree, String noteMedecin);
    
    void handleSelectCertificat(Long certificatId);
    
    void handleDeleteCertificat(Long certificatId);
    
    List<Certificat> getCertificatsByDossierId(Long dossierId);
    
    List<Certificat> getCertificatsByConsultationId(Long consultationId);
    
    Optional<Certificat> getCertificatById(Long id);
    
    void handleSaveCertificat(Certificat certificat);
}
