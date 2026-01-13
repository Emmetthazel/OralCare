package ma.oralCare.mvc.controllers.acte.api;

import ma.oralCare.entities.dossierMedical.Acte;

import java.util.List;
import java.util.Optional;

public interface ActeController {
    
    void refreshView();
    
    List<Acte> getAllActes();
    
    List<Acte> getActesByCategorie(String categorie);
    
    Optional<Acte> getActeById(Long id);
    
    void handleSelectActe(Long acteId);
    
    void handleFilterByCategorie(String categorie);
}
