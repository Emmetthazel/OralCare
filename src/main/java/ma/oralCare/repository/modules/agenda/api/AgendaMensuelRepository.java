package ma.oralCare.repository.modules.agenda.api;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.repository.common.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface AgendaMensuelRepository extends CrudRepository<AgendaMensuel, Long> {
    Optional<AgendaMensuel> findByMedecinId(Long medecinId);
    Optional<AgendaMensuel> findByMedecinIdAndMois(Long medecinId, Mois mois);
    AgendaMensuel updateJoursNonDisponible(Long agendaId, List<Jour> joursNonDisponible);
    List<Jour> findJoursNonDisponibleByMedecinIdAndMois(Long medecinId, Mois mois);
}


