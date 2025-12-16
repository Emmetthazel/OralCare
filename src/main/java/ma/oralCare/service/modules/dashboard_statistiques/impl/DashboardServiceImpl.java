package ma.oralCare.service.modules.dashboard_statistiques.impl;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.repository.modules.cabinet.api.StatistiquesRepository;
import ma.oralCare.repository.modules.cabinet.impl.StatistiquesRepositoryImpl;
import ma.oralCare.service.modules.dashboard_statistiques.api.DashboardService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Service simple pour construire des données de dashboard à partir des statistiques du cabinet.
 */
public class DashboardServiceImpl implements DashboardService {

    private final StatistiquesRepository statistiquesRepository;

    public DashboardServiceImpl() {
        this(new StatistiquesRepositoryImpl());
    }

    public DashboardServiceImpl(StatistiquesRepository statistiquesRepository) {
        this.statistiquesRepository = Objects.requireNonNull(statistiquesRepository);
    }

    public List<Statistiques> getStatistiquesForPeriod(LocalDate start, LocalDate end) {
        // Implémentation minimale : déléguée à findAll, ou à une future méthode findByPeriode
        // Ici, on retourne tout pour rester cohérent avec le repository existant.
        return statistiquesRepository.findAll();
    }
}


