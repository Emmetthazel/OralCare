package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Medecin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MedecinRepository extends CrudRepository<Medecin, Long> {

    Optional<Medecin> findByLogin(String login);
    Optional<Medecin> findByCin(String cin);
    List<Medecin> findAllBySpecialite(String specialite);
    List<Medecin> findAllByNomContaining(String nom);
}