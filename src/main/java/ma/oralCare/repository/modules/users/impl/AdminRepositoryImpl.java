package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
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
    private static final String SQL_INSERT_ADMIN = "INSERT INTO Admin(id_entite) VALUES (?)"; // Specific Admin (just ID)

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    // Pas de SQL_UPDATE_ADMIN nécessaire car la table Admin n'a pas de colonnes modifiables (que l'ID)

    // Jointure complète pour l'Admin
    private static final String BASE_SELECT_ADMIN_SQL = """
        SELECT u.nom, u.prenom, u.email, u.cin, u.tel, u.sexe, u.login, u.mot_de_pass, u.date_naissance, u.last_login_date, u.numero, u.rue, u.code_postal, u.ville, u.pays, u.complement,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Admin a
        JOIN utilisateur u ON a.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;


    // =========================================================================
    //                            CRUD (Création, Mise à jour, Suppression)
    // =========================================================================

    @Override
    public void create(Admin admin) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Démarrage de la transaction atomique

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, admin.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                admin.setIdEntite(baseId);
                admin.setDateCreation(now);
            }

            // 2. Utilisateur
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setLong(i++, admin.getIdEntite());
                psUtilisateur.setString(i++, admin.getNom());
                psUtilisateur.setString(i++, admin.getPrenom());
                psUtilisateur.setString(i++, admin.getEmail());
                psUtilisateur.setString(i++, admin.getCin());
                psUtilisateur.setString(i++, admin.getTel());
                psUtilisateur.setString(i++, admin.getSexe() != null ? admin.getSexe().name() : null);
                psUtilisateur.setString(i++, admin.getLogin());
                psUtilisateur.setString(i++, admin.getMotDePass());
                psUtilisateur.setDate(i++, admin.getDateNaissance() != null ? java.sql.Date.valueOf(admin.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, admin.getLastLoginDate() != null ? java.sql.Date.valueOf(admin.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, admin.getAdresse().getNumero());
                psUtilisateur.setString(i++, admin.getAdresse().getRue());
                psUtilisateur.setString(i++, admin.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, admin.getAdresse().getVille());
                psUtilisateur.setString(i++, admin.getAdresse().getPays());
                psUtilisateur.setString(i++, admin.getAdresse().getComplement());
                psUtilisateur.executeUpdate();
            }

            // 3. Admin
            try (PreparedStatement psAdmin = c.prepareStatement(SQL_INSERT_ADMIN)) {
                psAdmin.setLong(1, admin.getIdEntite()); // FK
                psAdmin.executeUpdate();
            }

            c.commit(); // Succès
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Admin.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création de l'Admin.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void update(Admin admin) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity (Mise à jour)
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, admin.getModifiePar());
                psBase.setLong(3, admin.getIdEntite());
                psBase.executeUpdate();
                admin.setDateDerniereModification(now);
            }

            // 2. Utilisateur (Mise à jour)
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setString(i++, admin.getNom());
                psUtilisateur.setString(i++, admin.getPrenom());
                psUtilisateur.setString(i++, admin.getEmail());
                psUtilisateur.setString(i++, admin.getCin());
                psUtilisateur.setString(i++, admin.getTel());
                psUtilisateur.setString(i++, admin.getSexe() != null ? admin.getSexe().name() : null);
                psUtilisateur.setString(i++, admin.getLogin());
                psUtilisateur.setString(i++, admin.getMotDePass());
                psUtilisateur.setDate(i++, admin.getDateNaissance() != null ? java.sql.Date.valueOf(admin.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, admin.getLastLoginDate() != null ? java.sql.Date.valueOf(admin.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, admin.getAdresse().getNumero());
                psUtilisateur.setString(i++, admin.getAdresse().getRue());
                psUtilisateur.setString(i++, admin.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, admin.getAdresse().getVille());
                psUtilisateur.setString(i++, admin.getAdresse().getPays());
                psUtilisateur.setString(i++, admin.getAdresse().getComplement());
                psUtilisateur.setLong(i++, admin.getIdEntite());
                psUtilisateur.executeUpdate();
            }

            // 3. Pas de mise à jour pour Admin (pas de colonnes)

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Admin.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour de l'Admin.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(Admin admin) {
        if (admin != null) deleteById(admin.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity supprime tout en cascade (Admin <- Utilisateur)
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'Admin.", e);
        }
    }


    // =========================================================================
    //                            READ & Méthodes de Recherche
    // =========================================================================

    @Override
    public Optional<Admin> findById(Long id) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE a.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                // RowMappers.mapAdmin doit mapper les colonnes Utilisateur et BaseEntity
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Admin.", e);
        }
    }

    @Override
    public List<Admin> findAll() {
        String sql = BASE_SELECT_ADMIN_SQL + " ORDER BY u.nom, u.prenom";
        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapAdmin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll Admin.", e);
        }
        return out;
    }

    @Override
    public Optional<Admin> findByLogin(String login) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE u.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLogin Admin.", e);
        }
    }

    @Override
    public Optional<Admin> findByCin(String cin) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE u.cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByCin Admin.", e);
        }
    }

    @Override
    public List<Admin> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_ADMIN_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ?";
        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapAdmin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByNomContaining Admin.", e);
        }
        return out;
    }
}