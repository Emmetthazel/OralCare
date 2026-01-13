package ma.oralCare.service.modules.ordonnance.impl;

import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.modules.dossierMedical.api.OrdonnanceRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.OrdonnanceRepositoryImpl;
import ma.oralCare.service.modules.ordonnance.api.OrdonnanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class OrdonnanceServiceImpl implements OrdonnanceService {
    
    private final OrdonnanceRepository ordonnanceRepository;
    
    public OrdonnanceServiceImpl() {
        this(new OrdonnanceRepositoryImpl());
    }
    
    public OrdonnanceServiceImpl(OrdonnanceRepository ordonnanceRepository) {
        this.ordonnanceRepository = Objects.requireNonNull(ordonnanceRepository);
    }
    
    @Override
    public Ordonnance createOrdonnance(Ordonnance ordonnance) {
        Objects.requireNonNull(ordonnance, "ordonnance ne doit pas être null");
        ordonnanceRepository.create(ordonnance);
        return ordonnance;
    }
    
    @Override
    public Optional<Ordonnance> getOrdonnanceById(Long id) {
        if (id == null) return Optional.empty();
        return ordonnanceRepository.findById(id);
    }
    
    @Override
    public List<Ordonnance> getAllOrdonnances() {
        return ordonnanceRepository.findAll();
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return ordonnanceRepository.findByDossierMedicaleId(dossierId);
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByConsultationId(Long consultationId) {
        if (consultationId == null) return List.of();
        return ordonnanceRepository.findByConsultationId(consultationId);
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByDate(LocalDate date) {
        if (date == null) return List.of();
        return ordonnanceRepository.findByDate(date);
    }
    
    @Override
    public Ordonnance updateOrdonnance(Ordonnance ordonnance) {
        Objects.requireNonNull(ordonnance, "ordonnance ne doit pas être null");
        if (ordonnance.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une ordonnance sans idEntite");
        }
        ordonnanceRepository.update(ordonnance);
        return ordonnance;
    }
    
    @Override
    public void deleteOrdonnance(Long id) {
        if (id == null) return;
        ordonnanceRepository.deleteById(id);
    }
    
    @Override
    public List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) return List.of();
        return ordonnanceRepository.findPrescriptionsByOrdonnanceId(ordonnanceId);
    }
}
