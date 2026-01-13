package ma.oralCare.service.modules.ordonnance.api;

import ma.oralCare.entities.dossierMedical.Prescription;

import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    
    Prescription createPrescription(Prescription prescription);
    
    Optional<Prescription> getPrescriptionById(Long id);
    
    List<Prescription> getAllPrescriptions();
    
    List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId);
    
    Prescription updatePrescription(Prescription prescription);
    
    void deletePrescription(Long id);
}
