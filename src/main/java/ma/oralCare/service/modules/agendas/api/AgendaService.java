package ma.oralCare.service.modules.agendas.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.agenda.RDV;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service pour la gestion de l'agenda et des rendez-vous.
 */
public interface AgendaService {

    // Agenda mensuel
    Optional<AgendaMensuel> getAgendaForMedecinAndMonth(Long medecinId, int annee, int mois);

    AgendaMensuel saveAgenda(AgendaMensuel agenda);

    // Rendez-vous
    RDV createRdv(RDV rdv);

    Optional<RDV> getRdvById(Long id);

    List<RDV> getRdvsByDate(LocalDate date);

    void deleteRdv(Long rdvId);
}


