package ma.oralCare.repository.medicament;

import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.entities.medicament.Prescription;
import ma.oralCare.repository.DbTestUtils;
import org.junit.jupiter.api.*;
import ma.oralCare.repository.modules.medicament.impl.PrescriptionRepositoryImpl;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrescriptionRepositoryImplTest {

    private PrescriptionRepositoryImpl repository;

    private Ordonnance testOrdonnance;
    private Medicament testMedicament;

    @BeforeAll
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();  // inclut Ordonnances et Medicaments

        repository = new PrescriptionRepositoryImpl();

        // récupérer un Ordonnance et un Medicament pour tests
        testOrdonnance = DbTestUtils.getFirstOrdonnance();
        testMedicament = DbTestUtils.getFirstMedicament();

        assertNotNull(testOrdonnance);
        assertNotNull(testMedicament);
    }

    @AfterAll
    void tearDown() {
        DbTestUtils.cleanAll();
    }

    // =========================
    // TEST CREATE
    // =========================
    @Test
    void testCreateAndFindById() {
        Prescription prescription = Prescription.builder()
                .quantite(2)
                .frequence("2 fois/jour")
                .dureeEnJours(5)
                .ordonnance(testOrdonnance)
                .medicament(testMedicament)
                .build();

        repository.create(prescription);

        assertNotNull(prescription.getId(), "L'ID doit être généré après création");

        Prescription found = repository.findById(prescription.getId());
        assertNotNull(found);
        assertEquals(2, found.getQuantite());
        assertEquals("2 fois/jour", found.getFrequence());
    }

    // =========================
    // TEST FIND ALL
    // =========================
    @Test
    void testFindAll() {
        List<Prescription> prescriptions = repository.findAll();
        assertNotNull(prescriptions);
        assertFalse(prescriptions.isEmpty(), "La liste des prescriptions ne doit pas être vide");
    }

    // =========================
    // TEST UPDATE
    // =========================
    @Test
    void testUpdate() {
        Prescription prescription = repository.findAll().get(0);
        prescription.setQuantite(10);
        prescription.setFrequence("1 fois/jour");

        repository.update(prescription);

        Prescription updated = repository.findById(prescription.getId());
        assertEquals(10, updated.getQuantite());
        assertEquals("1 fois/jour", updated.getFrequence());
    }

    // =========================
    // TEST DELETE
    // =========================
    @Test
    void testDelete() {
        Prescription prescription = Prescription.builder()
                .quantite(1)
                .frequence("3 fois/jour")
                .dureeEnJours(7)
                .ordonnance(testOrdonnance)
                .medicament(testMedicament)
                .build();
        repository.create(prescription);

        Long id = prescription.getId();
        repository.delete(prescription);

        Prescription deleted = repository.findById(id);
        assertNull(deleted, "La prescription doit être supprimée");
    }

    // =========================
    // TEST FIND BY ORDONNANCE ID
    // =========================
    @Test
    void testFindByOrdonnanceId() {
        List<Prescription> result = repository.findByOrdonnanceId(testOrdonnance.getId());
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Il doit y avoir des prescriptions pour l'ordonnance test");
    }
}
