package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.Staff;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StaffRepositoryImpl implements StaffRepository {

    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
    private static final String SQL_INSERT_UTILISATEUR = "INSERT INTO utilisateur(id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, date_naissance, last_login_date, numero, rue, code_postal, ville, pays, complement) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_STAFF = "INSERT INTO Staff(id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";
    private static final String SQL_UPDATE_UTILISATEUR = "UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?";
    private static final String SQL_UPDATE_STAFF = "UPDATE Staff SET salaire=?, prime=?, date_recrutement=?, solde_conge=?, cabinet_id=? WHERE id_entite=?";

    // Jointure pour le Staff - CORRECTION APPLIQUÉE ICI
    private static final String BASE_SELECT_STAFF_SQL = """
        SELECT s.salaire, s.prime, s.date_recrutement, s.solde_conge, s.cabinet_id,
               u.nom, u.prenom, u.email, u.cin, u.tel, u.sexe, u.login, u.mot_de_pass, u.date_naissance, u.last_login_date, u.numero, u.rue, u.code_postal, u.ville, u.pays, u.complement,
               b.id_entite, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par
        FROM Staff s
        JOIN utilisateur u ON s.id_entite = u.id_entite
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """; // FIN DE LA CORRECTION

    private final Connection connection;
    public StaffRepositoryImpl(Connection connection) {
        this.connection = connection;
    }
    @Override
    public void create(Staff staff) {
        Long baseId = null;
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Démarrage de la transaction atomique

            // 1. BaseEntity (Insertion)
            try (PreparedStatement psBase = c.prepareStatement(SQL_INSERT_BASE, Statement.RETURN_GENERATED_KEYS)) {
                // ... (Remplissage BaseEntity)
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, staff.getCreePar());
                psBase.executeUpdate();
                try (ResultSet keys = psBase.getGeneratedKeys()) {
                    if (keys.next()) baseId = keys.getLong(1);
                    else throw new SQLException("Échec de la récupération de l'ID BaseEntity.");
                }
                staff.setIdEntite(baseId);
                staff.setDateCreation(now);
            }

            // 2. Utilisateur (Insertion)
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                // ... (Remplissage Utilisateur)
                int i = 1;
                psUtilisateur.setLong(i++, staff.getIdEntite());
                psUtilisateur.setString(i++, staff.getNom());
                psUtilisateur.setString(i++, staff.getPrenom());
                psUtilisateur.setString(i++, staff.getEmail());
                psUtilisateur.setString(i++, staff.getCin());
                psUtilisateur.setString(i++, staff.getTel());
                psUtilisateur.setString(i++, staff.getSexe() != null ? staff.getSexe().name() : null);
                psUtilisateur.setString(i++, staff.getLogin());
                psUtilisateur.setString(i++, staff.getMotDePass());
                psUtilisateur.setDate(i++, staff.getDateNaissance() != null ? java.sql.Date.valueOf(staff.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, staff.getLastLoginDate() != null ? java.sql.Date.valueOf(staff.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, staff.getAdresse().getNumero());
                psUtilisateur.setString(i++, staff.getAdresse().getRue());
                psUtilisateur.setString(i++, staff.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, staff.getAdresse().getVille());
                psUtilisateur.setString(i++, staff.getAdresse().getPays());
                psUtilisateur.setString(i++, staff.getAdresse().getComplement());
                psUtilisateur.executeUpdate();
            }

            // 3. Staff (Insertion)
            try (PreparedStatement psStaff = c.prepareStatement(SQL_INSERT_STAFF)) {
                int i = 1;
                psStaff.setLong(i++, staff.getIdEntite()); // FK
                psStaff.setBigDecimal(i++, staff.getSalaire());
                psStaff.setBigDecimal(i++, staff.getPrime());
                psStaff.setDate(i++, staff.getDateRecrutement() != null ? java.sql.Date.valueOf(staff.getDateRecrutement()) : null);
                psStaff.setInt(i++, staff.getSoldeConge() != null ? staff.getSoldeConge() : 0);
                psStaff.setObject(i++, staff.getCabinetMedicale() != null ? staff.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.executeUpdate();
            }

            c.commit(); // Succès
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la création Staff.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la création du Staff.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void update(Staff staff) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. BaseEntity (Mise à jour)
            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                // ... (Mise à jour BaseEntity)
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setLong(2, staff.getModifiePar());
                psBase.setLong(3, staff.getIdEntite());
                psBase.executeUpdate();
                staff.setDateDerniereModification(now);
            }

            // 2. Utilisateur (Mise à jour)
            try (PreparedStatement psUtilisateur = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                // ... (Mise à jour Utilisateur)
                int i = 1;
                psUtilisateur.setString(i++, staff.getNom());
                psUtilisateur.setString(i++, staff.getPrenom());
                psUtilisateur.setString(i++, staff.getEmail());
                psUtilisateur.setString(i++, staff.getCin());
                psUtilisateur.setString(i++, staff.getTel());
                psUtilisateur.setString(i++, staff.getSexe() != null ? staff.getSexe().name() : null);
                psUtilisateur.setString(i++, staff.getLogin());
                psUtilisateur.setString(i++, staff.getMotDePass());
                psUtilisateur.setDate(i++, staff.getDateNaissance() != null ? java.sql.Date.valueOf(staff.getDateNaissance()) : null);
                psUtilisateur.setDate(i++, staff.getLastLoginDate() != null ? java.sql.Date.valueOf(staff.getLastLoginDate()) : null);
                psUtilisateur.setString(i++, staff.getAdresse().getNumero());
                psUtilisateur.setString(i++, staff.getAdresse().getRue());
                psUtilisateur.setString(i++, staff.getAdresse().getCodePostal());
                psUtilisateur.setString(i++, staff.getAdresse().getVille());
                psUtilisateur.setString(i++, staff.getAdresse().getPays());
                psUtilisateur.setString(i++, staff.getAdresse().getComplement());
                psUtilisateur.setLong(i++, staff.getIdEntite());
                psUtilisateur.executeUpdate();
            }

            // 3. Staff (Mise à jour)
            try (PreparedStatement psStaff = c.prepareStatement(SQL_UPDATE_STAFF)) {
                // ... (Mise à jour Staff)
                int i = 1;
                psStaff.setBigDecimal(i++, staff.getSalaire());
                psStaff.setBigDecimal(i++, staff.getPrime());
                psStaff.setDate(i++, staff.getDateRecrutement() != null ? java.sql.Date.valueOf(staff.getDateRecrutement()) : null);
                psStaff.setInt(i++, staff.getSoldeConge() != null ? staff.getSoldeConge() : 0);
                psStaff.setObject(i++, staff.getCabinetMedicale() != null ? staff.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
                psStaff.setLong(i++, staff.getIdEntite());
                psStaff.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) { try { c.rollback(); } catch (SQLException rollbackEx) { throw new RuntimeException("Rollback error lors de la mise à jour Staff.", rollbackEx); } }
            throw new RuntimeException("Erreur lors de la mise à jour du Staff.", e);
        } finally {
            if (c != null) { try { c.setAutoCommit(true); c.close(); } catch (SQLException closeEx) { throw new RuntimeException("Erreur de fermeture connexion.", closeEx); } }
        }
    }

    @Override
    public void delete(Staff staff) {
        if (staff != null) deleteById(staff.getIdEntite());
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans BaseEntity supprime tout en cascade
        String sql = "DELETE FROM BaseEntity WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du Staff.", e);
        }
    }


    // =========================================================================
    //                            READ & Méthodes de Recherche
    // =========================================================================

    @Override
    public Optional<Staff> findById(Long id) {
        String sql = BASE_SELECT_STAFF_SQL + " WHERE s.id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapStaff(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findById Staff.", e);
        }
    }

    @Override
    public List<Staff> findAll() {
        String sql = BASE_SELECT_STAFF_SQL + " ORDER BY u.nom, u.prenom";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAll Staff.", e);
        }
        return out;
    }

    @Override
    public List<Staff> findAllByCabinetId(Long cabinetId) {
        String sql = BASE_SELECT_STAFF_SQL + " WHERE s.cabinet_id = ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByCabinetId Staff.", e);
        }
        return out;
    }

    @Override
    public List<Staff> findAllBySalaireBetween(BigDecimal minSalaire, BigDecimal maxSalaire) {
        String sql = BASE_SELECT_STAFF_SQL + " WHERE s.salaire BETWEEN ? AND ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, minSalaire);
            ps.setBigDecimal(2, maxSalaire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllBySalaireBetween Staff.", e);
        }
        return out;
    }

    @Override
    public Optional<Staff> findByLogin(String login) {
        String sql = BASE_SELECT_STAFF_SQL + " WHERE u.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapStaff(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findByLogin Staff.", e);
        }
    }

    @Override
    public List<Staff> findAllByNomContaining(String nom) {
        String sql = BASE_SELECT_STAFF_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + nom + "%");
            ps.setString(2, "%" + nom + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de findAllByNomContaining Staff.", e);
        }
        return out;
    }
}