package ma.oralCare.service.modules.acte.impl;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.ActeRepositoryImpl;
import ma.oralCare.service.modules.acte.api.ActeService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ActeServiceImpl implements ActeService {
    
    private final ActeRepository acteRepository;
    
    public ActeServiceImpl() {
        this(new ActeRepositoryImpl());
    }
    
    public ActeServiceImpl(ActeRepository acteRepository) {
        this.acteRepository = Objects.requireNonNull(acteRepository);
    }
    
    @Override
    public Acte createActe(Acte acte) {
        Objects.requireNonNull(acte, "acte ne doit pas être null");
        acteRepository.create(acte);
        return acte;
    }
    
    @Override
    public Optional<Acte> getActeById(Long id) {
        if (id == null) return Optional.empty();
        return acteRepository.findById(id);
    }
    
    @Override
    public Optional<Acte> getActeByLibelle(String libelle) {
        if (libelle == null || libelle.isBlank()) return Optional.empty();
        return acteRepository.findByLibelle(libelle);
    }
    
    @Override
    public List<Acte> getAllActes() {
        return acteRepository.findAll();
    }
    
    @Override
    public List<Acte> getActesByCategorie(String categorie) {
        if (categorie == null || categorie.isBlank()) return List.of();
        return acteRepository.findByCategorie(categorie);
    }
    
    @Override
    public List<Acte> getActesPage(int limit, int offset) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit doit être > 0");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset ne peut pas être négatif");
        }
        return acteRepository.findPage(limit, offset);
    }
    
    @Override
    public Acte updateActe(Acte acte) {
        Objects.requireNonNull(acte, "acte ne doit pas être null");
        if (acte.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un acte sans idEntite");
        }
        acteRepository.update(acte);
        return acte;
    }
    
    @Override
    public void deleteActe(Long id) {
        if (id == null) return;
        acteRepository.deleteById(id);
    }
    
    @Override
    public long countActes() {
        return acteRepository.count();
    }
    
    @Override
    public boolean acteExists(Long id) {
        if (id == null) return false;
        return acteRepository.existsById(id);
    }
}
