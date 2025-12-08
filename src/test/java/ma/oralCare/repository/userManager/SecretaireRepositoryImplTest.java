package ma.oralCare.repository.userManager;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.staff.Secretaire;
import ma.oralCare.repository.DbTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ma.oralCare.repository.modules.userManager.impl.SecretaireRepositoryImpl;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test unitaire pour SecretaireRepositoryImpl.
 * Les tests interagissent avec une base de données de test.
 */
public class SecretaireRepositoryImplTest {

    private SecretaireRepositoryImpl secretaireRepository;
    private Secretaire testSecretaire;
    private Long testMedecinId = 901L; // ID arbitraire pour le médecin de test
    private Long testPatientId = 1L; // Utiliser un ID patient de DbTestUtils

    @BeforeEach
    void setUp() {
        secretaireRepository = new SecretaireRepositoryImpl();
        DbTestUtils.cleanAll();

        // Assurez-vous que le patient 1 et un médecin 901 existent
        seedPrerequisites();

        // 1. Insérer une secrétaire de base pour les tests CRUD
        testSecretaire = createAndPersistTestSecretaire("Chloé", "chloe.dupont@oralcare.ma", "chloe_sec", "123456789", 5.0);
    }

    @AfterEach
    void tearDown() {
        DbTestUtils.cleanAll();
    }

    // --- 0. Méthodes Utilitaire de Seeding Manuelle ---

    /**
     * Simule l'insertion en cascade pour Utilisateur, Staff et Secretaire.
     */
    private Secretaire createAndPersistTestSecretaire(String nom, String email, String login, String numCNSS, double commission) {
        Secretaire secretaire = new Secretaire();
        // Attributs Utilisateur
        secretaire.setNom(nom);
        // Le champ 'prenom' manque dans Utilisateur, nous l'ignorons ici mais il faudrait le corriger
        secretaire.setEmail(email);
        secretaire.setCin("CD12345");
        secretaire.setTel("0600000001");
        secretaire.setSexe(Sexe.FEMALE);
        secretaire.setLogin(login);
        secretaire.setMotDePass("pass123");
        secretaire.setDateNaissance(LocalDate.of(1990, 1, 1));

        // Attributs Staff
        secretaire.setSalaire(30000.0);
        secretaire.setPrime(1000.0);
        secretaire.setDateRecrutement(LocalDate.now());
        secretaire.setSoldeConge(25);

        // Attributs Secretaire
        secretaire.setNumCNSS(numCNSS);
        secretaire.setCommission(commission);

        // Insertion manuelle en cascade pour simuler create() qui n'est que partiellement implémentée
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);

            // 1. Insertion Utilisateur (Simplifiée, en utilisant un ID généré artificiellement ou récupéré)
            // Note: Nous assumons que les tables existent et que l'ID est généré.
            long generatedId = insertUtilisateur(c, nom, email, login);
            secretaire.setId(generatedId);

            // 2. Insertion Staff
            insertStaff(c, generatedId, secretaire.getSalaire());

