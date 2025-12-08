package ma.oralCare.repository.dossierMedical;

import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DossierMedicaleRepositoryImplTest {

    private DossierMedicaleRepositoryImpl repository;
    private DossierMedicale testDossier;

    // =========================
    // ✅ INITIALISATION
    // =========================
    @BeforeAll
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();

        repository = new DossierMedicaleRepositoryImpl();

        testDossier = DbTestUtils.getFirstDossierMedicale();
        assertNotNull(testDossier, "Le dossier médical seedé doit exister");
    }

    @AfterAll
    void teardown() {
        DbTestUtils.cleanAll();
    }

    // =========================
    // ✅ FIND BY ID
    // =========================
    @Test
    void testFindById() {
        DossierMedicale found = repository.findById(testDossier.getId());
        assertNotNull(found);
        assertEquals(testDossier.getId(), found.getId());
    }

    // =========================
    // ✅ FIND ALL
    // =========================
    @Test
    void testFindAll() {
        List<DossierMedicale> dossiers = repository.findAll();
        assertNotNull(dossiers);
        assertFalse(dossiers.isEmpty());
    }

    // =========================
    // ✅ FIND BY PATIENT ID
    // =========================
    @Test
    void testFindByPatientId() {
        Long patientId = 1L; // vient du seed SQL
        Optional<DossierMedicale> dossierOpt = repository.findByPatientId(patientId);
        assertTrue(dossierOpt.isPresent());
    }

    // =========================
    // ✅ CONSULTATIONS DU DOSSIER
    // =========================
    @Test
    void testFindConsultationsByDossierId() {
        List<Consultation> consultations =
                repository.findConsultationsByDossierId(testDossier.getId());

        assertNotNull(consultations);
    }

    // =========================
    // ✅ ORDONNANCES DU DOSSIER
    // =========================
    @Test
    void testFindOrdonnancesByDossierId() {
        List<Ordonnance> ordonnances =
                repository.findOrdonnancesByDossierId(testDossier.getId());

        assertNotNull(ordonnances);
    }

    // =========================
    // ✅ CERTIFICATS DU DOSSIER
    // =========================
    @Test
    void testFindCertificatsByDossierId() {
        List<Certificat> certificats =
                repository.findCertificatsByDossierId(testDossier.getId());

        assertNotNull(certificats);
    }
}
