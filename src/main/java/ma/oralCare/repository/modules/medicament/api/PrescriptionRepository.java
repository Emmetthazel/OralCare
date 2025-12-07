package ma.oralCare.repository.modules.medicament.api;

import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;

/**
 * Interface de dépôt (Repository) pour l'entité Prescription.
 * Gère les opérations CRUD et la recherche liées à une Ordonnance spécifique.
 */
public interface PrescriptionRepository extends CrudRepository<Prescription, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Basées sur l'Ordonnance) ---

    /**
     * Recherche toutes les prescriptions associées à une Ordonnance donnée.
     * @param ordonnanceId L'ID de l'Ordonnance.
     * @return Une liste des prescriptions de cette ordonnance.
     */
    List<Prescription> findByOrdonnanceId(Long ordonnanceId);

    // Les opérations CRUD de base (Ajouter/Modifier/Supprimer) sont héritées.
}