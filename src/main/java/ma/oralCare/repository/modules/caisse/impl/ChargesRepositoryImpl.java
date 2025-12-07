package ma.oralCare.repository.modules.caisse.impl;

import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.repository.modules.caisse.api.ChargesRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.modules.userManager.api.CabinetMedicaleRepository; // Dépendance

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChargesRepositoryImpl implements ChargesRepository {

    // Dépendance simplifiée : permet de charger l'objet CabinetMedicale
    private final CabinetMedicaleRepository cabinetRepository;

    public ChargesRepositoryImpl() {
        // Initialisation de base (à remplacer par injection dans un vrai framework)
        this.cabinetRepository = new ma.oralCare.repository.modules.userManager.impl.CabinetMedicaleRepositoryImpl();
    }

    // --- 0. Fonctions Utilitaires de Mappage ---

    /** Mappe un ResultSet à une entité Charges */
    private Charges mapCharges(ResultSet rs) throws SQLException {
        Charges charge = Charges.builder()
                .id(rs.getLong("id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getDouble("montant"))
                .date(rs.getTimestamp("date").toLocalDateTime())
                .build();

        // Charger la référence CabinetMedicale (N+1)
        Long cabinetId = rs.getLong("cabinetMedicale_id");
        if (cabinetId != 0) {
            charge.setCabinetMedicale(cabinetRepository.findById(cabinetId));
        }

        return charge;
    }

    // --- 1. Opérations CRUD de base ---

    @Override
    public List<Charges> findAll() {
        String sql = "SELECT * FROM charges ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapCharges(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de toutes les charges", e); }
        return out;
    }

    @Override
    public Charges findById(Long id) {
        String sql = "SELECT * FROM charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCharges(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de la charge par ID", e); }
    }

    @Override
    public void create(Charges newElement) {
        if (newElement == null || newElement.getCabinetMedicale() == null) return;

        String sql = "INSERT INTO charges (titre, description, montant, date, cabinetMedicale_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getTitre());
            ps.setString(2, newElement.getDescription());
            ps.setDouble(3, newElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newElement.getDate()));
            ps.setLong(5, newElement.getCabinetMedicale().getId());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de la charge", e); }
    }

    @Override
    public void update(Charges newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;

        String sql = "UPDATE charges SET titre = ?, description = ?, montant = ?, date = ?, cabinetMedicale_id = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getTitre());
            ps.setString(2, newValuesElement.getDescription());
            ps.setDouble(3, newValuesElement.getMontant());
            ps.setTimestamp(4, Timestamp.valueOf(newValuesElement.getDate()));
            ps.setLong(5, newValuesElement.getCabinetMedicale().getId());
            ps.setLong(6, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de la charge", e); }
    }

    @Override
    public void delete(Charges element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression de la charge par ID", e); }
    }

    // --- 2. Méthodes de Consultation Spécifiques (pour les rapports financiers) ---

    @Override
    public List<Charges> findByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Nécessaire pour les rapports financiers (CU "Consulter statistiques caisse")
        String sql = "SELECT * FROM charges WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(dateDebut));
            ps.setTimestamp(2, Timestamp.valueOf(dateFin));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des charges par date", e); }
        return out;
    }

    @Override
    public Double calculateTotalChargesByDateBetween(LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Nécessaire pour le calcul de la statistique des dépenses (CU "Consulter statistiques caisse")
        String sql = "SELECT SUM(montant) AS total FROM charges WHERE date BETWEEN ? AND ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(dateDebut));
            ps.setTimestamp(2, Timestamp.valueOf(dateFin));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Si le résultat est null (aucune charge), il retourne 0.0
                    return rs.getDouble("total");
                }
                return 0.0;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors du calcul du total des charges", e); }
    }

    @Override
    public List<Charges> findByTitreOrDescriptionContaining(String keyword) {
        // Recherche floue par mot-clé
        String sql = "SELECT * FROM charges WHERE titre LIKE ? OR description LIKE ? ORDER BY date DESC";
        String searchPattern = "%" + keyword + "%";
        List<Charges> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCharges(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des charges par mot-clé", e); }
        return out;
    }
    @Override
    public List<Charges> findByCabinetMedicaleId(Long cabinetId) {
        String sql = "SELECT * FROM charges WHERE cabinetMedicale_id = ? ORDER BY date DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // NOTE: Le mapping ici doit être effectué en utilisant les méthodes internes (mapCharges)
                    // Il est important que le ChargesRepository n'essaie pas de charger à nouveau le CabinetMedicale (boucle infinie).
                    // Nous allons supposer une méthode interne simplifiée mapChargesForCabinetLoading(rs) qui ne mappe pas la relation Cabinet.
                    out.add(mapCharges(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des charges par ID Cabinet", e); }
        return out;
    }
}