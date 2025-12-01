package ma.oralCare.repository.modules.rdv.api;

import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RDVRepository extends CrudRepository<RDV, Long> {

    List<RDV> findByDate(LocalDate date);

    List<RDV> findByStatut(StatutRDV statut);

    List<RDV> findByMotifContaining(String keyword);

    List<RDV> findByDateBetween(LocalDate debut, LocalDate fin);

    List<RDV> searchRDVs(String keyword); // recherche sur motif ou noteMedecin
}
