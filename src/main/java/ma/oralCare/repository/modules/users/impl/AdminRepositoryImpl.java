package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.users.Admin;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements AdminRepository {

    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, last_login_date, numero, rue, code_postal, ville, pays, complement) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_ADMIN = "INSERT INTO Admin(id_entite) VALUES (?)";

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";

    private static final String BASE_SELECT_ADMIN_SQL = """
        SELECT u.*, b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Admin a
        JOIN utilisateur u ON a.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    // ✅ Correction : Constructeur vide pour l'autonomie du Repository
    public AdminRepositoryImpl() {
    }

    @Override
    public void create(Admin admin) {
        LocalDateTime now = LocalDateTime.now();
        Connection c = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Transaction atomique pour les 3 tables

            // 1. Insertion dans BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                if (admin.getCreePar() != null) {
                    psBase.setLong(2, admin.getCreePar());
                } else {
                    psBase.setNull(2, Types.BIGINT);
                }
                psBase.executeUpdate();

                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) admin.setIdEntite(keys.getLong(1));
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                admin.setDateCreation(now);
            }

            // 2. Insertion dans Utilisateur
            try (PreparedStatement psU = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                mapUtilisateurParams(psU, admin);
                psU.executeUpdate();
            }

            // 3. Insertion dans Admin
            try (PreparedStatement psA = c.prepareStatement(SQL_INSERT_ADMIN)) {
                psA.setLong(1, admin.getIdEntite());
                psA.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new RuntimeException("Erreur lors de la création de l'Admin.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    @Override
    public void update(Admin admin) {
        LocalDateTime now = LocalDateTime.now();
        Connection c = null;

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Update BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                if (admin.getModifiePar() != null) {
                    psBase.setLong(2, admin.getModifiePar());
                } else {
                    psBase.setNull(2, Types.BIGINT);
                }
                psBase.setLong(3, admin.getIdEntite());
                psBase.executeUpdate();
                admin.setDateDerniereModification(now);
            }

            // 2. Update Utilisateur
            try (PreparedStatement psU = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psU.setString(i++, admin.getNom());
                psU.setString(i++, admin.getPrenom());
                psU.setString(i++, admin.getEmail());
                psU.setString(i++, admin.getCin());
                psU.setString(i++, admin.getTel());
                psU.setString(i++, admin.getSexe() != null ? admin.getSexe().name() : null);
                psU.setString(i++, admin.getLogin());
                psU.setString(i++, admin.getMotDePass());
                psU.setDate(i++, admin.getDateNaissance() != null ? Date.valueOf(admin.getDateNaissance()) : null);
                psU.setDate(i++, admin.getLastLoginDate() != null ? Date.valueOf(admin.getLastLoginDate()) : null);

                Adresse adr = admin.getAdresse();
                psU.setString(i++, adr != null ? adr.getNumero() : null);
                psU.setString(i++, adr != null ? adr.getRue() : null);
                psU.setString(i++, adr != null ? adr.getCodePostal() : null);
                psU.setString(i++, adr != null ? adr.getVille() : null);
                psU.setString(i++, adr != null ? adr.getPays() : null);
                psU.setString(i++, adr != null ? adr.getComplement() : null);

                psU.setLong(i++, admin.getIdEntite());
                psU.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            throw new RuntimeException("Erreur lors de la mise à jour de l'Admin.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); } }
        }
    }

    @Override
    public Optional<Admin> findById(Long id) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE a.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Admin> findAll() {
        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT_ADMIN_SQL + " ORDER BY u.nom")) {
            while (rs.next()) out.add(RowMappers.mapAdmin(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private void mapUtilisateurParams(PreparedStatement ps, Admin admin) throws SQLException {
        int i = 1;
        ps.setLong(i++, admin.getIdEntite());
        ps.setString(i++, admin.getNom());
        ps.setString(i++, admin.getPrenom());
        ps.setString(i++, admin.getEmail());
        ps.setString(i++, admin.getCin());
        ps.setString(i++, admin.getTel());
        ps.setString(i++, admin.getSexe() != null ? admin.getSexe().name() : null);
        ps.setString(i++, admin.getLogin());
        ps.setString(i++, admin.getMotDePass());
        ps.setDate(i++, admin.getDateNaissance() != null ? Date.valueOf(admin.getDateNaissance()) : null);
        ps.setDate(i++, admin.getLastLoginDate() != null ? Date.valueOf(admin.getLastLoginDate()) : null);

        Adresse adr = admin.getAdresse();
        ps.setString(i++, adr != null ? adr.getNumero() : null);
        ps.setString(i++, adr != null ? adr.getRue() : null);
        ps.setString(i++, adr != null ? adr.getCodePostal() : null);
        ps.setString(i++, adr != null ? adr.getVille() : null);
        ps.setString(i++, adr != null ? adr.getPays() : null);
        ps.setString(i++, adr != null ? adr.getComplement() : null);
    }

    @Override public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void delete(Admin admin) { if(admin != null) deleteById(admin.getIdEntite()); }
    @Override public Optional<Admin> findByLogin(String login) { return findOneBy("u.login", login); }
    @Override public Optional<Admin> findByCin(String cin) { return findOneBy("u.cin", cin); }

    private Optional<Admin> findOneBy(String col, String val) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE " + col + " = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, val);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public List<Admin> findAllByNomContaining(String nom) {
        List<Admin> out = new ArrayList<>();
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAdmin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}