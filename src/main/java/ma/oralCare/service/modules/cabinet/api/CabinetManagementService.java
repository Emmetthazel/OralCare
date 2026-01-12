package ma.oralCare.service.modules.cabinet.api;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import java.util.List;
import java.util.Optional;

/**
 * Interface définissant les services de gestion des cabinets médicaux.
 * Utilisée par l'administrateur pour configurer la structure de l'organisation.
 */
public interface CabinetManagementService {

    /**
     * Crée un nouveau cabinet médical dans le système.
     * Cette opération est transactionnelle : elle insère dans BaseEntity puis cabinet_medicale.
     * * @param cabinet L'entité cabinet contenant les informations et l'adresse.
     * @throws Exception Si le nom/email existe déjà ou en cas d'erreur SQL.
     */
    void createCabinet(CabinetMedicale cabinet) throws Exception;

    /**
     * Récupère la liste de tous les cabinets enregistrés.
     * @return Liste d'entités CabinetMedicale.
     */
    List<CabinetMedicale> getAllCabinets();

    /**
     * Recherche un cabinet par son identifiant unique.
     * @param id L'identifiant de l'entité.
     * @return Un Optional contenant le cabinet si trouvé.
     */
    Optional<CabinetMedicale> getCabinetById(Long id);

    /**
     * Met à jour les informations d'un cabinet existant.
     * @param cabinet L'entité avec les nouvelles valeurs.
     */
    void updateCabinet(CabinetMedicale cabinet) throws Exception;

    /**
     * Supprime un cabinet et toutes ses données liées (Staff, RDV, etc.) par cascade.
     * @param id L'identifiant du cabinet à supprimer.
     */
    void deleteCabinet(Long id) throws Exception;
}