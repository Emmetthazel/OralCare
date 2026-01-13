package ma.oralCare.service.modules.acte.api;

import ma.oralCare.entities.dossierMedical.Acte;

import java.util.List;
import java.util.Optional;

public interface ActeService {
    
    Acte createActe(Acte acte);
    
    Optional<Acte> getActeById(Long id);
    
    Optional<Acte> getActeByLibelle(String libelle);
    
    List<Acte> getAllActes();
    
    List<Acte> getActesByCategorie(String categorie);
    
    List<Acte> getActesPage(int limit, int offset);
    
    Acte updateActe(Acte acte);
    
    void deleteActe(Long id);
    
    long countActes();
    
    boolean acteExists(Long id);
}
