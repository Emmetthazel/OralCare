package ma.oralCare.repository.test;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.users.Notification;
import ma.oralCare.entities.users.Role;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.api.ActeRepository;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.oralCare.repository.modules.dossierMedical.api.DossierMedicaleRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.ActeRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.DossierMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.notification.api.NotificationRepository;
import ma.oralCare.repository.modules.notification.impl.NotificationRepositoryImpl;
import ma.oralCare.repository.modules.patient.api.PatientRepository;
import ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl;
import ma.oralCare.repository.modules.users.api.MedecinRepository;
import ma.oralCare.repository.modules.users.api.RoleRepository;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;
import ma.oralCare.repository.modules.users.impl.MedecinRepositoryImpl;
import ma.oralCare.repository.modules.users.impl.RoleRepositoryImpl;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.repository.test.DbTestUtils.Module;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class TestRepo {

    // --- Constantes (utilisent les valeurs de DbTestUtils) ---
    private static final Module MODULE_TO_TEST = Module.ALL;
    private static final Long CABINET_ID_TEST = DbTestUtils.CABINET_ID_TEST;
    private static final Long USER_ID_ADMIN = DbTestUtils.USER_ID_ADMIN;
    private static final Mois AGENDA_MOIS_TEST = DbTestUtils.AGENDA_MOIS_TEST;
    private static final int AGENDA_ANNEE_TEST = DbTestUtils.AGENDA_ANNEE_TEST;

    // Repositories
    private final PatientRepository patientRepository = new PatientRepositoryImpl();
    private final AgendaMensuelRepository agendaRepository = new AgendaMensuelRepositoryImpl();
    private final MedecinRepository medecinRepository = new MedecinRepositoryImpl();
    private final CabinetMedicaleRepository cabinetRepository = new CabinetMedicaleRepositoryImpl();
    private final ActeRepository acteRepository = new ActeRepositoryImpl();
    private final DossierMedicaleRepository dossierRepository = new DossierMedicaleRepositoryImpl();
    private final ConsultationRepository consultationRepository = new ConsultationRepositoryImpl();
    private final NotificationRepository notificationRepository = new NotificationRepositoryImpl();
    private final UtilisateurRepository utilisateurRepository = new UtilisateurRepositoryImpl(); // AJOUT
    private final RoleRepository roleRepository = new RoleRepositoryImpl(); // AJOUT
    // Référence à l'utilitaire de DB
    private final DbTestUtils dbUtils = DbTestUtils.getInstance();

    // Objets de test (pour stocker l'ID généré et les données initiales)
    private Patient patientTest = null;
    private AgendaMensuel agendaTest = null;
    private Medecin medecinTest = null;
    private CabinetMedicale cabinetTest = null;
    private Acte acteTest = null;
    private DossierMedicale dossierTest = null;
    private Consultation consultationTest = null;
    private Notification notificationTest = null; // Sera null si la création échoue
    private Utilisateur utilisateurTest = null; // AJOUT
    private Role roleTest = null; // AJOUT
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
                        "✅ [Utilisateur] CREATE OK. ID: " + utilisateurTest.getIdEntite() + ", Login: " + utilisateurTest.getLogin()
                        : "❌ [Utilisateur] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Utilisateur] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                utilisateurTest = null;
            }
        }

        if (module == Module.ALL && roleTest == null && utilisateurTest != null) {
            // Utilisation de RoleLibelle.ADMIN, en supposant que cette valeur existe dans votre enum
            Role nouveauRole = dbUtils.createRoleObject(RoleLibelle.ADMIN);
            try {
                roleRepository.create(nouveauRole);
                roleTest = nouveauRole;
                System.out.println(roleTest.getIdEntite() != null ?
                        "✅ [Role] CREATE OK. Libelle: " + roleTest.getLibelle()
                        : "❌ [Role] CREATE Échec (ID non généré).");

                // Tester l'association (addRoleToUtilisateur) dès la création
                utilisateurRepository.addRoleToUtilisateur(utilisateurTest.getIdEntite(), roleTest.getIdEntite());
                System.out.println("✅ [Role] Association ADD_ROLE OK.");

            } catch (Exception e) {
                // Cette erreur est celle que nous tentons de corriger (problème JDBC)
                System.err.println("❌ [Role] CREATE/Association Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                roleTest = null;
            }
        }

        // --- PRÉ-REQUIS II : Cabinet --- (Doit être fait en premier car tout le monde en dépend)
        if (cabinetRepository.findById(CABINET_ID_TEST).isEmpty()) {
            try {
                CabinetMedicale cabinetAInserer = dbUtils.createCabinetObject();
                cabinetAInserer.setIdEntite(CABINET_ID_TEST); // Tentative d'imposer l'ID pour les tests
                cabinetRepository.create(cabinetAInserer);
                cabinetTest = cabinetAInserer;
                System.out.println("✅ [Cabinet] PRÉ-REQUIS OK. ID: " + cabinetTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Cabinet] PRÉ-REQUIS Échec critique (Cabinet ID " + CABINET_ID_TEST + "L): " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                cabinetTest = null;
            }
        } else {
            cabinetTest = cabinetRepository.findById(CABINET_ID_TEST).get();
            System.out.println("✅ [Cabinet] PRÉ-REQUIS OK. ID " + cabinetTest.getIdEntite() + " existe déjà.");
        }

        // --- A. Insertion Medecin (Dépend de Cabinet) ---
        if ((module == Module.MEDECIN_ONLY || module == Module.ALL || module == Module.AGENDA_ONLY) && cabinetTest != null && medecinTest == null) {
            Medecin nouveauMedecin = dbUtils.createMedecinObject();
            nouveauMedecin.setCabinetMedicale(cabinetTest); // Assigner le cabinet pré-requis

            try {
                medecinRepository.create(nouveauMedecin);
                medecinTest = nouveauMedecin;
                System.out.println(medecinTest.getIdEntite() != null ?
                        "✅ [Medecin] CREATE OK. " + medecinTest.toString()
                        : "❌ [Medecin] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Medecin] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                medecinTest = null;
            }
        }

        // --- B. Insertion Patient ---
        if ((module == Module.PATIENT_ONLY || module == Module.ALL) && patientTest == null) {
            Patient nouveauPatient = dbUtils.createPatientObject();
            try {
                patientRepository.create(nouveauPatient);
                patientTest = nouveauPatient;
                System.out.println(patientTest.getIdEntite() != null ?
                        "✅ [Patient] CREATE OK. " + patientTest.toString()
                        : "❌ [Patient] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Patient] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                patientTest = null;
            }
        }

        // --- C. Insertion Dossier Medicale (Dépend de Patient et Medecin) ---
        if (patientTest != null && medecinTest != null && (module == Module.DOSSIER_ONLY || module == Module.ALL || module == Module.CONSULTATION_ONLY)) {
            DossierMedicale nouveauDossier = dbUtils.createDossierMedicaleObject(patientTest.getIdEntite(), medecinTest.getIdEntite());
            try {
                dossierRepository.create(nouveauDossier);
                dossierTest = nouveauDossier;
                System.out.println(dossierTest.getIdEntite() != null ?
                        "✅ [Dossier] CREATE OK. ID: " + dossierTest.getIdEntite()
                        : "❌ [Dossier] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Dossier] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                dossierTest = null;
            }
        }

        // --- D. Insertion Consultation (Dépend de Dossier Medicale) ---
        if (dossierTest != null && (module == Module.CONSULTATION_ONLY || module == Module.ALL)) {
            Consultation nouvelleConsultation = dbUtils.createConsultationObject(dossierTest.getIdEntite());
            try {
                consultationRepository.create(nouvelleConsultation);
                consultationTest = nouvelleConsultation;
                System.out.println(consultationTest.getIdEntite() != null ?
                        "✅ [Consultation] CREATE OK. ID: " + consultationTest.getIdEntite()
                        : "❌ [Consultation] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Consultation] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                consultationTest = null;
            }
        }

        // --- E. Insertion Agenda (Dépend de Medecin) ---
        if (module == Module.AGENDA_ONLY || (module == Module.ALL && medecinTest != null)) {
            // Utiliser medecinTest si créé, sinon un ID par défaut (qui pourrait échouer si l'ID n'est pas dans la DB)
            Long medecinId = (medecinTest != null) ? medecinTest.getIdEntite() : DbTestUtils.USER_ID_ADMIN;
            AgendaMensuel nouvelAgenda = dbUtils.createAgendaObject(medecinId);
            try {
                agendaRepository.create(nouvelAgenda);
                agendaTest = nouvelAgenda;
                System.out.println(agendaTest.getIdEntite() != null ?
                        "✅ [Agenda] CREATE OK. " + nouvelAgenda.toString()
                        : "❌ [Agenda] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Agenda] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                agendaTest = null;
            }
        }

        // --- F. Insertion Acte ---
        if (module == Module.ALL) {
            Acte nouvelActe = dbUtils.createActeObject(USER_ID_ADMIN);
            try {
                acteRepository.create(nouvelActe);
                acteTest = nouvelActe;
                System.out.println(acteTest.getIdEntite() != null ?
                        "✅ [Acte] CREATE OK. " + nouvelActe.toString()
                        : "❌ [Acte] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Acte] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                acteTest = null;
            }
        }

        // --- G. Insertion Notification (Dépend de Utilisateur/Medecin pour les destinataires) ---
        if (medecinTest != null && (module == Module.NOTIFICATION_ONLY || module == Module.ALL)) {
            // Créer un utilisateur destinataire (ici, le Medecin)
            List<Utilisateur> destinataires = List.of(medecinTest);
            Notification nouvelleNotification = dbUtils.createNotificationObject(destinataires);

            try {
                notificationRepository.create(nouvelleNotification);
                notificationTest = nouvelleNotification;
                System.out.println(notificationTest.getIdEntite() != null ?
                        "✅ [Notification] CREATE OK. Titre: " + notificationTest.getTitre()
                        : "❌ [Notification] CREATE Échec (ID non généré).");
            } catch (Exception e) {
                System.err.println("❌ [Notification] CREATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                notificationTest = null;
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

        // --- Sélection Utilisateur --- (Ajout)
        if (utilisateurTest != null) {
            Utilisateur foundUser = utilisateurRepository.findById(utilisateurTest.getIdEntite()).orElse(null);
            if (foundUser != null && foundUser.getLogin().equals(utilisateurTest.getLogin())) {
                System.out.println("✅ [Utilisateur] READ OK. Login: " + foundUser.getLogin());
            } else {
                System.err.println("❌ [Utilisateur] READ Échec.");
            }
        }

        // --- Sélection Rôle --- (Ajout)
        if (roleTest != null) {
            Role foundRole = roleRepository.findById(roleTest.getIdEntite()).orElse(null);
            if (foundRole != null && foundRole.getLibelle().equals(roleTest.getLibelle())) {
                System.out.println("✅ [Role] READ OK. Libelle: " + foundRole.getLibelle());
            } else {
                System.err.println("❌ [Role] READ Échec.");
            }
        }

        // --- Selection Cabinet (READ) ---
        if (cabinetTest != null) {
            CabinetMedicale foundCabinet = cabinetRepository.findById(cabinetTest.getIdEntite()).orElse(null);
            if (foundCabinet != null && foundCabinet.getNom().equals(cabinetTest.getNom())) {
                System.out.println("✅ [Cabinet] READ OK. Nom: " + foundCabinet.getNom());
            } else {
                System.err.println("❌ [Cabinet] READ Échec.");
            }
        }

        // --- Selection Medecin ---
        if ((module == Module.MEDECIN_ONLY || module == Module.ALL) && medecinTest != null) {
            Medecin foundMedecin = medecinRepository.findById(medecinTest.getIdEntite()).orElse(null);
            if (foundMedecin != null && foundMedecin.getLogin().equals(medecinTest.getLogin())) {
                System.out.println("✅ [Medecin] READ OK. " + foundMedecin.toString());
            } else {
                System.err.println("❌ [Medecin] READ Échec.");
            }
        }

        // --- Sélection Patient ---
        if ((module == Module.PATIENT_ONLY || module == Module.ALL) && patientTest != null) {
            Patient foundPatient = patientRepository.findById(patientTest.getIdEntite()).orElse(null);
            if (foundPatient != null && foundPatient.getTelephone().equals(patientTest.getTelephone())) {
                System.out.println("✅ [Patient] READ OK. " + foundPatient.toString());
            } else {
                System.err.println("❌ [Patient] READ Échec.");
            }
        }

        // --- Sélection Dossier Medicale ---
        // Vérifie si les objets parents existent avant d'essayer de trouver le Dossier
        if ((module == Module.DOSSIER_ONLY || module == Module.ALL) && dossierTest != null && patientTest != null) {
            DossierMedicale foundDossier = dossierRepository.findById(dossierTest.getIdEntite()).orElse(null);
            if (foundDossier != null && foundDossier.getPatient().getIdEntite().equals(patientTest.getIdEntite())) {
                System.out.println("✅ [Dossier] READ OK. ID: " + foundDossier.getIdEntite());
            } else {
                System.err.println("❌ [Dossier] READ Échec.");
            }
        }

        // --- Sélection Consultation ---
        if ((module == Module.CONSULTATION_ONLY || module == Module.ALL) && consultationTest != null && dossierTest != null) {
            Consultation foundConsultation = consultationRepository.findById(consultationTest.getIdEntite()).orElse(null);
            if (foundConsultation != null && foundConsultation.getDossierMedicale().getIdEntite().equals(dossierTest.getIdEntite())) {
                System.out.println("✅ [Consultation] READ OK. ID: " + foundConsultation.getIdEntite());
            } else {
                System.err.println("❌ [Consultation] READ Échec.");
            }
        }


        // --- Sélection Agenda ---
        if ((module == Module.AGENDA_ONLY || module == Module.ALL) && agendaTest != null) {
            AgendaMensuel foundAgenda = agendaRepository.findById(agendaTest.getIdEntite()).orElse(null);
            if (foundAgenda != null && foundAgenda.getMois().equals(AGENDA_MOIS_TEST)) {
                System.out.println("✅ [Agenda] READ OK. " + foundAgenda.toString());
            } else {
                System.err.println("❌ [Agenda] READ Échec.");
            }
        }

        // --- Sélection Acte ---
        if (module == Module.ALL && acteTest != null) {
            Optional<Acte> readActe = acteRepository.findById(acteTest.getIdEntite());
            if (readActe.isPresent() && readActe.get().getLibelle().equals(acteTest.getLibelle())) {
                System.out.printf("✅ [Acte] READ OK. %s%n", readActe.get().toString());
            } else {
                System.err.printf("❌ [Acte] READ Échec. ID: %d non trouvé ou données incorrectes.%n", acteTest.getIdEntite());
            }
        }

        // --- Sélection Notification ---
        if ((module == Module.NOTIFICATION_ONLY || module == Module.ALL) && notificationTest != null && medecinTest != null) {
            Optional<Notification> readNotif = notificationRepository.findById(notificationTest.getIdEntite());

            // Tester une méthode spécifique: trouver les notifications par utilisateur
            List<Notification> notifsForUser = notificationRepository.findByUtilisateurId(medecinTest.getIdEntite());

            if (readNotif.isPresent() && notifsForUser.stream().anyMatch(n -> n.getIdEntite().equals(notificationTest.getIdEntite()))) {
                System.out.println("✅ [Notification] READ OK (findById et findByUtilisateurId). Titre: " + readNotif.get().getTitre());
            } else {
                System.err.println("❌ [Notification] READ Échec.");
            }
        } else if ((module == Module.NOTIFICATION_ONLY || module == Module.ALL)) {
            // Afficher un échec clair si la création a échoué (notificationTest est null)
            System.err.println("❌ [Notification] READ Échec. Objet de test non initialisé (problème CREATE).");
        }
    }

    // =========================================================================
    //                            3. UPDATE
    // =========================================================================

    void updateProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 3. PROCESSUS DE MISE À JOUR (UPDATE) [" + module + "] ===");
        System.out.println("=================================================");

        // DÉFINITION UNIQUE DE LA VARIABLE DE MODIFICATION
        final Long MODIFICATEUR_ID = 2L;

        // --- Mise à jour Utilisateur --- (Ajout)
        if (utilisateurTest != null) {
            Utilisateur u = utilisateurRepository.findById(utilisateurTest.getIdEntite()).orElse(null);
            if (u != null) {
                final String NOUVEAU_PRENOM = "Test Updated";
                u.setPrenom(NOUVEAU_PRENOM);
                u.setModifiePar(MODIFICATEUR_ID);

                try {
                    utilisateurRepository.update(u);
                    Utilisateur updatedU = utilisateurRepository.findById(u.getIdEntite()).orElse(null);
                    if (updatedU != null && NOUVEAU_PRENOM.equals(updatedU.getPrenom())) {
                        System.out.println("✅ [Utilisateur] UPDATE OK. Nouveau Prenom: " + updatedU.getPrenom());
                    } else {
                        System.err.println("❌ [Utilisateur] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Utilisateur] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Cabinet ---
        if (cabinetTest != null) {
            CabinetMedicale c = cabinetRepository.findById(cabinetTest.getIdEntite()).orElse(null);
            if (c != null) {
                final String NOUVEAU_EMAIL = "new.email." + System.currentTimeMillis() + "@cabinet.ma";
                c.setEmail(NOUVEAU_EMAIL);
                c.setModifiePar(MODIFICATEUR_ID);
                // Prévention des NullPointer ou contraintes de non-nullité
                if (c.getNom() == null) c.setNom(cabinetTest.getNom());
                if (c.getCin() == null) c.setCin(cabinetTest.getCin());

                try {
                    cabinetRepository.update(c);
                    CabinetMedicale updatedC = cabinetRepository.findById(c.getIdEntite()).orElse(null);
                    if (updatedC != null && NOUVEAU_EMAIL.equals(updatedC.getEmail())) {
                        System.out.println("✅ [Cabinet] UPDATE OK. Nouvel Email: " + updatedC.getEmail());
                    } else {
                        System.err.println("❌ [Cabinet] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Cabinet] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Medecin ---
        if ((module == Module.MEDECIN_ONLY || module == Module.ALL) && medecinTest != null) {
            Medecin m = medecinRepository.findById(medecinTest.getIdEntite()).orElse(null);
            if (m != null) {
                final String NOUVEAU_NOM = "EL HAJJI UPDATED";
                m.setNom(NOUVEAU_NOM);
                m.setSpecialite("Chirurgie Dentaire");
                m.setModifiePar(MODIFICATEUR_ID);
                // Prévention des NullPointer ou contraintes de non-nullité
                if (m.getPrenom() == null) m.setPrenom(medecinTest.getPrenom());
                if (m.getLogin() == null) m.setLogin(medecinTest.getLogin());

                try {
                    medecinRepository.update(m);
                    Medecin updatedM = medecinRepository.findById(m.getIdEntite()).orElse(null);

                    if (updatedM != null && updatedM.getNom().equals(NOUVEAU_NOM)) {
                        System.out.println("✅ [Medecin] UPDATE OK. " + updatedM.toString());
                    } else {
                        System.err.println("❌ [Medecin] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Medecin] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Patient ---
        if ((module == Module.PATIENT_ONLY || module == Module.ALL) && patientTest != null) {
            Patient p = patientRepository.findById(patientTest.getIdEntite()).orElse(null);
            if (p != null) {
                final String NOUVEAU_ADRESSE = "Nouvelle Adresse Updated 2025";
                p.setAdresse(NOUVEAU_ADRESSE);
                p.setModifiePar(MODIFICATEUR_ID);

                try {
                    patientRepository.update(p);
                    Patient updatedP = patientRepository.findById(p.getIdEntite()).orElse(null);
                    if (updatedP != null && NOUVEAU_ADRESSE.equals(updatedP.getAdresse())) {
                        System.out.println("✅ [Patient] UPDATE OK. " + updatedP.toString());
                    } else {
                        System.err.println("❌ [Patient] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Patient] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Dossier Medicale ---
        if ((module == Module.DOSSIER_ONLY || module == Module.ALL) && dossierTest != null && medecinTest != null) {
            DossierMedicale dm = dossierRepository.findById(dossierTest.getIdEntite()).orElse(null);
            if (dm != null) {
                // Changer le médecin responsable (utiliser le même medecinTest si pas d'autre ID fiable)
                Long newMedecinId = medecinTest.getIdEntite();
                Medecin newMedecin = new Medecin();
                newMedecin.setIdEntite(newMedecinId);
                dm.setMedecin(newMedecin);
                dm.setModifiePar(MODIFICATEUR_ID);

                try {
                    dossierRepository.update(dm);
                    DossierMedicale updatedDm = dossierRepository.findById(dm.getIdEntite()).orElse(null);
                    if (updatedDm != null && updatedDm.getMedecin().getIdEntite().equals(newMedecinId)) {
                        System.out.println("✅ [Dossier] UPDATE OK. Nouveau Medecin ID: " + newMedecinId);
                    } else {
                        System.err.println("❌ [Dossier] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Dossier] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Consultation ---
        if ((module == Module.CONSULTATION_ONLY || module == Module.ALL) && consultationTest != null) {
            Consultation c = consultationRepository.findById(consultationTest.getIdEntite()).orElse(null);
            if (c != null) {
                final String NOUVELLE_OBS = "Nouvelle observation critique post-update.";
                c.setObservationMedecin(NOUVELLE_OBS);
                c.setStatut(StatutConsultation.COMPLETED);
                c.setModifiePar(MODIFICATEUR_ID);

                try {
                    consultationRepository.update(c);
                    Consultation updatedC = consultationRepository.findById(c.getIdEntite()).orElse(null);
                    if (updatedC != null && updatedC.getObservationMedecin().equals(NOUVELLE_OBS)) {
                        System.out.println("✅ [Consultation] UPDATE OK. Statut: " + updatedC.getStatut());
                    } else {
                        System.err.println("❌ [Consultation] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Consultation] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }


        // --- Mise à jour Agenda ---
        if ((module == Module.AGENDA_ONLY || module == Module.ALL) && agendaTest != null) {
            AgendaMensuel a = agendaRepository.findById(agendaTest.getIdEntite()).orElse(null);
            if (a != null) {
                a.setAnnee(AGENDA_ANNEE_TEST + 5); // Changer l'année
                a.setModifiePar(MODIFICATEUR_ID);

                try {
                    agendaRepository.update(a);
                    AgendaMensuel updatedA = agendaRepository.findById(a.getIdEntite()).orElse(null);
                    if (updatedA != null && updatedA.getAnnee() == (AGENDA_ANNEE_TEST + 5)) {
                        System.out.println("✅ [Agenda] UPDATE OK. " + updatedA.toString());
                    } else {
                        System.err.println("❌ [Agenda] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Agenda] UPDATE Échec critique: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Acte ---
        if (module == Module.ALL && acteTest != null) {
            Acte a = acteRepository.findById(acteTest.getIdEntite()).orElse(null);
            if (a != null) {
                final BigDecimal newPrice = new BigDecimal("950.00");
                a.setPrixDeBase(newPrice);
                a.setCategorie("EXAMEN");
                a.setModifiePar(MODIFICATEUR_ID);

                try {
                    acteRepository.update(a);
                    Optional<Acte> updatedActe = acteRepository.findById(a.getIdEntite());
                    if (updatedActe.isPresent() && updatedActe.get().getPrixDeBase().compareTo(newPrice) == 0) {
                        System.out.printf("✅ [Acte] UPDATE OK. %s%n", updatedActe.get().toString());
                    } else {
                        System.err.println("❌ [Acte] UPDATE Échec de vérification.");
                    }
                } catch (Exception e) {
                    System.err.printf("❌ [Acte] UPDATE Échec critique: %s%n", (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
                }
            }
        }

        // --- Mise à jour Notification (Marquer comme lu) ---
        // CORRECTION: Vérifier si notificationTest est initialisé (la création doit avoir réussi)
        if ((module == Module.NOTIFICATION_ONLY || module == Module.ALL) && notificationTest != null && medecinTest != null) {
            try {
                notificationRepository.markAsRead(notificationTest.getIdEntite(), medecinTest.getIdEntite());
                System.out.println("✅ [Notification] UPDATE OK (MarkAsRead). ID: " + notificationTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Notification] UPDATE Échec critique (MarkAsRead): " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        } else if ((module == Module.NOTIFICATION_ONLY || module == Module.ALL)) {
            System.err.println("❌ [Notification] UPDATE Échec critique (MarkAsRead): Objet de test non initialisé (problème CREATE).");
        }
    }

    // =========================================================================
    //                            4. DELETE
    // =========================================================================

    void deleteProcess(Module module) {
        System.out.println("\n=================================================");
        System.out.println("=== 4. PROCESSUS DE SUPPRESSION (DELETE) [" + module + "] ===");
        System.out.println("=================================================");

        // Ordre de suppression : Des dépendances vers l'indépendant (pour les entités avec FK).

        // 1. Suppression Consultation (Dépend de Dossier)
        if ((module == Module.CONSULTATION_ONLY || module == Module.ALL) && consultationTest != null) {
            try {
                consultationRepository.deleteById(consultationTest.getIdEntite());
                System.out.println("✅ [Consultation] DELETE OK. ID: " + consultationTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Consultation] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 2. Suppression Notification (Indépendant, mais bonne pratique de nettoyer tôt)
        if ((module == Module.NOTIFICATION_ONLY || module == Module.ALL) && notificationTest != null) {
            try {
                notificationRepository.deleteById(notificationTest.getIdEntite());
                System.out.println("✅ [Notification] DELETE OK. ID: " + notificationTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Notification] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 3. Suppression Dossier Medicale (Dépend de Patient/Medecin)
        if ((module == Module.DOSSIER_ONLY || module == Module.ALL) && dossierTest != null) {
            try {
                dossierRepository.deleteById(dossierTest.getIdEntite());
                System.out.println("✅ [Dossier] DELETE OK. ID: " + dossierTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Dossier] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 4. Suppression Acte (Indépendant)
        if (module == Module.ALL && acteTest != null) {
            try {
                acteRepository.deleteById(acteTest.getIdEntite());
                System.out.println("✅ [Acte] DELETE OK. ID: " + acteTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Acte] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 5. Suppression Agenda (Dépend de Medecin)
        if ((module == Module.AGENDA_ONLY || module == Module.ALL) && agendaTest != null) {
            try {
                agendaRepository.deleteById(agendaTest.getIdEntite());
                System.out.println("✅ [Agenda] DELETE OK. ID: " + agendaTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Agenda] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 6. Suppression Medecin (Dépend de Cabinet, Utilisateur)
        if ((module == Module.MEDECIN_ONLY || module == Module.ALL) && medecinTest != null) {
            try {
                medecinRepository.deleteById(medecinTest.getIdEntite());
                System.out.println("✅ [Medecin] DELETE OK. ID: " + medecinTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Medecin] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 7. Suppression Patient (Indépendant)
        if ((module == Module.PATIENT_ONLY || module == Module.ALL) && patientTest != null) {
            try {
                patientRepository.deleteById(patientTest.getIdEntite());
                System.out.println("✅ [Patient] DELETE OK. ID: " + patientTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Patient] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 8. Suppression des dépendances Utilisateur/Rôle avant Cabinet.

        // Suppression de l'association Rôle->Utilisateur d'abord (Many-to-Many)
        if (module == Module.ALL && utilisateurTest != null && roleTest != null) {
            try {
                utilisateurRepository.removeRoleFromUtilisateur(utilisateurTest.getIdEntite(), roleTest.getIdEntite());
                System.out.println("✅ [Role] Association REMOVE_ROLE OK.");
            } catch (Exception e) {
                System.err.println("❌ [Role] Association REMOVE_ROLE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // Suppression du Rôle
        if (module == Module.ALL && roleTest != null) {
            try {
                roleRepository.deleteById(roleTest.getIdEntite());
                System.out.println("✅ [Role] DELETE OK. ID: " + roleTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Role] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // Suppression de l'Utilisateur
        if (module == Module.ALL && utilisateurTest != null) {
            try {
                utilisateurRepository.deleteById(utilisateurTest.getIdEntite());
                System.out.println("✅ [Utilisateur] DELETE OK. ID: " + utilisateurTest.getIdEntite());
            } catch (Exception e) {
                System.err.println("❌ [Utilisateur] DELETE Échec: " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }

        // 9. Suppression Cabinet (Dépendance de plus haut niveau)
        if (cabinetTest != null) {
            try {
                cabinetRepository.deleteById(cabinetTest.getIdEntite());
                System.out.println("✅ [Cabinet] DELETE OK. ID: " + cabinetTest.getIdEntite());
            } catch (Exception e) {
                // Cette suppression est critique car elle pourrait dépendre de Medecin, Medecin lui-même dépend de Cabinet.
                System.err.println("❌ [Cabinet] DELETE Échec critique (peut-être des FK non nettoyées): " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
            }
        }
    }
}