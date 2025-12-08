package ma.oralCare.repository.caisse;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.modules.caisse.impl.ChargesRepositoryImpl;
import ma.oralCare.repository.modules.userManager.impl.CabinetMedicaleRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ChargesRepositoryImplTest {

    private ChargesRepositoryImpl chargesRepository;
    private CabinetMedicaleRepositoryImpl cabinetRepository;
    private CabinetMedicale testCabinet;

    @BeforeAll
    void setup() {
        cabinetRepository = new CabinetMedicaleRepositoryImpl();
        chargesRepository = new ChargesRepositoryImpl();

        // Créer un cabinet test
        testCabinet = CabinetMedicale.builder()
                .nom("Cabinet Test")
                .email("cabinet@test.com")
                .tel1("0600000000")
                .build();
        cabinetRepository.create(testCabinet);
    }

    @AfterAll
    void teardown() {
        // Supprimer le cabinet test
        if (testCabinet.getId() != null) {
            cabinetRepository.deleteById(testCabinet.getId());
        }
    }

    @Test
    void testCreateAndFindById() {
        Charges charge = Charges.builder()
                .titre("Loyer")
                .description("Loyer du mois")
                .montant(5000.0)
                .date(LocalDateTime.now())
                .cabinetMedicale(testCabinet)
                .build();

        chargesRepository.create(charge);
        assertNotNull(charge.getId(), "L'ID doit être généré");

        Charges fetched = chargesRepository.findById(charge.getId());
        assertNotNull(fetched);
        assertEquals("Loyer", fetched.getTitre());
        assertEquals(testCabinet.getId(), fetched.getCabinetMedicale().getId());
    }

    @Test
    void testUpdate() {
        Charges charge = chargesRepository.findByTitreOrDescriptionContaining("Loyer").get(0);
        charge.setMontant(5500.0);
        chargesRepository.update(charge);

        Charges updated = chargesRepository.findById(charge.getId());
        assertEquals(5500.0, updated.getMontant());
    }

    @Test
    void testDelete() {
        Charges charge = chargesRepository.findByTitreOrDescriptionContaining("Loyer").get(0);
        Long id = charge.getId();
        chargesRepository.delete(charge);

        Charges deleted = chargesRepository.findById(id);
        assertNull(deleted, "La charge doit être supprimée");
    }

    @Test
    void testFindByDateBetweenAndCalculateTotal() {
        LocalDateTime now = LocalDateTime.now();
        Charges charge1 = Charges.builder()
                .titre("Eau")
                .description("Facture d'eau")
                .montant(300.0)
                .date(now.minusDays(2))
                .cabinetMedicale(testCabinet)
                .build();
        Charges charge2 = Charges.builder()
                .titre("Electricité")
                .description("Facture électricité")
                .montant(700.0)
                .date(now.minusDays(1))
                .cabinetMedicale(testCabinet)
                .build();

        chargesRepository.create(charge1);
        chargesRepository.create(charge2);

        List<Charges> charges = chargesRepository.findByDateBetween(now.minusDays(3), now);
        assertTrue(charges.size() >= 2);

        Double total = chargesRepository.calculateTotalChargesByDateBetween(now.minusDays(3), now);
        assertTrue(total >= 1000.0);
    }

    @Test
    void testFindByTitreOrDescriptionContaining() {
        List<Charges> result = chargesRepository.findByTitreOrDescriptionContaining("Eau");
        assertFalse(result.isEmpty());
        assertTrue(result.get(0).getTitre().contains("Eau") || result.get(0).getDescription().contains("Eau"));
    }

    @Test
    void testFindByCabinetMedicaleId() {
        List<Charges> charges = chargesRepository.findByCabinetMedicaleId(testCabinet.getId());
        assertFalse(charges.isEmpty());
        for (Charges c : charges) {
            assertEquals(testCabinet.getId(), c.getCabinetMedicale().getId());
        }
    }
}
