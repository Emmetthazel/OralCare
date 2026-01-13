package ma.oralCare.service.modules.dossier.impl;

import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import ma.oralCare.service.modules.dossier.api.DossierMedicaleService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DossierMedicaleServiceImpl implements DossierMedicaleService {
    
    private final DossierMedicaleRepository dossierRepository;
    
    public DossierMedicaleServiceImpl() {
        this(new DossierMedicaleRepositoryImpl());
    }
    
    public DossierMedicaleServiceImpl(DossierMedicaleRepository dossierRepository) {
        this.dossierRepository = Objects.requireNonNull(dossierRepository);
    }
    
    @Override
    public DossierMedicale createDossierMedicale(DossierMedicale dossier) {
        Objects.requireNonNull(dossier, "dossier ne doit pas être null");
        dossierRepository.create(dossier);
        return dossier;
    }
    
    @Override
    public Optional<DossierMedicale> getDossierById(Long id) {
        if (id == null) return Optional.empty();
        return dossierRepository.findById(id);
    }
    
    @Override
    public Optional<DossierMedicale> getDossierByPatientId(Long patientId) {
        if (patientId == null) return Optional.empty();
        return dossierRepository.findByPatientId(patientId);
    }
    
    @Override
    public List<DossierMedicale> getDossiersByMedecinId(Long medecinId) {
        if (medecinId == null) return List.of();
        return dossierRepository.findByMedecinId(medecinId);
    }
    
    @Override
    public List<DossierMedicale> getAllDossiers() {
        return dossierRepository.findAll();
    }
    
    @Override
    public DossierMedicale updateDossierMedicale(DossierMedicale dossier) {
        Objects.requireNonNull(dossier, "dossier ne doit pas être null");
        if (dossier.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour un dossier sans idEntite");
        }
        dossierRepository.update(dossier);
        return dossier;
    }
    
    @Override
    public void deleteDossierMedicale(Long id) {
        if (id == null) return;
        dossierRepository.deleteById(id);
    }
    
    @Override
    public List<Consultation> getConsultationsByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return dossierRepository.findConsultationsByDossierId(dossierId);
    }
    
    @Override
    public List<Ordonnance> getOrdonnancesByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return dossierRepository.findOrdonnancesByDossierId(dossierId);
    }
    
    @Override
    public List<Certificat> getCertificatsByDossierId(Long dossierId) {
        if (dossierId == null) return List.of();
        return dossierRepository.findCertificatsByDossierId(dossierId);
    }
}
