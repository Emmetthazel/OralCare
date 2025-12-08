package ma.oralCare.repository.dossierMedical;

import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.acte.Acte;
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConsultationRepositoryImplTest {

    private ConsultationRepositoryImpl repository;
    private DossierMedicale testDossier;

    @BeforeAll
    void setup() {
        repository = new ConsultationRepositoryImpl();

        // Création d'un dossier médical test (assurez-vous qu'il existe ou créez-le via repository)
        testDossier = DossierMedicale.builder()
                .id(1L)
                .build();
    }

    @Test
    void testCreateAndFindById() {
        Consultation consultation = Consultation.builder()
                .date(LocalDate.now())
                .statut(StatutConsultation.SCHEDULED)
                .observationMedecin("Observation test")
                .dossierMedicale(testDossier)
                .build();

        repository.create(consultation);
        assertNotNull(consultation.getId(), "L'ID doit être généré");

        Consultation fetched = repository.findById(consultation.getId());
        assertNotNull(fetched);
        assertEquals(StatutConsultation.SCHEDULED, fetched.getStatut());
        assertEquals("Observation test", fetched.getObservationMedecin());
    }

    @Test
    void testUpdate() {
        Consultation consultation = repository.findAll().get(0);
        consultation.setObservationMedecin("Updated observation");
        repository.update(consultation);

        Consultation updated = repository.findById(consultation.getId());
        assertEquals("Updated observation", updated.getObservationMedecin());
    }

    @Test
    void testDelete() {
        Consultation consultation = repository.findAll().get(0);
        Long id = consultation.getId();
        repository.delete(consultation);

        Consultation deleted = repository.findById(id);
        assertNull(deleted, "La consultation doit être supprimée");
    }

    @Test
    void testFindByDossierMedicaleId() {
        List<Consultation> list = repository.findByDossierMedicaleId(testDossier.getId());
        assertNotNull(list);
        for (Consultation c : list) {
            assertEquals(testDossier.getId(), c.getDossierMedicale().getId());
        }
    }

    @Test
    void testFindByStatut() {
        List<Consultation> list = repository.findByStatut(StatutConsultation.SCHEDULED);
        assertNotNull(list);
        for (Consultation c : list) {
            assertEquals(StatutConsultation.SCHEDULED, c.getStatut());
        }
    }

    @Test
    void testFindByDate() {
        LocalDate today = LocalDate.now();
        List<Consultation> list = repository.findByDate(today);
        assertNotNull(list);
        for (Consultation c : list) {
            assertEquals(today, c.getDate());
        }
    }

    @Test
    void testUpdateStatut() {
        Consultation consultation = repository.findAll().get(0);
        repository.updateStatut(consultation.getId(), StatutConsultation.COMPLETED);

        Consultation updated = repository.findById(consultation.getId());
        assertEquals(StatutConsultation.COMPLETED, updated.getStatut());
    }

    @Test
    void testUpdateObservation() {
        Consultation consultation = repository.findAll().get(0);
        repository.updateObservation(consultation.getId(), "Nouvelle observation");

        Consultation updated = repository.findById(consultation.getId());
        assertEquals("Nouvelle observation", updated.getObservationMedecin());
    }

    @Test
    void testAddIntervention() {
        Consultation consultation = repository.findAll().get(0);

        Acte acte = Acte.builder()
                .id(1L) // Assurez-vous que l'acte existe
                .libelle("Acte Test")
                .build();

        InterventionMedecin intervention = InterventionMedecin.builder()
                .prixDePatient(100.0)
                .numDent(11)
                .acte(acte)
                .build();

        repository.addIntervention(consultation.getId(), intervention);
        assertNotNull(intervention.getId(), "L'intervention doit avoir un ID généré");
    }
}
