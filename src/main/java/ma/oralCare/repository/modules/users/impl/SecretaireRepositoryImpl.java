package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal; // Nécessaire pour les setters de Staff

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // --- Requêtes SQL (Transversales + Spécifiques) ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, last_login_date, numero, rue, code_postal, ville, pays, complement) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_STAFF = "INSERT INTO Staff(id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_SECRETAIRE = "INSERT INTO Secretaire(id_entite, num_cnss, commission) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    private static final String SQL_UPDATE_STAFF = "UPDATE Staff SET salaire=?, prime=?, date_recrutement=?, solde_conge=?, cabinet_id=? WHERE id_entite=?";
    // Pas de SQL_UPDATE_SECRETAIRE nécessaire

    // Jointure complète pour la Secrétaire
    private static final String BASE_SELECT_SECRETAIRE_SQL = """
    SELECT sec.num_cnss, sec.commission, /* Ajout */
           s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_id,
           u.nom, u.prenom, u.email, u.cin, u.tel, u.sexe, u.login, u.mot_de_pass, u.date_naissance, u.last_login_date, u.numero, u.rue, u.code_postal, u.ville, u.pays, u.complement,
           b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
    FROM Secretaire sec
    JOIN Staff s ON sec.id_entite = s.id_entite
    JOIN utilisateur u ON s.id_entite = u.id_entite
    JOIN BaseEntity b ON u.id_entite = b.id_entite
    """;


    // =========================================================================
    //                            CRUD (Création, Mise à jour, Suppression)
    // =========================================================================

    @Override
    public void create(Secretaire secretaire) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Démarrage de la transaction atomique

            // 1. BaseEntity
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, secretaire.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                secretaire.setIdEntite(baseId);
                secretaire.setDateCreation(now);
            }

            // 2. Utilisateur
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setLong(i++, secretaire.getIdEntite());
                psUtilisateur.setString(i++, secretaire.getNom());
                psUtilisateur.setString(i++, secretaire.getPrenom());
                psUtilisateur.setString(i++, secretaire.getEmail());
                psUtilisateur.setString(i++, secretaire.getCin());
                psUtilisateur.setString(i++, secretaire.getTel());
                psUtilisateur.setString(i++, secretaire.getSexe() != null ? secretaire.getSexe().name() : null);
                psUtilisateur.setString(i++, secretaire.getLogin());
                psUtilisateur.setString(i++, secretaire.getMotDePass());
                psUtilisateur.setDate(i++, secretaire.getDateNaissance() != null ? java.sql.Date.valueOf(secretaire.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, secretaire.getLastLoginDate() != null ? java.sql.Date.valueOf(secretaire.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, secretaire.getAdresse().getNumero());
                psUtilisateur.setString(i++, secretaire.getAdresse().getRue());
                psUtilisateur.setString(i++, secretaire.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, secretaire.getAdresse().getVille());
                psUtilisateur.setString(i++, secretaire.getAdresse().getPays());
                psUtilisateur.setString(i++, secretaire.getAdresse().getComplement());
                psUtilisateur.executeUpdate();
            }

            // 3. Staff
            try (PreparedStatement psStaff = c.prepareStatement(SQL_INSERT_STAFF)) {
                int i = 1;
                psStaff.setLong(i++, secretaire.getIdEntite()); // FK
                psStaff.setBigDecimal(i++, secretaire.getSalaire() != null ? secretaire.getSalaire() : BigDecimal.ZERO);
                psStaff.setBigDecimal(i++, secretaire.getPrime() != null ? secretaire.getPrime() : BigDecimal.ZERO);
                psStaff.setDate(i++, secretaire.getDateRecrutement() != null ? java.sql.Date.valueOf(secretaire.getDateRecrutement()) : null);
                psStaff.setInt(i++, secretaire.getSoldeConge() != null ? secretaire.getSoldeConge() : 0);
                psStaff.setObject(i++, secretaire.getCabinetMedicale() != null ? secretaire.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.executeUpdate();
            }

            // 4. Secretaire
            try (PreparedStatement psSecretaire = c.prepareStatement(SQL_INSERT_SECRETAIRE)) {
                psSecretaire.setLong(1, secretaire.getIdEntite()); // FK
                psSecretaire.setString(2, secretaire.getNumCNSS()); // NOUVEAU
                psSecretaire.setBigDecimal(3, secretaire.getCommission()); // NOUVEAU
                psSecretaire.executeUpdate();
            }

            c.commit(); // Succès
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Secretaire.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création de la Secrétaire.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void update(Secretaire secretaire) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity (Mise à jour)
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, secretaire.getModifiePar());
                psBase.setLong(3, secretaire.getIdEntite());
                psBase.executeUpdate();
                secretaire.setDateDerniereModification(now);
            }

            // 2. Utilisateur (Mise à jour)
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                int i = 1;
                psUtilisateur.setString(i++, secretaire.getNom());
                psUtilisateur.setString(i++, secretaire.getPrenom());
                psUtilisateur.setString(i++, secretaire.getEmail());
                psUtilisateur.setString(i++, secretaire.getCin());
                psUtilisateur.setString(i++, secretaire.getTel());
                psUtilisateur.setString(i++, secretaire.getSexe() != null ? secretaire.getSexe().name() : null);
                psUtilisateur.setString(i++, secretaire.getLogin());
                psUtilisateur.setString(i++, secretaire.getMotDePass());
                psUtilisateur.setDate(i++, secretaire.getDateNaissance() != null ? java.sql.Date.valueOf(secretaire.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, secretaire.getLastLoginDate() != null ? java.sql.Date.valueOf(secretaire.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, secretaire.getAdresse().getNumero());
                psUtilisateur.setString(i++, secretaire.getAdresse().getRue());
                psUtilisateur.setString(i++, secretaire.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, secretaire.getAdresse().getVille());
                psUtilisateur.setString(i++, secretaire.getAdresse().getPays());
                psUtilisateur.setString(i++, secretaire.getAdresse().getComplement());
                psUtilisateur.setLong(i++, secretaire.getIdEntite());
                psUtilisateur.executeUpdate();
            }

            // 3. Staff (Mise à jour)
            try (PreparedStatement psStaff = c.prepareStatement(SQL_UPDATE_STAFF)) {
                int i = 1;
                psStaff.setBigDecimal(i++, secretaire.getSalaire());
                psStaff.setBigDecimal(i++, secretaire.getPrime());
                psStaff.setDate(i++, secretaire.getDateRecrutement() != null ? java.sql.Date.valueOf(secretaire.getDateRecrutement()) : null);
                psStaff.setInt(i++, secretaire.getSoldeConge() != null ? secretaire.getSoldeConge() : 0);
                psStaff.setObject(i++, secretaire.getCabinetMedicale() != null ? secretaire.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.setLong(i++, secretaire.getIdEntite());
                psStaff.executeUpdate();
            }

            // 4. Pas de mise à jour pour Secretaire

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Secretaire.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour de la Secrétaire.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(Secretaire secretaire) {
        if (secretaire != null) deleteById(secretaire.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity supprime tout en cascade (Secretaire <- Staff <- Utilisateur)
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la Secrétaire.", e);
        }
    }


    // =========================================================================
    //                            READ & Méthodes de Recherche
    // =========================================================================

    @Override
    public Optional<Secretaire> findById(Long id) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE sec.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                // RowMappers.mapSecretaire doit inclure le mapping de toutes les propriétés (Staff, Utilisateur)
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Secretaire.", e);
        }
    }

    @Override
    public List<Secretaire> findAll() {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " ORDER BY u.nom, u.prenom";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapSecretaire(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll Secretaire.", e);
        }
        return out;
    }

    @Override
    public Optional<Secretaire> findByLogin(String login) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE u.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLogin Secretaire.", e);
        }
    }

    @Override
    public Optional<Secretaire> findByCin(String cin) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE u.cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByCin Secretaire.", e);
        }
    }

    @Override
    public List<Secretaire> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ?";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapSecretaire(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByNomContaining Secretaire.", e);
        }
        return out;
    }

    @Override
    public List<Secretaire> findAllByCabinetId(Long cabinetId) {
        String sql = BASE_SELECT_SECRETAIRE_SQL + " WHERE s.cabinet_id = ?";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapSecretaire(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByCabinetId Secretaire.", e);
        }
        return out;
    }
}