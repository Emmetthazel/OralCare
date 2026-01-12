package ma.oralCare.service.modules.dashboard_statistiques.impl;

import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.service.modules.dashboard_statistiques.api.StatistiquesService;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiquesServiceImpl implements StatistiquesService {

    private final PatientRepository patientRepo;
    private final RDVRepository rdvRepo;

    @Override
    public List<Statistiques> getAll() {
        // Si vous avez un repository pour la table 'Statistiques' :
        // return statistiquesRepository.findAll();

        // Sinon, pour corriger l'erreur de compilation immédiatement :
        return new java.util.ArrayList<>();
    }
    // Injection des repositories par constructeur
    public StatistiquesServiceImpl(PatientRepository patientRepo, RDVRepository rdvRepo) {
        this.patientRepo = patientRepo;
        this.rdvRepo = rdvRepo;
    }

    @Override
    public int getTotalPatientsCount() {
        // Appelle le repo : SELECT COUNT(*) FROM patients
        return patientRepo.countAll();
    }

    @Override
    public int getTodayVisitsCount() {
        // Appelle le repo : SELECT COUNT(*) FROM rendez_vous WHERE date_rdv = CURRENT_DATE
        return rdvRepo.countByDate(LocalDate.now());
    }

    @Override
    public int getTotalApptsCount() {
        // Appelle le repo : SELECT COUNT(*) FROM rendez_vous
        return rdvRepo.countAll();
    }

    @Override
    public Map<String, Integer> getDashboardSummary() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalPatients", getTotalPatientsCount());
        stats.put("todayVisits", getTodayVisitsCount());
        stats.put("totalRendezVous", getTotalApptsCount());
        return stats;
    }
}