package ma.oralCare.service.modules.dossier.api;

import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Certificat;

import java.util.List;
import java.util.Optional;

public interface DossierMedicaleService {
    
    DossierMedicale createDossierMedicale(DossierMedicale dossier);
    
    Optional<DossierMedicale> getDossierById(Long id);
    
    Optional<DossierMedicale> getDossierByPatientId(Long patientId);
    
    List<DossierMedicale> getDossiersByMedecinId(Long medecinId);
    
    List<DossierMedicale> getAllDossiers();
    
    DossierMedicale updateDossierMedicale(DossierMedicale dossier);
    
    void deleteDossierMedicale(Long id);
    
    List<Consultation> getConsultationsByDossierId(Long dossierId);
    
    List<Ordonnance> getOrdonnancesByDossierId(Long dossierId);
    
    List<Certificat> getCertificatsByDossierId(Long dossierId);
}
