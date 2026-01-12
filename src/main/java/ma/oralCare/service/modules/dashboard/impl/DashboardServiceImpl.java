package ma.oralCare.service.modules.dashboard.impl;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.api.RevenuesRepository;
import ma.oralCare.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.service.modules.dashboard.api.DashboardService;
import ma.oralCare.service.modules.dashboard.dto.DashboardDTO;

import java.time.LocalDate;

public class DashboardServiceImpl implements DashboardService {

    // ✅ On utilise des instances de repository qui gèrent leurs propres connexions
    private final RDVRepository rdvRepo = new RDVRepositoryImpl();
    private final PatientRepository patientRepo = new PatientRepositoryImpl();
    private final RevenuesRepository revenuesRepo = new RevenuesRepositoryImpl();
    private final UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();

    @Override
    public DashboardDTO getDashboardData(LocalDate date, String userLogin) {
        final LocalDate targetDate = (date != null) ? date : LocalDate.now();
        DashboardDTO dto = new DashboardDTO();

        // 1. Profil Utilisateur
        if (userLogin != null && !userLogin.isEmpty()) {
            userRepo.findByLogin(userLogin).ifPresent(user -> {
                dto.setAdminName(user.getNom() + " " + user.getPrenom());
            });
        }

        if (dto.getAdminName() == null) {
            dto.setAdminName("Utilisateur");
        }

        // 2. Statistiques
        // Chaque repository appellera SessionFactory.getInstance().getConnection() en interne
        try {
            dto.setTotalPatients(patientRepo.countAll());
            dto.setRdvToday(rdvRepo.countByDate(targetDate));
            dto.setProchainsRDV(rdvRepo.findByDate(targetDate));

            Double dailyRev = revenuesRepo.calculateDailyRevenues(targetDate);
            dto.setRevenuesToday(dailyRev != null ? dailyRev : 0.0);

        } catch (Exception e) {
            System.err.println("[DashboardService] Erreur lors du chargement des statistiques : " + e.getMessage());
        }

        return dto;
    }
}