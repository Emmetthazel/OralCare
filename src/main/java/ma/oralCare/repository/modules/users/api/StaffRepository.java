package ma.oralCare.repository.modules.users.api;

import ma.oralCare.entities.users.Staff;
import ma.oralCare.repository.common.CrudRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface StaffRepository extends CrudRepository<Staff, Long> {

    List<Staff> findAllByCabinetId(Long cabinetId);
    List<Staff> findAllBySalaireBetween(BigDecimal minSalaire, BigDecimal maxSalaire);
    Optional<Staff> findByLogin(String login);
    List<Staff> findAllByNomContaining(String nom);

}