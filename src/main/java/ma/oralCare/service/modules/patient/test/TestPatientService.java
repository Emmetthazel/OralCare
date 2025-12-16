package ma.oralCare.service.modules.patient.test;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.test.DbTestUtils;
import ma.oralCare.repository.test.DbTestUtils.Module;
import ma.oralCare.service.modules.patient.api.AntecedentService;
import ma.oralCare.service.modules.patient.api.PatientService;
import ma.oralCare.service.modules.patient.impl.AntecedentServiceImpl;
import ma.oralCare.service.modules.patient.impl.PatientServiceImpl;

import java.util.List;
import java.util.Optional;

/**
 * Petit exécutable de test de la couche Service pour le module Patient,
 * dans le même esprit que {@code ma.oralCare.repository.test.TestRepo}.
 *
 * Il ne remplace pas de vrais tests unitaires JUnit, mais permet de
 * valider manuellement la bonne intégration Service <-> Repository.
 */
public class TestPatientService {

    private final DbTestUtils dbUtils = DbTestUtils.getInstance();

    private final PatientService patientService = new PatientServiceImpl();
    private final AntecedentService antecedentService = new AntecedentServiceImpl();

    private Patient patientTest;
    private Antecedent antecedentTest;

    public static void main(String[] args) {
        TestPatientService tester = new TestPatientService();

        System.out.println("=================================================");
        System.out.println("=== TESTS SERVICE PATIENT / ANTECEDENT (CRUD) ===");
        System.out.println("=================================================");

        tester.runTests();
    }

    private void runTests() {
        dbUtils.cleanUp(Module.PATIENT_ONLY);

        createProcess();
        readProcess();
        updateProcess();
        deleteProcess();

        System.out.println("\n=== FIN DES TESTS SERVICE PATIENT / ANTECEDENT ===");
    }

    // -------------------------------------------------------------------------
    // 1. CREATE
    // -------------------------------------------------------------------------

    private void createProcess() {
        System.out.println("\n--- [SERVICE] CREATE ---");

        // Création Patient via l'utilitaire déjà utilisé par TestRepo
        Patient nouveauPatient = dbUtils.createPatientObject();
        patientService.createPatient(nouveauPatient);
        patientTest = nouveauPatient;

        System.out.println(
                patientTest.getIdEntite() != null
                        ? "✅ [PatientService] CREATE OK. ID: " + patientTest.getIdEntite()
                        : "❌ [PatientService] CREATE Échec (ID non généré)."
        );

        // Création d'un antécédent puis liaison au patient
        antecedentTest = dbUtils.createAntecedentObject();
        antecedentService.createAntecedent(antecedentTest);

        System.out.println(
                antecedentTest.getIdEntite() != null
                        ? "✅ [AntecedentService] CREATE OK. ID: " + antecedentTest.getIdEntite()
                        : "❌ [AntecedentService] CREATE Échec (ID non généré)."
        );

        if (patientTest.getIdEntite() != null && antecedentTest.getIdEntite() != null) {
            patientService.addAntecedentToPatient(patientTest.getIdEntite(), antecedentTest.getIdEntite());
            System.out.println("✅ [PatientService] Lien Patient <-> Antécédent créé.");
        }
    }

    // -------------------------------------------------------------------------
    // 2. READ
    // -------------------------------------------------------------------------

    private void readProcess() {
        System.out.println("\n--- [SERVICE] READ ---");

        // Lecture par ID
        Optional<Patient> byId = patientService.getPatientById(patientTest.getIdEntite());
        System.out.println(
                byId.isPresent()
                        ? "✅ [PatientService] READ byId OK. Nom: " + byId.get().getNom()
                        : "❌ [PatientService] READ byId Échec."
        );

        // Recherche par email
        Optional<Patient> byEmail = patientService.getPatientByEmail(patientTest.getEmail());
        System.out.println(
                byEmail.isPresent()
                        ? "✅ [PatientService] READ byEmail OK."
                        : "❌ [PatientService] READ byEmail Échec."
        );

        // Liste d'antécédents du patient
        List<Antecedent> antecedents = patientService.getAntecedentsOfPatient(patientTest.getIdEntite());
        System.out.println(
                !antecedents.isEmpty()
                        ? "✅ [PatientService] READ AntecedentsOfPatient OK. Count: " + antecedents.size()
                        : "❌ [PatientService] READ AntecedentsOfPatient Échec."
        );
    }

    // -------------------------------------------------------------------------
    // 3. UPDATE
    // -------------------------------------------------------------------------

    private void updateProcess() {
        System.out.println("\n--- [SERVICE] UPDATE ---");

        // Mise à jour du téléphone du patient
        String newTel = "0611223344";
        patientTest.setTelephone(newTel);
        patientService.updatePatient(patientTest);

        Optional<Patient> updated = patientService.getPatientById(patientTest.getIdEntite());
        System.out.println(
                updated.isPresent() && newTel.equals(updated.get().getTelephone())
                        ? "✅ [PatientService] UPDATE OK. Nouveau Tel: " + updated.get().getTelephone()
                        : "❌ [PatientService] UPDATE Échec."
        );
    }

    // -------------------------------------------------------------------------
    // 4. DELETE
    // -------------------------------------------------------------------------

    private void deleteProcess() {
        System.out.println("\n--- [SERVICE] DELETE ---");

        // Suppression de la liaison puis des entités
        if (patientTest != null && antecedentTest != null) {
            patientService.removeAntecedentFromPatient(patientTest.getIdEntite(), antecedentTest.getIdEntite());
            List<Antecedent> afterUnlink = patientService.getAntecedentsOfPatient(patientTest.getIdEntite());
            System.out.println(
                    afterUnlink.isEmpty()
                            ? "✅ [PatientService] UNLINK Antecedent OK."
                            : "❌ [PatientService] UNLINK Antecedent Échec."
            );
        }

        if (antecedentTest != null) {
            antecedentService.deleteAntecedent(antecedentTest.getIdEntite());
            boolean stillPresent = antecedentService
                    .getAntecedentById(antecedentTest.getIdEntite())
                    .isPresent();
            System.out.println(
                    !stillPresent
                            ? "✅ [AntecedentService] DELETE OK."
                            : "❌ [AntecedentService] DELETE Échec."
            );
        }

        if (patientTest != null) {
            patientService.deletePatient(patientTest.getIdEntite());
            boolean stillPresent = patientService
                    .getPatientById(patientTest.getIdEntite())
                    .isPresent();
            System.out.println(
                    !stillPresent
                            ? "✅ [PatientService] DELETE OK."
                            : "❌ [PatientService] DELETE Échec."
            );
        }
    }
}


