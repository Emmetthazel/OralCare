package ma.oralCare.service.modules.ordonnance.impl;

import ma.oralCare.entities.dossierMedical.Prescription;
import ma.oralCare.repository.modules.dossierMedical.api.PrescriptionRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.PrescriptionRepositoryImpl;
import ma.oralCare.service.modules.ordonnance.api.PrescriptionService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PrescriptionServiceImpl implements PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;
    
    public PrescriptionServiceImpl() {
        this(new PrescriptionRepositoryImpl());
    }
    
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = Objects.requireNonNull(prescriptionRepository);
    }
    
    @Override
    public Prescription createPrescription(Prescription prescription) {
        Objects.requireNonNull(prescription, "prescription ne doit pas être null");
        prescriptionRepository.create(prescription);
        return prescription;
    }
    
    @Override
    public Optional<Prescription> getPrescriptionById(Long id) {
        if (id == null) return Optional.empty();
        return prescriptionRepository.findById(id);
    }
    
    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }
    
    @Override
    public List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId) {
        if (ordonnanceId == null) return List.of();
        return prescriptionRepository.findByOrdonnanceId(ordonnanceId);
    }
    
    @Override
    public Prescription updatePrescription(Prescription prescription) {
        Objects.requireNonNull(prescription, "prescription ne doit pas être null");
        if (prescription.getIdEntite() == null) {
            throw new IllegalArgumentException("Impossible de mettre à jour une prescription sans idEntite");
        }
        prescriptionRepository.update(prescription);
        return prescription;
    }
    
    @Override
    public void deletePrescription(Long id) {
        if (id == null) return;
        prescriptionRepository.deleteById(id);
    }
}
