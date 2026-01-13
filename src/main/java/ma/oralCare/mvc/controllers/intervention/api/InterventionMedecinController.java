package ma.oralCare.mvc.controllers.intervention.api;

import ma.oralCare.entities.dossierMedical.InterventionMedecin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InterventionMedecinController {
    
    void refreshView(Long consultationId);
    
    void handleAddIntervention(Long consultationId, Long acteId, Integer numDent, BigDecimal prixDePatient);
    
    void handleDeleteIntervention(Long interventionId);
    
    void handleUpdateIntervention(InterventionMedecin intervention);
    
    List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId);
    
    Optional<InterventionMedecin> getInterventionById(Long id);
    
    Double calculateTotalPriceByConsultationId(Long consultationId);
}
