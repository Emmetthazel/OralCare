package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface InterventionMedecinRepository extends CrudRepository<InterventionMedecin, Long> {
    List<InterventionMedecin> findByActeId(Long acteId);
    List<InterventionMedecin> findPage(int limit, int offset);
    List<InterventionMedecin> consulterParConsultation(Long idConsultation);
    Double calculateTotalPatientPriceByConsultationId(Long consultationId);
    InterventionMedecin appliquerRemisePonctuelle(Long interventionId, Double pourcentageRemise);
    Optional<Acte> findActeByInterventionId(Long interventionId);
    boolean existsByConsultationActeAndDent(Long consultationId, Long acteId, Integer numDent);
    List<InterventionMedecin> findByNumDent(Integer numDent);
}
