package ma.oralCare.service.modules.cabinet.test;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.repository.test.DbTestUtils;
import ma.oralCare.repository.test.DbTestUtils.Module;
import ma.oralCare.service.modules.cabinet.impl.CabinetMedicalServiceImpl;
import ma.oralCare.service.modules.cabinet.impl.ParametrageServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Test console simple pour les services de cabinet (infos, charges).
 */
public class TestCabinetServices {

    private final DbTestUtils dbUtils = DbTestUtils.getInstance();

    private final CabinetMedicalServiceImpl cabinetService = new CabinetMedicalServiceImpl();
    private final ParametrageServiceImpl parametrageService = new ParametrageServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST CABINET SERVICES ===");
        TestCabinetServices tester = new TestCabinetServices();
        tester.run();
        System.out.println("=== FIN TEST CABINET SERVICES ===");
    }

    private void run() {
        dbUtils.cleanUp(Module.CHARGES_ONLY);

        // 1. Charger ou créer un cabinet de test (même ID que DbTestUtils)
        Long cabinetId = DbTestUtils.CABINET_ID_TEST;
        Optional<CabinetMedicale> existant = parametrageService.chargerParametrage(cabinetId);
        CabinetMedicale cabinet;

        if (existant.isEmpty()) {
            cabinet = dbUtils.createCabinetObject();
            cabinet.setIdEntite(cabinetId);
            cabinetService.saveCabinet(cabinet);
            System.out.println("✅ Cabinet créé via service. ID = " + cabinet.getIdEntite());
        } else {
            cabinet = existant.get();
            System.out.println("ℹ Cabinet déjà présent. ID = " + cabinet.getIdEntite());
        }

        // 2. Créer une charge pour ce cabinet
        Charges charge = dbUtils.createChargesObject(cabinet);
        cabinetService.createCharge(charge);
        System.out.println(charge.getIdEntite() != null
                ? "✅ Charge créée via service. ID = " + charge.getIdEntite()
                : "❌ Échec création Charge via service");

        // 3. Lire les charges par cabinet et sur une période
        List<Charges> chargesByCabinet = cabinetService.getChargesByCabinet(cabinet.getIdEntite());
        System.out.println("Charges par cabinet: " + chargesByCabinet.size());

        LocalDateTime start = charge.getDate().minusDays(1);
        LocalDateTime end = charge.getDate().plusDays(1);
        List<Charges> chargesByDate = cabinetService.getChargesBetween(start, end);
        Double total = cabinetService.getTotalChargesBetween(start, end);

        System.out.println("Charges par période: " + chargesByDate.size() + ", Total = " + total);
    }
}


