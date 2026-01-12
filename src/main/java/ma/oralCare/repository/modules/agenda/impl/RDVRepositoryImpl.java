package ma.oralCare.repository.modules.agenda.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVRepositoryImpl implements RDVRepository {

    public RDVRepositoryImpl() {}

    // --- SQL QUERIES ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_DELETE_BASE = "DELETE FROM BaseEntity WHERE id_entite = ?";

    private static final String SQL_INSERT_RDV = """
        INSERT INTO RDV (id_entite, date, heure, motif, statut, note_medecin, consultation_id, dossier_medicale_id) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_UPDATE_RDV = """
        UPDATE RDV SET date=?, heure=?, motif=?, statut=?, note_medecin=?, consultation_id=?, dossier_medicale_id=? 
        WHERE id_entite=?
        """;

    private static final String BASE_SELECT_RDV_SQL = """
        SELECT r.*, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par,
               p.nom as patient_nom, p.prenom as patient_prenom,
               dm.medecin_id
        FROM RDV r
        JOIN BaseEntity b ON r.id_entite = b.id_entite
        JOIN DossierMedicale dm ON r.dossier_medicale_id = dm.id_entite
        JOIN Patient p ON dm.patient_id = p.id_entite
    """;

    // =========================================================================
    // ✅ CRÉATION (Supporte Transaction et Standard)
    // =========================================================================

    @Override
    public void create(RDV rdv) {
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                create(rdv, c);
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RuntimeException("Erreur création RDV", e); }
    }

    public void create(RDV rdv, Connection c) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        // 1. BaseEntity
        try (PreparedStatement psB = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
            psB.setTimestamp(1, Timestamp.valueOf(now));
            psB.setObject(2, rdv.getCreePar(), Types.BIGINT);
            psB.executeUpdate();
            try (ResultSet rs = psB.getGeneratedKeys()) {
                if (rs.next()) rdv.setIdEntite(rs.getLong(1));
            }
        }
        // 2. RDV
        try (PreparedStatement psR = c.prepareStatement(SQL_INSERT_RDV)) {
            psR.setLong(1, rdv.getIdEntite());
            psR.setDate(2, Date.valueOf(rdv.getDate()));
            psR.setTime(3, Time.valueOf(rdv.getHeure()));
            psR.setString(4, rdv.getMotif());
            psR.setString(5, rdv.getStatut().name());
            psR.setString(6, rdv.getNoteMedecin());
            psR.setObject(7, rdv.getConsultation() != null ? rdv.getConsultation().getIdEntite() : null, Types.BIGINT);
            psR.setLong(8, rdv.getDossierMedicale().getIdEntite());
            psR.executeUpdate();
        }
    }

    // =========================================================================
    // ✅ MISE À JOUR (Supporte Transaction et Standard)
    // =========================================================================

    @Override
    public void update(RDV rdv) {
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                update(rdv, c);
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        } catch (SQLException e) { throw new RuntimeException("Erreur update RDV", e); }
    }

    public void update(RDV rdv, Connection c) throws SQLException {
        // 1. BaseEntity update
        try (PreparedStatement psB = c.prepareStatement(SQL_UPDATE_BASE)) {
            psB.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            psB.setObject(2, rdv.getModifiePar(), Types.BIGINT);
            psB.setLong(3, rdv.getIdEntite());
            psB.executeUpdate();
        }
        // 2. RDV update
        try (PreparedStatement psR = c.prepareStatement(SQL_UPDATE_RDV)) {
            psR.setDate(1, Date.valueOf(rdv.getDate()));
            psR.setTime(2, Time.valueOf(rdv.getHeure()));
            psR.setString(3, rdv.getMotif());
            psR.setString(4, rdv.getStatut().name());
            psR.setString(5, rdv.getNoteMedecin());
            psR.setObject(6, rdv.getConsultation() != null ? rdv.getConsultation().getIdEntite() : null, Types.BIGINT);
            psR.setLong(7, rdv.getDossierMedicale().getIdEntite());
            psR.setLong(8, rdv.getIdEntite());
            psR.executeUpdate();
        }
    }

    // =========================================================================
    // ✅ RECHERCHES (READ)
    // =========================================================================

    @Override
    public Optional<RDV> findById(Long id) {
        return executeFindQuery(BASE_SELECT_RDV_SQL + " WHERE r.id_entite = ?", id);
    }

    @Override
    public List<RDV> findByDateAndMedecin(LocalDate date, Long medecinId) {
        return executeFindAllQuery(BASE_SELECT_RDV_SQL + " WHERE r.date = ? AND dm.medecin_id = ? ORDER BY r.heure ASC",
                Date.valueOf(date), medecinId);
    }

    @Override
    public List<RDV> findAll() {
        return executeFindAllQuery(BASE_SELECT_RDV_SQL + " ORDER BY r.date DESC, r.heure ASC");
    }

    @Override
    public RDV updateStatut(Long id, StatutRDV statut) {
        String sql = "UPDATE RDV SET statut = ? WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, statut.name());
            ps.setLong(2, id);
            ps.executeUpdate();
            return findById(id).orElse(null);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_DELETE_BASE)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // =========================================================================
    // ✅ MÉTHODES TECHNIQUES PRIVÉES
    // =========================================================================

    private Optional<RDV> executeFindQuery(String sql, Object... params) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapRDV(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private List<RDV> executeFindAllQuery(String sql, Object... params) {
        List<RDV> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapRDV(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public List<RDV> findByDate(LocalDate d) { return executeFindAllQuery(BASE_SELECT_RDV_SQL + " WHERE r.date=?", Date.valueOf(d)); }
    @Override public List<RDV> findByStatut(StatutRDV s) { return executeFindAllQuery(BASE_SELECT_RDV_SQL + " WHERE r.statut=?", s.name()); }
    @Override public List<RDV> findByDossierMedicaleId(Long id) { return executeFindAllQuery(BASE_SELECT_RDV_SQL + " WHERE r.dossier_medicale_id=?", id); }
    @Override public Optional<RDV> findByConsultationId(Long id) { return executeFindQuery(BASE_SELECT_RDV_SQL + " WHERE r.consultation_id=?", id); }
    @Override public int countByDate(LocalDate d) { return 0; } // À implémenter si besoin
    @Override public int countAll() { return 0; }
    @Override public boolean existsByDateAndHeureAndMedecinId(LocalDate d, LocalTime h, Long m) { return false; }
    @Override public void delete(RDV rdv) { if(rdv != null) deleteById(rdv.getIdEntite()); }
}