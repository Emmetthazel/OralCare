package ma.oralCare.repository.modules.agenda.impl;

import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.repository.modules.agenda.api.AgendaMensuelRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// NOTE: RowMappers.mapAgendaMensuel(rs) et RowMappers.mapMedecin(rs) sont supposés exister.
public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // --- Fonctions utilitaires de conversion ---

    /** Convertit List<Jour> en String CSV pour la BDD */
    private String joursListToCsv(List<Jour> jours) {
        if (jours == null || jours.isEmpty()) return "";
        return jours.stream().map(Jour::name).collect(Collectors.joining(","));
    }

    /** Convertit String CSV de la BDD en List<Jour> */
    private List<Jour> csvToJoursList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new ArrayList<>();
        return Arrays.stream(csv.split(","))
                .map(s -> {
                    try {
                        return Jour.valueOf(s.trim());
                    } catch (IllegalArgumentException e) {
                        return null; // Gérer les valeurs invalides
                    }
                })
                .filter(j -> j != null)
                .collect(Collectors.toList());
    }

    // --- Mappeur d'AgendaMensuel (Exemple de base) ---
    // Note: Dans une implémentation réelle, cette logique serait dans RowMappers.
    private AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        // Le mappage complet d'AgendaMensuel nécessite le médecin et les JoursNonDisponible
        String joursCsv = rs.getString("joursNonDisponible");

        // ATTENTION: Le mappage du Medecin doit être fait par un MedecinRepository ou être très basique ici.
        // Par souci de simplicité dans ce Repository (et non un Service), nous ne mappons pas l'objet Medecin complet.

        return AgendaMensuel.builder()
                .id(rs.getLong("id"))
                .mois(Mois.valueOf(rs.getString("mois")))
                .joursNonDisponible(csvToJoursList(joursCsv))
                // Le médecin est omis ou mappé partiellement, car c'est une relation complexe
                // .medecin(RowMappers.mapMedecin(rs)) // Ceci nécessiterait une jointure ou une autre requête.
                .build();
    }


    // --- 1. Opérations CRUD de base ---

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM agendaregister"; // Nom de table supposé: agendaregister
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapAgendaMensuel(rs)); // Utilisation du mapper local
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de tous les agendas", e); }
        return out;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM agendaregister WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAgendaMensuel(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'agenda par ID", e); }
    }

    @Override
    public void create(AgendaMensuel newElement) {
        if (newElement == null || newElement.getMedecin() == null || newElement.getMedecin().getId() == null) {
            throw new IllegalArgumentException("L'agenda, le médecin ou l'ID du médecin ne peut être nul.");
        }

        String sql = "INSERT INTO agendaregister (medecin_id, mois, joursNonDisponible) VALUES (?, ?, ?)";
        String joursCsv = joursListToCsv(newElement.getJoursNonDisponible());

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, newElement.getMedecin().getId()); // Clé étrangère
            ps.setString(2, newElement.getMois().name());
            ps.setString(3, joursCsv);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de l'agenda mensuel", e); }
    }

    @Override
    public void update(AgendaMensuel newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;

        String sql = "UPDATE agendaregister SET mois = ?, joursNonDisponible = ? WHERE id = ?";
        String joursCsv = joursListToCsv(newValuesElement.getJoursNonDisponible());

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getMois().name());
            ps.setString(2, joursCsv);
            ps.setLong(3, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de l'agenda mensuel", e); }
    }

    @Override
    public void delete(AgendaMensuel element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM agendaregister WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de l'agenda mensuel par ID", e); }
    }

    // --- 2. Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<AgendaMensuel> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM agendaregister WHERE medecin_id = ? ORDER BY mois DESC LIMIT 1";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAgendaMensuel(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'agenda par ID Médecin", e); }
    }

    @Override
    public Optional<AgendaMensuel> findByMedecinIdAndMois(Long medecinId, Mois mois) {
        String sql = "SELECT * FROM agendaregister WHERE medecin_id = ? AND mois = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setString(2, mois.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAgendaMensuel(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'agenda par Médecin et Mois", e); }
    }

    // --- 3. Méthodes de Gestion Spécifiques ---

    @Override
    public AgendaMensuel updateJoursNonDisponible(Long agendaId, List<Jour> joursNonDisponible) {
        String joursCsv = joursListToCsv(joursNonDisponible);
        String sql = "UPDATE agendaregister SET joursNonDisponible = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, joursCsv);
            ps.setLong(2, agendaId);
            ps.executeUpdate();

            // Récupérer l'entité mise à jour pour la retourner
            return findById(agendaId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour des jours non disponibles", e); }
    }

    @Override
    public List<Jour> findJoursNonDisponibleByMedecinIdAndMois(Long medecinId, Mois mois) {
        // Simplement, récupère l'agenda et extrait la liste
        Optional<AgendaMensuel> agendaOpt = findByMedecinIdAndMois(medecinId, mois);

        return agendaOpt.map(AgendaMensuel::getJoursNonDisponible)
                .orElseGet(ArrayList::new);
    }
}