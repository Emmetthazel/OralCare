package ma.oralCare.service.modules.agendas.impl;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.service.modules.agendas.api.AgendaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implémentation de {@link AgendaService} déléguant aux repositories d'agenda.
 */
public class AgendaServiceImpl implements AgendaService {

    private final AgendaMensuelRepository agendaRepository;
    private final RDVRepository rdvRepository;

    public AgendaServiceImpl() {
        this(new AgendaMensuelRepositoryImpl(), new RDVRepositoryImpl());
    }

    public AgendaServiceImpl(AgendaMensuelRepository agendaRepository,
                             RDVRepository rdvRepository) {
        this.agendaRepository = Objects.requireNonNull(agendaRepository);
        this.rdvRepository = Objects.requireNonNull(rdvRepository);
    }

    @Override
    public Optional<AgendaMensuel> getAgendaForMedecinAndMonth(Long medecinId, int annee, int mois) {
        if (medecinId == null) return Optional.empty();
        Mois m = Mois.values()[mois - 1]; // on suppose mois 1-12
        return agendaRepository.findByMedecinIdAndMoisAndAnnee(medecinId, m, annee);
    }

    @Override
    public AgendaMensuel saveAgenda(AgendaMensuel agenda) {
        Objects.requireNonNull(agenda, "agenda ne doit pas être null");
        if (agenda.getIdEntite() == null) {
            agendaRepository.create(agenda);
        } else {
            agendaRepository.update(agenda);
        }
        return agenda;
    }

    @Override
    public RDV createRdv(RDV rdv) {
        Objects.requireNonNull(rdv, "rdv ne doit pas être null");
        rdvRepository.create(rdv);
        return rdv;
    }

    @Override
    public Optional<RDV> getRdvById(Long id) {
        if (id == null) return Optional.empty();
        return rdvRepository.findById(id);
    }

    @Override
    public List<RDV> getRdvsByDate(LocalDate date) {
        if (date == null) return List.of();
        return rdvRepository.findByDate(date);
    }

    @Override
    public void deleteRdv(Long rdvId) {
        if (rdvId == null) return;
        rdvRepository.deleteById(rdvId);
    }
}


