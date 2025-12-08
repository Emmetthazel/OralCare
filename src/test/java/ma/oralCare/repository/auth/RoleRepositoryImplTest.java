package ma.oralCare.repository.auth;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.notification.Role;
import ma.oralCare.repository.modules.auth.impl.RoleRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RoleRepositoryImplTest {

    private RoleRepositoryImpl roleRepository;

    @BeforeAll
    void setup() {
        roleRepository = new RoleRepositoryImpl();

        // Optionnel : nettoyer la table role si besoin
        // DbTestUtils.cleanRoles(); // si tu as une utilitaire pour ça
    }

    @AfterAll
    void teardown() {
        // Optionnel : nettoyer après tests
        // DbTestUtils.cleanRoles();
    }

    @Test
    void testCreateAndFindById() {
        Role role = Role.builder()
                .libelle(RoleLibelle.DOCTOR)
                .privileges(Arrays.asList("CREATE_RDV", "VIEW_RDV"))
                .build();

        roleRepository.create(role);
        assertNotNull(role.getId(), "L'ID du rôle doit être généré");

        Role fetched = roleRepository.findById(role.getId());
        assertNotNull(fetched, "Le rôle récupéré ne doit pas être null");
        assertEquals(RoleLibelle.DOCTOR, fetched.getLibelle());
        assertEquals(2, fetched.getPrivileges().size());
    }

    @Test
    void testUpdate() {
        Role role = Role.builder()
                .libelle(RoleLibelle.SECRETARY)
                .privileges(Arrays.asList("VIEW_RDV"))
                .build();

        roleRepository.create(role);
        role.getPrivileges().add("CREATE_RDV");
        roleRepository.update(role);

        Role updated = roleRepository.findById(role.getId());
        assertEquals(2, updated.getPrivileges().size(), "Les privilèges doivent être mis à jour");
    }

    @Test
    void testDelete() {
        Role role = Role.builder()
                .libelle(RoleLibelle.RECEPTIONIST)
                .privileges(Arrays.asList("VIEW_RDV"))
                .build();

        roleRepository.create(role);
        Long id = role.getId();

        roleRepository.delete(role);
        Role deleted = roleRepository.findById(id);
        assertNull(deleted, "Le rôle doit être supprimé");
    }

    @Test
    void testFindByLibelle() {
        Role role = Role.builder()
                .libelle(RoleLibelle.ADMIN)
                .privileges(Arrays.asList("ALL_PRIVILEGES"))
                .build();

        roleRepository.create(role);

        Optional<Role> fetched = roleRepository.findByLibelle("ADMIN");
        assertTrue(fetched.isPresent(), "Le rôle ADMIN doit être trouvé");
        assertEquals(RoleLibelle.ADMIN, fetched.get().getLibelle());
    }

    @Test
    void testFindByPrivilege() {
        Role role = Role.builder()
                .libelle(RoleLibelle.DOCTOR)
                .privileges(Arrays.asList("CREATE_RDV", "VIEW_RDV"))
                .build();
        roleRepository.create(role);

        List<Role> roles = roleRepository.findByPrivilege("CREATE_RDV");
        assertFalse(roles.isEmpty(), "Au moins un rôle doit contenir le privilège CREATE_RDV");
        assertTrue(roles.stream().anyMatch(r -> r.getLibelle() == RoleLibelle.DOCTOR));
    }

    @Test
    void testAddPrivilegeToRole() {
        Role role = Role.builder()
                .libelle(RoleLibelle.SECRETARY)
                .privileges(Arrays.asList("VIEW_RDV"))
                .build();
        roleRepository.create(role);

        Role updated = roleRepository.addPrivilegeToRole(role.getId(), "CREATE_RDV");
        assertTrue(updated.getPrivileges().contains("CREATE_RDV"), "Le privilège doit être ajouté");
    }

    @Test
    void testRemovePrivilegeFromRole() {
        Role role = Role.builder()
                .libelle(RoleLibelle.RECEPTIONIST)
                .privileges(Arrays.asList("VIEW_RDV", "CREATE_RDV"))
                .build();
        roleRepository.create(role);

        Role updated = roleRepository.removePrivilegeFromRole(role.getId(), "CREATE_RDV");
        assertFalse(updated.getPrivileges().contains("CREATE_RDV"), "Le privilège doit être retiré");
        assertTrue(updated.getPrivileges().contains("VIEW_RDV"));
    }
}
