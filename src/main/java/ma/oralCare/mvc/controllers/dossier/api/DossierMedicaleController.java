package ma.oralCare.mvc.controllers.dossier.api;

import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.patient.Patient;

import java.util.List;
import java.util.Optional;

public interface DossierMedicaleController {
    
    void refreshView();
    
    Optional<DossierMedicale> getDossierByPatientId(Long patientId);
    
    List<DossierMedicale> getDossiersByMedecinId(Long medecinId);
    
    DossierMedicale createDossierForPatient(Patient patient, Long medecinId);
    
    void handleSearchPatient(String searchTerm);
    
    void handleSelectDossier(Long dossierId);
    
    void handleSaveNotes(Long dossierId, String notes);
    
    List<DossierMedicale> getAllDossiers();
}
