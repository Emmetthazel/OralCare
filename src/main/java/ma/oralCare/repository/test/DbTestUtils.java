package ma.oralCare.repository.test;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Certificat;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.*;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.users.*;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.cabinet.api.ChargesRepository;
import ma.oralCare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.users.api.MedecinRepository;
import ma.oralCare.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.repository.modules.users.api.SecretaireRepository;
import ma.oralCare.repository.modules.users.api.StaffRepository;
import ma.oralCare.repository.modules.users.impl.MedecinRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.ActeRepositoryImpl;
import ma.oralCare.repository.modules.notification.impl.NotificationRepositoryImpl;
import ma.oralCare.repository.modules.users.impl.SecretaireRepositoryImpl;
import ma.oralCare.repository.modules.users.impl.StaffRepositoryImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Utilitaire pour la construction et le nettoyage des objets de test.
 * Assure l'unicité des champs (CIN, Email, Login) grâce à System.currentTimeMillis().
 */
public class DbTestUtils {

    // Utilisation des mêmes constantes que dans TestRepo pour les objets créés
    public enum Module { ALL, PATIENT_ONLY, AGENDA_ONLY, MEDECIN_ONLY , DOSSIER_ONLY, CONSULTATION_ONLY, NOTIFICATION_ONLY, CERTIFICAT_ONLY, CHARGES_ONLY}

    public static final Long CABINET_ID_TEST = 1L; // ID assumé pour le cabinet de base
    public static final Long USER_ID_ADMIN = 1L;   // ID assumé pour l'utilisateur admin
    public static final Long USER_ID_MEDECIN = 101L; // ID fictif pour les tests de Medecin/Agenda
    public static final String PATIENT_TEST_EMAIL_PREFIX = "amina.belhaj.test";
    public static final String CHARGES_TEST_TITLE_PREFIX = "Loyer Mensuel Cabinet "; // Préfixe pour le nettoyage
    public static final String PATIENT_TEST_TEL_INIT = "0601020304";
    public static final Mois AGENDA_MOIS_TEST = Mois.DECEMBER;
    public static final int AGENDA_ANNEE_TEST = LocalDate.now().getYear() + 1;

    // Repositories : Ajout de tous les repos pour permettre un nettoyage complet si nécessaire.
    private final PatientRepository patientRepository = new PatientRepositoryImpl();
    private final AgendaMensuelRepository agendaRepository = new AgendaMensuelRepositoryImpl();
    private final MedecinRepository medecinRepository = new MedecinRepositoryImpl();
    private final CabinetMedicaleRepository cabinetRepository = new CabinetMedicaleRepositoryImpl();
    private final DossierMedicaleRepository dossierRepository = new DossierMedicaleRepositoryImpl();
    private final ConsultationRepository consultationRepository = new ConsultationRepositoryImpl();
    private final ActeRepository acteRepository = new ActeRepositoryImpl();
    private final NotificationRepository notificationRepository = new NotificationRepositoryImpl();
    private final StaffRepository staffRepository = new StaffRepositoryImpl();
    private final SecretaireRepository secretaireRepository = new SecretaireRepositoryImpl();
    private final ChargesRepository chargesRepository = new ChargesRepositoryImpl(); // <-- Ajout du ChargesRepository
    // Instance Singleton
    private static DbTestUtils instance;
    private DbTestUtils() {}

    // Fournit l'accès singleton
    public static DbTestUtils getInstance() {
        if (instance == null) {
            instance = new DbTestUtils();
        }
        return instance;
    }

