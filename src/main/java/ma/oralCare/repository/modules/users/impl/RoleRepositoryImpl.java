package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.Role;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {

    // --- Requêtes SQL ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_ROLE = "INSERT INTO role(id_entite, libelle) VALUES (?, ?)";
    private static final String SQL_INSERT_PRIVILEGE = "INSERT INTO role_privileges(role_id, privilege) VALUES (?, ?)";

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_ROLE = "UPDATE role SET libelle=? WHERE id_entite=?";

    private static final String BASE_SELECT_ROLE_SQL = """
        SELECT r.libelle,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM role r
        JOIN BaseEntity b ON r.id_entite = b.id_entite
        """;

    private static final String SQL_SELECT_PRIVILEGES = "SELECT privilege FROM role_privileges WHERE role_id = ?";
    private static final String SQL_REMOVE_PRIVILEGE = "DELETE FROM role_privileges WHERE role_id = ? AND privilege = ?";
    private static final String SQL_DELETE_ROLE_UTILISATEUR = "DELETE FROM utilisateur_role WHERE role_id = ?";

    // ✅ Constructeur autonome (Correction apportée ici)
    public RoleRepositoryImpl() {
    }

    // =========================================================================
    //                            CRUD
    // =========================================================================

    @Override
    public void create(Role role) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, role.getCreePar() != null ? role.getCreePar() : 1L, Types.BIGINT);
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) role.setIdEntite(keys.getLong(1));
                    else throw new SQLException("Échec récupération ID BaseEntity.");
                }
                role.setDateCreation(now);
            }

            // 2. Role
            try (PreparedStatement psRole = c.prepareStatement(SQL_INSERT_ROLE)) {
                psRole.setLong(1, role.getIdEntite());
                psRole.setString(2, role.getLibelle().name());
                psRole.executeUpdate();
            }

            // 3. Privileges
            if (role.getPrivileges() != null && !role.getPrivileges().isEmpty()) {
                insertPrivilegesBatch(c, role.getIdEntite(), role.getPrivileges());
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur création Role", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public void update(Role role) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, role.getModifiePar() != null ? role.getModifiePar() : 1L, Types.BIGINT);
                psBase.setLong(3, role.getIdEntite());
                psBase.executeUpdate();
                role.setDateDerniereModification(now);
            }

            // 2. Role
            try (PreparedStatement psRole = c.prepareStatement(SQL_UPDATE_ROLE)) {
                psRole.setString(1, role.getLibelle().name());
                psRole.setLong(2, role.getIdEntite());
                psRole.executeUpdate();
            }

            // 3. Privileges (Clean and Insert)
            setPrivileges(role.getIdEntite(), role.getPrivileges(), c);

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur mise à jour Role", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public void deleteById(Long id) {
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // Nettoyage des tables dépendantes
            try (PreparedStatement ps1 = c.prepareStatement(SQL_DELETE_ROLE_UTILISATEUR)) { ps1.setLong(1, id); ps1.executeUpdate(); }
            try (PreparedStatement ps2 = c.prepareStatement("DELETE FROM role_privileges WHERE role_id = ?")) { ps2.setLong(1, id); ps2.executeUpdate(); }

            // Suppression physique (Cascade vers la table role)
            try (PreparedStatement ps3 = c.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
                ps3.setLong(1, id);
                ps3.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur suppression Role", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // =========================================================================
    //                            READ
    // =========================================================================

    @Override
    public Optional<Role> findById(Long id) {
        String sql = BASE_SELECT_ROLE_SQL + " WHERE r.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = RowMappers.mapRole(rs);
                    role.setPrivileges(findPrivilegesByRoleId(role.getIdEntite()));
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        String sql = BASE_SELECT_ROLE_SQL + " WHERE r.libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = RowMappers.mapRole(rs);
                    role.setPrivileges(findPrivilegesByRoleId(role.getIdEntite()));
                    return Optional.of(role);
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(BASE_SELECT_ROLE_SQL)) {
            while (rs.next()) {
                Role role = RowMappers.mapRole(rs);
                role.setPrivileges(findPrivilegesByRoleId(role.getIdEntite()));
                roles.add(role);
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return roles;
    }

    // =========================================================================
    //                            PRIVILEGES HELPERS
    // =========================================================================

    @Override
    public List<String> findPrivilegesByRoleId(Long roleId) {
        List<String> privileges = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_PRIVILEGES)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) privileges.add(rs.getString("privilege"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return privileges;
    }

    private void setPrivileges(Long roleId, List<String> privileges, Connection c) throws SQLException {
        try (PreparedStatement psDelete = c.prepareStatement("DELETE FROM role_privileges WHERE role_id=?")) {
            psDelete.setLong(1, roleId);
            psDelete.executeUpdate();
        }
        if (privileges != null && !privileges.isEmpty()) {
            insertPrivilegesBatch(c, roleId, privileges);
        }
    }

    private void insertPrivilegesBatch(Connection c, Long roleId, List<String> privileges) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(SQL_INSERT_PRIVILEGE)) {
            for (String p : privileges) {
                ps.setLong(1, roleId);
                ps.setString(2, p);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override public void delete(Role role) { if(role != null) deleteById(role.getIdEntite()); }
    @Override public void addPrivilegeToRole(Long roleId, String privilege) { /* impl via connection unique */ }
    @Override public void removePrivilegeFromRole(Long roleId, String privilege) { /* impl via connection unique */ }
}