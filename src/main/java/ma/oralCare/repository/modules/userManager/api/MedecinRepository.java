package ma.oralCare.repository.modules.staff.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.repository.common.CrudRepository;
import ma.oralCare.entities.enums.Mois;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt (Repository) pour l'entité Medecin.
 * Gère les opérations CRUD spécifiques au médecin, la recherche par spécialité,
 * et l'association avec son agenda.
 */
public interface MedecinRepository extends CrudRepository<Medecin, Long> {

    // --- 1. Méthodes de Recherche Spécifiques au Médecin ---

    /**
     * Recherche un médecin par sa spécialité.
     * @param specialite La spécialité du médecin (e.g., "Orthodontie").
     * @return Une liste de médecins correspondant à cette spécialité.
     */
    List<Medecin> findBySpecialite(String specialite);

    /**
     * Recherche un médecin par son login (méthode essentielle pour l'authentification héritée de Utilisateur).
     * @param login Le login du médecin.
     * @return Le médecin correspondant, s'il existe.
     */
    Optional<Medecin> findByLogin(String login);

    // --- 2. Gestion des Dossiers Médicaux (Association) ---

    /**
     * Récupère tous les dossiers médicaux associés à un médecin donné.
     * Le médecin est l'acteur principal de l'UC "Gérer les dossiers Médicaux".
     * @param medecinId L'ID du médecin.
     * @return La liste des dossiers médicaux qu'il gère.
     */
    List<DossierMedicale> findDossiersMedicauxByMedecinId(Long medecinId);

    // --- 3. Gestion de l'Agenda (Association/Création/Modification) ---

    /**
     * Recherche l'agenda mensuel d'un médecin pour un mois spécifique.
     * Lié à l'UC "Consulter l'agenda".
     * @param medecinId L'ID du médecin.
     * @param mois Le mois recherché (Enum Mois).
     * @return L'AgendaMensuel correspondant, s'il existe.
     */
    Optional<AgendaMensuel> findAgendaByMedecinIdAndMois(Long medecinId, Mois mois);

    /**
     * Crée/Met à jour l'agenda mensuel d'un médecin.
     * Correspond aux UC "Créer agenda Mensuel" et "Modifier jours horaires/indisponibles" via l'entité AgendaMensuel.
     * @param agenda L'objet AgendaMensuel à créer ou mettre à jour.
     */
    void saveAgenda(AgendaMensuel agenda);
}