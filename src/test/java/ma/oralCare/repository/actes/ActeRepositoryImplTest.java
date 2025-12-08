package ma.oralCare.repository.actes;

import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.enums.StatistiqueCategorie;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.actes.api.ActeRepository;
import ma.oralCare.repository.modules.actes.impl.ActeRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class ActeRepositoryImplTest {

    private ActeRepository repo;

    @BeforeEach
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset(); // inclut patients, actes, interventions, etc.

        repo = new ActeRepositoryImpl();
    }

    // =========================
    // ✅ CRUD
    // =========================

    @Test
    @DisplayName("1) findAll : retourne tous les actes")
    void testFindAll() {
        List<Acte> list = repo.findAll();
        assertThat(list).isNotEmpty();
    }

    @Test
    @DisplayName("2) findById : retourne un acte existant")
    void testFindById() {
        Acte acte = repo.findById(1L);
        assertThat(acte).isNotNull();
        assertThat(acte.getLibelle()).isNotBlank();
    }

    @Test
    @DisplayName("3) create : insère un nouvel acte")
    void testCreate() {
        Acte acte = Acte.builder()
                .libelle("Blanchiment")
                .prixDeBase(1200.0)
                .categorie(StatistiqueCategorie.TREATMENT_COUNT.name()) // ⚠️ String
                .build();

        repo.create(acte);

        assertThat(acte.getId()).isNotNull();

        Acte fromDb = repo.findById(acte.getId());
        assertThat(fromDb.getLibelle()).isEqualTo("Blanchiment");
        assertThat(fromDb.getCategorie()).isEqualTo(StatistiqueCategorie.TREATMENT_COUNT.name());
    }

    @Test
    @DisplayName("4) update : modifie le prix")
    void testUpdate() {
        Acte acte = repo.findById(1L);
        acte.setPrixDeBase(999.0);

        repo.update(acte);

        Acte updated = repo.findById(1L);
        assertThat(updated.getPrixDeBase()).isEqualTo(999.0);
    }

    @Test
    @DisplayName("5) deleteById : supprime un acte")
    void testDeleteById() {
        long before = repo.count();

        repo.deleteById(3L);

        assertThat(repo.findById(3L)).isNull();
        assertThat(repo.count()).isEqualTo(before - 1);
    }

    // =========================
    // ✅ RECHERCHES METIER
    // =========================

    @Test
    @DisplayName("6) findByLibelle : recherche par mot-clé")
    void testFindByLibelle() {
        Optional<Acte> acte = repo.findByLibelle("Extraction");

        assertThat(acte).isPresent();
        assertThat(acte.get().getLibelle()).containsIgnoringCase("Extraction");
    }

    @Test
    @DisplayName("7) findByCategorie : filtre par catégorie")
    void testFindByCategorie() {
        List<Acte> list = repo.findByCategorie(StatistiqueCategorie.TREATMENT_COUNT.name());

        assertThat(list).isNotEmpty();
        // ✅ Comparaison avec equals sur String
        assertThat(list).allMatch(a -> a.getCategorie().equals(StatistiqueCategorie.TREATMENT_COUNT.name()));
    }

    // =========================
    // ✅ TECHNIQUE
    // =========================

    @Test
    @DisplayName("8) existsById / count / pagination")
    void testExistsCountPaging() {
        assertThat(repo.existsById(1L)).isTrue();
        assertThat(repo.existsById(999L)).isFalse();

        assertThat(repo.count()).isGreaterThan(0);

        var page = repo.findPage(2, 0);
        assertThat(page).hasSize(2);
    }
}