    // =========================================================================
    //                             Méthodes de CRÉATION
    // =========================================================================
    // ... (Après createStaffObject ou ailleurs dans la section CRÉATION) ...
    public Antecedent createAntecedentObject() {
        return Antecedent.builder()
                .idEntite(null)
                .nom("Diabète Type 2 - Test " + System.currentTimeMillis())
                .categorie(CategorieAntecedent.MALADIE_CHRONIQUE)
                .niveauDeRisque(NiveauDeRisque.HIGH)
                // BaseEntity
                .creePar(USER_ID_ADMIN)
                .modifiePar(null)
                // Relation Many-to-Many non chargée initialement
                .patients(Collections.emptyList())
                .build();
    }
    public Admin createAdminObject() {

        // Création d'une adresse fictive pour le test
        Adresse adminAdresse = Adresse.builder()
                .rue("1 Rue de l'Admin")
                .ville("Rabat")
                .codePostal("10000")
                .pays("Maroc")
                .build();

        return Admin.builder()
                .idEntite(null)
                .nom("Admin")
                .prenom("SuperUser")
                .email("admin_" + System.currentTimeMillis() + "@oralcare.ma")
                .tel("0600000001")
                .cin("A000000")
                .sexe(Sexe.FEMALE)
                .login("admintest" + System.currentTimeMillis())
                .motDePass("Pass1234")
                .dateNaissance(LocalDate.of(1985, 1, 1))

                // Champs spécifiques à Utilisateur
                .adresse(adminAdresse)
                .roles(Collections.emptyList())
                .notifications(Collections.emptyList())
                .lastLoginDate(null) // Laisser null pour que la DB le gère ou pour le premier login

                // Attributs de la classe de base (BaseEntity)
                // On utilise USER_ID_ADMIN comme creePar par convention de test
                .creePar(USER_ID_ADMIN)
                .modifiePar(null)
                .build();
    }
    // --- 10. SECRETAIRE ---
    public Secretaire createSecretaireObject() {
        long timeSuffix = System.currentTimeMillis();
        String uniqueCinPart = String.valueOf(timeSuffix % 1000000);
        String uniqueLogin = "secretaire.test" + timeSuffix;

        return Secretaire.builder()
                // BaseEntity
                .creePar(USER_ID_ADMIN)

                // Utilisateur (Hérité)
                .nom("Benali")
                .prenom("Sofia")
                .email("sofia.benali." + timeSuffix + "@cabinet.ma")
                .cin("SCRET" + uniqueCinPart)
                .tel("0699887755")
                .sexe(Sexe.FEMALE)
                .login(uniqueLogin)
                .motDePass("password_secret")
                .dateNaissance(LocalDate.of(1998, 1, 20))
                .adresse(Adresse.builder()
                        .numero("10")
                        .rue("Rue de la Secrétaire")
                        .codePostal("10000")
                        .ville("Rabat")
                        .pays("Maroc")
                        .build())

                // Staff (Hérité)
                .salaire(new BigDecimal("15000.00"))
                .prime(new BigDecimal("500.00"))
                .dateRecrutement(LocalDate.now().minusMonths(6))
                .soldeConge(15)
                // Le cabinet est setté dans TestRepo

                // Secretaire (Spécifique)
                .numCNSS("CNSS" + uniqueCinPart)
                .commission(new BigDecimal("0.10"))
                .build();
    }
    public Staff createStaffObject() {
        return Staff.builder()
                // BaseEntity
                .creePar(USER_ID_ADMIN)

                // Utilisateur (Hérité)
                .nom("Dupont")
                .prenom("Alice")
                .email("alice.dupont." + System.currentTimeMillis() + "@cabinet.ma")
                .cin("AZ457896")
                .tel("0600112233")
                .sexe(Sexe.FEMALE)
                .login("staff_alice")
                .motDePass("password123")
                .dateNaissance(LocalDate.of(1990, 5, 15))
                .adresse(Adresse.builder()
                        .numero("45")
                        .rue("Rue des Employés")
                        .codePostal("75000")
                        .ville("Paris")
                        .pays("France")
                        .build())

                // Staff (Spécifique)
                .salaire(new BigDecimal("35000.00"))
                .prime(new BigDecimal("2000.00"))
                .dateRecrutement(LocalDate.now().minusYears(2))
                .soldeConge(20)
                // Le cabinet est généralement setté dans TestRepo avant l'appel au repository
                .cabinetMedicale(null)

                .build();
    }
    public Utilisateur createUtilisateurObject(String prefixe) {
        long timeSuffix = System.currentTimeMillis() % 1000000;
        String email = prefixe + "." + timeSuffix + "@oralcare.ma";
        String login = prefixe + ".test" + timeSuffix;
        String cinPart = String.valueOf(timeSuffix);

        Adresse adresse = new Adresse(
                "123", "Rue de Test " + cinPart, "10000", "Rabat", "Maroc", "Appt 1A"
        );

        return Utilisateur.builder()
                .nom(prefixe.toUpperCase())
                .prenom("Test")
                .email(email)
                .cin("CDUTEST" + cinPart) // CIN unique
                .tel("05" + cinPart + "1111")
                .sexe(Sexe.FEMALE)
                .login(login)
                .motDePass("hashed_password")
                .dateNaissance(LocalDate.of(1995, 10, 10))
                .creePar(USER_ID_ADMIN)
                .adresse(adresse)
                .build();
    }
    public Role createRoleObject(RoleLibelle libelle) {
        return Role.builder()
                .libelle(libelle) // Utilisation directe de l'énumération
                .privileges(List.of())
                .creePar(USER_ID_ADMIN)
                .build();
    }
    // --- 1. CABINET ---
    public CabinetMedicale createCabinetObject() {
        CabinetMedicale cabinet = new CabinetMedicale();
        cabinet.setCreePar(USER_ID_ADMIN);

        long timeSuffix = System.currentTimeMillis();
        cabinet.setNom("Cabinet Test Principal - " + timeSuffix);
        cabinet.setEmail("contact.cabinet." + timeSuffix + "@oralcare.ma");

        Adresse adresseCabinet = new Adresse(
                "1", "Avenue du Cabinet", "10000", "Rabat", "Maroc", null
        );
        cabinet.setAdresse(adresseCabinet);

        String uniqueCinPart = String.valueOf(timeSuffix % 100000);
        cabinet.setCin("CAB" + uniqueCinPart);
        String uniqueTelPart = String.valueOf(timeSuffix % 1000000);
        cabinet.setTel1("0537" + uniqueTelPart);
        return cabinet;
    }

