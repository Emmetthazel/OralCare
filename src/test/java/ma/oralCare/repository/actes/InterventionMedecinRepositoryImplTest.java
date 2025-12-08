package ma.oralCare.repository.actes;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.InterventionMedecin;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.actes.api.InterventionMedecinRepository;
import ma.oralCare.repository.modules.actes.impl.InterventionMedecinRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class InterventionMedecinRepositoryImplTest {

    private InterventionMedecinRepository repo;

    private Acte sampleActe;
    private Consultation sampleConsultation;

    @BeforeEach
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset(); // Inclut actes, consultations, interventions, etc.

        repo = new InterventionMedecinRepositoryImpl();

        // Exemple : récupérer un acte et une consultation existants
        sampleActe = DbTestUtils.getFirstActe();
        sampleConsultation = DbTestUtils.getFirstConsultation();
    }

    // =========================
    // ✅ CRUD
    // =========================

    @Test
    @DisplayName("1) create : insère une nouvelle intervention")
    void testCreate() {
        InterventionMedecin intervention = InterventionMedecin.builder()
                .acte(sampleActe)
                .consultation(sampleConsultation)
                .numDent(12)
                .prixDePatient(500.0)
                .build();

        repo.create(intervention);

        assertThat(intervention.getId()).isNotNull();

        InterventionMedecin fromDb = repo.findById(intervention.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getNumDent()).isEqualTo(12);
        assertThat(fromDb.getPrixDePatient()).isEqualTo(500.0);
    }

    @Test
    @DisplayName("2) findById : retourne une intervention existante")
    void testFindById() {
        InterventionMedecin first = DbTestUtils.getFirstIntervention();
        InterventionMedecin fetched = repo.findById(first.getId());

        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(first.getId());
    }

    @Test
    @DisplayName("3) update : modifie le prix de patient")
    void testUpdate() {
        InterventionMedecin intervention = DbTestUtils.getFirstIntervention();
        intervention.setPrixDePatient(750.0);

        repo.update(intervention);

        InterventionMedecin updated = repo.findById(intervention.getId());
        assertThat(updated.getPrixDePatient()).isEqualTo(750.0);
    }

    @Test
    @DisplayName("4) deleteById : supprime une intervention")
    void testDeleteById() {
        InterventionMedecin intervention = DbTestUtils.getFirstIntervention();
        long before = repo.findAll().size();

        repo.deleteById(intervention.getId());

        assertThat(repo.findById(intervention.getId())).isNull();
        assertThat(repo.findAll().size()).isEqualTo(before - 1);
    }

    // =========================
    // ✅ Méthodes métier
    // =========================

    @Test
    @DisplayName("5) findByActeId : récupère toutes les interventions pour un acte")
    void testFindByActeId() {
        List<InterventionMedecin> list = repo.findByActeId(sampleActe.getId());

        assertThat(list).isNotEmpty();
        assertThat(list).allMatch(i -> i.getActe().getId().equals(sampleActe.getId()));
    }

    @Test
    @DisplayName("6) findByNumDent : récupère toutes les interventions pour un numéro de dent")
    void testFindByNumDent() {
        List<InterventionMedecin> list = repo.findByNumDent(12);

        assertThat(list).allMatch(i -> i.getNumDent().equals(12));
    }

    @Test
    @DisplayName("7) calculateTotalPatientPriceByConsultationId : somme des prix pour une consultation")
    void testCalculateTotalPatientPriceByConsultationId() {
        Double total = repo.calculateTotalPatientPriceByConsultationId(sampleConsultation.getId());

        assertThat(total).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("8) appliquerRemisePonctuelle : applique une remise")
    void testAppliquerRemisePonctuelle() {
        InterventionMedecin intervention = DbTestUtils.getFirstIntervention();
        Double prixInitial = intervention.getPrixDePatient();

        InterventionMedecin updated = repo.appliquerRemisePonctuelle(intervention.getId(), 10.0);
        assertThat(updated.getPrixDePatient()).isEqualTo(prixInitial * 0.9);
    }

    @Test
    @DisplayName("9) findActeByInterventionId : récupère l'acte lié")
    void testFindActeByInterventionId() {
        InterventionMedecin intervention = DbTestUtils.getFirstIntervention();

        Optional<Acte> acte = repo.findActeByInterventionId(intervention.getId());
        assertThat(acte).isPresent();
        assertThat(acte.get().getId()).isEqualTo(intervention.getActe().getId());
    }

    @Test
    @DisplayName("10) existsByConsultationActeAndDent : vérifie existence")
    void testExistsByConsultationActeAndDent() {
        InterventionMedecin intervention = DbTestUtils.getFirstIntervention();

        boolean exists = repo.existsByConsultationActeAndDent(
                intervention.getConsultation().getId(),
                intervention.getActe().getId(),
                intervention.getNumDent()
        );

        assertThat(exists).isTrue();
    }
}
