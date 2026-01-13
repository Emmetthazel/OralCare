package ma.oralCare.mvc.controllers.ordonnance.api;

import ma.oralCare.entities.dossierMedical.Ordonnance;

import java.util.List;
import java.util.Optional;

public interface OrdonnanceController {
    
    void refreshView();
    
    void handleCreateOrdonnance(Long consultationId, Long dossierId);
    
    void handleSelectOrdonnance(Long ordonnanceId);
    
    void handleDeleteOrdonnance(Long ordonnanceId);
    
    List<Ordonnance> getOrdonnancesByDossierId(Long dossierId);
    
    List<Ordonnance> getOrdonnancesByConsultationId(Long consultationId);
    
    Optional<Ordonnance> getOrdonnanceById(Long id);
    
    void handleAddPrescription(Long ordonnanceId);
    
    void handleSaveOrdonnance(Ordonnance ordonnance);
}