    // --- 2. MEDECIN ---
    public Medecin createMedecinObject() {
        Medecin m = new Medecin();
        m.setCreePar(USER_ID_ADMIN);
        m.setNom("EL HAJJI");
        m.setPrenom("Youssef");

        long timeSuffix = System.currentTimeMillis();
        m.setEmail("test.medecin." + timeSuffix + "@oralcare.ma");
        m.setLogin("medecin.test" + timeSuffix);

        m.setMotDePass("hashed_password");
        String uniqueTelPart = String.valueOf(timeSuffix % 100000);
        m.setTel("05" + uniqueTelPart + "0000");
        m.setSexe(Sexe.MALE);
        m.setDateNaissance(LocalDate.of(1980, 5, 15));

        // CORRECTION D'UNICITÉ : Utilisation d'un suffixe temporel
        String uniqueCinPart = String.valueOf(timeSuffix % 1000000);
        m.setCin("CDTEST" + uniqueCinPart);

        m.setAdresse(new Adresse(
                "123", "Rue des Doctors", "10000", "Rabat", "Maroc", "Bureau 1"
        ));

        m.setSalaire(new java.math.BigDecimal("50000.00"));
        m.setPrime(new java.math.BigDecimal("5000.00"));
        m.setDateRecrutement(LocalDate.now());
        m.setSoldeConge(20);

        // Association FK
        CabinetMedicale cabinet = new CabinetMedicale();
        cabinet.setIdEntite(CABINET_ID_TEST);
        m.setCabinetMedicale(cabinet);

        m.setSpecialite("Orthodontie");
        return m;
    }

