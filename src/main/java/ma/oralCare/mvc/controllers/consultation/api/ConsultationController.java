package ma.oralCare.mvc.controllers.consultation.api;

import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;
import ma.oralCare.entities.dossierMedical.Consultation;

public interface ConsultationController {
    
    void preparerNouvelleConsultation(RDVPanelDTO rdv);
    
    void handleFinishConsultation(String observations);
    
    void handleAddIntervention();
    
    void handleNavigateToOrdonnances();
    
    void handleNavigateToCertificats();
    
    void handleNavigateToFactures();
    
    Long getCurrentConsultationId();
    
    Consultation getCurrentConsultation();
}