            // 3. Insertion Secretaire
            insertSecretaire(c, generatedId, numCNSS, commission);

            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de seeding Secrétaire", e);
        }

        return secretaire;
    }

    /** Ajout des données nécessaires pour les tests de RDV et Facture. */
    private void seedPrerequisites() {
        // Ajout d'un patient et son dossier médical
        DbTestUtils.seedPatients();
        DbTestUtils.seedDossiersMedicaux();

        // Ajout d'un médecin pour les tests d'agenda
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            insertUtilisateur(c, "Dr. Med", "med@oral.ma", "drmed");
            insertStaff(c, testMedecinId, 80000.0);
            c.prepareStatement("INSERT INTO medecins (id, specialite) VALUES (?, 'Generaliste')")
                    .setLong(1, testMedecinId);
            // Ajout d'un agenda pour le médecin
            insertTestAgenda(c, 500L, testMedecinId, Mois.DECEMBER.name());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de seeding prérequis", e);
        }

        // Ajout d'un RDV
        insertTestRDV(10L, testPatientId, testMedecinId, LocalDate.now(), StatutRDV.PENDING.name());
        insertTestRDV(11L, testPatientId, testMedecinId, LocalDate.now().minusDays(1), StatutRDV.COMPLETED.name());

        // Ajout d'une Facture (nécessite SituationFinanciere, qui dépend de DossierMedicale)
        insertTestFacture(20L, testPatientId);
    }

    private void insertTestRDV(Long id, Long patientId, Long medecinId, LocalDate date, String statut) {
        // Le DossierMedicaleId du patient 1 est 1 (selon DbTestUtils.seedDossiersMedicaux)
        String sql = "INSERT INTO RDV (id, date, heure, statut, medecinId, dossierMedicaleId) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setDate(2, Date.valueOf(date));
            ps.setTime(3, Time.valueOf(LocalTime.of(10, 0)));
            ps.setString(4, statut);
            ps.setLong(5, medecinId);
            ps.setLong(6, 1L);
            ps.executeUpdate();
        } catch (SQLException e) { /* Ignoré */ }
    }

    private void insertTestAgenda(Connection c, Long id, Long medecinId, String mois) throws SQLException {
        String sql = "INSERT INTO AgendaMensuel (id, mois, joursNonDisponible, medecinId) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, mois);
            ps.setString(3, "[]");
            ps.setLong(4, medecinId);
            ps.executeUpdate();
        }
    }

    private void insertTestFacture(Long id, Long patientId) {
        // Nécessite l'insertion d'une SituationFinanciere liée au DossierMedicale (id=1) du patient (id=1)
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);
            // 1. Insérer SituationFinanciere
            PreparedStatement psSf = c.prepareStatement("INSERT INTO SituationFinanciere (id, totalDu, dossierMedicaleId) VALUES (?, ?, ?)");
            psSf.setLong(1, 100L);
            psSf.setDouble(2, 500.0);
            psSf.setLong(3, 1L); // DossierMedicaleId
            psSf.executeUpdate();

            // 2. Insérer Facture
            PreparedStatement psF = c.prepareStatement("INSERT INTO Facture (id, dateFacture, totaleFacture, totalePaye, reste, situationFinanciereId) VALUES (?, ?, ?, ?, ?, ?)");
            psF.setLong(1, id);
            psF.setDate(2, Date.valueOf(LocalDate.now()));
            psF.setDouble(3, 500.0);
            psF.setDouble(4, 100.0);
            psF.setDouble(5, 400.0);
            psF.setLong(6, 100L); // SituationFinanciereId
            psF.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de seeding Facture", e);
        }
    }

    // Utilitaires d'insertion en cascade
    private long insertUtilisateur(Connection c, String nom, String email, String login) throws SQLException {
        // Utiliser une ID fixe pour le médecin de test ou une ID générée pour les secrétaires
        Long id = "Dr. Med".equals(nom) ? testMedecinId : null;
        String sql = id == null
                ? "INSERT INTO Utilisateur (nom, email, cin, tel, sexe, login, motDePass, dateNaissance) VALUES (?, ?, 'CIN', 'TEL', 'FEMALE', ?, 'PASS', CURDATE())"
                : "INSERT INTO Utilisateur (id, nom, email, cin, tel, sexe, login, motDePass, dateNaissance) VALUES (?, ?, ?, 'CIN', 'TEL', 'FEMALE', ?, 'PASS', CURDATE())";

        try (PreparedStatement ps = id == null ? c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                : c.prepareStatement(sql)) {
            int i = 1;
            if (id != null) ps.setLong(i++, id);
            ps.setString(i++, nom);
            ps.setString(i++, email);
            ps.setString(i++, login);
            ps.executeUpdate();

            if (id == null) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getLong(1);
                }
            }
            return id;
        }
    }

    private void insertStaff(Connection c, Long id, Double salaire) throws SQLException {
        String sql = "INSERT INTO Staff (id, salaire, prime, dateRecrutement, soldeConge) VALUES (?, ?, ?, CURDATE(), 25)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setDouble(2, salaire);
            ps.setDouble(3, 1000.0);
            ps.executeUpdate();
        }
    }

    private void insertSecretaire(Connection c, Long id, String numCNSS, double commission) throws SQLException {
        String sql = "INSERT INTO Secretaire (id, numCNSS, commission) VALUES (?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setString(2, numCNSS);
            ps.setDouble(3, commission);
            ps.executeUpdate();
        }
    }

    // --------------------------------------
    // 1. Opérations CRUD de base
    // --------------------------------------

    @Test
    void testFindAll() {
        // Étant donné : Une secrétaire est déjà insérée dans setUp()
        createAndPersistTestSecretaire("Lea", "lea@oralcare.ma", "lea_sec", "987654321", 10.0);

        // Quand
        List<Secretaire> secretaires = secretaireRepository.findAll();

        // Alors
        assertEquals(2, secretaires.size(), "Il devrait y avoir deux secrétaires");
    }

    @Test
    void testFindById() {
        // Quand
        Secretaire foundSecretaire = secretaireRepository.findById(testSecretaire.getId());
        Secretaire notFoundSecretaire = secretaireRepository.findById(999L);

        // Alors
        assertNotNull(foundSecretaire, "La secrétaire devrait être trouvée par son ID");
        assertEquals(testSecretaire.getId(), foundSecretaire.getId());
        assertEquals("chloe_sec", foundSecretaire.getLogin());
        assertNull(notFoundSecretaire, "Aucune secrétaire ne devrait être trouvée pour un ID inexistant");
    }

    @Test
    void testCreate() {
        // Étant donné
        Secretaire newSecretaire = new Secretaire();
        newSecretaire.setNom("Marc");
        newSecretaire.setEmail("marc.sec@oralcare.ma");
        newSecretaire.setLogin("marc_sec");
        newSecretaire.setNumCNSS("111222333");
        newSecretaire.setCommission(7.5);

        // Simuler les champs manquants requis par la DB (pour que create fonctionne)
        newSecretaire.setCin("C111");
        newSecretaire.setMotDePass("pass");
        newSecretaire.setDateNaissance(LocalDate.now());
        newSecretaire.setSexe(Sexe.MALE);
        newSecretaire.setSalaire(30000.0);
        newSecretaire.setPrime(1000.0);
        newSecretaire.setDateRecrutement(LocalDate.now());
        newSecretaire.setSoldeConge(25);


        // Quand
        // NOTE: La méthode create dans l'implémentation fournie est incomplète (elle suppose l'ID déjà généré).
        // Le test ici ne fonctionnera que si le mécanisme d'ID/cascade est géré par la DB ou un ORM.
        // En l'état, on teste la partie `INSERT INTO Secretaire`
        // Pour un test réaliste, nous devons lui donner un ID qui existe dans Staff et Utilisateur.

        // Pour le test, nous allons simuler un ID déjà inséré dans Staff et Utilisateur.
        // Cependant, l'implémentation fournie nécessite l'ID en entrée, ce qui est inhabituel.
        // On contourne en insérant manuellement Staff/Utilisateur puis en appelant create.

        try (Connection c = SessionFactory.getInstance().getConnection()) {
            // ID arbitraire qui sera le même pour U, S et Sec
            long tempId = 902L;
            insertUtilisateur(c, newSecretaire.getNom(), newSecretaire.getEmail(), newSecretaire.getLogin());
            insertStaff(c, tempId, newSecretaire.getSalaire());
            newSecretaire.setId(tempId);
        } catch (SQLException e) {
            fail("Préparation de l'ID échouée : " + e.getMessage());
        }

        secretaireRepository.create(newSecretaire);

        // Alors
        Secretaire found = secretaireRepository.findById(newSecretaire.getId());
        assertNotNull(found);
        assertEquals("111222333", found.getNumCNSS());
    }

    @Test
    void testUpdate() {
        // Étant donné
        Secretaire secretaireToUpdate = secretaireRepository.findById(testSecretaire.getId());
        secretaireToUpdate.setCommission(12.5);
        secretaireToUpdate.setNumCNSS("999888777");

        // Quand
        secretaireRepository.update(secretaireToUpdate);

        // Alors
        Secretaire updated = secretaireRepository.findById(testSecretaire.getId());
        assertEquals(12.5, updated.getCommission());
        assertEquals("999888777", updated.getNumCNSS());
    }

    @Test
    void testDeleteById() {
        // La méthode est non implémentée et doit lever une exception
        assertThrows(UnsupportedOperationException.class, () -> secretaireRepository.deleteById(testSecretaire.getId()),
                "La méthode deleteById doit lever une exception.");
    }

    // ------------------------------------------
    // 2. Méthodes de Recherche Spécifiques
    // ------------------------------------------

    @Test
    void testFindByLogin() {
        // Quand
        Optional<Secretaire> found = secretaireRepository.findByLogin("chloe_sec");
        Optional<Secretaire> notFound = secretaireRepository.findByLogin("unknown_login");

        // Alors
        assertTrue(found.isPresent(), "La secrétaire devrait être trouvée par son login");
        assertEquals(testSecretaire.getId(), found.get().getId());
        assertTrue(notFound.isEmpty(), "L'Optional devrait être vide pour un login inexistant");
    }

    // ------------------------------------------
    // 3. Gestion des RDV
    // ------------------------------------------

    @Test
    void testFindRDVByDate() {
        // Étant donné : Deux RDV sont insérés pour aujourd'hui (dans seedPrerequisites)

        // Quand
        List<RDV> rdvs = secretaireRepository.findRDVByDate(LocalDate.now());

        // Alors
        assertEquals(1, rdvs.size(), "Il devrait y avoir 1 RDV pour la date d'aujourd'hui");
        assertEquals(StatutRDV.PENDING.name(), rdvs.get(0).getStatut());
    }

    @Test
    void testFindRDVByPatientId() {
        // Étant donné : Deux RDV sont insérés pour le patient 1 (dans seedPrerequisites)

        // Quand
        List<RDV> rdvs = secretaireRepository.findRDVByPatientId(testPatientId);

        // Alors
        assertEquals(2, rdvs.size(), "Il devrait y avoir 2 RDV pour le patient de test");
    }

    @Test
    void testUpdateRDVStatus() {
        // Étant donné
        Long rdvId = 10L;
        String nouveauStatut = StatutRDV.CANCELLED.name();

        // Quand
        secretaireRepository.updateRDVStatus(rdvId, nouveauStatut);

        // Alors
        // Vérification directe dans la DB ou via la méthode findById (non implémentée ici)
        // Vérifions via la DB
        String sql = "SELECT statut FROM RDV WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, rdvId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(nouveauStatut, rs.getString("statut"));
            }
        } catch (SQLException e) {
            fail("Erreur de vérification du statut RDV: " + e.getMessage());
        }
    }

    // ------------------------------------------
    // 4. Gestion de la Caisse/Factures
    // ------------------------------------------

    @Test
    void testFindAllFactures() {
        // Étant donné : Une facture est insérée (dans seedPrerequisites)

        // Quand
        List<Facture> factures = secretaireRepository.findAllFactures();

        // Alors
        assertEquals(1, factures.size(), "Il devrait y avoir une facture totale");
    }

    @Test
    void testFindFacturesByPatientId() {
        // Étant donné : Une facture liée au patient 1 est insérée

        // Quand
        List<Facture> factures = secretaireRepository.findFacturesByPatientId(testPatientId);
        List<Facture> notFound = secretaireRepository.findFacturesByPatientId(999L);

        // Alors
        assertEquals(1, factures.size(), "Il devrait y avoir 1 facture pour le patient de test");
        assertTrue(notFound.isEmpty(), "Aucune facture ne devrait être trouvée pour ce patient");
    }

    @Test
    void testEnregistrerPaiementFacture() {
        // Étant donné
        Long factureId = 20L;
        double montantPaye = 50.0; // Montant initial payé : 100.0, Reste : 400.0

        // Quand
        secretaireRepository.enregistrerPaiementFacture(factureId, montantPaye);

        // Alors
        // Vérification directe dans la DB
        String sql = "SELECT totaleFacture, totalePaye, reste FROM Facture WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, factureId);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next());
                assertEquals(500.0, rs.getDouble("totaleFacture"), 0.001);
                assertEquals(150.0, rs.getDouble("totalePaye"), 0.001); // 100.0 + 50.0
                assertEquals(350.0, rs.getDouble("reste"), 0.001);   // 500.0 - 150.0
            }
        } catch (SQLException e) {
            fail("Erreur de vérification du paiement de la facture: " + e.getMessage());
        }
    }

    // ------------------------------------------
    // 5. Gestion de l'Agenda du Médecin
    // ------------------------------------------

    @Test
    void testFindMedecinAgenda() {
        // Étant donné : Un agenda pour le médecin 901 en DÉCEMBRE est inséré

        // Quand (Existant)
        Optional<AgendaMensuel> found = secretaireRepository.findMedecinAgenda(testMedecinId, Mois.DECEMBER.name());

        // Quand (Non Existant)
        Optional<AgendaMensuel> notFound = secretaireRepository.findMedecinAgenda(testMedecinId, Mois.JANUARY.name());

        // Alors
        assertTrue(found.isPresent(), "L'agenda devrait être trouvé");
        assertEquals(Mois.DECEMBER.name(), found.get().getMois());
        assertTrue(notFound.isEmpty(), "L'Optional devrait être vide");
    }
}