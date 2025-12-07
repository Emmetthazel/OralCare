package ma.oralCare.repository.modules.caisse.api;

import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface de dépôt (Repository) pour l'entité Charges.
 * Gère les dépenses ou charges financières du Cabinet Médical.
 */
public interface ChargesRepository extends CrudRepository<Charges, Long> {

    // --- Méthodes Spécifiques de Consultation (Basées sur le temps et la finance) ---

    /**
     * Recherche toutes les charges enregistrées pour une période donnée.
     * Nécessaire pour les CU de consultation de statistiques de caisse et de rapports financiers.
     * @param dateDebut La date et heure de début de la période (inclusive).
     * @param dateFin La date et heure de fin de la période (inclusive).
     * @return Une liste des charges correspondant à la période.
     */
    List<Charges> findByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Calcule le montant total des charges pour une période donnée.
     * Essentiel pour le CU "Consulter statistiques de caisse".
     * @param dateDebut La date et heure de début de la période.
     * @param dateFin La date et heure de fin de la période.
     * @return Le montant total des charges pour cette période.
     */
    Double calculateTotalChargesByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    /**
     * Recherche les charges par titre ou par une partie de leur description.
     * Utile pour la consultation et la recherche de dépenses spécifiques.
     * @param keyword Le mot-clé à rechercher dans le titre ou la description.
     * @return Une liste des charges dont le titre ou la description correspond.
     */
    List<Charges> findByTitreOrDescriptionContaining(String keyword);

    // NOTE: Puisque Charges est associé à CabinetMedicale (relation ManyToOne),
    // la recherche par cabinet peut être omise si un seul cabinet est géré.
    // Si plusieurs cabinets sont possibles, ajouter :
    // List<Charges> findByCabinetMedicaleId(Long cabinetId);
    List<Charges> findByCabinetMedicaleId(Long cabinetId);
}