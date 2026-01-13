package ma.oralCare.mvc.controllers.acte.impl;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.mvc.controllers.acte.api.ActeController;
import ma.oralCare.mvc.ui1.medecin.TreatmentView;
import ma.oralCare.service.modules.acte.api.ActeService;
import ma.oralCare.service.modules.acte.impl.ActeServiceImpl;

import java.util.List;
import java.util.Optional;

public class ActeControllerImpl implements ActeController {
    
    private final TreatmentView view;
    private final ActeService acteService;
    
    public ActeControllerImpl(TreatmentView view) {
        this.view = view;
        this.acteService = new ActeServiceImpl();
    }
    
    @Override
    public void refreshView() {
        try {
            List<Acte> actes = acteService.getAllActes();
            // Mettre à jour la vue avec les actes
            // À implémenter selon TreatmentView
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Acte> getAllActes() {
        return acteService.getAllActes();
    }
    
    @Override
    public List<Acte> getActesByCategorie(String categorie) {
        if (categorie == null || categorie.isBlank()) return List.of();
        return acteService.getActesByCategorie(categorie);
    }
    
    @Override
    public Optional<Acte> getActeById(Long id) {
        if (id == null) return Optional.empty();
        return acteService.getActeById(id);
    }
    
    @Override
    public void handleSelectActe(Long acteId) {
        if (acteId == null) return;
        // Logique de sélection d'acte
        // À implémenter selon TreatmentView
    }
    
    @Override
    public void handleFilterByCategorie(String categorie) {
        List<Acte> actes = getActesByCategorie(categorie);
        // Mettre à jour la vue avec les actes filtrés
        // À implémenter selon TreatmentView
    }
}
