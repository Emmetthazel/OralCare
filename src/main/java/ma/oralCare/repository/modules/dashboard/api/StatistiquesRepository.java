package ma.oralCare.repository.modules.dashboard.api;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Statistiques.
 * Gère le CRUD et les recherches spécifiques aux indicateurs de performance.
 */
public interface StatistiquesRepository extends CrudRepository<Statistiques, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Filtrage) ---

    /**
     * Recherche toutes les statistiques associées à un Cabinet Médical spécifique.
     * @param cabinetMedicaleId L'ID du cabinet médical.
     * @return Une liste des statistiques pour ce cabinet.
     */
    List<Statistiques> findByCabinetMedicaleId(Long cabinetMedicaleId);

    /**
     * Recherche les statistiques par leur catégorie (Ex: REVENUE, PATIENT_COUNT).
     * @param categorie La catégorie de la statistique.
     * @return Une liste des statistiques correspondantes.
     */
    List<Statistiques> findByCategorie(StatistiqueCategorie categorie);

    /**
     * Recherche les statistiques calculées à une date précise.
     * @param dateCalcul La date de calcul.
     * @return Une liste des statistiques générées à cette date.
     */
    List<Statistiques> findByDateCalcul(LocalDate dateCalcul);

    /**
     * Recherche les statistiques calculées entre deux dates.
     * Utilisé pour les graphiques de tendances sur une période.
     * @param startDate Date de début de la période.
     * @param endDate Date de fin de la période.
     * @return La liste des statistiques dans cette période.
     */
    List<Statistiques> findByDateCalculBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Recherche la dernière valeur enregistrée pour une catégorie spécifique.
     * Utile pour afficher le chiffre clé actuel sur un tableau de bord.
     * @param categorie La catégorie de la statistique.
     * @param cabinetMedicaleId L'ID du cabinet.
     * @return La statistique la plus récente, si elle existe.
     */
    Optional<Statistiques> findLatestByCategorieAndCabinet(StatistiqueCategorie categorie, Long cabinetMedicaleId);
}