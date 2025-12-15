package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Admin;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

// L'interface Admin hérite du CRUD de base pour Admin et Long
public interface AdminRepository extends CrudRepository<Admin, Long> {

    Optional<Admin> findByLogin(String login);
    Optional<Admin> findByCin(String cin);
    List<Admin> findAllByNomContaining(String nom);
}