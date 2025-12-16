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
        // 1. Création d'un admin
        CreateAdminRequest req = new CreateAdminRequest(
                "AdminTest",
                "admin.test@" + System.currentTimeMillis() + ".ma",
                "Adresse admin",
                "CIN123456",
                "0600000000",
                Sexe.FEMALE,
                "adminLogin" + System.currentTimeMillis(),
                "password",
                LocalDate.of(1990, 1, 1),
                10000.0,
                1000.0,
                LocalDate.now(),
                20
        );

        UserAccountDto created = userService.createAdmin(req);
        System.out.println("✅ Admin créé. ID = " + created.id());

        // 2. Lecture / recherche
        UserAccountDto byId = userService.getUserById(created.id());
        System.out.println(byId != null
                ? "✅ Lecture par ID OK. Login = " + byId.login()
                : "❌ Lecture par ID Échec");

        List<UserAccountDto> all = userService.getAllUsers();
        System.out.println("Utilisateurs totaux: " + all.size());
    }
}


