package ma.oralCare.service.modules.consultation.api;

import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.enums.StatutConsultation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultationService {
    
    Consultation createConsultation(Consultation consultation);
    
    Optional<Consultation> getConsultationById(Long id);
    
    List<Consultation> getAllConsultations();
    
    List<Consultation> getConsultationsByDossierId(Long dossierId);
    
    List<Consultation> getConsultationsByStatut(StatutConsultation statut);
    
    List<Consultation> getConsultationsByDate(LocalDate date);
    
    Consultation updateConsultation(Consultation consultation);
    
    void updateConsultationStatut(Long id, StatutConsultation statut);
    
    void updateConsultationObservation(Long id, String observation);
    
    void deleteConsultation(Long id);
    
    Consultation terminerConsultation(Long consultationId, String observations);
}
