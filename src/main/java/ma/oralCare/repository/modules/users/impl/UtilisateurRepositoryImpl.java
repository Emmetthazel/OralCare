package ma.oralCare.repository.modules.users.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.*;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    // --- Requêtes SQL Fixes (Alignées sur votre script SQL) ---
    private static final String SQL_INSERT_BASE = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";

    private static final String SQL_INSERT_UTILISATEUR = """
        INSERT INTO utilisateur (id_entite, nom, prenom, email, cin, tel, sexe, login, mot_de_pass, 
        date_naissance, last_login_date, numero, rue, code_postal, ville, pays, complement) 
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String SQL_UPDATE_BASE = "UPDATE BaseEntity SET date_derniere_modification=?, modifie_par=? WHERE id_entite=?";

    private static final String SQL_UPDATE_UTILISATEUR = """
        UPDATE utilisateur SET nom=?, prenom=?, email=?, cin=?, tel=?, sexe=?, login=?, 
        mot_de_pass=?, date_naissance=?, last_login_date=?, numero=?, rue=?, 
        code_postal=?, ville=?, pays=?, complement=? WHERE id_entite=?
        """;

    private static final String BASE_SELECT_UTILISATEUR_SQL = """
        SELECT u.*, b.date_creation, b.date_derniere_modification, b.cree_par, b.modifie_par 
        FROM utilisateur u 
        JOIN BaseEntity b ON u.id_entite = b.id_entite
        """;

    private static final String SQL_SELECT_USER_STAFF_HIERARCHY = """
        SELECT u.nom, u.prenom, u.email, 'Actif' as statut, c.nom as cabinet_nom,
               CASE WHEN m.id_entite IS NOT NULL THEN 'MÉDECIN'
                    WHEN sec.id_entite IS NOT NULL THEN 'SECRÉTAIRE'
                    ELSE 'STAFF' END as role_type
        FROM utilisateur u
        JOIN Staff st ON u.id_entite = st.id_entite
        LEFT JOIN cabinet_medicale c ON st.cabinet_id = c.id_entite
        LEFT JOIN Medecin m ON u.id_entite = m.id_entite
        LEFT JOIN Secretaire sec ON u.id_entite = sec.id_entite
        WHERE (u.nom LIKE ? OR u.email LIKE ? OR c.nom LIKE ?)
        """;

    public UtilisateurRepositoryImpl() {}

    // =========================================================================
    // ✅ RECHERCHE
    // =========================================================================

    @Override public Optional<Utilisateur> findById(Long id) { return findOneByColumn("u.id_entite", id); }
    @Override public Optional<Utilisateur> findByEmail(String email) { return findOneByColumn("u.email", email); }
    @Override public Optional<Utilisateur> findByLogin(String login) { return findOneByColumn("u.login", login); }
    @Override public Optional<Utilisateur> findByCin(String cin) { return findOneByColumn("u.cin", cin); }

    @Override
    public Optional<Utilisateur> findOneByColumn(String columnName, Object value) {
        String sql = BASE_SELECT_UTILISATEUR_SQL + " WHERE " + columnName + " = ?";

        // La connexion est ouverte ICI et sera fermée AUTOMATIQUEMENT à la fin du bloc
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, value);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // On mappe l'utilisateur tant que la connexion est ouverte
                    Utilisateur u = RowMappers.mapUtilisateur(rs);

                    // On peut charger les rôles ici ou après
                    u.setRoles(findRolesByUtilisateurId(u.getIdEntite()));
                    return Optional.of(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL : " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        List<Role> roles = new ArrayList<>();
        // Correction : utilisateur_id au lieu de user_id
        String sql = """
            SELECT r.*, be.* FROM role r 
            JOIN BaseEntity be ON r.id_entite = be.id_entite 
            JOIN utilisateur_role ur ON r.id_entite = ur.role_id 
            WHERE ur.utilisateur_id = ?
            """;
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roles.add(RowMappers.mapRole(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return roles;
    }

    // =========================================================================
    // ✅ PERSISTENCE
    // =========================================================================

    @Override
    public void update(Utilisateur u) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            try (PreparedStatement psBase = c.prepareStatement(SQL_UPDATE_BASE)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                psBase.setObject(2, u.getModifiePar() != null ? u.getModifiePar() : 1L, Types.BIGINT);
                psBase.setLong(3, u.getIdEntite());
                psBase.executeUpdate();
            }

            try (PreparedStatement psU = c.prepareStatement(SQL_UPDATE_UTILISATEUR)) {
                mapParamsForUpdate(psU, u);
                psU.executeUpdate();
            }

            c.commit();
            u.setDateDerniereModification(now);
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Erreur lors de l'update", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    // =========================================================================
    // ✅ HELPERS MAPPING
    // =========================================================================

    private void mapParams(PreparedStatement ps, Utilisateur u) throws SQLException {
        int i = 1;
        ps.setLong(i++, u.getIdEntite());
        ps.setString(i++, u.getNom());
        ps.setString(i++, u.getPrenom());
        ps.setString(i++, u.getEmail());
        ps.setString(i++, u.getCin());
        ps.setString(i++, u.getTel());
        ps.setString(i++, u.getSexe() != null ? u.getSexe().name() : null);
        ps.setString(i++, u.getLogin());
        ps.setString(i++, u.getMotDePass()); // mot_de_pass sans 'e'
        ps.setDate(i++, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);
        ps.setDate(i++, u.getLastLoginDate() != null ? Date.valueOf(u.getLastLoginDate()) : null);

        if (u.getAdresse() != null) {
            ps.setString(i++, u.getAdresse().getNumero());
            ps.setString(i++, u.getAdresse().getRue());
            ps.setString(i++, u.getAdresse().getCodePostal());
            ps.setString(i++, u.getAdresse().getVille());
            ps.setString(i++, u.getAdresse().getPays());
            ps.setString(i++, u.getAdresse().getComplement());
        } else {
            for(int j=0; j<6; j++) ps.setNull(i++, Types.VARCHAR);
        }
    }

    private void mapParamsForUpdate(PreparedStatement ps, Utilisateur u) throws SQLException {
        int i = 1;
        ps.setString(i++, u.getNom());
        ps.setString(i++, u.getPrenom());
        ps.setString(i++, u.getEmail());
        ps.setString(i++, u.getCin());
        ps.setString(i++, u.getTel());
        ps.setString(i++, u.getSexe() != null ? u.getSexe().name() : null);
        ps.setString(i++, u.getLogin());
        ps.setString(i++, u.getMotDePass());
        ps.setDate(i++, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);
        ps.setDate(i++, u.getLastLoginDate() != null ? Date.valueOf(u.getLastLoginDate()) : null);

        if (u.getAdresse() != null) {
            ps.setString(i++, u.getAdresse().getNumero());
            ps.setString(i++, u.getAdresse().getRue());
            ps.setString(i++, u.getAdresse().getCodePostal());
            ps.setString(i++, u.getAdresse().getVille());
            ps.setString(i++, u.getAdresse().getPays());
            ps.setString(i++, u.getAdresse().getComplement());
        } else {
            for(int j=0; j<6; j++) ps.setNull(i++, Types.VARCHAR);
        }
        ps.setLong(i, u.getIdEntite());
    }

    // =========================================================================
    // ✅ AUTRES MÉTHODES (Rôles, Statut, Password)
    // =========================================================================

    @Override
    public void updateStatus(String email, String status) {
        // Désactivé car pas de colonne statut_compte en DB
        System.out.println("Action ignorée : colonne statut absente en DB.");
    }

    @Override
    public void addRoleToUtilisateur(Long uId, Long rId) {
        String sql = "INSERT IGNORE INTO utilisateur_role(utilisateur_id, role_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uId);
            ps.setLong(2, rId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeRoleFromUtilisateur(Long uId, Long rId) {
        String sql = "DELETE FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, uId);
            ps.setLong(2, rId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void updatePassword(Long userId, String encodedPassword) {
        String sql = "UPDATE utilisateur SET mot_de_pass = ? WHERE id_entite = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, encodedPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override public void save(Utilisateur u) { if (u.getIdEntite() == null) create(u); else update(u); }
    @Override public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM BaseEntity WHERE id_entite = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override public void delete(Utilisateur u) { if(u!=null) deleteById(u.getIdEntite()); }

    @Override
    public List<Utilisateur> findAll() {
        List<Utilisateur> list = new ArrayList<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(BASE_SELECT_UTILISATEUR_SQL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override public List<UserStaffDTO> findAllStaffWithCabinetDetails(String search) {
        List<UserStaffDTO> list = new ArrayList<>();
        String pattern = "%" + (search == null ? "" : search) + "%";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SQL_SELECT_USER_STAFF_HIERARCHY)) {
            ps.setString(1, pattern); ps.setString(2, pattern); ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new UserStaffDTO(
                            rs.getString("nom"), rs.getString("prenom"),
                            rs.getString("email"), rs.getString("statut"),
                            rs.getString("role_type"), rs.getString("cabinet_nom")
                    ));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public List<String> findAllCabinetNames() {
        List<String> names = new ArrayList<>();
        String sql = "SELECT nom FROM cabinet_medicale ORDER BY nom ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                names.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des noms de cabinets : " + e.getMessage());
            throw new RuntimeException(e);
        }
        return names;
    }

    // Méthodes requises par l'interface mais non implémentées ici
    @Override public List<Utilisateur> findAllByNomContaining(String nom) { return new ArrayList<>(); }
    @Override public List<Utilisateur> findAllByRole(String role) { return new ArrayList<>(); }
    @Override
    public Long findCabinetIdByName(String cabinetName) {
        String sql = "SELECT id_entite FROM cabinet_medicale WHERE nom = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cabinetName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong("id_entite");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    @Override public void updatePassword(String login, String encodedPassword) {}

    @Override
    public Long findRoleIdByName(String roleName) {
        if (roleName == null || roleName.isEmpty()) return null;

        // 1. Traduction systématique pour correspondre au CHECK CONSTRAINT du SQL
        String dbRoleName;
        String upper = roleName.toUpperCase().trim();

        if (upper.contains("MEDECIN") || upper.contains("DOCTOR")) {
            dbRoleName = "DOCTOR";
        } else if (upper.contains("SECRETAIRE") || upper.contains("SECRETARY")) {
            dbRoleName = "SECRETARY";
        } else if (upper.contains("ADMIN")) {
            dbRoleName = "ADMIN";
        } else {
            dbRoleName = "RECEPTIONIST";
        }

        System.out.println("[DEBUG-REPO] Mapping du rôle: '" + roleName + "' -> '" + dbRoleName + "'");

        // 2. Requête SQL
        String sql = "SELECT id_entite FROM role WHERE libelle = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dbRoleName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Long id = rs.getLong("id_entite");
                    System.out.println("[DEBUG-REPO] ID trouvé pour " + dbRoleName + " : " + id);
                    return id;
                } else {
                    System.err.println("❌ ERREUR : Le rôle '" + dbRoleName + "' n'existe pas dans la table 'role'.");
                    System.err.println("👉 Vérifiez que vous avez bien inséré les rôles par défaut (INSERT INTO role...).");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ ERREUR SQL lors de findRoleIdByName : " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void create(Utilisateur u) {
        Connection c = null;
        LocalDateTime now = LocalDateTime.now();

        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // 🔑 Transaction : Indispensable pour l'héritage multi-tables

            // --- ÉTAPE 1 : BaseEntity (Génération de l'ID AUTO_INCREMENT) ---
            String sqlBase = "INSERT INTO BaseEntity(date_creation, cree_par) VALUES(?, ?)";
            try (PreparedStatement psBase = c.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                psBase.setTimestamp(1, Timestamp.valueOf(now));
                // Si creePar est null, on met l'ID 1 (système/admin par défaut)
                psBase.setObject(2, u.getCreePar() != null ? u.getCreePar() : 1L, Types.BIGINT);
                psBase.executeUpdate();

                try (ResultSet rs = psBase.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setIdEntite(rs.getLong(1)); // 👈 On récupère l'ID crucial ici
                    } else {
                        throw new SQLException("Échec de la récupération de l'ID pour BaseEntity.");
                    }
                }
            }

            // --- ÉTAPE 2 : Table Utilisateur (Commune à tous) ---
            try (PreparedStatement psU = c.prepareStatement(SQL_INSERT_UTILISATEUR)) {
                mapParams(psU, u); // u.getIdEntite() est maintenant utilisé comme PK
                psU.executeUpdate();
            }

            // --- ÉTAPE 3 : Tables Spécifiques (Selon le type réel de l'objet) ---
            if (u instanceof Staff staff) {
                insertStaffDetails(c, staff);
            } else if (u instanceof Admin) {
                // Table Admin dans votre SQL : juste un lien vers utilisateur
                String sqlAdmin = "INSERT INTO Admin (id_entite) VALUES (?)";
                try (PreparedStatement psAdmin = c.prepareStatement(sqlAdmin)) {
                    psAdmin.setLong(1, u.getIdEntite());
                    psAdmin.executeUpdate();
                }
            }

            c.commit(); // ✅ On valide tout le bloc
            u.setDateCreation(now);
            System.out.println("✅ Utilisateur " + u.getLogin() + " créé avec ID: " + u.getIdEntite());

        } catch (SQLException e) {
            if (c != null) {
                try { c.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            System.err.println("❌ Erreur de persistance : " + e.getMessage());
            throw new RuntimeException("Impossible de créer l'utilisateur", e);
        } finally {
            if (c != null) {
                try { c.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    /**
     * Gère l'insertion dans Staff et ses sous-tables (Medecin/Secretaire)
     */
    private void insertStaffDetails(Connection c, Staff staff) throws SQLException {
        // 1. Table Staff
        String sqlStaff = """
        INSERT INTO Staff (id_entite, salaire, prime, date_recrutement, solde_conge, cabinet_id) 
        VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = c.prepareStatement(sqlStaff)) {
            ps.setLong(1, staff.getIdEntite());
            ps.setBigDecimal(2, staff.getSalaire());
            ps.setBigDecimal(3, staff.getPrime());
            ps.setDate(4, staff.getDateRecrutement() != null ? Date.valueOf(staff.getDateRecrutement()) : null);
            ps.setObject(5, staff.getSoldeConge(), Types.INTEGER);
            ps.setObject(6, staff.getCabinetMedicale() != null ? staff.getCabinetMedicale().getIdEntite() : null, Types.BIGINT);
            ps.executeUpdate();
        }

        // 2. Spécialisation (Médecin ou Secrétaire)
        if (staff instanceof Medecin medecin) {
            String sqlMed = "INSERT INTO Medecin (id_entite, specialite) VALUES (?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlMed)) {
                ps.setLong(1, medecin.getIdEntite());
                ps.setString(2, medecin.getSpecialite());
                ps.executeUpdate();
            }
        } else if (staff instanceof Secretaire sec) {
            String sqlSec = "INSERT INTO Secretaire (id_entite, num_cnss, commission) VALUES (?, ?, ?)";
            try (PreparedStatement ps = c.prepareStatement(sqlSec)) {
                ps.setLong(1, sec.getIdEntite());
                ps.setString(2, sec.getNumCNSS());
                ps.setBigDecimal(3, sec.getCommission());
                ps.executeUpdate();
            }
        }
    }


}