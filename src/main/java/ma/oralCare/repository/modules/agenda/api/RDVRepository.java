package ma.oralCare.repository.modules.agenda.api;

import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface RDVRepository extends CrudRepository<RDV, Long> {
    List<RDV> findByDossierMedicaleId(Long dossierMedicaleId);
    Optional<RDV> findByConsultationId(Long consultationId);
    List<RDV> findByDate(LocalDate date);
    boolean existsByDateAndHeureAndMedecinId(LocalDate date, LocalTime heure, Long medecinId);
    RDV updateStatut(Long rdvId, StatutRDV nouveauStatut);
    List<RDV> findByStatut(StatutRDV statut);
}
