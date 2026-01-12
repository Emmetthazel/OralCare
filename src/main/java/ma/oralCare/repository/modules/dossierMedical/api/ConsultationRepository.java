/*package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.CrudRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public interface ConsultationRepository extends CrudRepository<Consultation, Long> {

    void create(Consultation consultation);
    Consultation create(Consultation consultation, Connection connection) throws SQLException;

    List<Consultation> findByDossierMedicaleId(Long dossierMedicaleId);


    List<Consultation> findByStatut(StatutConsultation statut);


    List<Consultation> findByDate(LocalDate date);


    void updateStatut(Long id, StatutConsultation nouveauStatut);


    void updateObservation(Long id, String observation);


    void addIntervention(Long consultationId, InterventionMedecin intervention);

}
*/