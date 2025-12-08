package ma.oralCare.repository.auth;

import ma.oralCare.entities.common.Adresse;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.notification.Role;
import ma.oralCare.entities.staff.Utilisateur;
import ma.oralCare.repository.modules.auth.impl.RoleRepositoryImpl;
import ma.oralCare.repository.modules.auth.impl.UtilisateurRepositoryImpl;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UtilisateurRepositoryImplTest {

    private UtilisateurRepositoryImpl utilisateurRepository;
    private RoleRepositoryImpl roleRepository;

    private Role testRole;

    @BeforeAll
    void setup() {
        roleRepository = new RoleRepositoryImpl();
        utilisateurRepository = new UtilisateurRepositoryImpl();

        // Créer un rôle test
        testRole = Role.builder()
                .libelle(RoleLibelle.DOCTOR)
                .privileges(Arrays.asList("CREATE_RDV", "VIEW_RDV"))
                .build();
        roleRepository.create(testRole);
    }

    @AfterAll
    void teardown() {
        // Nettoyer les rôles test
        if (testRole.getId() != null) {
            roleRepository.deleteById(testRole.getId());
        }
    }

    @Test
    void testCreateAndFindById() {
        Utilisateur user = Utilisateur.builder()
                .nom("Alice Test")
                .email("alice@test.com")
                .cin("CIN123")
                .tel("0600000000")
                .sexe(Sexe.FEMALE)
                .login("alice")
                .motDePass("password")
                .dateNaissance(LocalDate.of(1990, 1, 1))
                .adresse(new Adresse("12", "Rue Test", "10000", "Casablanca", "Maroc", null))
                .roles(Arrays.asList(testRole))
                .build();

        utilisateurRepository.create(user);
        assertNotNull(user.getId(), "L'ID doit être généré");

        Utilisateur fetched = utilisateurRepository.findById(user.getId());
        assertNotNull(fetched);
        assertEquals("Alice Test", fetched.getNom());
        assertEquals(1, fetched.getRoles().size());
        assertEquals(testRole.getId(), fetched.getRoles().get(0).getId());
    }

    @Test
    void testUpdate() {
        Utilisateur user = utilisateurRepository.findByLogin("alice").orElseThrow();
        user.setNom("Alice Updated");
        utilisateurRepository.update(user);

        Utilisateur updated = utilisateurRepository.findById(user.getId());
        assertEquals("Alice Updated", updated.getNom());
    }

    @Test
    void testDelete() {
        Utilisateur user = utilisateurRepository.findByLogin("alice").orElseThrow();
        Long id = user.getId();
        utilisateurRepository.delete(user);

        Utilisateur deleted = utilisateurRepository.findById(id);
        assertNull(deleted, "L'utilisateur doit être supprimé");
    }

    @Test
    void testFindByLoginAndCin() {
        Utilisateur user = Utilisateur.builder()
                .nom("Bob")
                .email("bob@test.com")
                .cin("CIN456")
                .tel("0600111122")
                .sexe(Sexe.MALE)
                .login("bob")
                .motDePass("secret")
                .dateNaissance(LocalDate.of(1985, 5, 10))
                .adresse(new Adresse("34", "Rue Exemple", "20000", "Rabat", "Maroc", null))
                .roles(Arrays.asList(testRole))
                .build();
        utilisateurRepository.create(user);

        Optional<Utilisateur> byLogin = utilisateurRepository.findByLogin("bob");
        assertTrue(byLogin.isPresent());
        assertEquals("bob", byLogin.get().getLogin());

        Optional<Utilisateur> byCin = utilisateurRepository.findByCin("CIN456");
        assertTrue(byCin.isPresent());
        assertEquals("CIN456", byCin.get().getCin());
    }

    @Test
    void testUpdateMotDePasse() {
        Utilisateur user = utilisateurRepository.findByLogin("bob").orElseThrow();
        utilisateurRepository.updateMotDePasse(user.getId(), "newPassword");

        Utilisateur updated = utilisateurRepository.findById(user.getId());
        assertEquals("newPassword", updated.getMotDePass());
    }

    @Test
    void testAddAndRemoveRole() {
        Utilisateur user = utilisateurRepository.findByLogin("bob").orElseThrow();

        // Créer un nouveau rôle
        Role newRole = Role.builder().libelle(RoleLibelle.SECRETARY).privileges(Arrays.asList("VIEW_RDV")).build();
        roleRepository.create(newRole);

        // Ajouter rôle
        utilisateurRepository.addRoleToUtilisateur(user.getId(), newRole.getId());
        Utilisateur updated = utilisateurRepository.findById(user.getId());
        assertTrue(updated.getRoles().stream().anyMatch(r -> r.getId().equals(newRole.getId())));

        // Supprimer rôle
        utilisateurRepository.removeRoleFromUtilisateur(user.getId(), newRole.getId());
        updated = utilisateurRepository.findById(user.getId());
        assertFalse(updated.getRoles().stream().anyMatch(r -> r.getId().equals(newRole.getId())));

        roleRepository.deleteById(newRole.getId());
    }

    @Test
    void testFindByRoleLibelle() {
        List<Utilisateur> users = utilisateurRepository.findByRoleLibelle("DOCTOR");
        assertFalse(users.isEmpty(), "Doit trouver au moins un utilisateur avec rôle DOCTOR");
    }
}
