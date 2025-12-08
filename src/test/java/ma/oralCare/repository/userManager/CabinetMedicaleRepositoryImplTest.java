package ma.oralCare.repository.userManager;

import ma.oralCare.repository.modules.userManager.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.common.Adresse;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CabinetMedicaleRepositoryImplTest {

    private static CabinetMedicaleRepositoryImpl cabinetRepo;

    @BeforeAll
    static void setup() {
        cabinetRepo = new CabinetMedicaleRepositoryImpl();
    }

    @Test
    @Order(1)
    void testCreateCabinet() {
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom("Cabinet Test")
                .email("cabinet@test.com")
                .cin("CIN123")
                .tel1("0600000000")
                .tel2("0611111111")
                .siteWeb("www.cabinet-test.com")
                .instagram("@cabinettest")
                .facebook("cabinetTest")
                .description("Cabinet pour test JUnit")
                .adresse(new Adresse("10", "Rue Test", "10000", "Casablanca", "Maroc"))
                .build();

        cabinetRepo.create(cabinet);

        assertNotNull(cabinet.getId(), "ID doit être généré après insertion");

        // Vérifier que le cabinet existe en DB
        CabinetMedicale retrieved = cabinetRepo.findById(cabinet.getId());
        assertNotNull(retrieved);
        assertEquals("Cabinet Test", retrieved.getNom());
        assertEquals("cabinet@test.com", retrieved.getEmail());
    }

    @Test
    @Order(2)
    void testFindAllCabinets() {
        List<CabinetMedicale> cabinets = cabinetRepo.findAll();
        assertFalse(cabinets.isEmpty(), "Il doit y avoir au moins un cabinet");
    }

    @Test
    @Order(3)
    void testFindByCin() {
        Optional<CabinetMedicale> optCabinet = cabinetRepo.findByCin("CIN123");
        assertTrue(optCabinet.isPresent());
        assertEquals("Cabinet Test", optCabinet.get().getNom());
    }

    @Test
    @Order(4)
    void testFindByEmail() {
        Optional<CabinetMedicale> optCabinet = cabinetRepo.findByEmail("cabinet@test.com");
        assertTrue(optCabinet.isPresent());
        assertEquals("Cabinet Test", optCabinet.get().getNom());
    }

    @Test
    @Order(5)
    void testUpdateCabinet() {
        CabinetMedicale cabinet = cabinetRepo.findByCin("CIN123").orElseThrow();
        cabinet.setNom("Cabinet Modifié");
        cabinet.setEmail("cabinet-modifie@test.com");

        cabinetRepo.update(cabinet);

        CabinetMedicale updated = cabinetRepo.findById(cabinet.getId());
        assertEquals("Cabinet Modifié", updated.getNom());
        assertEquals("cabinet-modifie@test.com", updated.getEmail());
    }

    @Test
    @Order(6)
    void testDeleteCabinet() {
        CabinetMedicale cabinet = cabinetRepo.findByCin("CIN123").orElseThrow();
        cabinetRepo.delete(cabinet);

        CabinetMedicale deleted = cabinetRepo.findById(cabinet.getId());
        assertNull(deleted, "Cabinet doit être supprimé");
    }
}
