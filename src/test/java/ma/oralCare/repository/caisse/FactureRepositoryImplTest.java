package ma.oralCare.repository.caisse;

import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.modules.caisse.impl.FactureRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FactureRepositoryImplTest {

    private FactureRepositoryImpl factureRepository;
    private SituationFinanciere testSituation;
    private Consultation testConsultation;

    @BeforeAll
    void setup() {
        factureRepository = new FactureRepositoryImpl();

        // Créer une situation financiere test
        testSituation = SituationFinanciere.builder()
                .totaleDesActes(0.0)
                .totalePaye(0.0)
                .credit(0.0)
                .build();
        // Si vous avez un repository pour SituationFinanciere, utilisez-le pour créer l'objet

        // Créer une consultation test
        testConsultation = Consultation.builder()
                .observationMedecin("Test consultation")
                .build();
        // Idem, utilisez repository si nécessaire
    }

    @Test
    void testCreateAndFindById() {
        Facture facture = Facture.builder()
                .totaleFacture(1000.0)
                .totalePaye(0.0)
                .reste(1000.0)
                .statut(StatutFacture.PENDING)
                .dateFacture(LocalDateTime.now())
                .situationFinanciere(testSituation)
                .consultation(testConsultation)
                .build();

        factureRepository.create(facture);
        assertNotNull(facture.getId(), "L'ID doit être généré");

        Facture fetched = factureRepository.findById(facture.getId());
        assertNotNull(fetched);
        assertEquals(1000.0, fetched.getTotaleFacture());
        assertEquals(StatutFacture.PENDING, fetched.getStatut());
    }

    @Test
    void testUpdate() {
        Facture facture = factureRepository.findByStatut(StatutFacture.PENDING).get(0);
        facture.setTotalePaye(200.0);
        factureRepository.update(facture);

        Facture updated = factureRepository.findById(facture.getId());
        assertEquals(200.0, updated.getTotalePaye());
    }

    @Test
    void testDelete() {
        Facture facture = factureRepository.findByStatut(StatutFacture.PENDING).get(0);
        Long id = facture.getId();
        factureRepository.delete(facture);

        Facture deleted = factureRepository.findById(id);
        assertNull(deleted, "La facture doit être supprimée");
    }

    @Test
    void testFindBySituationFinanciereId() {
        List<Facture> factures = factureRepository.findBySituationFinanciereId(testSituation.getId());
        assertNotNull(factures);
        for (Facture f : factures) {
            assertEquals(testSituation.getId(), f.getSituationFinanciere().getId());
        }
    }

    @Test
    void testFindByConsultationId() {
        List<Facture> factures = factureRepository.findByConsultationId(testConsultation.getId());
        assertNotNull(factures);
        for (Facture f : factures) {
            assertEquals(testConsultation.getId(), f.getConsultation().getId());
        }
    }

    @Test
    void testFindByStatut() {
        List<Facture> pending = factureRepository.findByStatut(StatutFacture.PENDING);
        assertNotNull(pending);
        for (Facture f : pending) {
            assertEquals(StatutFacture.PENDING, f.getStatut());
        }
    }

    @Test
    void testFindByDateFactureBetween() {
        LocalDateTime now = LocalDateTime.now();
        List<Facture> factures = factureRepository.findByDateFactureBetween(now.minusDays(1), now.plusDays(1));
        assertNotNull(factures);
    }

    @Test
    void testEnregistrerPaiement() {
        Facture facture = factureRepository.findByStatut(StatutFacture.PENDING).get(0);
        double montantPaye = 500.0;

        Facture updated = factureRepository.enregistrerPaiement(facture.getId(), montantPaye);
        assertEquals(facture.getTotalePaye() + montantPaye, updated.getTotalePaye());
        assertEquals(facture.getTotaleFacture() - updated.getTotalePaye(), updated.getReste());
        if (updated.getReste() <= 0) {
            assertEquals(StatutFacture.PAID, updated.getStatut());
        }
    }

    @Test
    void testAnnulerFacture() {
        Facture facture = factureRepository.findByStatut(StatutFacture.PENDING).get(0);
        factureRepository.annulerFacture(facture.getId());

        Facture annulled = factureRepository.findById(facture.getId());
        assertEquals(StatutFacture.OVERDUE, annulled.getStatut());
        assertEquals(0.0, annulled.getTotalePaye());
        assertEquals(0.0, annulled.getReste());
    }

    @Test
    void testUpdateTotaux() {
        Facture facture = factureRepository.findByStatut(StatutFacture.PENDING).get(0);
        factureRepository.updateTotaux(facture.getId(), 2000.0, 0.0);

        Facture updated = factureRepository.findById(facture.getId());
        assertEquals(2000.0, updated.getTotaleFacture());
        assertEquals(0.0, updated.getReste());
        assertEquals(StatutFacture.PAID, updated.getStatut());
    }
}
