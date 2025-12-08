package ma.oralCare.repository.medicament;

import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.medicament.Medicament;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.medicament.impl.MedicamentRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedicamentRepositoryImplTest {

    private MedicamentRepositoryImpl repository;

    // =========================
    // ✅ INITIALISATION
    // =========================
    @BeforeAll
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();  // inclut maintenant les médicaments

        repository = new MedicamentRepositoryImpl();
    }

    @AfterAll
    void tearDown() {
        DbTestUtils.cleanAll();
    }

    // =========================
    //  FIND ALL
    // =========================
    @Test
    void testFindAll() {
        List<Medicament> medicaments = repository.findAll();
        assertNotNull(medicaments);
        assertFalse(medicaments.isEmpty(), "La liste des médicaments ne doit pas être vide");
    }

    // =========================
    // FIND BY ID
    // =========================
    @Test
    void testFindById() {
        Medicament med = DbTestUtils.getFirstMedicament();
        assertNotNull(med);

        Medicament found = repository.findById(med.getId());
        assertNotNull(found);
        assertEquals(med.getId(), found.getId());
    }

    // =========================
    // CREATE
    // =========================
    @Test
    void testCreate() {
        Medicament med = Medicament.builder()
                .nom("Ibuprofène")
                .laboratoire("Bayer")
                .type("Anti-inflammatoire")
                .forme(FormeMedicament.TABLET)
                .remboursable(true)
                .prixUnitaire(22.5)
                .description("Douleur et inflammation")
                .build();

        repository.create(med);

        assertNotNull(med.getId(), "L'ID du médicament doit être généré");

        Medicament found = repository.findById(med.getId());
        assertNotNull(found);
        assertEquals("Ibuprofène", found.getNom());
    }

    // =========================
    // UPDATE
    // =========================
    @Test
    void testUpdate() {
        Medicament med = DbTestUtils.getFirstMedicament();
        assertNotNull(med);

        med.setPrixUnitaire(99.9);
        med.setDescription("Description modifiée");

        repository.update(med);

        Medicament updated = repository.findById(med.getId());
        assertEquals(99.9, updated.getPrixUnitaire());
        assertEquals("Description modifiée", updated.getDescription());
    }

    // =========================
    // DELETE
    // =========================
    @Test
    void testDelete() {
        Medicament med = Medicament.builder()
                .nom("TestDelete")
                .laboratoire("TestLab")
                .type("TestType")
                .forme(FormeMedicament.SYRUP)
                .remboursable(false)
                .prixUnitaire(12.0)
                .description("À supprimer")
                .build();

        repository.create(med);
        Long id = med.getId();

        repository.delete(med);

        Medicament deleted = repository.findById(id);
        assertNull(deleted, "Le médicament doit être supprimé");
    }

    // =========================
    // FIND BY NOM CONTAINING
    // =========================
    @Test
    void testFindByNomContaining() {
        List<Medicament> result = repository.findByNomContaining("Para");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // =========================
    // FIND BY LABORATOIRE
    // =========================
    @Test
    void testFindByLaboratoire() {
        List<Medicament> result = repository.findByLaboratoire("Sanofi");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // =========================
    // FIND BY FORME
    // =========================
    @Test
    void testFindByForme() {
        List<Medicament> result = repository.findByForme(FormeMedicament.TABLET);
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    // =========================
    // FIND BY REMBOURSABLE
    // =========================
    @Test
    void testFindByRemboursable() {
        List<Medicament> remboursables = repository.findByRemboursable(true);
        assertNotNull(remboursables);
        assertFalse(remboursables.isEmpty());
    }
}
