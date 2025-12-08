package ma.oralCare.repository.dossierMedical;

import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.modules.dossierMedical.impl.AntecedentRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AntecedentRepositoryImplTest {

    private AntecedentRepositoryImpl repository;
    private Patient testPatient;

    @BeforeAll
    void setup() {
        repository = new AntecedentRepositoryImpl();

        // Création d'un patient test
        testPatient = Patient.builder()
                .id(1L) // Assurez-vous que ce patient existe ou créez-le via un repository
                .nom("TestPatient")
                .prenom("TP")
                .build();
    }

    @Test
    void testCreateAndFindById() {
        Antecedent antecedent = Antecedent.builder()
                .nom("Test Antecedent")
                .categorie(CategorieAntecedent.ALLERGIE)
                .niveauRisque(NiveauDeRisque.HIGH)
                .build();

        repository.create(antecedent);
        assertNotNull(antecedent.getId(), "L'ID doit être généré");

        Antecedent fetched = repository.findById(antecedent.getId());
        assertNotNull(fetched);
        assertEquals("Test Antecedent", fetched.getNom());
        assertEquals(CategorieAntecedent.ALLERGIE, fetched.getCategorie());
    }

    @Test
    void testUpdate() {
        Antecedent antecedent = repository.findAll().get(0);
        antecedent.setNom("Updated Name");
        repository.update(antecedent);

        Antecedent updated = repository.findById(antecedent.getId());
        assertEquals("Updated Name", updated.getNom());
    }

    @Test
    void testDelete() {
        Antecedent antecedent = repository.findAll().get(0);
        Long id = antecedent.getId();
        repository.delete(antecedent);

        Antecedent deleted = repository.findById(id);
        assertNull(deleted, "L'antécédent doit être supprimé");
    }

    @Test
    void testFindByCategorie() {
        List<Antecedent> list = repository.findByCategorie(CategorieAntecedent.ALLERGIE);
        assertNotNull(list);
        for (Antecedent a : list) {
            assertEquals(CategorieAntecedent.ALLERGIE, a.getCategorie());
        }
    }

    @Test
    void testFindByNiveauRisque() {
        List<Antecedent> list = repository.findByNiveauRisque(NiveauDeRisque.HIGH);
        assertNotNull(list);
        for (Antecedent a : list) {
            assertEquals(NiveauDeRisque.HIGH, a.getNiveauRisque());
        }
    }

    @Test
    void testFindByNomContaining() {
        List<Antecedent> list = repository.findByNomContaining("Test");
        assertNotNull(list);
        for (Antecedent a : list) {
            assertTrue(a.getNom().contains("Test"));
        }
    }

    @Test
    void testLinkAndUnlinkAntecedentToPatient() {
        Antecedent antecedent = Antecedent.builder()
                .nom("Relation Test")
                .categorie(CategorieAntecedent.AUTRE)
                .niveauRisque(NiveauDeRisque.MEDIUM)
                .build();

        repository.create(antecedent);

        // Lier au patient
        repository.linkAntecedentToPatient(antecedent.getId(), testPatient.getId());
        List<Antecedent> patientAntecedents = repository.findByPatientId(testPatient.getId());
        assertTrue(patientAntecedents.stream().anyMatch(a -> a.getId().equals(antecedent.getId())));

        // Dissocier du patient
        repository.unlinkAntecedentFromPatient(antecedent.getId(), testPatient.getId());
        List<Antecedent> patientAntecedentsAfter = repository.findByPatientId(testPatient.getId());
        assertFalse(patientAntecedentsAfter.stream().anyMatch(a -> a.getId().equals(antecedent.getId())));
    }

    @Test
    void testFindByPatientId() {
        List<Antecedent> list = repository.findByPatientId(testPatient.getId());
        assertNotNull(list);
    }
}
