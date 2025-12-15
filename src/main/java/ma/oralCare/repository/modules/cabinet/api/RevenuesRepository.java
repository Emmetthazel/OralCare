package ma.oralCare.repository.modules.cabinet.api;

import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;


public interface RevenuesRepository extends CrudRepository<Revenues, Long> {


    List<Revenues> findByCabinetMedicaleId(Long cabinetMedicaleId);


    List<Revenues> findByTitreContaining(String titre);


    Double calculateTotalRevenuesBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Revenues> findPage(int limit, int offset);
}