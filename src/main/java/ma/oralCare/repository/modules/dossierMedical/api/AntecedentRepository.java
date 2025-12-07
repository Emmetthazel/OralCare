package ma.oralCare.repository.modules.dossierMedical.api;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Antecedent.
 * Gère le CRUD des antécédents génériques et les relations Patient-Antecedent.
 */
public interface AntecedentRepository extends CrudRepository<Antecedent, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (Antécédents génériques) ---

    /**
     * Recherche les antécédents par leur catégorie.
     * @param categorie La catégorie d'antécédent (ALLERGIE, MALADIE_CHRONIQUE, etc.).
     * @return Une liste des antécédents dans cette catégorie.
     */
    List<Antecedent> findByCategorie(CategorieAntecedent categorie);

    /**
     * Recherche les antécédents par leur niveau de risque.
     * @param niveauRisque Le niveau de risque (LOW, MEDIUM, HIGH).
     * @return Une liste des antécédents de ce niveau de risque.
     */
    List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque);

    /**
     * Recherche les antécédents par nom (correspondance partielle).
     * @param nom Fragment de texte à chercher dans le nom.
     * @return La liste des antécédents contenant ce fragment.
     */
    List<Antecedent> findByNomContaining(String nom);

    // --- 2. Méthodes de Gestion de la Relation Patient-Antecedent ---

    /**
     * Récupère tous les antécédents associés à un patient spécifique.
     * @param patientId L'ID du patient.
     * @return La liste des antécédents de ce patient.
     */
    List<Antecedent> findByPatientId(Long patientId);

    /**
     * Associe un antécédent existant à un patient (crée un lien dans la table de jointure).
     * @param antecedentId L'ID de l'antécédent.
     * @param patientId L'ID du patient.
     */
    void linkAntecedentToPatient(Long antecedentId, Long patientId);

    /**
     * Dissocie un antécédent d'un patient (supprime le lien dans la table de jointure).
     * @param antecedentId L'ID de l'antécédent.
     * @param patientId L'ID du patient.
     */
    void unlinkAntecedentFromPatient(Long antecedentId, Long patientId);
}