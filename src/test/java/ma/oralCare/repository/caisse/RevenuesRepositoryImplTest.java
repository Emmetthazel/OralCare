package ma.oralCare.repository.caisse;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.repository.modules.caisse.impl.RevenuesRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RevenuesRepositoryImplTest {

    private RevenuesRepositoryImpl revenuesRepository;
    private CabinetMedicale testCabinet;

    @BeforeAll
    void setup() {
        revenuesRepository = new RevenuesRepositoryImpl();

        // Création d'un cabinet médical test
        testCabinet = CabinetMedicale.builder()
                .nom("Cabinet Test")
                .email("test@cabinet.com")
                .tel1("0612345678")
                .build();
        // Si vous avez un repository pour CabinetMedicale, utilisez-le pour créer l'objet et récupérer l'ID
    }

    @Test
    void testCreateAndFindById() {
        Revenues revenue = Revenues.builder()
                .titre("Consultation")
                .description("Revenu pour consultation")
                .montant(500.0)
                .date(LocalDateTime.now())
                .cabinetMedicale(testCabinet)
                .build();

        revenuesRepository.create(revenue);
        assertNotNull(revenue.getId(), "L'ID doit être généré");

        Revenues fetched = revenuesRepository.findById(revenue.getId());
        assertNotNull(fetched);
        assertEquals(500.0, fetched.getMontant());
        assertEquals("Consultation", fetched.getTitre());
    }

    @Test
    void testUpdate() {
        Revenues revenue = revenuesRepository.findAll().get(0);
        revenue.setMontant(750.0);
        revenue.setTitre("Consultation Mise à Jour");
        revenuesRepository.update(revenue);

        Revenues updated = revenuesRepository.findById(revenue.getId());
        assertEquals(750.0, updated.getMontant());
        assertEquals("Consultation Mise à Jour", updated.getTitre());
    }

    @Test
    void testDelete() {
        Revenues revenue = revenuesRepository.findAll().get(0);
        Long id = revenue.getId();
        revenuesRepository.delete(revenue);

        Revenues deleted = revenuesRepository.findById(id);
        assertNull(deleted, "Le revenu doit être supprimé");
    }

    @Test
    void testFindByCabinetMedicaleId() {
        List<Revenues> list = revenuesRepository.findByCabinetMedicaleId(testCabinet.getId());
        assertNotNull(list);
        for (Revenues r : list) {
            assertEquals(testCabinet.getId(), r.getCabinetMedicale().getId());
        }
    }

    @Test
    void testFindByTitreContaining() {
        Revenues revenue = Revenues.builder()
                .titre("Test Recherche")
                .description("Description")
                .montant(100.0)
                .date(LocalDateTime.now())
                .cabinetMedicale(testCabinet)
                .build();
        revenuesRepository.create(revenue);

        List<Revenues> result = revenuesRepository.findByTitreContaining("Recherche");
        assertFalse(result.isEmpty());
        boolean found = result.stream().anyMatch(r -> r.getId().equals(revenue.getId()));
        assertTrue(found, "Le revenu recherché doit être présent");
    }

    @Test
    void testCalculateTotalRevenuesBetween() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        double total = revenuesRepository.calculateTotalRevenuesBetween(start, end);
        assertTrue(total >= 0.0);
    }

    @Test
    void testFindPage() {
        // Créer quelques revenus pour la pagination
        for (int i = 0; i < 5; i++) {
            Revenues r = Revenues.builder()
                    .titre("PageTest " + i)
                    .description("Desc")
                    .montant(50.0 + i)
                    .date(LocalDateTime.now())
                    .cabinetMedicale(testCabinet)
                    .build();
            revenuesRepository.create(r);
        }

        List<Revenues> page = revenuesRepository.findPage(3, 0);
        assertEquals(3, page.size());

        List<Revenues> nextPage = revenuesRepository.findPage(3, 3);
        assertTrue(nextPage.size() >= 0);
    }
}
