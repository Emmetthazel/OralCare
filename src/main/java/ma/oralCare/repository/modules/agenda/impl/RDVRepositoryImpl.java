package ma.oralCare.repository.modules.agenda.impl;

import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.common.RowMappers;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// NOTE: RowMappers.mapRDV(rs) est supposé exister.
public class RDVRepositoryImpl implements RDVRepository {

    // --- Mappeur d'RDV (Exemple de base) ---
    // Dans une implémentation réelle, ceci serait plus complexe (récupération des objets liés)
    private RDV mapRDV(ResultSet rs) throws SQLException {
        // Mappage des champs simples
        RDV rdv = RDV.builder()
                .id(rs.getLong("id"))
                .date(rs.getDate("date").toLocalDate())
                .heure(rs.getTime("heure").toLocalTime())
                .motif(rs.getString("motif"))
                .statut(StatutRDV.valueOf(rs.getString("statut")))
                .noteMedecin(rs.getString("noteMedecin"))
                .build();

        // Pour un mapping complet, il faudrait charger consultation et dossierMedicale ici
        // Cependant, dans un Repository JDBC, on se contente souvent du champ principal et des IDs FK.
        return rdv;
    }


    // --- 1. Opérations CRUD de base ---

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM rdv ORDER BY date, heure";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRDV(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de tous les RDV", e); }
        return out;
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM rdv WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRDV(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche du RDV par ID", e); }
    }

    @Override
    public void create(RDV newElement) {
        if (newElement == null || newElement.getDossierMedicale() == null || newElement.getDossierMedicale().getId() == null) {
            throw new IllegalArgumentException("Le RDV ou le Dossier Médical ne peut être nul.");
        }

        // Clés étrangères : dossier_id et consultation_id (peut être null)
        Long dossierId = newElement.getDossierMedicale().getId();
        Long consultationId = newElement.getConsultation() != null ? newElement.getConsultation().getId() : null;

        // Champs supposés : dossier_id, consultation_id, date, heure, motif, statut, noteMedecin
        String sql = "INSERT INTO rdv (dossier_id, consultation_id, date, heure, motif, statut, noteMedecin) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, dossierId);
            if (consultationId != null) {
                ps.setLong(2, consultationId);
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setDate(3, Date.valueOf(newElement.getDate()));
            ps.setTime(4, Time.valueOf(newElement.getHeure()));
            ps.setString(5, newElement.getMotif());
            ps.setString(6, newElement.getStatut().name());
            ps.setString(7, newElement.getNoteMedecin());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création du RDV", e); }
    }

    @Override
    public void update(RDV newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;

        Long dossierId = newValuesElement.getDossierMedicale() != null ? newValuesElement.getDossierMedicale().getId() : null;
        Long consultationId = newValuesElement.getConsultation() != null ? newValuesElement.getConsultation().getId() : null;

        String sql = "UPDATE rdv SET dossier_id = ?, consultation_id = ?, date = ?, heure = ?, motif = ?, statut = ?, noteMedecin = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (dossierId != null) {
                ps.setLong(1, dossierId);
            } else {
                ps.setNull(1, Types.BIGINT);
            }
            if (consultationId != null) {
                ps.setLong(2, consultationId);
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setDate(3, Date.valueOf(newValuesElement.getDate()));
            ps.setTime(4, Time.valueOf(newValuesElement.getHeure()));
            ps.setString(5, newValuesElement.getMotif());
            ps.setString(6, newValuesElement.getStatut().name());
            ps.setString(7, newValuesElement.getNoteMedecin());
            ps.setLong(8, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour du RDV", e); }
    }

    @Override
    public void delete(RDV element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rdv WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression du RDV par ID", e); }
    }

    // --- 2. Méthodes de Recherche Spécifiques ---

    @Override
    public List<RDV> findByDossierMedicaleId(Long dossierMedicaleId) {
        String sql = "SELECT * FROM rdv WHERE dossier_id = ? ORDER BY date DESC, heure DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicaleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des RDV par Dossier Médical", e); }
        return out;
    }

    @Override
    public Optional<RDV> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM rdv WHERE consultation_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRDV(rs));
                // Si rs.next() est faux, on atteint ici et on retourne un Optional vide
                return Optional.empty();
            }
        } catch (SQLException e) {
            // En cas d'erreur SQL, on lance une exception Runtime.
            throw new RuntimeException("Erreur lors de la recherche du RDV par Consultation ID", e);
        }
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        String sql = "SELECT * FROM rdv WHERE date = ? ORDER BY heure ASC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des RDV par date", e); }
        return out;
    }

    @Override
    public boolean existsByDateAndHeureAndMedecinId(LocalDate date, LocalTime heure, Long medecinId) {
        // Logique de jointure pour lier RDV -> DossierMedicale -> Medecin
        // On suppose que la table DossierMedicale a une colonne 'medecin_id'
        String sql = """
            SELECT 1 FROM rdv r 
            JOIN DossierMedicale dm ON r.dossier_id = dm.id 
            WHERE r.date = ? AND r.heure = ? AND dm.medecin_id = ? AND r.statut IN ('PLANIFIE', 'CONFIRME')
            LIMIT 1
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));
            ps.setTime(2, Time.valueOf(heure));
            ps.setLong(3, medecinId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la vérification de l'existence du RDV pour le médecin", e); }
    }

    // --- 3. Méthodes d'Action et de Mise à Jour ---

    @Override
    public RDV updateStatut(Long rdvId, StatutRDV nouveauStatut) {
        String sql = "UPDATE rdv SET statut = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut.name());
            ps.setLong(2, rdvId);
            ps.executeUpdate();

            return findById(rdvId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour du statut du RDV", e); }
    }

    @Override
    public List<RDV> findByStatut(StatutRDV statut) {
        String sql = "SELECT * FROM rdv WHERE statut = ? ORDER BY date ASC, heure ASC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des RDV par statut", e); }
        return out;
    }
}