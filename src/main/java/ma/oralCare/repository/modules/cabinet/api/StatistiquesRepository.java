package ma.oralCare.repository.modules.cabinet.api;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface StatistiquesRepository extends CrudRepository<Statistiques, Long> {

    List<Statistiques> findByCabinetMedicaleId(Long cabinetMedicaleId);


    List<Statistiques> findByCategorie(StatistiqueCategorie categorie);

    List<Statistiques> findByDateCalcul(LocalDate dateCalcul);


    List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate);

    Optional<Statistiques> findLatestByCategorieAndCabinet(StatistiqueCategorie categorie, Long cabinetMedicaleId);
}