package ma.oralCare.repository.userManager;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.repository.DbTestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ma.oralCare.repository.modules.userManager.impl.MedecinRepositoryImpl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de test unitaire pour MedecinRepositoryImpl.
 * Les tests interagissent avec une base de données de test (via DbTestUtils et SessionFactory).
 */
public class MedecinRepositoryImplTest {

    private MedecinRepositoryImpl medecinRepository;

    // Medecin de test pré-inséré pour les tests de recherche et de suppression
    private Medecin testMedecin;

    @BeforeEach
    void setUp() {
        medecinRepository = new MedecinRepositoryImpl();
        DbTestUtils.cleanAll();

        // Insérer un médecin de base pour les tests
        testMedecin = createAndPersistTestMedecin("Dupont", "jean.dupont@oralcare.ma", "12345678", "0600000001", "Orthodontie");
    }

    @AfterEach
    void tearDown() {
        DbTestUtils.cleanAll();
    }

    // --- Méthode utilitaire pour créer et persister un médecin ---
    private Medecin createAndPersistTestMedecin(String nom, String email, String login, String tel, String specialite) {
        Medecin medecin = new Medecin();
        // Attributs Utilisateur
        medecin.setNom(nom);
        medecin.setEmail(email);
        medecin.setCin("CD12345");
        medecin.setTel(tel);
        medecin.setSexe(ma.oralCare.entities.enums.Sexe.MALE);
        medecin.setLogin(login);
        medecin.setMotDePass("pass123");
        medecin.setDateNaissance(LocalDate.of(1980, 1, 1));

        // Attributs Staff
        medecin.setSalaire(50000.0);
        medecin.setPrime(5000.0);
        medecin.setDateRecrutement(LocalDate.now());
        medecin.setSoldeConge(25);

        // Attributs Medecin
        medecin.setSpecialite(specialite);

        // Appel direct à la méthode create pour persister et obtenir l'ID
        medecinRepository.create(medecin);

        return medecin;
    }

    // --------------------------------------
    // 1. Opérations CRUD de base (Simplifié)
    // --------------------------------------

    @Test
    void testFindAll() {
        // Étant donné : Deux médecins sont insérés
        createAndPersistTestMedecin("Smith", "smith@oralCare.ma", "smith_doc", "0600000002", "Parodontie");

        // Quand
        List<Medecin> medecins = medecinRepository.findAll();

        // Alors
        assertEquals(2, medecins.size(), "Il devrait y avoir deux médecins");
    }

    @Test
    void testFindById() {
        // Quand
        Medecin foundMedecin = medecinRepository.findById(testMedecin.getId());
        Medecin notFoundMedecin = medecinRepository.findById(999L);

        // Alors
        assertNotNull(foundMedecin, "Le médecin devrait être trouvé par son ID");
        assertNull(notFoundMedecin, "Aucun médecin ne devrait être trouvé pour un ID inexistant");
    }

    @Test
    void testCreate() {
        // Étant donné
        Medecin newMedecin = new Medecin();
        newMedecin.setNom("Lefevre");
        newMedecin.setLogin("sl_doc");
        newMedecin.setSpecialite("Chirurgie");
        // Les autres attributs doivent être initialisés pour que la méthode create fonctionne.

        // Quand
        medecinRepository.create(newMedecin);

        // Alors
        assertNotNull(newMedecin.getId(), "L'ID du médecin doit être généré après la création");
    }

    @Test
    void testDeleteById() {
        // La méthode est intentionnellement non implémentée, elle doit donc lever une exception
        assertThrows(UnsupportedOperationException.class, () -> medecinRepository.deleteById(testMedecin.getId()),
                "La méthode deleteById doit lever une exception car non implémentée.");
    }

    // ------------------------------------------
    // 2. Méthodes de Recherche Spécifiques (Simplifié)
    // ------------------------------------------

    @Test
    void testFindBySpecialite() {
        // Étant donné : Deux médecins en Orthodontie et un en Pédiatrie
        createAndPersistTestMedecin("Martin", "martin@oralcare.ma", "martin_doc", "0600000004", "Orthodontie");
        createAndPersistTestMedecin("Dubois", "dubois@oralcare.ma", "dubois_doc", "0600000005", "Pédiatrie");

        // Quand
        List<Medecin> orthoMedecins = medecinRepository.findBySpecialite("Orthodontie");
        List<Medecin> nonFound = medecinRepository.findBySpecialite("Généraliste");

        // Alors
        assertEquals(2, orthoMedecins.size(), "Il devrait y avoir 2 médecins en Orthodontie");
        assertTrue(nonFound.isEmpty(), "Il ne devrait y avoir aucun médecin Généraliste");
    }

