package ma.oralCare.service.modules.caisse.test;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.repository.test.DbTestUtils;
import ma.oralCare.repository.test.DbTestUtils.Module;
import ma.oralCare.service.modules.caisse.impl.CaisseServiceImpl;
import ma.oralCare.service.modules.caisse.impl.FactureServiceImpl;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Test console simple pour les services de caisse / facture.
 */
public class TestCaisseServices {

    private final DbTestUtils dbUtils = DbTestUtils.getInstance();

    private final CaisseServiceImpl caisseService = new CaisseServiceImpl();
    private final FactureServiceImpl factureService = new FactureServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST CAISSE SERVICES ===");
        TestCaisseServices tester = new TestCaisseServices();
        tester.run();
        System.out.println("=== FIN TEST CAISSE SERVICES ===");
    }

    private void run() {
        dbUtils.cleanUp(Module.ALL); // Nettoyage large pour éviter les conflits

        // Ce test est volontairement léger, car la création complète de SituationFinanciere
        // et Facture dépend fortement du schéma et des autres modules.
        // On se contente ici de vérifier que les méthodes peuvent être appelées sans erreur
        // sur une plage de dates vide.

        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        List<Facture> factures = factureService.getFacturesBetween(start, end);
        System.out.println("Appel getFacturesBetween OK, factures trouvées: " + factures.size());
    }
}


