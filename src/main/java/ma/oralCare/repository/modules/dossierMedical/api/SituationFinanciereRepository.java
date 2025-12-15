package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.dossierMedical.SituationFinanciere;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;


public interface SituationFinanciereRepository extends CrudRepository<SituationFinanciere, Long> {


    Optional<SituationFinanciere> findByPatientId(Long patientId);


    List<SituationFinanciere> findActiveSituations();


    List<SituationFinanciere> findAllByPatientId(Long patientId);

    void reinitialiserSF(Long id);


    void updateTotaux(Long sfId, Double montantTotalActes, Double montantTotalPaye, Double nouveauCredit);


    List<Facture> findFacturesBySituationFinanciereId(Long sfId);
}