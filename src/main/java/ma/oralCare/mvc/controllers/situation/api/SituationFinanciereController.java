package ma.oralCare.mvc.controllers.situation.api;

import ma.oralCare.entities.dossierMedical.SituationFinanciere;

import java.util.Optional;

public interface SituationFinanciereController {
    
    void refreshView(Long dossierId);
    
    Optional<SituationFinanciere> getSituationByDossierId(Long dossierId);
    
    Double calculerSoldePatient(Long patientId);
    
    void handleUpdateSituation(SituationFinanciere situation);
    
    void handleRefreshSituation(Long dossierId);
}
