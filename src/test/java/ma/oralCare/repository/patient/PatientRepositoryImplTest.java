package ma.oralCare.repository.patient;

import ma.oralCare.entities.enums.Assurance;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatientRepositoryImplTest {

    private PatientRepositoryImpl patientRepository;

    @BeforeAll
    void setupAll() {
        patientRepository = new PatientRepositoryImpl();
    }

    @BeforeEach
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset();
    }

    @Test
    void testCreateAndFindById() {
        Patient patient = Patient.builder()
                .nom("Doe")
                .prenom("John")
                .adresse("123 Rue Test")
                .telephone("0600000000")
                .email("john.doe@test.com")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .sexe(Sexe.MALE)
                .assurance(Assurance.CNSS)
                .build();

        patientRepository.create(patient);

        assertNotNull(patient.getId(), "L'ID doit être généré après création");

        Patient fromDb = patientRepository.findById(patient.getId());
        assertNotNull(fromDb, "Le patient créé doit pouvoir être retrouvé");
        assertEquals("Doe", fromDb.getNom());
        assertEquals("John", fromDb.getPrenom());
    }

    @Test
    void testFindAll() {
        List<Patient> all = patientRepository.findAll();
        assertNotNull(all);
        assertFalse(all.isEmpty(), "Il doit y avoir au moins un patient dans la base");
    }

    @Test
    void testUpdate() {
        Patient patient = DbTestUtils.getFirstPatient();
        assertNotNull(patient);

        String newAdresse = "456 Rue Modifiée";
        patient.setAdresse(newAdresse);
        patientRepository.update(patient);

        Patient fromDb = patientRepository.findById(patient.getId());
        assertEquals(newAdresse, fromDb.getAdresse(), "L'adresse doit être mise à jour");
    }

    @Test
    void testDelete() {
        Patient patient = DbTestUtils.getFirstPatient();
        assertNotNull(patient);

        patientRepository.delete(patient);

        Patient fromDb = patientRepository.findById(patient.getId());
        assertNull(fromDb, "Le patient doit être supprimé");
    }

    @Test
    void testFindByNomAndPrenom() {
        Patient patient = DbTestUtils.getFirstPatient();
        List<Patient> patients = patientRepository.findByNomAndPrenom(patient.getNom(), patient.getPrenom());

        assertNotNull(patients);
        assertTrue(patients.stream().anyMatch(p -> p.getId().equals(patient.getId())),
                "Le patient doit être retrouvé par nom et prénom");
    }

    @Test
    void testFindByTelephone() {
        Patient patient = DbTestUtils.getFirstPatient();
        Optional<Patient> optPatient = patientRepository.findByTelephone(patient.getTelephone());

        assertTrue(optPatient.isPresent());
        assertEquals(patient.getId(), optPatient.get().getId());
    }

    @Test
    void testFindByDateNaissanceBefore() {
        LocalDate date = LocalDate.now();
        List<Patient> patients = patientRepository.findByDateNaissanceBefore(date);

        assertNotNull(patients);
        assertFalse(patients.isEmpty(), "Doit trouver des patients nés avant aujourd'hui");
    }

    @Test
    void testFindByAssurance() {
        Patient patient = DbTestUtils.getFirstPatient();
        List<Patient> patients = patientRepository.findByAssurance(patient.getAssurance());

        assertNotNull(patients);
        assertTrue(patients.stream().anyMatch(p -> p.getId().equals(patient.getId())),
                "Le patient doit être retrouvé par assurance");
    }

    @Test
    void testAntecedentAssociation() {
        Patient patient = DbTestUtils.getFirstPatient();
        Antecedent antecedent = DbTestUtils.getFirstAntecedent();

        patientRepository.addAntecedentToPatient(patient.getId(), antecedent.getId());

        List<Antecedent> antecedents = patientRepository.findAntecedentsByPatientId(patient.getId());
        assertTrue(antecedents.stream().anyMatch(a -> a.getId().equals(antecedent.getId())),
                "L'antécédent doit être associé au patient");

        patientRepository.removeAntecedentFromPatient(patient.getId(), antecedent.getId());

        antecedents = patientRepository.findAntecedentsByPatientId(patient.getId());
        assertFalse(antecedents.stream().anyMatch(a -> a.getId().equals(antecedent.getId())),
                "L'antécédent doit être dissocié du patient");
    }
}
