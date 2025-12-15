package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ActeRepository extends CrudRepository<Acte,Long> {
    Optional<Acte> findByLibelle(String libelle);
    List<Acte> findByCategorie(String categorie);
    boolean existsById(Long id);
    long count();
    List<Acte> findPage(int limit, int offset);

    // ---- Navigation inverse ----
    List<InterventionMedecin> findInterventionsByActeId(Long acteId);
    Optional<Acte> findByInterventionMedecinId(Long interventionMedecinId);

}

