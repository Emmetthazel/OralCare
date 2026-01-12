package ma.oralCare.repository.modules.patient.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.patient.*;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryImpl implements PatientRepository {

    // ✅ ÉTAPE 1 : Constructeur par défaut (plus d'injection de connection)
    public PatientRepositoryImpl() {}

    private static final String BASE_SELECT = """
        SELECT p.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par 
        FROM Patient p 
        JOIN BaseEntity b ON p.id_entite = b.id_entite
    """;

    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM Patient";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur comptage patients", e);
        }
    }

    @Override
    public void create(Patient p) {
        LocalDateTime now = LocalDateTime.now();
        String sqlBase = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
        String sqlPatient = """
            INSERT INTO Patient(id_entite, nom, prenom, date_de_naissance, email, sexe, adresse, telephone, assurance)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psBase = conn.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(now));
                    psBase.setObject(2, p.getCreePar() != null ? p.getCreePar() : 1L, Types.BIGINT);
                    psBase.executeUpdate();
                    try (ResultSet keys = psBase.getGeneratedKeys()) {
                        if (keys.next()) p.setIdEntite(keys.getLong(1));
                    }
                }
                try (PreparedStatement psPatient = conn.prepareStatement(sqlPatient)) {
                    psPatient.setLong(1, p.getIdEntite());
                    psPatient.setString(2, p.getNom());
                    psPatient.setString(3, p.getPrenom());
                    psPatient.setDate(4, p.getDateDeNaissance() != null ? Date.valueOf(p.getDateDeNaissance()) : null);
                    psPatient.setString(5, p.getEmail());
                    psPatient.setString(6, p.getSexe() != null ? p.getSexe().name() : null);
                    psPatient.setString(7, p.getAdresse());
                    psPatient.setString(8, p.getTelephone());
                    psPatient.setString(9, p.getAssurance() != null ? p.getAssurance().name() : null);
                    psPatient.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur création patient", e);
        }
    }

    @Override
    public void update(Patient p) {
        LocalDateTime now = LocalDateTime.now();
        String sqlBase = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
        String sqlPatient = "UPDATE Patient SET nom=?, prenom=?, date_de_naissance=?, email=?, sexe=?, adresse=?, telephone=?, assurance=? WHERE id_entite=?";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement psBase = conn.prepareStatement(sqlBase)) {
                    psBase.setTimestamp(1, Timestamp.valueOf(now));
                    psBase.setObject(2, p.getModifiePar() != null ? p.getModifiePar() : 1L, Types.BIGINT);
                    psBase.setLong(3, p.getIdEntite());
                    psBase.executeUpdate();
                }
                try (PreparedStatement psPatient = conn.prepareStatement(sqlPatient)) {
                    psPatient.setString(1, p.getNom());
                    psPatient.setString(2, p.getPrenom());
                    psPatient.setDate(3, p.getDateDeNaissance() != null ? Date.valueOf(p.getDateDeNaissance()) : null);
                    psPatient.setString(4, p.getEmail());
                    psPatient.setString(5, p.getSexe() != null ? p.getSexe().name() : null);
                    psPatient.setString(6, p.getAdresse());
                    psPatient.setString(7, p.getTelephone());
                    psPatient.setString(8, p.getAssurance() != null ? p.getAssurance().name() : null);
                    psPatient.setLong(9, p.getIdEntite());
                    psPatient.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur update patient", e);
        }
    }

    @Override
    public Optional<Patient> findById(Long id) {
        String sql = BASE_SELECT + " WHERE p.id_entite = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Patient> findAll() {
        String sql = BASE_SELECT + " ORDER BY p.nom, p.prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapPatient(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public void deleteById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = BASE_SELECT + " WHERE p.nom LIKE ? OR p.prenom LIKE ? ORDER BY p.nom, p.prenom";
        List<Patient> out = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par 
            FROM Antecedent a 
            JOIN Patient_Antecedent pa ON pa.antecedent_id = a.id_entite
            JOIN BaseEntity b ON a.id_entite = b.id_entite 
            WHERE pa.patient_id = ?
            ORDER BY a.nom
        """;
        List<Antecedent> out = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return executeSingleResultQuery(BASE_SELECT + " WHERE p.email = ?", email);
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        return executeSingleResultQuery(BASE_SELECT + " WHERE p.telephone = ?", telephone);
    }

    private Optional<Patient> executeSingleResultQuery(String sql, String param) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override public boolean existsById(Long id) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM Patient WHERE id_entite=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public long count() { return countAll(); }
    @Override public void delete(Patient p) { if (p != null) deleteById(p.getIdEntite()); }

    @Override public List<Patient> findPage(int limit, int offset) {
        String sql = BASE_SELECT + " ORDER BY p.id_entite LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit); ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override public void addAntecedentToPatient(Long pId, Long aId) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO Patient_Antecedent(patient_id, antecedent_id) VALUES (?,?)")) {
            ps.setLong(1, pId); ps.setLong(2, aId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void removeAntecedentFromPatient(Long pId, Long aId) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Patient_Antecedent WHERE patient_id=? AND antecedent_id=?")) {
            ps.setLong(1, pId); ps.setLong(2, aId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void removeAllAntecedentsFromPatient(Long pId) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM Patient_Antecedent WHERE patient_id=?")) {
            ps.setLong(1, pId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Patient> getPatientsByAntecedent(Long aId) {
        String sql = BASE_SELECT + " JOIN Patient_Antecedent pa ON pa.patient_id = p.id_entite WHERE pa.antecedent_id = ?";
        List<Patient> out = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, aId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}