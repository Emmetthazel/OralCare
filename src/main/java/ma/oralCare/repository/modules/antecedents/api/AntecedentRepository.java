package ma.oralCare.repository.modules.antecedents.api;

import ma.oralCare.entities.patient.Antecedent;
import java.util.List;

/**
 * Interface pour gérer les antécédents médicaux
 */
public interface AntecedentRepository {

    /**
     * Récupère un antecedent par son ID
     */
    Antecedent findById(Long id);

    /**
     * Récupère tous les antécédents
     */
    List<Antecedent> findAll();

    /**
     * Récupère tous les antécédents d'un patient
     */
    List<Antecedent> findByPatientId(Long patientId);

    /**
     * Sauvegarde un nouvel antécédent
     */
    Antecedent save(Antecedent antecedent);

    /**
     * Met à jour un antécédent existant
     */
    Antecedent update(Antecedent antecedent);

    /**
     * Supprime un antécédent par son ID
     */
    boolean delete(Long id);
}
