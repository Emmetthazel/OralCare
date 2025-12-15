package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.entities.users.Role;
import ma.oralCare.repository.common.RowMappers; // Doit contenir mapUtilisateur et mapRole
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// L'implémentation correspond à l'interface UtilisateurRepository figée sur Utilisateur et Long.
public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    // --- Requêtes SQL (utilisées par cette classe et les classes enfants via composition) ---

    // Insertion
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, last_login_date, numero, rue, code_postal, ville, pays, complement) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    // Mise à jour
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    // Sélection de base
    private static final String BASE_SELECT_UTILISATEUR_SQL = """
        SELECT u.*, b.* FROM utilisateur u
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    // Rôles
    private static final String SQL_SELECT_ROLES = """
        SELECT r.id_entite, r.libelle, rb.date_creation, rb.cree_par, rb.date_derniere_modification, rb.modifie_par
        FROM role r
        JOIN BaseEntity rb ON r.id_entite = rb.id_entite
        JOIN utilisateur_role ur ON r.id_entite = ur.role_id
        WHERE ur.utilisateur_id = ?
        """;
    private static final String SQL_ADD_ROLE = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
    private static final String SQL_REMOVE_ROLE = "DELETE FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";


    // =========================================================================
    //                            CRUD (Création, Mise à jour, Suppression)
    // =========================================================================

    @Override
    public void create(Utilisateur utilisateur) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Démarrage de la transaction

            // 1. Insertion BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, utilisateur.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                utilisateur.setIdEntite(baseId);
                utilisateur.setDateCreation(now);
            }

            // 2. Insertion Utilisateur
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setLong(i++, utilisateur.getIdEntite());
                psUtilisateur.setString(i++, utilisateur.getNom());
                psUtilisateur.setString(i++, utilisateur.getPrenom());
                psUtilisateur.setString(i++, utilisateur.getEmail());
                psUtilisateur.setString(i++, utilisateur.getCin());
                psUtilisateur.setString(i++, utilisateur.getTel());
                psUtilisateur.setString(i++, utilisateur.getSexe() != null ? utilisateur.getSexe().name() : null);
                psUtilisateur.setString(i++, utilisateur.getLogin());
                psUtilisateur.setString(i++, utilisateur.getMotDePass());
                psUtilisateur.setDate(i++, utilisateur.getDateNaissance() != null ? java.sql.Date.valueOf(utilisateur.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, utilisateur.getLastLoginDate() != null ? java.sql.Date.valueOf(utilisateur.getLastLoginDate()) : null);
                // Assurez-vous que l'objet Adresse est non null ou gérez les valeurs null
                psUtilisateur.setString(i++, utilisateur.getAdresse().getNumero());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getRue());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getVille());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getPays());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getComplement());
                psUtilisateur.executeUpdate();
            }

            c.commit(); // Fin de la transaction
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Utilisateur.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création de l'Utilisateur.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void update(Utilisateur utilisateur) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, utilisateur.getModifiePar());
                psBase.setLong(3, utilisateur.getIdEntite());
                psBase.executeUpdate();
                utilisateur.setDateDerniereModification(now);
            }

            // 2. Mise à jour Utilisateur
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setString(i++, utilisateur.getNom());
                psUtilisateur.setString(i++, utilisateur.getPrenom());
                psUtilisateur.setString(i++, utilisateur.getEmail());
                psUtilisateur.setString(i++, utilisateur.getCin());
                psUtilisateur.setString(i++, utilisateur.getTel());
                psUtilisateur.setString(i++, utilisateur.getSexe() != null ? utilisateur.getSexe().name() : null);
                psUtilisateur.setString(i++, utilisateur.getLogin());
                psUtilisateur.setString(i++, utilisateur.getMotDePass());

                // Paramètre 10: date_naissance
                psUtilisateur.setDate(i++, utilisateur.getDateNaissance() != null ? java.sql.Date.valueOf(utilisateur.getDateNaissance()) : null);

                // Paramètre 11: last_login_date
                psUtilisateur.setDate(i++, utilisateur.getLastLoginDate() != null ? java.sql.Date.valueOf(utilisateur.getLastLoginDate()) : null);

                // Adresse (12 à 16)
                psUtilisateur.setString(i++, utilisateur.getAdresse().getNumero());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getRue());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getVille());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getPays());
                psUtilisateur.setString(i++, utilisateur.getAdresse().getComplement());

                // WHERE clause (17)
                psUtilisateur.setLong(i++, utilisateur.getIdEntite());
                psUtilisateur.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Utilisateur.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour de l'Utilisateur.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(Utilisateur utilisateur) {
        this.deleteById(utilisateur.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity entraîne la suppression en CASCADE sur Utilisateur, Staff, etc.
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'Utilisateur.", e);
        }
    }


    // =========================================================================
    //                            READ (Méthodes de Recherche)
    // =========================================================================

    @Override
    public Optional<Utilisateur> findById(Long id) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE u.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                // RowMappers.mapUtilisateur doit mapper le ResultSet à un objet Utilisateur
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Utilisateur.", e);
        }
    }

    @Override
    public List<Utilisateur> findAll() {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " ORDER BY u.nom, u.prenom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll Utilisateur.", e);
        }
        return out;
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE u.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLogin Utilisateur.", e);
        }
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE u.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByEmail Utilisateur.", e);
        }
    }

    @Override
    public Optional<Utilisateur> findByCin(String cin) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE u.cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapUtilisateur(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByCin Utilisateur.", e);
        }
    }

    @Override
    public List<Utilisateur> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ?";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByNomContaining Utilisateur.", e);
        }
        return out;
    }

    @Override
    public List<Utilisateur> findAllByRole(String roleLibelle) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + """
            JOIN utilisateur_role ur ON u.id_entite = ur.utilisateur_id
            JOIN role r ON ur.role_id = r.id_entite
            WHERE r.libelle = ?
            ORDER BY u.nom, u.prenom
            """;
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roleLibelle);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapUtilisateur(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByRole Utilisateur.", e);
        }
        return out;
    }


    // =========================================================================
    //                            Gestion des Rôles
    // =========================================================================

    @Override
    public List<Role> findRolesByUtilisateurId(Long id) {
        List<Role> roles = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_ROLES)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // RowMappers.mapRole doit mapper le ResultSet à un objet Role
                    roles.add(RowMappers.mapRole(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findRolesByUtilisateurId.", e);
        }
        return roles;
    }

    @Override
    public void addRoleToUtilisateur(Long utilisateurId, Long roleId) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_ADD_ROLE)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du rôle à l'utilisateur.", e);
        }
    }

    @Override
    public void removeRoleFromUtilisateur(Long utilisateurId, Long roleId) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_REMOVE_ROLE)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du rôle de l'utilisateur.", e);
        }
    }
}