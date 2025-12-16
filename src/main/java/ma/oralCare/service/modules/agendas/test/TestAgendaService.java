package ma.oralCare.service.modules.agendas.test;

import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.repository.test.DbTestUtils;
import ma.oralCare.repository.test.DbTestUtils.Module;
import ma.oralCare.service.modules.agendas.api.AgendaService;
import ma.oralCare.service.modules.agendas.impl.AgendaServiceImpl;

import java.util.List;

/**
 * Test console simple pour le service d'agenda.
 */
public class TestAgendaService {

    private final DbTestUtils dbUtils = DbTestUtils.getInstance();
    private final AgendaService agendaService = new AgendaServiceImpl();

    public static void main(String[] args) {
        System.out.println("=== TEST AGENDA SERVICE ===");
        TestAgendaService tester = new TestAgendaService();
        tester.run();
        System.out.println("=== FIN TEST AGENDA SERVICE ===");
    }

    private void run() {
        dbUtils.cleanUp(Module.AGENDA_ONLY);

        // Création d'un RDV de test via DbTestUtils
        RDV rdv = dbUtils.createRdvObject(DbTestUtils.CABINET_ID_TEST, DbTestUtils.USER_ID_MEDECIN, null);
        agendaService.createRdv(rdv);
        System.out.println("✅ RDV créé via service. ID = " + rdv.getIdEntite());

        // Lecture par date
        List<RDV> rdvs = agendaService.getRdvsByDate(rdv.getDate());
        System.out.println("RDV trouvés à la date " + rdv.getDate() + " : " + rdvs.size());
    }
}


