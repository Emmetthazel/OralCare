package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SecretaireRepository extends CrudRepository<Secretaire, Long> {

    Optional<Secretaire> findByLogin(String login);
    Optional<Secretaire> findByCin(String cin);
    List<Secretaire> findAllByNomContaining(String nom);
    List<Secretaire> findAllByCabinetId(Long cabinetId);
}