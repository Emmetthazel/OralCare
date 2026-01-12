package ma.oralCare.repository.modules.cabinet.api;

import ma.oralCare.entities.cabinet.*;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CabinetMedicaleRepository extends CrudRepository<CabinetMedicale, Long> {

    long countAll();
    long countActiveRecently();
    Optional<CabinetMedicale> findByNom(String nom);
    List<CabinetMedicale> findAllByNomContaining(String nom);
    List<CabinetMedicale> findAllByVille(String ville);
}