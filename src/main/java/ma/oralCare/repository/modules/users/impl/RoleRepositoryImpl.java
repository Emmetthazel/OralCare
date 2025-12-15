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
    private static final String SQL_DELETE_ROLE_UTILISATEUR = "DELETE FROM utilisateur_role WHERE role_id = ?"; // Nettoyage de la jointure

    // =========================================================================
    //                            CRUD (Création, Mise à jour, Suppression)
    // =========================================================================

    @Override
    public void create(Role role) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, role.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                role.setIdEntite(baseId);
                role.setDateCreation(now);
            }

            // 2. Role
            try (PreparedStatement psRole = c.prepareStatement(SQL_INSERT_ROLE)) {
                psRole.setLong(1, role.getIdEntite());
                psRole.setString(2, role.getLibelle().name());
                psRole.executeUpdate();
            }

            // 3. Privileges (BATCH insertion)
            if (role.getPrivileges() != null && !role.getPrivileges().isEmpty()) {
                try (PreparedStatement psPrivilege = c.prepareStatement(SQL_INSERT_PRIVILEGE)) {
                    for (String privilege : role.getPrivileges()) {
                        psPrivilege.setLong(1, role.getIdEntite());
                        psPrivilege.setString(2, privilege);
                        psPrivilege.addBatch();
                    }
                    psPrivilege.executeBatch();
                }
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Role.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création du Rôle.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
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
                psBase.setLong(2, role.getModifiePar());
                psBase.setLong(3, role.getIdEntite());
                psBase.executeUpdate();
                role.setDateDerniereModification(now);
            }

            // 2. Role (Mise à jour du libellé)
            try (PreparedStatement psRole = c.prepareStatement(SQL_UPDATE_ROLE)) {
                psRole.setString(1, role.getLibelle().name());                psRole.setLong(2, role.getIdEntite());
                psRole.executeUpdate();
            }

            // 3. Privileges : Nous utilisons setPrivileges pour gérer le DELETE ALL + INSERT NEW
            if (role.getPrivileges() != null) {
                setPrivileges(role.getIdEntite(), role.getPrivileges(), c);
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Role.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour du Rôle.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(Role role) {
        if (role != null) deleteById(role.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        Connection c = null;
        String sqlDeletePrivileges = "DELETE FROM role_privileges WHERE role_id = ?";

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Supprimer la jointure utilisateur_role (si non gérée par DB CASCADE)
            try (PreparedStatement psUserRole = c.prepareStatement(SQL_DELETE_ROLE_UTILISATEUR)) {
                psUserRole.setLong(1, id);
                psUserRole.executeUpdate();
            }

            // 2. Supprimer les privilèges (si non géré par DB CASCADE)
            try (PreparedStatement psPrivileges = c.prepareStatement(sqlDeletePrivileges)) {
                psPrivileges.setLong(1, id);
                psPrivileges.executeUpdate();
            }

            // 3. Supprimer BaseEntity (supprime Role en CASCADE)
            String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
            try (PreparedStatement psBase = c.prepareStatement(sql)) {
                psBase.setLong(1, id);
                psBase.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la suppression Role.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la suppression du Rôle.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }


    // =========================================================================
    //                            READ & Recherche
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
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Role.", e);
        }
    }

    @Override
    public List<Role> findAll() {
        String sql = BASE_SELECT_ROLE_SQL + " ORDER BY r.libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Role role = RowMappers.mapRole(rs);
                role.setPrivileges(findPrivilegesByRoleId(role.getIdEntite()));
                out.add(role);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll Role.", e);
        }
        return out;
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
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLibelle Role.", e);
        }
    }

    // =========================================================================
    //                            Gestion des Privilèges (Many-to-Many)
    // =========================================================================

    @Override
    public List<String> findPrivilegesByRoleId(Long roleId) {
        List<String> privileges = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_PRIVILEGES)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    privileges.add(rs.getString("privilege"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des privilèges par ID Role.", e);
        }
        return privileges;
    }

    @Override
    public void addPrivilegeToRole(Long roleId, String privilege) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_INSERT_PRIVILEGE)) {
            ps.setLong(1, roleId);
            ps.setString(2, privilege);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du privilège au rôle.", e);
        }
    }

    @Override
    public void removePrivilegeFromRole(Long roleId, String privilege) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_REMOVE_PRIVILEGE)) {
            ps.setLong(1, roleId);
            ps.setString(2, privilege);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du privilège du rôle.", e);
        }
    }

    /**
     * Méthode privée pour gérer le nettoyage et l'insertion des privilèges dans une transaction existante.
     */
    private void setPrivileges(Long roleId, List<String> privileges, Connection c) throws SQLException {
        // Supprimer tous les privilèges existants
        String sqlDelete = "DELETE FROM role_privileges WHERE role_id=?";
        try (PreparedStatement psDelete = c.prepareStatement(sqlDelete)) {
            psDelete.setLong(1, roleId);
            psDelete.executeUpdate();
        }

        // Insérer la nouvelle liste (BATCH insertion)
        if (privileges != null && !privileges.isEmpty()) {
            try (PreparedStatement psPrivilege = c.prepareStatement(SQL_INSERT_PRIVILEGE)) {
                for (String privilege : privileges) {
                    psPrivilege.setLong(1, roleId);
                    psPrivilege.setString(2, privilege);
                    psPrivilege.addBatch();
                }
                psPrivilege.executeBatch();
            }
        }
    }
}