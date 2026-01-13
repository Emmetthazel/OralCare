package ma.oralCare.service.modules.caisse.impl;

import ma.oralCare.entities.dossierMedical.SituationFinanciere;
import ma.oralCare.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.SituationFinanciereRepositoryImpl;
import ma.oralCare.service.modules.caisse.api.SituationFinanciereService;

import java.util.Objects;
import java.util.Optional;

public class SituationFinanciereServiceImpl implements SituationFinanciereService {
    
    private final SituationFinanciereRepository situationRepository;
    
    public SituationFinanciereServiceImpl() {
        this(new SituationFinanciereRepositoryImpl());
    }
    
    public SituationFinanciereServiceImpl(SituationFinanciereRepository situationRepository) {
        this.situationRepository = Objects.requireNonNull(situationRepository);
    }
    
    @Override
    public Optional<SituationFinanciere> getSituationFinanciereById(Long id) {
        if (id == null) return Optional.empty();
        return situationRepository.findById(id);
    }
    
    @Override
    public SituationFinanciere sauverSituationFinanciere(SituationFinanciere situation) {
        Objects.requireNonNull(situation, "situation ne doit pas être null");
        if (situation.getIdEntite() == null) {
            situationRepository.create(situation);
        } else {
            situationRepository.update(situation);
        }
        return situation;
    }
    
    @Override
    public Double calculerSoldePatient(Long patientId) {
        // TODO: Implémenter le calcul du solde via le repository
        return 0.0;
    }
    
    @Override
    public void mettreAJourSolde(Long situationId) {
        if (situationId == null) return;
        // TODO: Implémenter la mise à jour du solde
    }
}
