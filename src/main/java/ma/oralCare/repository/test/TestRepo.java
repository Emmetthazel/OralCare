package ma.oralCare.repository.test;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.users.*;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.cabinet.api.ChargesRepository;
import ma.oralCare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;
import ma.oralCare.repository.modules.dossierMedical.api.CertificatRepository;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.ActeRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.CertificatRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;
import ma.oralCare.repository.modules.notification.impl.NotificationRepositoryImpl;
import ma.oralCare.repository.modules.patient.api.AntecedentRepository;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.patient.impl.AntecedentRepositoryImpl;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.repository.modules.users.api.*;
import ma.oralCare.repository.modules.users.impl.*;
import ma.oralCare.repository.test.DbTestUtils.Module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TestRepo {

    // --- Constantes (utilisent les valeurs de DbTestUtils) ---
    private static final Module MODULE_TO_TEST = Module.ALL;
    private static final Long CABINET_ID_TEST = DbTestUtils.CABINET_ID_TEST;
    private static final Long USER_ID_ADMIN = DbTestUtils.USER_ID_ADMIN;
    private static final Long MODIFICATEUR_ID = 1L;

    // Repositories
    private final ChargesRepository chargesRepository = new ChargesRepositoryImpl(); // <--- VERIFIER CETTE LIGNE
    private final RDVRepository rdvRepository = new RDVRepositoryImpl();
    private final PatientRepository patientRepository = new PatientRepositoryImpl();
    private final AntecedentRepository antecedentRepository = new AntecedentRepositoryImpl();
    private final AgendaMensuelRepository agendaRepository = new AgendaMensuelRepositoryImpl();
    private final MedecinRepository medecinRepository = new MedecinRepositoryImpl();
    private final StaffRepository staffRepository = new StaffRepositoryImpl();
    private final CabinetMedicaleRepository cabinetRepository = new CabinetMedicaleRepositoryImpl();
    private final ActeRepository acteRepository = new ActeRepositoryImpl();
    private final DossierMedicaleRepository dossierRepository = new DossierMedicaleRepositoryImpl();
    private final ConsultationRepository consultationRepository = new ConsultationRepositoryImpl();
    private final NotificationRepository notificationRepository = new NotificationRepositoryImpl();
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl();
    private final RoleRepository roleRepository = new RoleRepositoryImpl();
    private final SecretaireRepository secretaireRepository = new SecretaireRepositoryImpl();
    private final AdminRepository adminRepository = new AdminRepositoryImpl();
    private final CertificatRepository certificatRepository = new CertificatRepositoryImpl(); // NOUVEAU REPOSITORY
    // Référence à l'utilitaire de DB
    private final DbTestUtils dbUtils = DbTestUtils.getInstance();

    // Objets de test (pour stocker l'ID généré et les données initiales)
    private RDV rdvTest = null;
    private Patient patientTest = null;
    private Antecedent antecedentTest = null;
    private AgendaMensuel agendaTest = null;
    private Medecin medecinTest = null;
    private Staff staffTest = null;
    private CabinetMedicale cabinetTest = null;
    private Acte acteTest = null;
    private DossierMedicale dossierTest = null;
    private Consultation consultationTest = null;
    private Notification notificationTest = null;
    private Utilisateur utilisateurTest = null;
    private Role roleTest = null;
    private Secretaire secretaireTest = null;
    private Admin adminTest = null;
    private Certificat certificatTest = null;
    private Charges chargesTest = null;
    // NOUVEL OBJET DE TEST
    // --- MAIN METHOD ---
    public static void main(String[] args) {
        TestRepo tester = new TestRepo();
        System.out.println("=================================================");
        System.out.println("=== DÉMARRAGE DES TESTS CRUD pour le module: " + MODULE_TO_TEST + " ===");
        System.out.println("=================================================");

        tester.runTests(MODULE_TO_TEST);
    }

    public void runTests(Module module) {
        // 1. CREATE
        insertProcess(module);

        // 2. READ
        selectProcess(module);

        // 3. UPDATE
        updateProcess(module);

        // 4. DELETE
        deleteProcess(module);

        System.out.println("\n=== FIN DES TESTS CRUD ===");
    }


    // =========================================================================
    //                            1. CREATE (INSERTION)
    // =========================================================================

    void insertProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 1. PROCESSUS D'INSERTION (CREATE) [" + module + "] ===");
        System.out.println("=================================================");

        // Nettoyage préalable (important pour l'unicité)
        dbUtils.cleanUp(module);

        // --- PRÉ-REQUIS I : Utilisateur et Rôle ---
        if (module == Module.ALL && utilisateurTest == null) {
            Utilisateur nouvelUtilisateur = dbUtils.createUtilisateurObject("ADMIN");
            try {
                utilisateurRepository.create(nouvelUtilisateur);
                utilisateurTest = nouvelUtilisateur;
                System.out.println(utilisateurTest.getIdEntite() != null ?
                        "[Utilisateur] CREATE OK. ID: " + utilisateurTest.getIdEntite() + ", Login: " + utilisateurTest.getLogin()
                        : "[Utilisateur] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Utilisateur] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                utilisateurTest = null;
            }
        }

        if (module == Module.ALL && roleTest == null && utilisateurTest != null) {
            Role nouveauRole = dbUtils.createRoleObject(RoleLibelle.ADMIN);
            try {
                roleRepository.create(nouveauRole);
                roleTest = nouveauRole;
                System.out.println(roleTest.getIdEntite() != null ?
                        "[Role] CREATE OK. Libelle: " + roleTest.getLibelle()
                        : "[Role] CREATE Échec (ID non généré).");

                utilisateurRepository.addRoleToUtilisateur(utilisateurTest.getIdEntite(), roleTest.getIdEntite());
                System.out.println("[Role] Association ADD_ROLE OK.");

            } catch (Exception e) {
                System.err.println("[Role] CREATE/Association Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                roleTest = null;
            }
        }

        // --- A. Insertion Admin ---
        if (module == Module.ALL && adminTest == null) {
            Admin nouvelAdmin = dbUtils.createAdminObject();

            try {
                adminRepository.create(nouvelAdmin);
                adminTest = nouvelAdmin;
                System.out.println(adminTest.getIdEntite() != null ?
                        "[Admin] CREATE OK. ID: " + adminTest.getIdEntite() + ", Login: " + adminTest.getLogin()
                        : "[Admin] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Admin] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                adminTest = null;
            }
        }


        // --- PRÉ-REQUIS II : Cabinet ---
        if (cabinetRepository.findById(CABINET_ID_TEST).isEmpty()) {
            try {
                CabinetMedicale cabinetAInserer = dbUtils.createCabinetObject();
                cabinetAInserer.setIdEntite(CABINET_ID_TEST);
                cabinetRepository.create(cabinetAInserer);
                cabinetTest = cabinetAInserer;
                System.out.println("[Cabinet] PRÉ-REQUIS OK. ID: " + cabinetTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("[Cabinet] PRÉ-REQUIS Échec critique (Cabinet ID " + CABINET_ID_TEST + "L): " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                cabinetTest = null;
            }
        } else {
            cabinetTest = cabinetRepository.findById(CABINET_ID_TEST).get();
            System.out.println("[Cabinet] PRÉ-REQUIS OK. ID: " + cabinetTest.getIdEntite());
        }

        // --- B. Insertion Staff (Dépend de Cabinet) ---
        if (module == Module.ALL && cabinetTest != null && staffTest == null) {
            Staff nouveauStaff = dbUtils.createStaffObject();
            nouveauStaff.setCabinetMedicale(cabinetTest);

            try {
                staffRepository.create(nouveauStaff);
                staffTest = nouveauStaff;
                System.out.println(staffTest.getIdEntite() != null ?
                        "[Staff] CREATE OK. ID: " + staffTest.getIdEntite() + ", Nom: " + staffTest.getNom()
                        : "[Staff] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Staff] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                staffTest = null;
            }
        }

        // --- C. Insertion Secretaire (Dépend de Cabinet) ---
        if (module == Module.ALL && cabinetTest != null && secretaireTest == null) {
            Secretaire nouvelleSecretaire = dbUtils.createSecretaireObject();
            nouvelleSecretaire.setCabinetMedicale(cabinetTest);

            try {
                secretaireRepository.create(nouvelleSecretaire);
                secretaireTest = nouvelleSecretaire;
                System.out.println(secretaireTest.getIdEntite() != null ?
                        "[Secretaire] CREATE OK. ID: " + secretaireTest.getIdEntite() + ", CIN: " + secretaireTest.getCin()
                        : "[Secretaire] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Secretaire] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                secretaireTest = null;
            }
        }

        // --- D. Insertion Medecin (Dépend de Cabinet) ---
        if ((module == Module.MEDECIN_ONLY || module == Module.ALL || module == Module.AGENDA_ONLY) && cabinetTest != null && medecinTest == null) {
            Medecin nouveauMedecin = dbUtils.createMedecinObject();
            nouveauMedecin.setCabinetMedicale(cabinetTest);

            try {
                medecinRepository.create(nouveauMedecin);
                medecinTest = nouveauMedecin;
                System.out.println(medecinTest.getIdEntite() != null ?
                        "[Medecin] CREATE OK. " + medecinTest.toString()
                        : "[Medecin] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Medecin] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                medecinTest = null;
            }
        }

        // --- E. Insertion Patient ---
        if ((module == Module.PATIENT_ONLY || module == Module.ALL) && patientTest == null) {
            Patient nouveauPatient = dbUtils.createPatientObject();
            try {
                patientRepository.create(nouveauPatient);
                patientTest = nouveauPatient;
                System.out.println(patientTest.getIdEntite() != null ?
                        "[Patient] CREATE OK. " + patientTest.toString()
                        : "[Patient] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Patient] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                patientTest = null;
            }
        }

        // --- ** NOVEAUTÉ **: Gestion Antecedent/Patient (Assure que patientTest existe pour le lien M-t-M) ---
        // (Le bloc précédent assurait déjà patientTest si le module le nécessitait)
        if (patientTest != null && (module == Module.PATIENT_ONLY || module == Module.ALL)) {
            Antecedent nouvelAntecedent = dbUtils.createAntecedentObject();
            try {
                antecedentRepository.create(nouvelAntecedent);
                antecedentTest = nouvelAntecedent;
                System.out.println(antecedentTest.getIdEntite() != null ?
                        "[Antecedent] CREATE OK. ID: " + antecedentTest.getIdEntite()
                        : "[Antecedent] CREATE Échec (ID non généré).");

                // Association Many-to-Many
                patientRepository.addAntecedentToPatient(patientTest.getIdEntite(), antecedentTest.getIdEntite());
                System.out.println("[Antecedent] Association ADD_ANTECEDENT OK.");

            } catch (Exception e) {
                System.err.println("[Antecedent] CREATE/Association Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                antecedentTest = null;
            }
        }

        // --- ** NOVEAUTÉ **: Insertion Charges (Dépend de Cabinet) ---
        if ((module == DbTestUtils.Module.CHARGES_ONLY || module == DbTestUtils.Module.ALL) && cabinetTest != null && chargesTest == null) {
            Charges nouvelleCharge = dbUtils.createChargesObject(cabinetTest);
            try {
                // CORRECTION: Utilisation de l'instance chargesRepository
                chargesRepository.create(nouvelleCharge);
                chargesTest = nouvelleCharge;
                System.out.println(chargesTest.getIdEntite() != null ?
                        "[Charges] CREATE OK. ID: " + chargesTest.getIdEntite() + ", Montant: " + chargesTest.getMontant()
                        : "[Charges] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Charges] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                chargesTest = null;
            }
        }

        // --- F. Insertion Dossier Medicale (Dépend de Patient et Medecin) ---
        if (patientTest != null && medecinTest != null && (module == Module.DOSSIER_ONLY || module == Module.ALL || module == Module.CONSULTATION_ONLY || module == Module.CERTIFICAT_ONLY)) {
            DossierMedicale nouveauDossier = dbUtils.createDossierMedicaleObject(patientTest.getIdEntite(), medecinTest.getIdEntite());
            try {
                dossierRepository.create(nouveauDossier);
                dossierTest = nouveauDossier;
                System.out.println(dossierTest.getIdEntite() != null ?
                        "[Dossier] CREATE OK. ID: " + dossierTest.getIdEntite()
                        : "[Dossier] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Dossier] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                dossierTest = null;
            }
        }

        // --- G. Insertion Consultation (Dépend de Dossier Medicale) ---
        if (dossierTest != null && (module == Module.CONSULTATION_ONLY || module == Module.ALL || module == Module.CERTIFICAT_ONLY)) {
            Consultation nouvelleConsultation = dbUtils.createConsultationObject(dossierTest.getIdEntite());
            try {
                consultationRepository.create(nouvelleConsultation);
                consultationTest = nouvelleConsultation;
                System.out.println(consultationTest.getIdEntite() != null ?
                        "[Consultation] CREATE OK. ID: " + consultationTest.getIdEntite()
                        : "[Consultation] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Consultation] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                consultationTest = null;
            }
        }

        // --- ** CERTIFICAT **: Insertion Certificat (Dépend de Consultation) ---
        if (consultationTest != null && (module == Module.CERTIFICAT_ONLY || module == Module.ALL)) {
            Certificat nouveauCertificat = dbUtils.createCertificatObject(consultationTest);
            try {
                certificatRepository.create(nouveauCertificat);
                certificatTest = nouveauCertificat;
                System.out.println(certificatTest.getIdEntite() != null ?
                        "[Certificat] CREATE OK. ID: " + certificatTest.getIdEntite()
                        : "[Certificat] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Certificat] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                certificatTest = null;
            }
        }

        // --- H. Insertion Agenda (Dépend de Medecin) ---
        if (module == Module.AGENDA_ONLY || (module == Module.ALL && medecinTest != null)) {
            Long medecinId = (medecinTest != null) ? medecinTest.getIdEntite() : DbTestUtils.USER_ID_ADMIN;
            AgendaMensuel nouvelAgenda = dbUtils.createAgendaObject(medecinId);
            try {
                agendaRepository.create(nouvelAgenda);
                agendaTest = nouvelAgenda;
                System.out.println(agendaTest.getIdEntite() != null ?
                        "[Agenda] CREATE OK. " + nouvelAgenda.toString()
                        : "[Agenda] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Agenda] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                agendaTest = null;
            }
        }

        // --- I. Insertion Acte ---
        if (module == Module.ALL) {
            Acte nouvelActe = dbUtils.createActeObject(USER_ID_ADMIN);
            try {
                acteRepository.create(nouvelActe);
                acteTest = nouvelActe;
                System.out.println(acteTest.getIdEntite() != null ?
                        "[Acte] CREATE OK. " + nouvelActe.toString()
                        : "[Acte] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Acte] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                acteTest = null;
            }
        }

        // --- J. Insertion Notification (Dépend de Utilisateur/Medecin pour les destinataires) ---
        if (medecinTest != null && (module == Module.NOTIFICATION_ONLY || module == Module.ALL)) {
            List<Utilisateur> destinataires = List.of(medecinTest);
            Notification nouvelleNotification = dbUtils.createNotificationObject(destinataires);

            try {
                notificationRepository.create(nouvelleNotification);
                notificationTest = nouvelleNotification;
                System.out.println(notificationTest.getIdEntite() != null ?
                        "[Notification] CREATE OK. Titre: " + notificationTest.getTitre()
                        : "[Notification] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("[Notification] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                notificationTest = null;
            }
        }

        // --- K. Insertion RDV (Dépend de Dossier Médicale et Medecin) ---
        if (dossierTest != null && (module == DbTestUtils.Module.AGENDA_ONLY || module == DbTestUtils.Module.ALL)) {
            // Utilisation d'un Dossier et d'un Medecin pour vérifier la disponibilité
            Long medecinId = (medecinTest != null) ? medecinTest.getIdEntite() : 1L;
            RDV nouveauRdv = dbUtils.createRdvObject(dossierTest.getIdEntite(), medecinId, null);
            try {
                // On s'assure qu'aucun RDV n'existe déjà pour ce créneau (Test de la vérification)
                if (!rdvRepository.existsByDateAndHeureAndMedecinId(nouveauRdv.getDate(), nouveauRdv.getHeure(), medecinId)) {
                    rdvRepository.create(nouveauRdv);
                    rdvTest = nouveauRdv;
                    System.out.println(rdvTest.getIdEntite() != null ?
                            "[RDV] CREATE OK. ID: " + rdvTest.getIdEntite() + ", Date: " + rdvTest.getDate()
                            : "[RDV] CREATE Échec (ID non généré).");
                } else {
                    System.err.println("[RDV] CREATE Échec. Le créneau était déjà réservé.");
                }
            } catch (Exception e) {
                System.err.println("[RDV] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                rdvTest = null;
            }
        }
    }

    // =========================================================================
    //                            2. READ (SÉLECTION)
    // =========================================================================

    void selectProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 2. PROCESSUS DE SÉLECTION (READ) [" + module + "] ===");
        System.out.println("=================================================");

        // --- Sélection Utilisateur ---
        if (utilisateurTest != null) {
            Utilisateur foundUser = utilisateurRepository.findById(utilisateurTest.getIdEntite()).orElse(null);
            if (foundUser != null && foundUser.getLogin().equals(utilisateurTest.getLogin())) {
                System.out.println("[Utilisateur] READ OK. Login: " + foundUser.getLogin());
            } else {
                System.err.println("[Utilisateur] READ Échec.");
            }
        }

        // --- Sélection Rôle ---
        if (roleTest != null) {
            Role foundRole = roleRepository.findById(roleTest.getIdEntite()).orElse(null);
            if (foundRole != null && foundRole.getLibelle().equals(roleTest.getLibelle())) {
                System.out.println("[Role] READ OK. Libelle: " + foundRole.getLibelle());
            } else {
                System.err.println("[Role] READ Échec.");
            }
        }

        // --- Sélection Admin ---
        if (adminTest != null) {
            Optional<Admin> foundAdminOpt = adminRepository.findById(adminTest.getIdEntite());

            // Test 1: findById
            if (foundAdminOpt.isPresent() && foundAdminOpt.get().getLogin().equals(adminTest.getLogin())) {
                System.out.println("[Admin] READ OK (findById). Login: " + foundAdminOpt.get().getLogin());
            } else {
                System.err.println("[Admin] READ Échec (findById).");
            }

            // Test 2: findByNomContaining
            String nomPartiel = adminTest.getNom().substring(0, Math.min(adminTest.getNom().length(), 3));
            List<Admin> adminByNom = adminRepository.findAllByNomContaining(nomPartiel);
            if (adminByNom != null && adminByNom.size() >= 1) {
                System.out.println("[Admin] READ OK (findAllByNomContaining). Count: " + adminByNom.size());
            } else {
                System.err.println("[Admin] READ Échec (findAllByNomContaining).");
            }
        }

        // --- Selection Secretaire et Méthodes de recherche ---
        if (secretaireTest != null) {
            // 1. READ by ID
            Optional<Secretaire> foundSecretaireOpt = secretaireRepository.findById(secretaireTest.getIdEntite());
            if (foundSecretaireOpt.isPresent() && foundSecretaireOpt.get().getLogin().equals(secretaireTest.getLogin())) {
                System.out.println("[Secretaire] READ OK (findById). Login: " + foundSecretaireOpt.get().getLogin());
            } else {
                System.err.println("[Secretaire] READ Échec (findById).");
            }

            // 2. Test findByLogin
            Optional<Secretaire> foundSecretaireByLoginOpt = secretaireRepository.findByLogin(secretaireTest.getLogin());
            if (foundSecretaireByLoginOpt.isPresent()) {
                System.out.println("[Secretaire] READ OK (findByLogin).");
            } else {
                System.err.println("[Secretaire] READ Échec (findByLogin).");
            }

            // 3. Test findByCin
            Optional<Secretaire> foundSecretaireByCinOpt = secretaireRepository.findByCin(secretaireTest.getCin());
            if (foundSecretaireByCinOpt.isPresent()) {
                System.out.println("[Secretaire] READ OK (findByCin).");
            } else {
                System.err.println("[Secretaire] READ Échec (findByCin).");
            }

            // 4. Test findAllByNomContaining
            String nomPartiel = secretaireTest.getNom().substring(0, Math.min(secretaireTest.getNom().length(), 3));
            List<Secretaire> secretaireByNom = secretaireRepository.findAllByNomContaining(nomPartiel);
            if (secretaireByNom != null && secretaireByNom.size() >= 1) {
                System.out.println("[Secretaire] READ OK (findAllByNomContaining). Count: " + secretaireByNom.size());
            } else {
                System.err.println("[Secretaire] READ Échec (findAllByNomContaining).");
            }

            // 5. Test findAllByCabinetId
            if (cabinetTest != null) {
                List<Secretaire> secretaireByCabinet = secretaireRepository.findAllByCabinetId(cabinetTest.getIdEntite());
                if (secretaireByCabinet != null && secretaireByCabinet.size() >= 1) {
                    System.out.println("[Secretaire] READ OK (findAllByCabinetId). Count: " + secretaireByCabinet.size());
                } else {
                    System.err.println("[Secretaire] READ Échec (findAllByCabinetId). Count: " + (secretaireByCabinet != null ? secretaireByCabinet.size() : "null"));
                }
            }
        }


        // --- Selection Cabinet (READ) ---
        if (cabinetTest != null) {
            CabinetMedicale foundCabinet = cabinetRepository.findById(cabinetTest.getIdEntite()).orElse(null);
            if (foundCabinet != null && foundCabinet.getNom().equals(cabinetTest.getNom())) {
                System.out.println("[Cabinet] READ OK. Nom: " + foundCabinet.getNom());
            } else {
                System.err.println("[Cabinet] READ Échec.");
            }
        }

        // --- Selection Staff (READ) ---
        if (staffTest != null) {
            Staff foundStaff = staffRepository.findById(staffTest.getIdEntite()).orElse(null);
            if (foundStaff != null && staffTest.getLogin().equals(foundStaff.getLogin())) {
                System.out.println("[Staff] READ OK (findById). Login: " + foundStaff.getLogin());
            } else {
                System.err.println("[Staff] READ Échec (findById).");
            }
            Optional<Staff> foundStaffByLogin = staffRepository.findByLogin(staffTest.getLogin());
            if (foundStaffByLogin.isPresent()) {
                System.out.println("[Staff] READ OK (findByLogin).");
            } else {
                System.err.println("[Staff] READ Échec (findByLogin).");
            }
        }

        // --- Selection Medecin (READ) ---
        if (medecinTest != null) {
            Medecin foundMedecin = medecinRepository.findById(medecinTest.getIdEntite()).orElse(null);
            if (foundMedecin != null && foundMedecin.getNom().equals(medecinTest.getNom())) {
                System.out.println("[Medecin] READ OK. " + foundMedecin.toString());
            } else {
                System.err.println("[Medecin] READ Échec.");
            }
        }

        // --- Selection Patient (READ) ---
        if (patientTest != null) {
            Patient foundPatient = patientRepository.findById(patientTest.getIdEntite()).orElse(null);
            if (foundPatient != null && foundPatient.getNom().equals(patientTest.getNom())) {
                System.out.println("[Patient] READ OK. " + foundPatient.toString());
            } else {
                System.err.println("[Patient] READ Échec.");
            }
        }

        // --- ** NOVEAUTÉ **: Selection Antecedent (READ) et recherches spécifiques ---
        if (antecedentTest != null) {
            Optional<Antecedent> foundAntecedentOpt = antecedentRepository.findById(antecedentTest.getIdEntite());
            if (foundAntecedentOpt.isPresent() && foundAntecedentOpt.get().getNom().equals(antecedentTest.getNom())) {
                System.out.println("[Antecedent] READ OK (findById). Nom: " + foundAntecedentOpt.get().getNom());
            } else {
                System.err.println("[Antecedent] READ Échec (findById).");
            }

            List<Antecedent> foundByCat = antecedentRepository.findByCategorie(antecedentTest.getCategorie());
            if (!foundByCat.isEmpty()) {
                System.out.println("[Antecedent] READ OK (findByCategorie).");
            } else {
                System.err.println("[Antecedent] READ Échec (findByCategorie).");
            }

            // [CORRECTION] : Vérification du lien Many-to-Many
            if (patientTest != null) {
                try {
                    List<Antecedent> foundByPatient = antecedentRepository.findByPatientId(patientTest.getIdEntite());

                    // On vérifie que la liste n'est pas vide et qu'elle contient bien l'antécédent inséré
                    if (!foundByPatient.isEmpty() && foundByPatient.stream().anyMatch(a -> a.getIdEntite().equals(antecedentTest.getIdEntite()))) {
                        System.out.println("[Antecedent] READ OK (findByPatientId - Lien OK).");
                    } else {
                        // C'est ici que l'erreur se produit si l'Antécédent n'est pas vu
                        System.err.println("[Antecedent] READ Échec (findByPatientId - Lien ÉCHEC). Count trouvé: " + foundByPatient.size());
                    }
                } catch (Exception e) {
                    System.err.println("[Antecedent] READ Échec critique (findByPatientId): " + e.getMessage());
                }
            }
        }

        // --- Selection Dossier Medicale (READ) ---
        if (dossierTest != null) {
            DossierMedicale foundDossier = dossierRepository.findById(dossierTest.getIdEntite()).orElse(null);
            if (foundDossier != null) {
                System.out.println("[Dossier] READ OK. ID: " + foundDossier.getIdEntite());
            } else {
                System.err.println("[Dossier] READ Échec.");
            }
        }

        // --- Selection Consultation (READ) ---
        if (consultationTest != null) {
            Consultation foundConsultation = consultationRepository.findById(consultationTest.getIdEntite()).orElse(null);
            if (foundConsultation != null) {
                System.out.println("[Consultation] READ OK. ID: " + foundConsultation.getIdEntite());
            } else {
                System.err.println("[Consultation] READ Échec.");
            }
        }

        // --- Selection Agenda (READ) ---
        if (agendaTest != null) {
            AgendaMensuel foundAgenda = agendaRepository.findById(agendaTest.getIdEntite()).orElse(null);
            if (foundAgenda != null && foundAgenda.getMois().equals(agendaTest.getMois())) {
                System.out.println("[Agenda] READ OK. " + foundAgenda.toString());
            } else {
                System.err.println("[Agenda] READ Échec.");
            }
        }

        // --- Selection Acte (READ) ---
        if (acteTest != null) {
            Acte foundActe = acteRepository.findById(acteTest.getIdEntite()).orElse(null);
            if (foundActe != null && acteTest.getLibelle().equals(foundActe.getLibelle())) {
                System.out.println("[Acte] READ OK. " + foundActe.toString());
            } else {
                System.err.println("[Acte] READ Échec.");
            }
        }

        // --- Selection Notification (READ) ---
        if (notificationTest != null && medecinTest != null) {
            Optional<Notification> foundNotif = notificationRepository.findById(notificationTest.getIdEntite());
            List<Notification> notifsByUser = notificationRepository.findByUtilisateurId(medecinTest.getIdEntite());

            if (foundNotif.isPresent() && notifsByUser.stream().anyMatch(n -> n.getIdEntite().equals(notificationTest.getIdEntite()))) {
                System.out.println("[Notification] READ OK (findById et findByUtilisateurId). Titre: " + foundNotif.get().getTitre());
            } else {
                System.err.println("[Notification] READ Échec.");
            }
        }
        if (certificatTest != null && dossierTest != null) {
            // 1. Test findById (READ C de CRUD)
            Optional<Certificat> foundCertificatOpt = certificatRepository.findById(certificatTest.getIdEntite());
            if (foundCertificatOpt.isPresent() && foundCertificatOpt.get().getDuree() == certificatTest.getDuree()) {
                System.out.println("[Certificat] READ OK (findById). Durée: " + foundCertificatOpt.get().getDuree());
            } else {
                System.err.println("[Certificat] READ Échec (findById).");
            }

            // 2. Test findByDossierMedicaleId (Recherche Spécifique via JOIN)
            List<Certificat> foundByDossier = certificatRepository.findByDossierMedicaleId(dossierTest.getIdEntite());
            if (!foundByDossier.isEmpty() && foundByDossier.get(0).getIdEntite().equals(certificatTest.getIdEntite())) {
                System.out.println("[Certificat] READ OK (findByDossierMedicaleId). Count: " + foundByDossier.size());
            } else {
                System.err.println("[Certificat] READ Échec (findByDossierMedicaleId).");
            }

            // 3. Test findValidCertificates (Recherche Spécifique par Date)
            // Le certificat créé est valide si sa date_fin est future. On vérifie avec la date d'hier.
            List<Certificat> foundValid = certificatRepository.findValidCertificates(LocalDate.now().minusDays(1));
            if (!foundValid.isEmpty() && foundValid.stream().anyMatch(c -> c.getIdEntite().equals(certificatTest.getIdEntite()))) {
                System.out.println("[Certificat] READ OK (findValidCertificates).");
            } else {
                System.err.println("[Certificat] READ Échec (findValidCertificates).");
            }
        }
        // --- Selection RDV (READ) ---
        if (rdvTest != null) {
            Optional<RDV> foundRdvOpt = rdvRepository.findById(rdvTest.getIdEntite());

            // Test 1: findById
            if (foundRdvOpt.isPresent() && foundRdvOpt.get().getMotif().equals(rdvTest.getMotif())) {
                System.out.println("[RDV] READ OK (findById). Motif: " + foundRdvOpt.get().getMotif());
            } else {
                System.err.println("[RDV] READ Échec (findById).");
            }

            // Test 2: findByDossierMedicaleId
            if (dossierTest != null) {
                List<RDV> rdvByDossier = rdvRepository.findByDossierMedicaleId(dossierTest.getIdEntite());
                if (!rdvByDossier.isEmpty() && rdvByDossier.get(0).getIdEntite().equals(rdvTest.getIdEntite())) {
                    System.out.println("[RDV] READ OK (findByDossierMedicaleId). Count: " + rdvByDossier.size());
                } else {
                    System.err.println("[RDV] READ Échec (findByDossierMedicaleId).");
                }
            }

            // Test 3: findByDate
            List<RDV> rdvByDate = rdvRepository.findByDate(rdvTest.getDate());
            if (!rdvByDate.isEmpty()) {
                System.out.println("[RDV] READ OK (findByDate). Count: " + rdvByDate.size());
            } else {
                System.err.println("[RDV] READ Échec (findByDate).");
            }

            // Test 4: findByStatut
            List<RDV> rdvByStatut = rdvRepository.findByStatut(StatutRDV.PENDING);
            if (!rdvByStatut.isEmpty()) {
                System.out.println("[RDV] READ OK (findByStatut). Count: " + rdvByStatut.size());
            } else {
                System.err.println("[RDV] READ Échec (findByStatut).");
            }
        }
        // ... (Après la sélection Staff/Secretaire)

        // --- ** NOVEAUTÉ **: Sélection Charges (READ) et recherches spécifiques ---
        if (chargesTest != null && cabinetTest != null) {

            // 1. Test findById (READ C de CRUD)
            Optional<Charges> foundChargesOpt = chargesRepository.findById(chargesTest.getIdEntite());
            if (foundChargesOpt.isPresent() && foundChargesOpt.get().getTitre().equals(chargesTest.getTitre())) {
                System.out.println("[Charges] READ OK (findById). Titre: " + foundChargesOpt.get().getTitre());
            } else {
                System.err.println("[Charges] READ Échec (findById).");
            }

            // 2. Test findByCabinetMedicaleId
            List<Charges> foundByCabinet = chargesRepository.findByCabinetMedicaleId(cabinetTest.getIdEntite());
            if (!foundByCabinet.isEmpty() && foundByCabinet.stream().anyMatch(c -> c.getIdEntite().equals(chargesTest.getIdEntite()))) {
                System.out.println("[Charges] READ OK (findByCabinetMedicaleId). Count: " + foundByCabinet.size());
            } else {
                System.err.println("[Charges] READ Échec (findByCabinetMedicaleId).");
            }

            // 3. Test findByDateBetween (On utilise une période qui contient la date de la charge)
            LocalDateTime dateDebut = chargesTest.getDate().minusDays(1);
            LocalDateTime dateFin = chargesTest.getDate().plusDays(1);
            List<Charges> foundByDateRange = chargesRepository.findByDateBetween(dateDebut, dateFin);
            if (!foundByDateRange.isEmpty() && foundByDateRange.stream().anyMatch(c -> c.getIdEntite().equals(chargesTest.getIdEntite()))) {
                System.out.println("[Charges] READ OK (findByDateBetween). Count: " + foundByDateRange.size());
            } else {
                System.err.println("[Charges] READ Échec (findByDateBetween).");
            }

            // 4. Test calculateTotalChargesByDateBetween
            Double totalCharges = chargesRepository.calculateTotalChargesByDateBetween(dateDebut, dateFin);
            if (totalCharges > 0.0) {
                System.out.println("[Charges] READ OK (calculateTotalChargesByDateBetween). Total: " + totalCharges);
            } else {
                System.err.println("[Charges] READ Échec (calculateTotalChargesByDateBetween). Total: " + totalCharges);
            }

            String keyword = chargesTest.getTitre().substring(0, 5);
            List<Charges> foundByKeyword = chargesRepository.findByTitreOrDescriptionContaining(keyword);
            if (!foundByKeyword.isEmpty() && foundByKeyword.stream().anyMatch(c -> c.getIdEntite().equals(chargesTest.getIdEntite()))) {
                System.out.println("[Charges] READ OK (findByTitreOrDescriptionContaining). Count: " + foundByKeyword.size());
            } else {
                System.err.println("[Charges] READ Échec (findByTitreOrDescriptionContaining).");
            }
        }
    }


    void updateProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 3. PROCESSUS DE MISE À JOUR (UPDATE) [" + module + "] ===");
        System.out.println("=================================================");

        // --- Mise à jour Secretaire (pour tester les champs hérités et spécifiques) ---
        if (secretaireTest != null) {
            Secretaire s = secretaireRepository.findById(secretaireTest.getIdEntite()).orElse(null);
            if (s != null) {
                // Mise à jour d'un champ hérité de Staff
                s.setPrenom("Test Updated");
                s.setModifiePar(MODIFICATEUR_ID);

                try {
                    secretaireRepository.update(s);
                    Secretaire updatedS = secretaireRepository.findById(s.getIdEntite()).orElse(null);
                    if (updatedS != null && "Test Updated".equals(updatedS.getPrenom()) && MODIFICATEUR_ID.equals(updatedS.getModifiePar())) {
                        System.out.println("[Secretaire] UPDATE OK. Nouveau Prenom: " + updatedS.getPrenom());
                    } else {
                        System.err.println("[Secretaire] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Secretaire] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Utilisateur (Test de l'entité mère) ---
        if (utilisateurTest != null) {
            Utilisateur u = utilisateurRepository.findById(utilisateurTest.getIdEntite()).orElse(null);
            if (u != null) {
                u.setPrenom("Test Updated");
                u.setModifiePar(MODIFICATEUR_ID);
                try {
                    utilisateurRepository.update(u);
                    Utilisateur updatedU = utilisateurRepository.findById(u.getIdEntite()).orElse(null);
                    if (updatedU != null && "Test Updated".equals(updatedU.getPrenom())) {
                        System.out.println("[Utilisateur] UPDATE OK. Nouveau Prenom: " + updatedU.getPrenom());
                    } else {
                        System.err.println("[Utilisateur] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Utilisateur] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Admin ---
        if (adminTest != null) {
            Admin a = adminRepository.findById(adminTest.getIdEntite()).orElse(null);
            if (a != null) {
                String newTel = "0770000000";
                a.setTel(newTel);
                a.setModifiePar(MODIFICATEUR_ID);
                try {
                    adminRepository.update(a);
                    Admin updatedA = adminRepository.findById(a.getIdEntite()).orElse(null);
                    if (updatedA != null && newTel.equals(updatedA.getTel())) {
                        System.out.println("[Admin] UPDATE OK. Nouveau Tel: " + updatedA.getTel());
                    } else {
                        System.err.println("[Admin] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Admin] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }
        if (cabinetTest != null) {
            CabinetMedicale c = cabinetRepository.findById(cabinetTest.getIdEntite()).orElse(null);
            if (c != null) {
                long timeSuffix = System.currentTimeMillis();
                String newEmail = "new.email." + timeSuffix + "@cabinet.ma";
                c.setEmail(newEmail);
                c.setModifiePar(MODIFICATEUR_ID);
                try {
                    cabinetRepository.update(c);
                    CabinetMedicale updatedC = cabinetRepository.findById(c.getIdEntite()).orElse(null);
                    if (updatedC != null && newEmail.equals(updatedC.getEmail())) {
                        System.out.println("[Cabinet] UPDATE OK. Nouveau Email: " + updatedC.getEmail());
                    } else {
                        System.err.println("[Cabinet] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Cabinet] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }
        if (antecedentTest != null) {
            Antecedent a = antecedentRepository.findById(antecedentTest.getIdEntite()).orElse(null);
            if (a != null) {
                String newName = "Antécédent Mis à Jour " + System.currentTimeMillis();
                a.setNom(newName);
                a.setModifiePar(MODIFICATEUR_ID);
                try {
                    antecedentRepository.update(a);
                    Antecedent updatedA = antecedentRepository.findById(a.getIdEntite()).orElse(null);
                    if (updatedA != null && newName.equals(updatedA.getNom())) {
                        System.out.println("[Antecedent] UPDATE OK. Nouveau Nom: " + updatedA.getNom());
                    } else {
                        System.err.println("[Antecedent] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Antecedent] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Patient ---
        if (patientTest != null) {
            Patient p = patientRepository.findById(patientTest.getIdEntite()).orElse(null);
            if (p != null) {
                String newTel = "0699887766";
                p.setTelephone(newTel);
                p.setModifiePar(MODIFICATEUR_ID);
                try {
                    patientRepository.update(p);
                    Patient updatedP = patientRepository.findById(p.getIdEntite()).orElse(null);
                    if (updatedP != null && newTel.equals(updatedP.getTelephone())) {
                        System.out.println("[Patient] UPDATE OK. Nouveau Tel: " + updatedP.getTelephone());
                    } else {
                        System.err.println("[Patient] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Patient] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Consultation ---
        if (certificatTest != null) {
            final int NOUVELLE_DUREE = 10;
            final String NOUVELLE_NOTE = "Certificat prolongé de 10 jours.";

            certificatTest.setDuree(NOUVELLE_DUREE);
            certificatTest.setNoteMedecin(NOUVELLE_NOTE);
            certificatTest.setDateDerniereModification(LocalDateTime.now());
            certificatTest.setModifiePar(MODIFICATEUR_ID);

            try {
                certificatRepository.update(certificatTest);
                Optional<Certificat> updatedOpt = certificatRepository.findById(certificatTest.getIdEntite());

                if (updatedOpt.isPresent() && updatedOpt.get().getDuree() == NOUVELLE_DUREE && updatedOpt.get().getNoteMedecin().equals(NOUVELLE_NOTE)) {
                    System.out.println("[Certificat] UPDATE OK. Nouvelle Durée/Note: " + updatedOpt.get().getDuree());
                } else {
                    System.err.println("[Certificat] UPDATE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Certificat] UPDATE Échec critique: " + e.getMessage());
            }
        }
        if (consultationTest != null) {
            consultationTest.setStatut(StatutConsultation.COMPLETED);
            consultationTest.setDateDerniereModification(LocalDateTime.now());
            consultationTest.setModifiePar(MODIFICATEUR_ID);

            try {
                consultationRepository.update(consultationTest);
                Optional<Consultation> updatedOpt = consultationRepository.findById(consultationTest.getIdEntite());

                if (updatedOpt.isPresent() && updatedOpt.get().getStatut() == StatutConsultation.COMPLETED) {
                    System.out.println("[Consultation] UPDATE OK. Statut: COMPLETED");
                } else {
                    System.err.println("[Consultation] UPDATE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Consultation] UPDATE Échec critique: " + e.getMessage());
            }
        }


        // --- Mise à jour Dossier Medicale ---
        if (dossierTest != null) {
            DossierMedicale d = dossierRepository.findById(dossierTest.getIdEntite()).orElse(null);
            if (d != null) {
                d.setModifiePar(MODIFICATEUR_ID);
                try {
                    dossierRepository.update(d);
                    DossierMedicale updatedD = dossierRepository.findById(d.getIdEntite()).orElse(null);
                    if (updatedD != null && MODIFICATEUR_ID.equals(updatedD.getModifiePar())) {
                        System.out.println("[Dossier] UPDATE OK. Modification des métadonnées réussie. Modifié par: " + updatedD.getModifiePar());
                    } else {
                        System.err.println("[Dossier] UPDATE Échec.");
                    }
                } catch (Exception e) {
                    System.err.println("[Dossier] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }
        // --- Mise à jour RDV ---
        if (rdvTest != null) {
            RDV rdvToUpdate = rdvTest;
            String nouveauMotif = "Nettoyage annuel - U";
            rdvToUpdate.setModifiePar(MODIFICATEUR_ID);
            rdvToUpdate.setMotif(nouveauMotif);
            rdvToUpdate.setDate(rdvToUpdate.getDate().plusDays(1)); // Décaler la date

            try {
                rdvRepository.update(rdvToUpdate);
                Optional<RDV> updatedRdvOpt = rdvRepository.findById(rdvToUpdate.getIdEntite());

                if (updatedRdvOpt.isPresent() && updatedRdvOpt.get().getMotif().equals(nouveauMotif)) {
                    System.out.println("[RDV] UPDATE OK. Nouveau Motif/Date: " + updatedRdvOpt.get().getMotif());
                    RDV rdvUpdatedStatut = rdvRepository.updateStatut(rdvToUpdate.getIdEntite(), StatutRDV.CONFIRMED);
                    if (rdvUpdatedStatut != null && rdvUpdatedStatut.getStatut() == StatutRDV.CONFIRMED) {
                        System.out.println("[RDV] UPDATE_STATUT OK. Statut: " + rdvUpdatedStatut.getStatut());
                    } else {
                        System.err.println("[RDV] UPDATE_STATUT Échec.");
                    }
                } else {
                    System.err.println("[RDV] UPDATE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[RDV] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        if (chargesTest != null) {
            Charges c = chargesRepository.findById(chargesTest.getIdEntite()).orElse(null);
            if (c != null) {
                // Changement d'un champ spécifique
                BigDecimal nouveauMontant = c.getMontant().add(new BigDecimal("500.00"));
                c.setMontant(nouveauMontant);
                c.setDescription("Mise à jour du montant du loyer suite à un ajustement.");
                // Mise à jour des champs hérités
                c.setModifiePar(MODIFICATEUR_ID);

                try {
                    chargesRepository.update(c);
                    Charges updatedC = chargesRepository.findById(c.getIdEntite()).orElse(null);
                    if (updatedC != null
                            && nouveauMontant.compareTo(updatedC.getMontant()) == 0 // Utilisation de compareTo pour BigDecimal
                            && MODIFICATEUR_ID.equals(updatedC.getModifiePar())) {

                        System.out.println("[Charges] UPDATE OK. Nouveau Montant: " + updatedC.getMontant());
                    } else {
                        System.err.println("[Charges] UPDATE Échec. Montant attendu: " + nouveauMontant + ", Trouvé: " + (updatedC != null ? updatedC.getMontant() : "null"));
                    }
                } catch (Exception e) {
                    System.err.println("[Charges] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }
    }

    void deleteProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 4. PROCESSUS DE SUPPRESSION (DELETE) [" + module + "] ===");
        System.out.println("=================================================");

        if (antecedentTest != null && patientTest != null) {
            try {
                antecedentRepository.unlinkAntecedentFromPatient(antecedentTest.getIdEntite(), patientTest.getIdEntite());
                List<Antecedent> check = antecedentRepository.findByPatientId(patientTest.getIdEntite());
                if (check.isEmpty()) {
                    System.out.println("[Antecedent] Lien M-to-M retiré OK.");
                } else {
                    System.err.println("[Antecedent] Retrait Lien M-to-M Échec. Le lien persiste.");
                }

                antecedentRepository.deleteById(antecedentTest.getIdEntite());
                if (antecedentRepository.findById(antecedentTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Antecedent] DELETE OK.");
                } else {
                    System.err.println("[Antecedent] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Antecedent] DELETE Échec critique: " + e.getMessage());
            }
        }

        if (certificatTest != null) {
            try {
                certificatRepository.deleteById(certificatTest.getIdEntite());
                Optional<Certificat> check = certificatRepository.findById(certificatTest.getIdEntite());
                if (check.isEmpty()) {
                    System.out.println("[Certificat] DELETE OK.");
                } else {
                    System.err.println("[Certificat] DELETE Échec (L'entité persiste).");
                }
            } catch (Exception e) {
                System.err.println("[Certificat] DELETE Échec critique: " + e.getMessage());
            }
        }
        if (consultationTest != null) {
            try {
                consultationRepository.deleteById(consultationTest.getIdEntite());
                Optional<Consultation> check = consultationRepository.findById(consultationTest.getIdEntite());
                if (check.isEmpty()) {
                    System.out.println("[Consultation] DELETE OK.");
                } else {
                    System.err.println("[Consultation] DELETE Échec (L'entité persiste).");
                }
            } catch (Exception e) {
                System.err.println("[Consultation] DELETE Échec critique: " + e.getMessage());
            }
        }
        if (dossierTest != null) {
            try {
                dossierRepository.deleteById(dossierTest.getIdEntite());
                Optional<DossierMedicale> check = dossierRepository.findById(dossierTest.getIdEntite());
                if (check.isEmpty()) {
                    System.out.println("[Dossier] DELETE OK.");
                } else {
                    System.err.println("[Dossier] DELETE Échec (L'entité persiste).");
                }
            } catch (Exception e) {
                System.err.println("[Dossier] DELETE Échec critique: " + e.getMessage());
            }
        }
        if (chargesTest != null) {
            Long chargesId = chargesTest.getIdEntite();
            try {
                chargesRepository.deleteById(chargesId);
                Optional<Charges> check = chargesRepository.findById(chargesId);
                if (check.isEmpty()) {
                    System.out.println("[Charges] DELETE OK.");
                } else {
                    System.err.println("[Charges] DELETE Échec (L'entité persiste). ID: " + chargesId);
                }
            } catch (Exception e) {
                System.err.println("[Charges] DELETE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }
        if (patientTest != null) {
            try {
                patientRepository.deleteById(patientTest.getIdEntite());
                if (patientRepository.findById(patientTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Patient] DELETE OK.");
                } else {
                    System.err.println("[Patient] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Patient] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 5. Acte
        if (acteTest != null) {
            try {
                acteRepository.deleteById(acteTest.getIdEntite());
                if (acteRepository.findById(acteTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Acte] DELETE OK.");
                } else {
                    System.err.println("[Acte] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Acte] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 6. Notification (d'abord les liens utilisateurs)
        if (notificationTest != null) {
            try {
                // Supprime implicitement les liens dans la table de jointure si la FK est ON DELETE CASCADE,
                // sinon, il faudrait une méthode explicite `removeUtilisateurFromNotification`.
                notificationRepository.deleteById(notificationTest.getIdEntite());
                if (notificationRepository.findById(notificationTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Notification] DELETE OK.");
                } else {
                    System.err.println("[Notification] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Notification] DELETE Échec critique: " + e.getMessage());
            }
        }

        if (agendaTest != null) {
            try {
                agendaRepository.deleteById(agendaTest.getIdEntite());
                if (agendaRepository.findById(agendaTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Agenda] DELETE OK.");
                } else {
                    System.err.println("[Agenda] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Agenda] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 8. Medecin
        if (medecinTest != null) {
            try {
                medecinRepository.deleteById(medecinTest.getIdEntite());
                if (medecinRepository.findById(medecinTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Medecin] DELETE OK.");
                } else {
                    System.err.println("[Medecin] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Medecin] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 9. Secretaire
        if (secretaireTest != null) {
            try {
                secretaireRepository.deleteById(secretaireTest.getIdEntite());
                if (secretaireRepository.findById(secretaireTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Secretaire] DELETE OK.");
                } else {
                    System.err.println("[Secretaire] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Secretaire] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 10. Staff
        if (staffTest != null) {
            try {
                staffRepository.deleteById(staffTest.getIdEntite());
                if (staffRepository.findById(staffTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Staff] DELETE OK.");
                } else {
                    System.err.println("[Staff] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Staff] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 11. Admin
        if (adminTest != null) {
            try {
                adminRepository.deleteById(adminTest.getIdEntite());
                if (adminRepository.findById(adminTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Admin] DELETE OK.");
                } else {
                    System.err.println("[Admin] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Admin] DELETE Échec critique: " + e.getMessage());
            }
        }

        if (rdvTest != null) {
            Long rdvId = rdvTest.getIdEntite();
            try {
                rdvRepository.delete(rdvTest);
                if (rdvRepository.findById(rdvId).isEmpty()) {
                    System.out.println("[RDV] DELETE OK.");
                } else {
                    System.err.println("[RDV] DELETE Échec (Entité toujours présente). ID: " + rdvId);
                }
            } catch (Exception e) {
                System.err.println("[RDV] DELETE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }


        // 13. Utilisateur
        if (utilisateurTest != null) {
            try {
                utilisateurRepository.deleteById(utilisateurTest.getIdEntite());
                if (utilisateurRepository.findById(utilisateurTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Utilisateur] DELETE OK.");
                } else {
                    System.err.println("[Utilisateur] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[Utilisateur] DELETE Échec critique: " + e.getMessage());
            }
        }

        // 14. Cabinet (Nettoyage final)
        if (cabinetTest != null) {
            try {
                cabinetRepository.deleteById(cabinetTest.getIdEntite());
                if (cabinetRepository.findById(cabinetTest.getIdEntite()).isEmpty()) {
                    System.out.println("[Cabinet] DELETE OK (Nettoyage final).");
                } else {
                    System.err.println("[Cabinet] DELETE Échec (Nettoyage final).");
                }
            } catch (Exception e) {
                System.err.println("[Cabinet] DELETE Échec critique: " + e.getMessage());
            }
        }
        // --- Suppression RDV ---
        if (rdvTest != null) {
            Long id = rdvTest.getIdEntite();
            try {
                rdvRepository.delete(rdvTest);
                Optional<RDV> deletedRdv = rdvRepository.findById(id);

                if (deletedRdv.isEmpty()) {
                    System.out.println("[RDV] DELETE OK.");
                } else {
                    System.err.println("[RDV] DELETE Échec.");
                }
            } catch (Exception e) {
                System.err.println("[RDV] DELETE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        System.out.println("\n--- PROCESSUS DELETE TERMINÉ ---");
    }
}