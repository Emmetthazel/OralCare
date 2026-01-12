package ma.oralCare.service.modules.agenda.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaService {

    // --- Gestion des Agendas ---

    /**
     * Récupère l'agenda mensuel d'un médecin spécifique.
     */
    Optional<AgendaMensuel> getAgendaForMedecinAndMonth(Long medecinId, int annee, int mois);

    /**
     * Crée ou met à jour un agenda mensuel.
     */
    AgendaMensuel saveAgenda(AgendaMensuel agenda);

    // --- Gestion des Rendez-vous ---

    /**
     * Enregistre un nouveau rendez-vous.
     */
    RDV createRdv(RDV rdv);

    /**
     * Récupère un rendez-vous par son identifiant unique.
     */
    Optional<RDV> getRdvById(Long id);

    /**
     * Liste tous les rendez-vous pour une date précise.
     */
    List<RDV> getRdvsByDate(LocalDate date);

    /**
     * Récupère les rendez-vous du jour formatés pour l'affichage (Dashboard).
     */
    List<RDVDisplayModel> getTodayAppointments();

    /**
     * Supprime un rendez-vous de la base de données.
     */
    void deleteRdv(Long rdvId);
}