    // --- 3. PATIENT ---
    public Patient createPatientObject() {
        Patient p = new Patient();
        p.setCreePar(USER_ID_ADMIN);
        p.setNom("BELHAJ");
        p.setPrenom("Amina");
        p.setDateDeNaissance(LocalDate.of(1988, 11, 25));

        // CORRECTION D'UNICITÉ
        p.setEmail(PATIENT_TEST_EMAIL_PREFIX + System.currentTimeMillis() + "@testrepo.ma");

        p.setSexe(Sexe.FEMALE);
        p.setAdresse("15 Avenue des Forces, Casablanca");
        p.setTelephone(PATIENT_TEST_TEL_INIT + (System.currentTimeMillis() % 100)); // Rendre le tel unique
        p.setAssurance(Assurance.CNOPS);
        return p;
    }

    // --- 4. AGENDA ---
    public AgendaMensuel createAgendaObject(Long medecinId) {
        AgendaMensuel am = new AgendaMensuel();
        am.setCreePar(USER_ID_ADMIN);
        am.setMois(AGENDA_MOIS_TEST);
        am.setAnnee(AGENDA_ANNEE_TEST);

        Medecin medecin = new Medecin();
        medecin.setIdEntite(medecinId);
        am.setMedecin(medecin);

        am.setJoursNonDisponible(Arrays.asList(Jour.SATURDAY, Jour.SUNDAY));
        return am;
    }

    // --- 5. DOSSIER MEDICALE ---
    public DossierMedicale createDossierMedicaleObject(Long patientId, Long medecinId) {
        DossierMedicale dm = DossierMedicale.builder()
                .creePar(USER_ID_ADMIN)
                .build();

        Patient p = new Patient();
        p.setIdEntite(patientId);
        dm.setPatient(p);

        Medecin m = new Medecin();
        m.setIdEntite(medecinId);
        dm.setMedecin(m);

        return dm;
    }

    // --- 6. CONSULTATION ---
    public Consultation createConsultationObject(Long dossierId) {
        DossierMedicale dm = new DossierMedicale();
        dm.setIdEntite(dossierId);

        Consultation consultation = Consultation.builder()
                .creePar(USER_ID_ADMIN)
                .date(LocalDate.now().plusDays(1)) // Date future
                .statut(StatutConsultation.SCHEDULED)
                .observationMedecin("Consultation de contrôle post-traitement du " + LocalDate.now())
                .dossierMedicale(dm)
                .build();

        return consultation;
    }

    // --- 7. ACTE ---
    public Acte createActeObject(Long creeParId) {
        Acte acte = Acte.builder()
                .libelle("Consultation Orthodontique - " + System.currentTimeMillis())
                .categorie("CONSULTATION")
                .prixDeBase(new BigDecimal("750.00"))
                .creePar(creeParId)
                .build();
        return acte;
    }

    // --- 8. NOTIFICATION ---
    public Notification createNotificationObject(List<Utilisateur> destinataires) {
        Notification notification = Notification.builder()
                .creePar(USER_ID_ADMIN)
                .titre(NotificationTitre.APPOINTMENT_REMINDER)
                .message("Votre rendez-vous est prévu demain à 10h00.")
                .date(LocalDate.now())
                .time(LocalTime.of(8, 0))
                .type(NotificationType.ALERT)
                .priorite(NotificationPriorite.HIGH)
                .utilisateurs(destinataires)
                .build();
        return notification;
    }

    // --- 9. RDV (Rendez-vous) ---
    public RDV createRdvObject(Long dossierMedicaleId, Long medecinId, Long consultationId) {
        long timestamp = System.currentTimeMillis();

        // 1. Dépendances Obligatoires
        DossierMedicale dossierStub = DossierMedicale.builder().idEntite(dossierMedicaleId).build();

        // 2. Dépendance Optionnelle
        Consultation consultationStub = (consultationId != null)
                ? Consultation.builder().idEntite(consultationId).build()
                : null;

        // 3. Champs Temporels
        LocalDate dateRdv = LocalDate.now().plusDays(5);
        LocalTime heureRdv = LocalTime.of(10, 30);

        RDV rdv = RDV.builder()
                .date(dateRdv)
                .heure(heureRdv)
                .motif("Contrôle annuel de routine - " + timestamp)
                .statut(StatutRDV.PENDING)
                .noteMedecin("Préparation des radios panoramiques.")
                .dossierMedicale(dossierStub)
                .consultation(consultationStub) // <-- Ajout de la Consultation (peut être null)
                .creePar(1L) // Utilisateur Admin ou Medecin
                .build();

        return rdv;
    }

