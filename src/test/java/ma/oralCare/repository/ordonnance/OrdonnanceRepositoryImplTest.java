package ma.oralCare.repository.ordonnance;

import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.repository.DbTestUtils;
import org.junit.jupiter.api.*;
import ma.oralCare.repository.modules.ordonnance.impl.OrdonnanceRepositoryImpl;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrdonnanceRepositoryImplTest {

    private OrdonnanceRepositoryImpl ordonnanceRepository;

    @BeforeAll
    void setupAll() {
        ordonnanceRepository = new OrdonnanceRepositoryImpl();
    }

    @BeforeEach
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();
    }

    @Test
    void testCreateAndFindById() {
        Ordonnance ordonnance = Ordonnance.builder()
                .date(LocalDate.now())
                .dossierMedicale(DbTestUtils.getFirstDossierMedicale())
                .build();

        ordonnanceRepository.create(ordonnance);

        assertNotNull(ordonnance.getId(), "L'ID doit être généré après création");

        Ordonnance fromDb = ordonnanceRepository.findById(ordonnance.getId());
        assertNotNull(fromDb, "L'ordonnance créée doit pouvoir être retrouvée");
        assertEquals(ordonnance.getDate(), fromDb.getDate());
        assertEquals(ordonnance.getDossierMedicale().getId(), fromDb.getDossierMedicale().getId());
    }

    @Test
    void testFindAll() {
        List<Ordonnance> all = ordonnanceRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty(), "Il doit y avoir au moins une ordonnance dans la base");
    }

    @Test
    void testUpdate() {
        Ordonnance ordonnance = DbTestUtils.getFirstOrdonnance();
        assertNotNull(ordonnance);

        LocalDate newDate = ordonnance.getDate().plusDays(1);
        ordonnance.setDate(newDate);

        ordonnanceRepository.update(ordonnance);

        Ordonnance fromDb = ordonnanceRepository.findById(ordonnance.getId());
        assertEquals(newDate, fromDb.getDate(), "La date doit être mise à jour");
    }

    @Test
    void testDelete() {
        Ordonnance ordonnance = DbTestUtils.getFirstOrdonnance();
        assertNotNull(ordonnance);

        ordonnanceRepository.delete(ordonnance);

        Ordonnance fromDb = ordonnanceRepository.findById(ordonnance.getId());
        assertNull(fromDb, "L'ordonnance doit être supprimée");
    }

    @Test
    void testFindByDossierMedicaleId() {
        Long dossierId = DbTestUtils.getFirstDossierMedicale().getId();
        List<Ordonnance> ordonnances = ordonnanceRepository.findByDossierMedicaleId(dossierId);

        assertNotNull(ordonnances);
        assertFalse(ordonnances.isEmpty(), "Doit trouver des ordonnances pour le dossier");
        assertEquals(dossierId, ordonnances.get(0).getDossierMedicale().getId());
    }

    @Test
    void testFindByDate() {
        LocalDate date = LocalDate.now();
        Ordonnance ordonnance = Ordonnance.builder()
                .date(date)
                .dossierMedicale(DbTestUtils.getFirstDossierMedicale())
                .build();
        ordonnanceRepository.create(ordonnance);

        List<Ordonnance> ordonnances = ordonnanceRepository.findByDate(date);
        assertNotNull(ordonnances);
        assertTrue(ordonnances.stream().anyMatch(o -> o.getId().equals(ordonnance.getId())),
                "L'ordonnance créée doit être retrouvée par date");
    }

    @Test
    void testFindPrescriptionsByOrdonnanceId() {
        Ordonnance ordonnance = DbTestUtils.getFirstOrdonnance();
        assertNotNull(ordonnance);

        List<?> prescriptions = ordonnanceRepository.findPrescriptionsByOrdonnanceId(ordonnance.getId());
        assertNotNull(prescriptions);
    }
}
