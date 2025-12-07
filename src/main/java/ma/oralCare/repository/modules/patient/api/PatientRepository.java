package ma.oralCare.repository.modules.patient.api;

import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Patient.
 * Gère les opérations CRUD (Ajouter/Modifier/Supprimer/Consulter Patient) et la recherche.
 */
public interface PatientRepository extends CrudRepository<Patient, Long> {

    // --- 1. Méthodes de Recherche Spécifiques (UC: Consulter Patient, Consulter liste patients) ---

    /**
     * Recherche un patient par son nom et prénom (recherche exacte ou partielle).
     * @param nom Le nom du patient.
     * @param prenom Le prénom du patient.
     * @return Une liste de patients correspondants.
     */
    List<Patient> findByNomAndPrenom(String nom, String prenom);

    /**
     * Recherche un patient par son numéro de téléphone.
     * @param telephone Le numéro de téléphone du patient.
     * @return Le patient correspondant, s'il existe.
     */
    Optional<Patient> findByTelephone(String telephone);

    /**
     * Recherche les patients nés avant ou après une date donnée.
     * @param dateNaissance La date de référence.
     * @return Une liste de patients.
     */
    List<Patient> findByDateNaissanceBefore(LocalDate dateNaissance);

    /**
     * Recherche les patients qui possèdent une assurance spécifique.
     * @param assurance Le type d'assurance (CNOPS, CNSS, etc.).
     * @return Une liste de patients.
     */
    List<Patient> findByAssurance(Assurance assurance);


    // --- 2. Méthodes d'Association (UC: Affecter Antécédent) ---

    /**
     * Récupère la liste des antécédents médicaux d'un patient donné.
     * @param patientId L'ID du patient.
     * @return La liste des antécédents.
     */
    List<Antecedent> findAntecedentsByPatientId(Long patientId);

    /**
     * Associe un antécédent à un patient existant.
     * (Nécessaire pour l'UC "Affecter Antécédent").
     * @param patientId L'ID du patient.
     * @param antecedentId L'ID de l'antécédent à ajouter.
     */
    void addAntecedentToPatient(Long patientId, Long antecedentId);

    /**
     * Supprime l'association entre un antécédent et un patient.
     * @param patientId L'ID du patient.
     * @param antecedentId L'ID de l'antécédent à retirer.
     */
    void removeAntecedentFromPatient(Long patientId, Long antecedentId);
}