package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedecinRepositoryImpl implements MedecinRepository {

    // --- Requêtes SQL ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, numero, rue, code_postal, ville, pays, complement) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_STAFF = "INSERT INTO Staff(id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_MEDECIN = "INSERT INTO Medecin(id_entite, specialite) VALUES (?, ?)";

    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_STAFF = "UPDATE Staff SET salaire=?, prime=?, date_recrutement=?, solde_conge=?, cabinet_id=? WHERE id_entite=?";
    private static final String SQL_UPDATE_MEDECIN = "UPDATE Medecin SET specialite=? WHERE id_entite=?";

    private static final String BASE_SELECT_MEDECIN_SQL = """
        SELECT m.specialite, s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_id,
               u.nom, u.prenom, u.email, u.cin, u.tel, u.sexe, u.login, u.mot_de_pass, u.date_naissance, u.last_login_date, u.numero, u.rue, u.code_postal, u.ville, u.pays, u.complement,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Medecin m
        JOIN Staff s ON m.id_entite = s.id_entite
        JOIN utilisateur u ON s.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    // ✅ Constructeur autonome
    public MedecinRepositoryImpl() {
    }

    // =========================================================================
    //                            1. CREATE
    // =========================================================================

    @Override
    public void create(Medecin medecin) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();
        Long generatedId = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, medecin.getCreePar() != null ? medecin.getCreePar() : 1L);
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) generatedId = keys.getLong(1);
                    else throw new SQLException("Échec ID BaseEntity.");
                }
                medecin.setIdEntite(generatedId);
                medecin.setDateCreation(now);
            }

            // 2. Utilisateur
            try (PreparedStatement psUser = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                mapUtilisateurParams(psUser, medecin, generatedId);
                psUser.executeUpdate();
            }

            // 3. Staff
            try (PreparedStatement psStaff = c.prepareStatement(SQL_INSERT_STAFF)) {
                int i = 1;
                psStaff.setLong(i++, generatedId);
                psStaff.setBigDecimal(i++, medecin.getSalaire());
                psStaff.setBigDecimal(i++, medecin.getPrime());
                psStaff.setDate(i++, medecin.getDateRecrutement() != null ? Date.valueOf(medecin.getDateRecrutement()) : null);
                psStaff.setInt(i++, medecin.getSoldeConge());
                psStaff.setObject(i++, medecin.getCabinetMedicale() != null ? medecin.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.executeUpdate();
            }

            // 4. Medecin
            try (PreparedStatement psMedecin = c.prepareStatement(SQL_INSERT_MEDECIN)) {
                psMedecin.setLong(1, generatedId);
                psMedecin.setString(2, medecin.getSpecialite());
                psMedecin.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new RuntimeException("Erreur création Medecin", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    // =========================================================================
    //                            2. UPDATE
    // =========================================================================

    @Override
    public void update(Medecin medecin) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // A. UPDATE UTILISATEUR
            try (PreparedStatement psUser = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUser.setString(i++, medecin.getNom());
                psUser.setString(i++, medecin.getPrenom());
                psUser.setString(i++, medecin.getEmail());
                psUser.setString(i++, medecin.getCin());
                psUser.setString(i++, medecin.getTel());
                psUser.setString(i++, medecin.getSexe() != null ? medecin.getSexe().name() : null);
                psUser.setString(i++, medecin.getLogin());
                psUser.setString(i++, medecin.getMotDePass());
                psUser.setDate(i++, medecin.getDateNaissance() != null ? Date.valueOf(medecin.getDateNaissance()) : null);

                Adresse adr = medecin.getAdresse();
                psUser.setString(i++, adr != null ? adr.getNumero() : null);
                psUser.setString(i++, adr != null ? adr.getRue() : null);
                psUser.setString(i++, adr != null ? adr.getCodePostal() : null);
                psUser.setString(i++, adr != null ? adr.getVille() : null);
                psUser.setString(i++, adr != null ? adr.getPays() : null);
                psUser.setString(i++, adr != null ? adr.getComplement() : null);
                psUser.setLong(i++, medecin.getIdEntite());
                psUser.executeUpdate();
            }

            // B. UPDATE STAFF
            try (PreparedStatement psStaff = c.prepareStatement(SQL_UPDATE_STAFF)) {
                int i = 1;
                psStaff.setBigDecimal(i++, medecin.getSalaire());
                psStaff.setBigDecimal(i++, medecin.getPrime());
                psStaff.setDate(i++, medecin.getDateRecrutement() != null ? Date.valueOf(medecin.getDateRecrutement()) : null);
                psStaff.setInt(i++, medecin.getSoldeConge());
                psStaff.setObject(i++, medecin.getCabinetMedicale() != null ? medecin.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.setLong(i++, medecin.getIdEntite());
                psStaff.executeUpdate();
            }

            // C. UPDATE MEDECIN
            try (PreparedStatement psMedecin = c.prepareStatement(SQL_UPDATE_MEDECIN)) {
                psMedecin.setString(1, medecin.getSpecialite());
                psMedecin.setLong(2, medecin.getIdEntite());
                psMedecin.executeUpdate();
            }

            // D. UPDATE BASEENTITY
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, medecin.getModifiePar() != null ? medecin.getModifiePar() : 1L);
                psBase.setLong(3, medecin.getIdEntite());
                psBase.executeUpdate();
                medecin.setDateDerniereModification(now);
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new RuntimeException("Erreur mise à jour Medecin", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    // =========================================================================
    //                            READ & HELPERS
    // =========================================================================

    private void mapUtilisateurParams(PreparedStatement ps, Medecin m, Long id) throws SQLException {
        int i = 1;
        ps.setLong(i++, id);
        ps.setString(i++, m.getNom());
        ps.setString(i++, m.getPrenom());
        ps.setString(i++, m.getEmail());
        ps.setString(i++, m.getCin());
        ps.setString(i++, m.getTel());
        ps.setString(i++, m.getSexe() != null ? m.getSexe().name() : null);
        ps.setString(i++, m.getLogin());
        ps.setString(i++, m.getMotDePass());
        ps.setDate(i++, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);
        Adresse adr = m.getAdresse();
        ps.setString(i++, adr != null ? adr.getNumero() : null);
        ps.setString(i++, adr != null ? adr.getRue() : null);
        ps.setString(i++, adr != null ? adr.getCodePostal() : null);
        ps.setString(i++, adr != null ? adr.getVille() : null);
        ps.setString(i++, adr != null ? adr.getPays() : null);
        ps.setString(i++, adr != null ? adr.getComplement() : null);
    }

    @Override
    public Optional<Medecin> findById(Long id) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE m.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Medecin> findAll() {
        List<Medecin> list = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT_MEDECIN_SQL)) {
            while (rs.next()) list.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void delete(Medecin m) { if(m != null) deleteById(m.getIdEntite()); }
    @Override public Optional<Medecin> findByLogin(String login) { return findOneBy("u.login", login); }
    @Override public Optional<Medecin> findByCin(String cin) { return findOneBy("u.cin", cin); }

    private Optional<Medecin> findOneBy(String col, String val) {
        String sql = BASE_SELECT_MEDECIN_SQL + " WHERE " + col + " = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, val);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Medecin> findAllBySpecialite(String spec) { /* implémentation similaire */ return new ArrayList<>(); }
    @Override public List<Medecin> findAllByNomContaining(String nom) { /* implémentation similaire */ return new ArrayList<>(); }
}