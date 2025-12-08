package ma.oralCare.repository.certificat;

import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.repository.modules.certificat.impl.CertificatRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CertificatRepositoryImplTest {

    private CertificatRepositoryImpl repository;
    private DossierMedicale testDossier;

    @BeforeAll
    void setup() {
        repository = new CertificatRepositoryImpl();

        // Création d'un dossier médical test
        testDossier = DossierMedicale.builder()
                .id(1L) // Si vous avez un repository pour créer le dossier, créez-le d'abord
                .build();
    }

    @Test
    void testCreateAndFindById() {
        Certificat certif = Certificat.builder()
                .dateDebut(LocalDate.now())
                .dateFin(LocalDate.now().plusDays(5))
                .duree(5)
                .noteMedecin("Repos complet")
                .dossierMedicale(testDossier)
                .build();

        repository.create(certif);
        assertNotNull(certif.getId(), "L'ID doit être généré");

        Certificat fetched = repository.findById(certif.getId());
        assertNotNull(fetched);
        assertEquals("Repos complet", fetched.getNoteMedecin());
    }

    @Test
    void testUpdate() {
        Certificat certif = repository.findAll().get(0);
        certif.setNoteMedecin("Repos partiel");
        repository.update(certif);

        Certificat updated = repository.findById(certif.getId());
        assertEquals("Repos partiel", updated.getNoteMedecin());
    }

    @Test
    void testDelete() {
        Certificat certif = repository.findAll().get(0);
        Long id = certif.getId();
        repository.delete(certif);

        Certificat deleted = repository.findById(id);
        assertNull(deleted, "Le certificat doit être supprimé");
    }

    @Test
    void testFindByDossierMedicaleId() {
        List<Certificat> list = repository.findByDossierMedicaleId(testDossier.getId());
        assertNotNull(list);
        for (Certificat certif : list) {
            assertEquals(testDossier.getId(), certif.getDossierMedicale().getId());
        }
    }

    @Test
    void testFindByDateDebut() {
        LocalDate date = LocalDate.now();
        List<Certificat> list = repository.findByDateDebut(date);
        assertNotNull(list);
        for (Certificat certif : list) {
            assertEquals(date, certif.getDateDebut());
        }
    }

    @Test
    void testFindValidCertificates() {
        LocalDate today = LocalDate.now();
        List<Certificat> validList = repository.findValidCertificates(today);
        assertNotNull(validList);
        for (Certificat certif : validList) {
            assertTrue(!certif.getDateFin().isBefore(today));
        }
    }

    @Test
    void testFindByNoteMedecinContaining() {
        String fragment = "Repos";
        List<Certificat> list = repository.findByNoteMedecinContaining(fragment);
        assertNotNull(list);
        for (Certificat certif : list) {
            assertTrue(certif.getNoteMedecin().contains(fragment));
        }
    }
}
