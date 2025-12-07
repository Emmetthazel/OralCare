package ma.oralCare.repository.modules.medicament.api;

import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Medicament.
 * Gère le CRUD (Ajouter/Modifier/Supprimer Médicament) et la consultation du catalogue.
 */
public interface MedicamentRepository extends CrudRepository<Medicament, Long> {

    // --- 1. Opérations de Recherche et Consultation (UC: consulter Médicament, rechercher Médicament) ---

    /**
     * Recherche les médicaments par leur nom (recherche partielle pour l'autocomplétion ou la recherche).
     * Correspond à l'UC "rechercher Médicament".
     * @param nomPartiel Le début ou une partie du nom du médicament.
     * @return Une liste des médicaments correspondants.
     */
    List<Medicament> findByNomContaining(String nomPartiel);

    /**
     * Recherche les médicaments par leur laboratoire de fabrication.
     * @param laboratoire Le nom du laboratoire.
     * @return Une liste des médicaments du laboratoire spécifié.
     */
    List<Medicament> findByLaboratoire(String laboratoire);

    /**
     * Recherche les médicaments par leur forme pharmaceutique.
     * @param forme La forme (TABLET, SYRUP, etc.).
     * @return Une liste des médicaments ayant cette forme.
     */
    List<Medicament> findByForme(FormeMedicament forme);

    /**
     * Recherche les médicaments qui sont remboursables.
     * @param remboursable True pour les remboursables, False sinon.
     * @return Une liste des médicaments avec le statut de remboursement spécifié.
     */
    List<Medicament> findByRemboursable(Boolean remboursable);

    // Les opérations CRUD de base (Ajouter, Modifier, Supprimer Médicament) sont héritées de CrudRepository.
}