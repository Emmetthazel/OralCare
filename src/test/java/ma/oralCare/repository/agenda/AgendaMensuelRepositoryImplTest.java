package ma.oralCare.repository.agenda;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.staff.Medecin;
import ma.oralCare.repository.DbTestUtils;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.repository.modules.agenda.impl.AgendaMensuelRepositoryImpl;
import org.junit.jupiter.api.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.DisplayName.class)
class AgendaMensuelRepositoryImplTest {

    private AgendaMensuelRepository repo;

    private Medecin sampleMedecin;

    @BeforeEach
    void setup() {
        DbTestUtils.cleanAll();
        DbTestUtils.seedFullDataset(); // Si nécessaire, ajouter Medecins et AgendaMensuel

        repo = new AgendaMensuelRepositoryImpl();

        // Exemple : créer un médecin pour les tests
        sampleMedecin = new Medecin();
        sampleMedecin.setId(1L);
        sampleMedecin.setNom("Dr Ali");
        sampleMedecin.setSpecialite("Dentiste");
    }

    @Test
    @DisplayName("1) create : insérer un agenda mensuel")
    void testCreate() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.JANUARY)
                .joursNonDisponible(Arrays.asList(Jour.MONDAY, Jour.WEDNESDAY))
                .build();

        repo.create(agenda);

        assertThat(agenda.getId()).isNotNull();

        AgendaMensuel fromDb = repo.findById(agenda.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getMois()).isEqualTo(Mois.JANUARY);
        assertThat(fromDb.getJoursNonDisponible()).containsExactlyInAnyOrder(Jour.MONDAY, Jour.WEDNESDAY);
    }

    @Test
    @DisplayName("2) findById : récupérer un agenda existant")
    void testFindById() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.FEBRUARY)
                .joursNonDisponible(Arrays.asList(Jour.TUESDAY))
                .build();
        repo.create(agenda);

        AgendaMensuel fetched = repo.findById(agenda.getId());
        assertThat(fetched).isNotNull();
        assertThat(fetched.getId()).isEqualTo(agenda.getId());
    }

    @Test
    @DisplayName("3) update : modifier les jours non disponibles")
    void testUpdate() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.MARCH)
                .joursNonDisponible(Arrays.asList(Jour.FRIDAY))
                .build();
        repo.create(agenda);

        agenda.setJoursNonDisponible(Arrays.asList(Jour.MONDAY, Jour.THURSDAY));
        repo.update(agenda);

        AgendaMensuel updated = repo.findById(agenda.getId());
        assertThat(updated.getJoursNonDisponible()).containsExactlyInAnyOrder(Jour.MONDAY, Jour.THURSDAY);
    }

    @Test
    @DisplayName("4) deleteById : supprimer un agenda")
    void testDeleteById() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.APRIL)
                .joursNonDisponible(Arrays.asList(Jour.SATURDAY))
                .build();
        repo.create(agenda);

        long before = repo.findAll().size();
        repo.deleteById(agenda.getId());

        assertThat(repo.findById(agenda.getId())).isNull();
        assertThat(repo.findAll().size()).isEqualTo(before - 1);
    }

    @Test
    @DisplayName("5) findByMedecinId : récupérer l'agenda le plus récent du médecin")
    void testFindByMedecinId() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.MAY)
                .joursNonDisponible(Arrays.asList(Jour.SUNDAY))
                .build();
        repo.create(agenda);

        Optional<AgendaMensuel> opt = repo.findByMedecinId(sampleMedecin.getId());
        assertThat(opt).isPresent();
        assertThat(opt.get().getId()).isEqualTo(agenda.getId());
    }

    @Test
    @DisplayName("6) findByMedecinIdAndMois : récupérer agenda spécifique")
    void testFindByMedecinIdAndMois() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.JUNE)
                .joursNonDisponible(Arrays.asList(Jour.TUESDAY))
                .build();
        repo.create(agenda);

        Optional<AgendaMensuel> opt = repo.findByMedecinIdAndMois(sampleMedecin.getId(), Mois.JUNE);
        assertThat(opt).isPresent();
        assertThat(opt.get().getMois()).isEqualTo(Mois.JUNE);
    }

    @Test
    @DisplayName("7) updateJoursNonDisponible : modifier jours")
    void testUpdateJoursNonDisponible() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.JULY)
                .joursNonDisponible(Arrays.asList(Jour.MONDAY))
                .build();
        repo.create(agenda);

        List<Jour> newJours = Arrays.asList(Jour.FRIDAY, Jour.SUNDAY);
        AgendaMensuel updated = repo.updateJoursNonDisponible(agenda.getId(), newJours);

        assertThat(updated.getJoursNonDisponible()).containsExactlyInAnyOrderElementsOf(newJours);
    }

    @Test
    @DisplayName("8) findJoursNonDisponibleByMedecinIdAndMois : récupérer jours")
    void testFindJoursNonDisponibleByMedecinIdAndMois() {
        AgendaMensuel agenda = AgendaMensuel.builder()
                .medecin(sampleMedecin)
                .mois(Mois.AUGUST)
                .joursNonDisponible(Arrays.asList(Jour.WEDNESDAY, Jour.THURSDAY))
                .build();
        repo.create(agenda);

        List<Jour> jours = repo.findJoursNonDisponibleByMedecinIdAndMois(sampleMedecin.getId(), Mois.AUGUST);
        assertThat(jours).containsExactlyInAnyOrder(Jour.WEDNESDAY, Jour.THURSDAY);
    }
}
