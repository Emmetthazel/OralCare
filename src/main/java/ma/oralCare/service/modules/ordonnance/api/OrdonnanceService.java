package ma.oralCare.service.modules.ordonnance.api;

import ma.oralCare.entities.dossierMedical.Ordonnance;
import ma.oralCare.entities.dossierMedical.Prescription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrdonnanceService {
    
    Ordonnance createOrdonnance(Ordonnance ordonnance);
    
    Optional<Ordonnance> getOrdonnanceById(Long id);
    
    List<Ordonnance> getAllOrdonnances();
    
    List<Ordonnance> getOrdonnancesByDossierId(Long dossierId);
    
    List<Ordonnance> getOrdonnancesByConsultationId(Long consultationId);
    
    List<Ordonnance> getOrdonnancesByDate(LocalDate date);
    
    Ordonnance updateOrdonnance(Ordonnance ordonnance);
    
    void deleteOrdonnance(Long id);
    
    List<Prescription> getPrescriptionsByOrdonnanceId(Long ordonnanceId);
}