    public Certificat createCertificatObject(Consultation consultation) {
        if (consultation == null || consultation.getIdEntite() == null) {
            throw new IllegalArgumentException("La Consultation doit être persistée et fournie pour créer un Certificat.");
        }

        // S'assurer que la consultation a le statut "COMPLETED" si possible,
        // car un certificat est généralement délivré après une consultation terminée.
        if (consultation.getStatut() != StatutConsultation.COMPLETED) {
            consultation.setStatut(StatutConsultation.COMPLETED);
        }

        return Certificat.builder()
                .dateCreation(LocalDateTime.now())
                .creePar(USER_ID_ADMIN)

                // La clé primaire et étrangère est liée à la consultation
                .idEntite(consultation.getIdEntite())

                // Champs spécifiques
                .dateDebut(LocalDate.now().plusDays(1))
                .dateFin(LocalDate.now().plusDays(8))
                .duree(7)
                .noteMedecin("Arrêt de travail nécessaire suite à l'extraction dentaire.")

                // Relation One-to-One
                .consultation(consultation)

                // Le champ consultation_id est géré via la consultation et l'idEntite
                .build();
    }

    // --- 11. CHARGES ---
    /**
     * Crée un objet Charges pour les tests.
     * Le cabinet doit être fourni et avoir un ID persistant.
     */
    public Charges createChargesObject(CabinetMedicale cabinet) { // <-- CORRECTION DE LA SIGNATURE
        if (cabinet == null || cabinet.getIdEntite() == null) {
            throw new IllegalArgumentException("Le CabinetMedicale fourni ne doit pas être null et doit avoir un ID.");
        }

        long timeSuffix = System.currentTimeMillis();

        return Charges.builder()
                .idEntite(null)
                // BaseEntity
                .creePar(USER_ID_ADMIN)
                .modifiePar(null)

                // Charges (Spécifique)
                .titre(CHARGES_TEST_TITLE_PREFIX + timeSuffix) // Utilisation du préfixe constant
                .description("Paiement du loyer du mois de " + LocalDate.now().getMonth() + " (Test)")
                .montant(new BigDecimal("15000.00"))
                .date(LocalDateTime.now().minusDays(3)) // Date d'aujourd'hui moins 3 jours
                .cabinetMedicale(cabinet) // <-- CORRECTION : Utilisation de l'objet cabinet passé en paramètre
                .build();
    }


    // =========================================================================
    //                             Méthode de NETTOYAGE (CleanUp)
    // =========================================================================

