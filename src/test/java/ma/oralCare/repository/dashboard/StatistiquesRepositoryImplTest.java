package ma.oralCare.repository.dashboard;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.modules.dashboard.impl.StatistiquesRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatistiquesRepositoryImplTest {

    private StatistiquesRepositoryImpl repository;
    private CabinetMedicale testCabinet;

    @BeforeAll
    void setup() {
        repository = new StatistiquesRepositoryImpl();

        // Création d'un cabinet test
        testCabinet = CabinetMedicale.builder()
                .id(1L) // Assurez-vous que ce cabinet existe ou créez-le via un repository
                .build();
    }

    @Test
    void testCreateAndFindById() {
        Statistiques stat = Statistiques.builder()
                .nom("Test Statistique")
                .categorie(StatistiqueCategorie.REVENUE)
                .chiffre(1000.0)
                .dateCalcul(LocalDate.now())
                .cabinetMedicale(testCabinet)
                .build();

        repository.create(stat);
        assertNotNull(stat.getId(), "L'ID doit être généré");

        Statistiques fetched = repository.findById(stat.getId());
        assertNotNull(fetched);
        assertEquals("Test Statistique", fetched.getNom());
        assertEquals(StatistiqueCategorie.REVENUE, fetched.getCategorie());
    }

    @Test
    void testUpdate() {
        Statistiques stat = repository.findAll().get(0);
        stat.setChiffre(2000.0);
        repository.update(stat);

        Statistiques updated = repository.findById(stat.getId());
        assertEquals(2000.0, updated.getChiffre());
    }

    @Test
    void testDelete() {
        Statistiques stat = repository.findAll().get(0);
        Long id = stat.getId();
        repository.delete(stat);

        Statistiques deleted = repository.findById(id);
        assertNull(deleted, "La statistique doit être supprimée");
    }

    @Test
    void testFindByCabinetMedicaleId() {
        List<Statistiques> list = repository.findByCabinetMedicaleId(testCabinet.getId());
        assertNotNull(list);
        for (Statistiques s : list) {
            assertEquals(testCabinet.getId(), s.getCabinetMedicale().getId());
        }
    }

    @Test
    void testFindByCategorie() {
        List<Statistiques> list = repository.findByCategorie(StatistiqueCategorie.REVENUE);
        assertNotNull(list);
        for (Statistiques s : list) {
            assertEquals(StatistiqueCategorie.REVENUE, s.getCategorie());
        }
    }

    @Test
    void testFindByDateCalcul() {
        LocalDate today = LocalDate.now();
        List<Statistiques> list = repository.findByDateCalcul(today);
        assertNotNull(list);
        for (Statistiques s : list) {
            assertEquals(today, s.getDateCalcul());
        }
    }

    @Test
    void testFindByDateCalculBetween() {
        LocalDate start = LocalDate.now().minusDays(7);
        LocalDate end = LocalDate.now();
        List<Statistiques> list = repository.findByDateCalculBetween(start, end);
        assertNotNull(list);
        for (Statistiques s : list) {
            assertTrue(!s.getDateCalcul().isBefore(start) && !s.getDateCalcul().isAfter(end));
        }
    }

    @Test
    void testFindLatestByCategorieAndCabinet() {
        Optional<Statistiques> latest = repository.findLatestByCategorieAndCabinet(StatistiqueCategorie.REVENUE, testCabinet.getId());
        assertTrue(latest.isPresent());
        Statistiques stat = latest.get();
        assertEquals(StatistiqueCategorie.REVENUE, stat.getCategorie());
        assertEquals(testCabinet.getId(), stat.getCabinetMedicale().getId());
    }
}
