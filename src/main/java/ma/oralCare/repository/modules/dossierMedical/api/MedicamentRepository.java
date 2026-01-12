package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;


public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    void save(Medicament medicament); // Ajout de la méthode save

    List<Medicament> findByNomContaining(String nomPartiel);

    List<Medicament> findByLaboratoire(String laboratoire);


    List<Medicament> findByForme(FormeMedicament forme);

    List<Medicament> findByRemboursable(Boolean remboursable);

}