    /**
     * Tente de nettoyer les données spécifiques au module avant un test.
     * Note: Dans un vrai environnement de test, on nettoierait souvent tout
     * ou on utiliserait des transactions rollback. Ici, on cible les données par défaut.
     */
    public void cleanUp(Module module) {

        System.out.println("   [Patient] Nettoyage préalable OK."); // Ce log semble venir de l'initialisation de TestRepo.

        // Le nettoyage est mieux géré dans TestRepo.deleteProcess, mais on peut ajouter un nettoyage pré-test ici
        // pour les objets que l'on sait exister par défaut (ID fixes comme USER_ID_MEDECIN) ou par email/CIN fixe.

        if (module == Module.CHARGES_ONLY || module == Module.ALL) {
            // Nettoyage des objets Charges créés par le test
            chargesRepository.findAll().stream()
                    .filter(c -> c.getTitre() != null && c.getTitre().startsWith(CHARGES_TEST_TITLE_PREFIX))
                    .forEach(c -> {
                        try {
                            chargesRepository.deleteById(c.getIdEntite());
                            System.out.println("   [Charges] Nettoyage de la charge ID: " + c.getIdEntite() + " OK.");
                        } catch (Exception e) { System.err.println("   [Charges] Échec nettoyage par Titre (ID: " + c.getIdEntite() + "): " + e.getMessage()); }
                    });
        }

        if (module == Module.MEDECIN_ONLY || module == Module.ALL) {
            // Tentative de trouver et nettoyer des médecins de test potentiellement laissés
            medecinRepository.findAll().stream()
                    .filter(m -> m.getCin() != null && m.getCin().startsWith("CDTEST"))
                    .forEach(m -> {
                        try {
                            medecinRepository.deleteById(m.getIdEntite());
                        } catch (Exception e) { System.err.println("   [Medecin] Échec nettoyage par CIN (ID: " + m.getIdEntite() + "): " + e.getMessage()); }
                    });
        }

        if (module == Module.PATIENT_ONLY || module == Module.ALL) {
            // Tentative de trouver et nettoyer des patients de test potentiellement laissés
            patientRepository.findAll().stream()
                    .filter(p -> p.getEmail() != null && p.getEmail().startsWith(PATIENT_TEST_EMAIL_PREFIX))
                    .forEach(p -> {
                        try {
                            patientRepository.deleteById(p.getIdEntite());
                        } catch (Exception e) { System.err.println("   [Patient] Échec nettoyage par Email (ID: " + p.getIdEntite() + "): " + e.getMessage()); }
                    });
        }

        if (module == Module.AGENDA_ONLY || module == Module.ALL) {
            agendaRepository.findByMedecinIdAndMoisAndAnnee(USER_ID_MEDECIN, AGENDA_MOIS_TEST, AGENDA_ANNEE_TEST)
                    .ifPresent(a -> {
                        try {
                            agendaRepository.deleteById(a.getIdEntite());
                            System.out.println("   [Agenda] Nettoyage préalable OK.");
                        } catch (Exception e) { System.err.println("   [Agenda] Échec nettoyage : " + e.getMessage()); }
                    });
        }
        if (module == Module.ALL) {
            // Tentative de trouver et nettoyer le staff de test
            // On se base sur un critère non-ID qui est le prefix du login "staff_alice"
            staffRepository.findAll().stream()
                    .filter(s -> s.getLogin() != null && s.getLogin().startsWith("staff_alice"))
                    .forEach(s -> {
                        try {
                            staffRepository.deleteById(s.getIdEntite());
                        } catch (Exception e) { System.err.println("   [Staff] Échec nettoyage par Login (ID: " + s.getIdEntite() + "): " + e.getMessage()); }
                    });
        }
        if (module == Module.ALL) {
            // Tentative de trouver et nettoyer le staff de test
            // On se base sur un critère non-ID qui est le prefix du login "staff_alice"
            staffRepository.findAll().stream()
                    .filter(s -> s.getLogin() != null && s.getLogin().startsWith("staff_alice"))
                    .forEach(s -> {
                        try {
                            staffRepository.deleteById(s.getIdEntite());
                        } catch (Exception e) { System.err.println("   [Staff] Échec nettoyage par Login (ID: " + s.getIdEntite() + "): " + e.getMessage()); }
                    });

            // Tentative de trouver et nettoyer la secrétaire de test <-- AJOUTÉ
            secretaireRepository.findAll().stream()
                    .filter(sec -> sec.getLogin() != null && sec.getLogin().startsWith("secretaire.test"))
                    .forEach(sec -> {
                        try {
                            secretaireRepository.deleteById(sec.getIdEntite());
                        } catch (Exception e) { System.err.println("   [Secretaire] Échec nettoyage par Login (ID: " + sec.getIdEntite() + "): " + e.getMessage()); }
                    });
        }
    }
}