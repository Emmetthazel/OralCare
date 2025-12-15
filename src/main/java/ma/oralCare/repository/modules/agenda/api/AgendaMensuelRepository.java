package ma.oralCare.repository.modules.agenda.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.repository.common.CrudRepository;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AgendaMensuelRepository extends CrudRepository<AgendaMensuel, Long> {

    Optional<AgendaMensuel> findByMedecinIdAndMoisAndAnnee(Long medecinId, Mois mois, int annee);
    Optional<AgendaMensuel> findCurrentAgenda(Long medecinId, LocalDate date);
    List<AgendaMensuel> findAllByMedecinId(Long medecinId);
    void addJourNonDisponible(Long agendaId, Jour jour);
    void removeJourNonDisponible(Long agendaId, Jour jour);
    List<Jour> findJoursNonDisponiblesByAgendaId(Long agendaId);
    void setJoursNonDisponible(Long agendaId, List<Jour> joursNonDisponible);

}