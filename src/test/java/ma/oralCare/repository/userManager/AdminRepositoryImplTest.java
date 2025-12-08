package ma.oralCare.repository.userManager;

import ma.oralCare.repository.modules.userManager.impl.AdminRepositoryImpl;
import ma.oralCare.entities.staff.Admin;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdminRepositoryImplTest {

    private static AdminRepositoryImpl adminRepo;

    @BeforeAll
    static void setup() {
        adminRepo = new AdminRepositoryImpl();
    }

    @Test
    @Order(1)
    void testCreateAdmin() {
        Admin newAdmin = new Admin();
        newAdmin.setNom("InsertedAdmin");
        newAdmin.setEmail("inserted@admin.com");
        newAdmin.setCin("CIN999");
        newAdmin.setLogin("insertedLogin");
        newAdmin.setMotDePass("password");

        // Création réelle
        adminRepo.create(newAdmin);

        assertNotNull(newAdmin.getId(), "ID doit être généré après insertion");

        // Vérifier que l'admin existe en DB
        Admin retrieved = adminRepo.findById(newAdmin.getId());
        assertNotNull(retrieved);
        assertEquals("InsertedAdmin", retrieved.getNom());
        assertEquals("inserted@admin.com", retrieved.getEmail());
        assertEquals("CIN999", retrieved.getCin());
        assertEquals("insertedLogin", retrieved.getLogin());
    }

    @Test
    @Order(2)
    void testFindAllAdmins() {
        List<Admin> admins = adminRepo.findAll();
        assertFalse(admins.isEmpty(), "Il doit y avoir au moins un admin");
    }

    @Test
    @Order(3)
    void testFindByLogin() {
        Optional<Admin> optAdmin = adminRepo.findByLogin("insertedLogin");
        assertTrue(optAdmin.isPresent());
        assertEquals("InsertedAdmin", optAdmin.get().getNom());
    }

    @Test
    @Order(4)
    void testFindByCin() {
        Optional<Admin> optAdmin = adminRepo.findByCin("CIN999");
        assertTrue(optAdmin.isPresent());
        assertEquals("InsertedAdmin", optAdmin.get().getNom());
    }

    @Test
    @Order(5)
    void testDeleteAdmin() {
        // Récupérer l'admin créé
        Admin admin = adminRepo.findByLogin("insertedLogin").orElseThrow();
        adminRepo.delete(admin);

        // Vérifier suppression
        Admin deleted = adminRepo.findById(admin.getId());
        assertNull(deleted, "Admin doit être supprimé");
    }
}
