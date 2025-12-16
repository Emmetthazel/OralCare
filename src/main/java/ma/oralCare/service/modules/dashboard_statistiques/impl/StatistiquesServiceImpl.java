package ma.oralCare.service.modules.dashboard_statistiques.impl;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.repository.modules.cabinet.api.StatistiquesRepository;
import ma.oralCare.repository.modules.cabinet.impl.StatistiquesRepositoryImpl;
import ma.oralCare.service.modules.dashboard_statistiques.api.StatistiquesService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service CRUD simplifié pour les entités Statistiques.
 */
public class StatistiquesServiceImpl implements StatistiquesService {

    private final StatistiquesRepository statistiquesRepository;

    public StatistiquesServiceImpl() {
        this(new StatistiquesRepositoryImpl());
    }

    public StatistiquesServiceImpl(StatistiquesRepository statistiquesRepository) {
        this.statistiquesRepository = Objects.requireNonNull(statistiquesRepository);
    }

    public Statistiques createStatistique(Statistiques statistiques) {
        Objects.requireNonNull(statistiques, "statistiques ne doit pas être null");
        statistiquesRepository.create(statistiques);
        return statistiques;
    }

    public Statistiques updateStatistique(Statistiques statistiques) {
        Objects.requireNonNull(statistiques, "statistiques ne doit pas être null");
        statistiquesRepository.update(statistiques);
        return statistiques;
    }

    public Optional<Statistiques> getById(Long id) {
        if (id == null) return Optional.empty();
        return statistiquesRepository.findById(id);
    }

    public List<Statistiques> getAll() {
        return statistiquesRepository.findAll();
    }

    public void deleteById(Long id) {
        if (id == null) return;
        statistiquesRepository.deleteById(id);
    }
}


