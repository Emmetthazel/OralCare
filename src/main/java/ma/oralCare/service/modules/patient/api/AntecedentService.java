package ma.oralCare.service.modules.patient.api;

import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;

import java.util.List;
import java.util.Optional;

/**
 * Couche service pour la gestion des antécédents médicaux.
 */
public interface AntecedentService {

    // --- CRUD de base ---

    Antecedent createAntecedent(Antecedent antecedent);

    Antecedent updateAntecedent(Antecedent antecedent);

    Optional<Antecedent> getAntecedentById(Long id);

    List<Antecedent> getAllAntecedents();

    void deleteAntecedent(Long id);

    // --- Recherches spécifiques ---

    List<Antecedent> findByCategorie(CategorieAntecedent categorie);

    List<Antecedent> findByNiveauRisque(NiveauDeRisque niveauRisque);

    List<Antecedent> findByNomContaining(String nom);

    List<Antecedent> findByPatientId(Long patientId);

    // --- Gestion des liens Patient <-> Antécédent ---

    void linkAntecedentToPatient(Long antecedentId, Long patientId);

    void unlinkAntecedentFromPatient(Long antecedentId, Long patientId);
}

