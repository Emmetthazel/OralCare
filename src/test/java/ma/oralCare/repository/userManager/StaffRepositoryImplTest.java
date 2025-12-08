package ma.oralCare.repository.userManager;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.staff.Staff;
import ma.oralCare.repository.modules.userManager.api.StaffRepository;
import ma.oralCare.repository.modules.userManager.impl.StaffRepositoryImpl;
import ma.oralCare.repository.modules.auth.api.UtilisateurRepository;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StaffRepositoryImplTest {

    private static StaffRepository staffRepository;
    private static Staff staff;
    private static CabinetMedicale cabinet;

    @BeforeAll
    static void setup() {
        // Ici tu dois initialiser le SessionFactory et éventuellement une base de test
        UtilisateurRepository utilisateurRepository = null; // Stub ou mock si nécessaire
        staffRepository = new StaffRepositoryImpl(utilisateurRepository);

        // Création d’un cabinet pour test
        cabinet = CabinetMedicale.builder()
                .id(1L) // Supposons que cet ID existe dans la DB
                .nom("Cabinet Test")
                .build();

        staff = new Staff();
        staff.setNom("John Doe");
        staff.setSalaire(5000.0);
        staff.setPrime(500.0);
        staff.setDateRecrutement(LocalDate.of(2023, 1, 10));
        staff.setSoldeConge(15);
        staff.setCabinetMedicale(cabinet);
        staff.setLogin("johndoe");
        staff.setCin("AA123456");
        staff.setEmail("johndoe@test.com");
        staff.setDateNaissance(LocalDate.of(1990, 5, 20));
    }

    @Test
    @Order(1)
    void testCreateStaff() {
        staffRepository.create(staff);
        assertNotNull(staff.getId(), "L'ID doit être généré après la création");
    }

    @Test
    @Order(2)
    void testFindById() {
        Staff found = staffRepository.findById(staff.getId());
        assertNotNull(found);
        assertEquals(staff.getNom(), found.getNom());
    }

    @Test
    @Order(3)
    void testFindAll() {
        List<Staff> staffList = staffRepository.findAll();
        assertFalse(staffList.isEmpty());
    }

    @Test
    @Order(4)
    void testFindByLogin() {
        Optional<Staff> opt = staffRepository.findByLogin("johndoe");
        assertTrue(opt.isPresent());
        assertEquals(staff.getNom(), opt.get().getNom());
    }

    @Test
    @Order(5)
    void testFindByCin() {
        Optional<Staff> opt = staffRepository.findByCin("AA123456");
        assertTrue(opt.isPresent());
        assertEquals(staff.getNom(), opt.get().getNom());
    }

    @Test
    @Order(6)
    void testUpdateSalaireAndPrime() {
        staffRepository.updateSalaireAndPrime(staff.getId(), 6000.0, 600.0);
        Staff updated = staffRepository.findById(staff.getId());
        assertEquals(6000.0, updated.getSalaire());
        assertEquals(600.0, updated.getPrime());
    }

    @Test
    @Order(7)
    void testUpdateSoldeConge() {
        staffRepository.updateSoldeConge(staff.getId(), 20);
        Staff updated = staffRepository.findById(staff.getId());
        assertEquals(20, updated.getSoldeConge());
    }

    @Test
    @Order(8)
    void testDeleteStaff() {
        staffRepository.delete(staff);
        Staff deleted = staffRepository.findById(staff.getId());
        assertNull(deleted, "Le staff doit être supprimé de la base");
    }
}
