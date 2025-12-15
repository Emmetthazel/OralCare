package ma.oralCare.repository.modules.cabinet.api;

import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface de dépôt (Repository) pour l'entité Charges.
 * Gère les dépenses ou charges financières du Cabinet Médical.
 */
public interface ChargesRepository extends CrudRepository<Charges, Long> {

    List<Charges> findByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin);


    Double calculateTotalChargesByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin);


    List<Charges> findByTitreOrDescriptionContaining(String keyword);

    List<Charges> findByCabinetMedicaleId(Long cabinetId);
}