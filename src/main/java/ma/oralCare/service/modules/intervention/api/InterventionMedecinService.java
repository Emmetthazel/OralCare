package ma.oralCare.service.modules.intervention.api;

import ma.oralCare.entities.dossierMedical.InterventionMedecin;

import java.util.List;
import java.util.Optional;

public interface InterventionMedecinService {
    
    InterventionMedecin createIntervention(InterventionMedecin intervention);
    
    Optional<InterventionMedecin> getInterventionById(Long id);
    
    List<InterventionMedecin> getAllInterventions();
    
    List<InterventionMedecin> getInterventionsByConsultationId(Long consultationId);
    
    List<InterventionMedecin> getInterventionsByActeId(Long acteId);
    
    InterventionMedecin updateIntervention(InterventionMedecin intervention);
    
    void deleteIntervention(Long id);
    
    Double calculateTotalPriceByConsultationId(Long consultationId);
}
