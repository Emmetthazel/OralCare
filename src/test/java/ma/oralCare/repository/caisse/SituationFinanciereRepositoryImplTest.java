package ma.oralCare.repository.caisse;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.entities.enums.EnPromo;
import ma.oralCare.entities.enums.StatutSituationFinanciere;
import ma.oralCare.repository.modules.caisse.impl.SituationFinanciereRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SituationFinanciereRepositoryImplTest {

    private SituationFinanciereRepositoryImpl repository;
    private DossierMedicale testDossier;

    @BeforeAll
    void setup() {
        repository = new SituationFinanciereRepositoryImpl();

        // Création d'un dossier médical test
        testDossier = DossierMedicale.builder()
                .id(1L) // Si vous avez un repository pour créer le dossier, créez-le d'abord
                .build();
    }

    @Test
    void testCreateAndFindById() {
        SituationFinanciere sf = SituationFinanciere.builder()
                .totaleDesActes(1000.0)
                .totalePaye(500.0)
                .credit(500.0)
                .statut(StatutSituationFinanciere.ACTIVE)
                .enPromo(EnPromo.NO)
                .dossierMedicale(testDossier)
                .build();

        repository.create(sf);
        assertNotNull(sf.getId(), "L'ID doit être généré");

        SituationFinanciere fetched = repository.findById(sf.getId());
        assertNotNull(fetched);
        assertEquals(1000.0, fetched.getTotaleDesActes());
    }

    @Test
    void testUpdate() {
        SituationFinanciere sf = repository.findAll().get(0);
        sf.setTotalePaye(800.0);
        repository.update(sf);

        SituationFinanciere updated = repository.findById(sf.getId());
        assertEquals(800.0, updated.getTotalePaye());
    }

    @Test
    void testDelete() {
        SituationFinanciere sf = repository.findAll().get(0);
        Long id = sf.getId();
        repository.delete(sf);

        SituationFinanciere deleted = repository.findById(id);
        assertNull(deleted, "La situation financière doit être supprimée");
    }

    @Test
    void testFindByPatientId() {
        Optional<SituationFinanciere> sfOpt = repository.findByPatientId(testDossier.getPatient() != null ? testDossier.getPatient().getId() : 1L);
        // Selon la base, l'Optional peut être vide
        assertTrue(sfOpt.isEmpty() || sfOpt.get().getDossierMedicale().getId().equals(testDossier.getId()));
    }

    @Test
    void testFindActiveSituations() {
        List<SituationFinanciere> activeList = repository.findActiveSituations();
        assertNotNull(activeList);
        for (SituationFinanciere sf : activeList) {
            assertTrue(sf.getStatut() == StatutSituationFinanciere.ACTIVE || sf.getCredit() > 0);
        }
    }

    @Test
    void testFindAllByPatientId() {
        List<SituationFinanciere> list = repository.findAllByPatientId(testDossier.getPatient() != null ? testDossier.getPatient().getId() : 1L);
        assertNotNull(list);
        for (SituationFinanciere sf : list) {
            assertEquals(testDossier.getId(), sf.getDossierMedicale().getId());
        }
    }

    @Test
    void testReinitialiserSF() {
        SituationFinanciere sf = repository.findAll().get(0);
        repository.reinitialiserSF(sf.getId());

        SituationFinanciere refreshed = repository.findById(sf.getId());
        assertEquals(0.0, refreshed.getCredit());
        assertEquals(StatutSituationFinanciere.CLOSED, refreshed.getStatut());
    }

    @Test
    void testUpdateTotaux() {
        SituationFinanciere sf = repository.findAll().get(0);
        repository.updateTotaux(sf.getId(), 1200.0, 1200.0, 0.0);

        SituationFinanciere updated = repository.findById(sf.getId());
        assertEquals(1200.0, updated.getTotaleDesActes());
        assertEquals(1200.0, updated.getTotalePaye());
        assertEquals(0.0, updated.getCredit());
        assertEquals(StatutSituationFinanciere.CLOSED, updated.getStatut());
    }
}
