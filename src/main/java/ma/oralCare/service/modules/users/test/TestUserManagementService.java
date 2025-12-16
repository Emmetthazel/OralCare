package ma.oralCare.service.modules.users.test;

import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.service.modules.users.api.UserManagementService;
import ma.oralCare.service.modules.users.dto.CreateAdminRequest;
import ma.oralCare.service.modules.users.dto.UserAccountDto;
import ma.oralCare.service.modules.users.impl.UserManagementServiceImpl;

import java.time.LocalDate;
import java.util.List;

/**
 * Test console simple pour le service de gestion des utilisateurs.
 */
public class TestUserManagementService {

    private final UserManagementService userService = new UserManagementServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST USER MANAGEMENT SERVICE ===");
        TestUserManagementService tester = new TestUserManagementService();
        tester.run();
        System.out.println("=== FIN TEST USER MANAGEMENT SERVICE ===");
    }

    private void run() {
        long timestamp = System.currentTimeMillis();
        String uniqueEmail = "admin.test." + timestamp + "@oralcare.ma";
        String uniqueLogin = "adminLogin" + timestamp;
        String uniqueCin = "CIN" + timestamp;
        String uniqueTel = "06" + String.format("%08d", timestamp);

        // 1. Création d'un admin
        CreateAdminRequest req = new CreateAdminRequest(
                // 1. nom
                "AdminTest",
                // 2. PRENOM (VALEUR REQUISE)
                "Superviseur",
                // 3. email
                uniqueEmail,
                // 4. adresse
                "123, Rue du Test Admin, Rabat",
                // 5. cin
                uniqueCin, // Rendre CIN unique aussi
                // 6. tel
                uniqueTel,
                // 7. sexe
                Sexe.FEMALE,
                // 8. login
                uniqueLogin,
                // 9. motDePasse
                "password",
                // 10. dateNaissance
                LocalDate.of(1990, 1, 1),
                // 11. salaire
                10000.0,
                // 12. prime
                1000.0,
                // 13. dateRecrutement
                LocalDate.now(),
                // 14. soldeConge
                20
        );

        UserAccountDto created = userService.createAdmin(req);
        System.out.println("Admin créé. ID = " + created.id());

        // 2. Lecture / recherche
        UserAccountDto byId = userService.getUserById(created.id());
        System.out.println(byId != null
                ? "Lecture par ID OK. Login = " + byId.login()
                : "Lecture par ID Échec");

        // 3. (Test findAll)
        List<UserAccountDto> all = userService.getAllUsers();
        System.out.println("Utilisateurs totaux: " + all.size());
    }
}