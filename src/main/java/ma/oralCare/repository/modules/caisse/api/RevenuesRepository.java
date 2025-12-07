package ma.oralCare.repository.modules.cabinet.api;

import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Revenues (Recettes/Gains du cabinet).
 * Gère le CRUD et les recherches pour l'analyse financière.
 */
public interface RevenuesRepository extends CrudRepository<Revenues, Long> {

    // --- Méthodes de Recherche Spécifiques (pour Rapports et Analyses) ---

    /**
     * Recherche toutes les recettes associées à un Cabinet Médical spécifique.
     * @param cabinetMedicaleId L'ID du cabinet médical.
     * @return Une liste des recettes pour ce cabinet.
     */
    List<Revenues> findByCabinetMedicaleId(Long cabinetMedicaleId);

    /**
     * Recherche les recettes par titre (ou une correspondance partielle de titre).
     * @param titre Le titre de la recette à rechercher.
     * @return Une liste des recettes correspondantes.
     */
    List<Revenues> findByTitreContaining(String titre);

    /**
     * Calcule la somme totale des recettes sur une période donnée.
     * Utilisé pour générer des rapports de rentabilité quotidiens/mensuels.
     * @param startDate Date et heure de début de la période.
     * @param endDate Date et heure de fin de la période.
     * @return Le montant total des recettes pendant cette période.
     */
    Double calculateTotalRevenuesBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Récupère une page de recettes pour la pagination dans l'interface de gestion.
     * @param limit Nombre maximal d'éléments à retourner.
     * @param offset Décalage à partir du début de la liste.
     * @return La liste des Revenues pour la page demandée.
     */
    List<Revenues> findPage(int limit, int offset);
}