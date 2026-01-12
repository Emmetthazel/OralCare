package ma.oralCare.mvc.controllers.admin.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController;
import ma.oralCare.mvc.controllers.admin.dto.AdminDashboardDTO;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.system.api.LogRepository;
import ma.oralCare.repository.modules.system.api.SystemRepository;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository; // ✅ Import ajouté

import java.util.List;
import java.util.ArrayList;

public class AdminDashboardControllerImpl implements AdminDashboardController {

    private final CabinetMedicaleRepository cabinetRepo;
    private final LogRepository logRepo;
    private final SystemRepository systemRepo;
    private final UtilisateurRepository utilisateurRepo; // ✅ Champ ajouté
    private final String currentAdminLogin; // Renommé login pour clarté

    public AdminDashboardControllerImpl(CabinetMedicaleRepository cabinetRepo,
                                        LogRepository logRepo,
                                        SystemRepository systemRepo,
                                        UtilisateurRepository utilisateurRepo, // ✅ Ajouté au constructeur
                                        String adminLogin) {
        this.cabinetRepo = cabinetRepo;
        this.logRepo = logRepo;
        this.systemRepo = systemRepo;
        this.utilisateurRepo = utilisateurRepo;
        this.currentAdminLogin = adminLogin;
    }

    @Override
    public AdminDashboardDTO getDashboardData() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        try {
            // --- 1. SÉCURITÉ : Vérification de la ligne de vie de la connexion ---
            checkAndRestoreConnection();

            // --- 2. IDENTITÉ & CIVILITÉ (M./Mme) ---
            // On récupère l'utilisateur complet via son login
            java.util.Optional<ma.oralCare.entities.users.Utilisateur> adminOpt = utilisateurRepo.findByLogin(this.currentAdminLogin);

            if (adminOpt.isPresent()) {
                ma.oralCare.entities.users.Utilisateur admin = adminOpt.get();
                dto.setAdminName(admin.getNom());

                // On récupère le nom de l'enum (MALE ou FEMALE)
                if (admin.getSexe() != null) {
                    dto.setAdminGender(admin.getSexe().name());
                } else {
                    dto.setAdminGender("OTHER");
                }
            } else {
                // Fallback si l'utilisateur n'est pas trouvé dans la table utilisateur
                dto.setAdminName(this.currentAdminLogin);
                dto.setAdminGender("MALE");
            }

            // --- 3. STATISTIQUES DES LOGS (Carte 1) ---
            dto.setTodayLogsCount(logRepo.countTodayLogs());

            // --- 4. STATISTIQUES SYSTÈME (Carte 2 & Cloche) ---
            // Taille de la base de données
            dto.setDatabaseSize(systemRepo.getDatabaseSizeInMB());

            // Statut du système (ex: succès ou échec de la dernière sauvegarde)
            String status = systemRepo.getConfigStatus("BACKUP_STATUS");
            dto.setSystemStatus(status != null ? status : "REUSSI");

            // --- 5. STATISTIQUES CABINETS (Carte 3) ---
            dto.setTotalCabinets(cabinetRepo.countAll());
            dto.setActiveCabinets(cabinetRepo.countActiveRecently());

            // --- 6. DERNIÈRES ACTIONS (Audit Trail pour le Footer) ---
            // On récupère les 15 derniers logs pour l'affichage dans le JTextArea
            java.util.List<String> logs = logRepo.getLatestLogs(15);
            dto.setLatestActions(logs != null ? logs : new java.util.ArrayList<>());

        } catch (Exception e) {
            // Log de l'erreur pour le debug
            System.err.println("❌ [DASHBOARD-ERROR] Échec de la collecte des données : " + e.getMessage());
            e.printStackTrace();

            // Valeurs de secours (Fail-safe) pour éviter que l'UI ne reste vide
            dto.setSystemStatus("ECHEC");
            dto.setAdminName(this.currentAdminLogin);
            dto.setAdminGender("MALE");
            dto.setLatestActions(java.util.Collections.singletonList("Erreur de chargement des logs..."));
        }

        return dto;
    }

    private void checkAndRestoreConnection() {
        try {
            java.sql.Connection conn = SessionFactory.getInstance().getConnection();
            if (conn == null || conn.isClosed()) {
                System.out.println("⚠️ [DASHBOARD] Restauration de la connexion...");
            }
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Erreur connexion : " + e.getMessage());
        }
    }
}