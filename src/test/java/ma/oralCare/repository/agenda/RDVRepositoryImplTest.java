package ma.oralCare.repository.agenda;

import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.repository.DbTestUtils;
import org.junit.jupiter.api.*;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RDVRepositoryImplTest {

    private RDVRepositoryImpl rdvRepository;
    private DossierMedicale testDossier;

    @BeforeAll
    void setup() {
        // Nettoyer et insérer un jeu de données complet
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();

        rdvRepository = new RDVRepositoryImpl();

        // On récupère un dossier existant pour associer aux RDV
        testDossier = DbTestUtils.getFirstDossierMedicale();
        assertNotNull(testDossier, "Le dossier médical de test ne doit pas être null");
    }

    @AfterAll
    void teardown() {
        DbTestUtils.cleanAll();
    }

    @Test
    void testCreateAndFindById() {
        RDV rdv = RDV.builder()
                .dossierMedicale(testDossier)
                .date(LocalDate.now().plusDays(1))
                .heure(LocalTime.of(10, 0))
                .motif("Contrôle")
                .statut(StatutRDV.PENDING)
                .noteMedecin("Aucune")
                .build();

        rdvRepository.create(rdv);
        assertNotNull(rdv.getId(), "L'ID du RDV doit être généré");

        RDV fetched = rdvRepository.findById(rdv.getId());
        assertNotNull(fetched, "Le RDV récupéré ne doit pas être null");
        assertEquals(rdv.getMotif(), fetched.getMotif());
    }

    @Test
    void testUpdate() {
        RDV rdv = DbTestUtils.getFirstRDV();
        assertNotNull(rdv, "Le RDV existant doit être récupéré");

        rdv.setMotif("Motif mis à jour");
        rdvRepository.update(rdv);

        RDV updated = rdvRepository.findById(rdv.getId());
        assertEquals("Motif mis à jour", updated.getMotif());
    }

    @Test
    void testDelete() {
        RDV rdv = DbTestUtils.getFirstRDV();
        assertNotNull(rdv, "Le RDV existant doit être récupéré");

        Long id = rdv.getId();
        rdvRepository.delete(rdv);

        RDV deleted = rdvRepository.findById(id);
        assertNull(deleted, "Le RDV doit être supprimé");
    }

    @Test
    void testFindByDossierMedicaleId() {
        List<RDV> rdvs = rdvRepository.findByDossierMedicaleId(testDossier.getId());
        assertNotNull(rdvs);
        assertFalse(rdvs.isEmpty(), "La liste des RDV pour le dossier doit contenir au moins un élément");
    }

    @Test
    void testExistsByDateAndHeureAndMedecinId() {
        RDV rdv = DbTestUtils.getFirstRDV();
        assertNotNull(rdv, "Le RDV existant doit être récupéré");

        boolean exists = rdvRepository.existsByDateAndHeureAndMedecinId(
                rdv.getDate(),
                rdv.getHeure(),
                rdv.getDossierMedicale().getMedecin().getId()
        );
        assertTrue(exists, "Le RDV doit exister pour le médecin à cette date et heure");
    }

    @Test
    void testUpdateStatut() {
        RDV rdv = DbTestUtils.getFirstRDV();
        assertNotNull(rdv);

        RDV updated = rdvRepository.updateStatut(rdv.getId(), StatutRDV.CANCELLED);
        assertEquals(StatutRDV.CANCELLED, updated.getStatut());
    }

    @Test
    void testFindByStatut() {
        List<RDV> rdvs = rdvRepository.findByStatut(StatutRDV.PENDING);
        assertNotNull(rdvs);
    }
}