    @Test
    void testFindByLogin() {
        // Quand
        Optional<Medecin> found = medecinRepository.findByLogin("12345678");
        Optional<Medecin> notFound = medecinRepository.findByLogin("unknown_login");

        // Alors
        assertTrue(found.isPresent(), "Le médecin devrait être trouvé par son login");
        assertTrue(notFound.isEmpty(), "L'Optional devrait être vide pour un login inexistant");
    }

    // ------------------------------------------
    // 3. Gestion des Dossiers Médicaux (Simplifié)
    // ------------------------------------------

    @Test
    void testFindDossiersMedicaux() throws SQLException {
        // Étant donné
        Long medecinId = testMedecin.getId();

        // Simuler l'insertion de dossiers médicaux pour le médecin de test
        insertTestDossier(10L, medecinId);
        insertTestDossier(11L, medecinId);

        // Quand
        List<DossierMedicale> dossiers = medecinRepository.findDossiersMedicauxByMedecinId(medecinId);
        List<DossierMedicale> dossiersNotFound = medecinRepository.findDossiersMedicauxByMedecinId(999L);

        // Alors
        assertEquals(2, dossiers.size(), "Le médecin devrait avoir 2 dossiers médicaux");
        assertTrue(dossiersNotFound.isEmpty(), "Aucun dossier ne devrait être trouvé pour un ID médecin inconnu");
    }

    // Utilitaires pour les dossiers
    private void insertTestDossier(Long id, Long medecinId) throws SQLException {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO DossierMedicale (id_dm, date_de_creation, medecinId) VALUES (?, ?, ?)"
             )) {
            ps.setLong(1, id);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setLong(3, medecinId);
            ps.executeUpdate();
        }
    }


    // --------------------------------------
    // 4. Gestion de l'Agenda (Simplifié)
    // --------------------------------------

    @Test
    void testFindAgendaByMedecinIdAndMois() throws SQLException {
        // Étant donné : Un agenda est inséré
        insertTestAgenda(100L, testMedecin.getId(), Mois.JANUARY, List.of(Jour.TUESDAY));

        // Quand (Existant)
        Optional<AgendaMensuel> found = medecinRepository.findAgendaByMedecinIdAndMois(testMedecin.getId(), Mois.JANUARY);

        // Quand (Non Existant)
        Optional<AgendaMensuel> notFound = medecinRepository.findAgendaByMedecinIdAndMois(testMedecin.getId(), Mois.FEBRUARY);


        // Alors
        assertTrue(found.isPresent(), "L'agenda devrait être trouvé");
        assertTrue(notFound.isEmpty(), "L'Optional devrait être vide pour un agenda inexistant");
    }

    @Test
    void testSaveAgenda() throws SQLException {
        // --- 1. Test de Création ---
        AgendaMensuel newAgenda = AgendaMensuel.builder()
                .mois(Mois.MARCH)
                .joursNonDisponible(List.of(Jour.FRIDAY))
                .medecin(testMedecin)
                .build();

        // Quand (Création)
        medecinRepository.saveAgenda(newAgenda);
        assertNotNull(newAgenda.getId(), "L'ID de l'agenda doit être généré après la création");

        Optional<AgendaMensuel> created = medecinRepository.findAgendaByMedecinIdAndMois(testMedecin.getId(), Mois.MARCH);
        assertTrue(created.isPresent(), "L'agenda créé doit exister.");

        // --- 2. Test de Mise à Jour ---
        AgendaMensuel agendaToUpdate = created.get();
        agendaToUpdate.setJoursNonDisponible(Collections.emptyList());
        agendaToUpdate.setMois(Mois.APRIL); // Changement pour vérifier la mise à jour de la colonne mois

        // Quand (Mise à Jour)
        medecinRepository.saveAgenda(agendaToUpdate);

        // Alors (Vérification Mise à Jour)
        Optional<AgendaMensuel> updated = medecinRepository.findAgendaByMedecinIdAndMois(testMedecin.getId(), Mois.APRIL);
        assertTrue(updated.isPresent(), "L'agenda mis à jour devrait exister sous le nouveau mois (AVRIL)");

        Optional<AgendaMensuel> oldMonth = medecinRepository.findAgendaByMedecinIdAndMois(testMedecin.getId(), Mois.MARCH);
        assertTrue(oldMonth.isEmpty(), "L'ancien mois (MARS) ne devrait plus exister si la mise à jour a réussi.");
    }

    // Utilitaires pour l'agenda
    private void insertTestAgenda(Long id, Long medecinId, Mois mois, List<Jour> joursNonDisponible) throws SQLException {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO AgendaMensuel (id, mois, joursNonDisponible, medecinId) VALUES (?, ?, ?, ?)"
             )) {
            ps.setLong(1, id);
            ps.setString(2, mois.name());
            ps.setString(3, joursNonDisponible.toString());
            ps.setLong(4, medecinId);
            ps.executeUpdate();
        }
    }
}