/*package ma.oralCare.repository.modules.dossierMedical.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.InterventionMedecin;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    public ConsultationRepositoryImpl() {}

    private static final String BASE_SELECT_SQL = """
        SELECT c.*, 
               b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par,
               dm.medecin_id
        FROM Consultation c 
        JOIN BaseEntity b ON c.id_entite = b.id_entite
        JOIN DossierMedicale dm ON c.dossier_medicale_id = dm.id_entite
        """;

    @Override
    public void create(Consultation consultation) {
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                this.create(consultation, connection);
                connection.commit();
                // Pas de return ici
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Consultation create(Consultation consultation, Connection connection) throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        String sqlBase = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
        String sqlConsultation = """
        INSERT INTO Consultation(id_entite, date, statut, observation_medecin, dossier_medicale_id, libelle)
        VALUES(?, ?, ?, ?, ?, ?)
        """;

        // 1. Insertion BaseEntity et récupération de l'ID
        try (PreparedStatement psBase = connection.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
            psBase.setTimestamp(1, Timestamp.valueOf(now));
            psBase.setObject(2, consultation.getCreePar() != null ? consultation.getCreePar() : 1L, Types.BIGINT);
            psBase.executeUpdate();
            try (ResultSet keys = psBase.getGeneratedKeys()) {
                if (keys.next()) {
                    consultation.setIdEntite(keys.getLong(1));
                }
            }
        }

        // 2. Insertion Consultation
        try (PreparedStatement psCons = connection.prepareStatement(sqlConsultation)) {
            psCons.setLong(1, consultation.getIdEntite());
            psCons.setDate(2, consultation.getDate() != null ? Date.valueOf(consultation.getDate()) : Date.valueOf(LocalDate.now()));
            psCons.setString(3, consultation.getStatut() != null ? consultation.getStatut().name() : StatutConsultation.SCHEDULED.name());
            psCons.setString(4, consultation.getObservationMedecin());
            psCons.setLong(5, consultation.getDossierMedicale().getIdEntite());
            psCons.setString(6, consultation.getLibelle());
            psCons.executeUpdate();
        }

        return consultation;
    }
    @Override
    public void update(Consultation consultation) {
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            try {
                update(consultation, connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'update de la Consultation", e);
        }
    }

    public void update(Consultation consultation, Connection connection) throws SQLException {
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlConsultation = """
            UPDATE Consultation SET date=?, statut=?, observation_medecin=?, dossier_medicale_id=?, libelle=?
            WHERE id_entite=?
            """;

        try (PreparedStatement psBase = connection.prepareStatement(sqlBase)) {
            psBase.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            psBase.setObject(2, consultation.getModifiePar() != null ? consultation.getModifiePar() : 1L, Types.BIGINT);
            psBase.setLong(3, consultation.getIdEntite());
            psBase.executeUpdate();
        }

        try (PreparedStatement psCons = connection.prepareStatement(sqlConsultation)) {
            psCons.setDate(1, Date.valueOf(consultation.getDate()));
            psCons.setString(2, consultation.getStatut().name());
            psCons.setString(3, consultation.getObservationMedecin());
            psCons.setLong(4, consultation.getDossierMedicale().getIdEntite());
            psCons.setString(5, consultation.getLibelle());
            psCons.setLong(6, consultation.getIdEntite());
            psCons.executeUpdate();
        }
    }

    // =========================================================================
    // ✅ AUTRES MÉTHODES (findById, findAll, etc.)
    // =========================================================================

    @Override
    public Optional<Consultation> findById(Long id) {
        String sql = BASE_SELECT_SQL + " WHERE c.id_entite = ?";
        List<Consultation> results = executeSelectQuery(sql, id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<Consultation> findAll() {
        return executeSelectQuery(BASE_SELECT_SQL + " ORDER BY c.date DESC");
    }

    private List<Consultation> executeSelectQuery(String sql, Object... params) {
        List<Consultation> out = new ArrayList<>();
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setObject(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public void deleteById(Long id) {
        try (Connection connection = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement("DELETE FROM BaseEntity WHERE id_entite=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Consultation> findByDossierMedicaleId(Long id) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE c.dossier_medicale_id = ?", id); }
    @Override public List<Consultation> findByStatut(StatutConsultation s) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE c.statut = ?", s.name()); }
    @Override public List<Consultation> findByDate(LocalDate d) { return executeSelectQuery(BASE_SELECT_SQL + " WHERE c.date = ?", Date.valueOf(d)); }
    @Override public void updateStatut(Long id, StatutConsultation s) { }
    @Override public void updateObservation(Long id, String obs) { }
    @Override public void addIntervention(Long cid, InterventionMedecin i) { }
    @Override public void delete(Consultation c) { if(c != null) deleteById(c.getIdEntite()); }
}
*/