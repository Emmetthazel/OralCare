package ma.oralCare.repository.modules.userManager.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.staff.Staff;
import ma.oralCare.repository.common.RowMappers;
import ma.oralCare.repository.modules.userManager.api.StaffRepository;
import ma.oralCare.repository.modules.auth.api.UtilisateurRepository; // Non utilisé directement dans la logique DB pure pour éviter la double gestion

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du StaffRepository en utilisant JDBC.
 * Gère les opérations CRUD sur l'entité Staff (qui hérite d'Utilisateur) et les recherches spécifiques.
 */
public class StaffRepositoryImpl implements StaffRepository {

    private final UtilisateurRepository utilisateurRepository;

    // Requête de base pour joindre Utilisateur et Staff
    private static final String SELECT_STAFF_BASE =
            "SELECT U.*, S.salaire, S.prime, S.dateRecrutement, S.soldeConge, S.cabinetMedicaleId " +
                    "FROM Utilisateur U " +
                    "JOIN Staff S ON U.id = S.id";


    public StaffRepositoryImpl(UtilisateurRepository utilisateurRepository /*, JdbcTemplate jdbcTemplate, StaffRowMapper staffRowMapper */) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // --- 1. Implémentation des Méthodes CRUD de CrudRepository ---

    @Override
    public List<Staff> findAll() {
        String sql = SELECT_STAFF_BASE;
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStaff(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tout le personnel (Staff)", e);
        }
        return out;
    }

    @Override
    public Staff findById(Long id) {
        String sql = SELECT_STAFF_BASE + " WHERE U.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStaff(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par ID", e);
        }
    }

    @Override
    public void create(Staff newElement) {
        if (newElement == null) return;
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false); // Début de transaction

            // 1. Insertion dans Utilisateur (Nous supposons l'ordre des colonnes ou une procédure stockée)
            // L'ID doit être généré et récupéré pour la table Staff.
            // SQL simplifié (non complet) pour Utilisateur.
            String sqlUser = "INSERT INTO Utilisateur (nom, email, cin, tel, sexe, login, motDePass, dateNaissance) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psU = c.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);

            // Set des paramètres Utilisateur (omission des détails)
            // psU.setString(1, newElement.getNom()); ...
            psU.executeUpdate();

            ResultSet rsU = psU.getGeneratedKeys();
            Long userId = rsU.next() ? rsU.getLong(1) : null;
            if (userId == null) throw new SQLException("Échec de la récupération de l'ID Utilisateur.");
            newElement.setId(userId);

            // 2. Insertion dans Staff
            String sqlStaff = "INSERT INTO Staff (id, salaire, prime, dateRecrutement, soldeConge, cabinetMedicaleId) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement psS = c.prepareStatement(sqlStaff);

            psS.setLong(1, userId);
            psS.setDouble(2, newElement.getSalaire());
            psS.setDouble(3, newElement.getPrime());
            psS.setDate(4, newElement.getDateRecrutement() != null ? Date.valueOf(newElement.getDateRecrutement()) : null);
            psS.setInt(5, newElement.getSoldeConge());
            psS.setObject(6, newElement.getCabinetMedicale() != null ? newElement.getCabinetMedicale().getId() : null, Types.BIGINT);

            psS.executeUpdate();
            c.commit(); // Validation de la transaction
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { /* Log */ }
            throw new RuntimeException("Erreur lors de la création de Staff (transaction failed)", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { /* Log */ }
        }
    }

    @Override
    public void update(Staff newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Mise à jour de la table Utilisateur (Simplifiée)
            // UPDATE Utilisateur SET nom = ?, email = ?, ... WHERE id = ?
            // Préparation et exécution de la mise à jour Utilisateur...

            // 2. Mise à jour de la table Staff
            String sqlStaff = "UPDATE Staff SET salaire = ?, prime = ?, dateRecrutement = ?, soldeConge = ?, cabinetMedicaleId = ? WHERE id = ?";
            PreparedStatement psS = c.prepareStatement(sqlStaff);

            psS.setDouble(1, newValuesElement.getSalaire());
            psS.setDouble(2, newValuesElement.getPrime());
            psS.setDate(3, newValuesElement.getDateRecrutement() != null ? Date.valueOf(newValuesElement.getDateRecrutement()) : null);
            psS.setInt(4, newValuesElement.getSoldeConge());
            psS.setObject(5, newValuesElement.getCabinetMedicale() != null ? newValuesElement.getCabinetMedicale().getId() : null, Types.BIGINT);
            psS.setLong(6, newValuesElement.getId());

            psS.executeUpdate();
            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { /* Log */ }
            throw new RuntimeException("Erreur lors de la mise à jour de Staff (transaction failed)", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { /* Log */ }
        }
    }

    @Override
    public void delete(Staff staff) {
        if (staff != null && staff.getId() != null) deleteById(staff.getId());
    }

    @Override
    public void deleteById(Long id) {
        Connection c = null;
        try {
            c = SessionFactory.getInstance().getConnection();
            c.setAutoCommit(false);

            // 1. Suppression dans Staff (supprime de la sous-classe)
            String sqlStaff = "DELETE FROM Staff WHERE id = ?";
            try (PreparedStatement psS = c.prepareStatement(sqlStaff)) {
                psS.setLong(1, id);
                psS.executeUpdate();
            }

            // 2. Suppression dans Utilisateur (supprime de l'entité de base)
            String sqlUser = "DELETE FROM Utilisateur WHERE id = ?";
            try (PreparedStatement psU = c.prepareStatement(sqlUser)) {
                psU.setLong(1, id);
                psU.executeUpdate();
            }

            c.commit();
        } catch (SQLException e) {
            if (c != null) try { c.rollback(); } catch (SQLException ex) { /* Log */ }
            throw new RuntimeException("Erreur lors de la suppression de Staff par ID (transaction failed)", e);
        } finally {
            if (c != null) try { c.setAutoCommit(true); c.close(); } catch (SQLException ex) { /* Log */ }
        }
    }

    // --- 2. Implémentation des Méthodes de Recherche Utilisateur (Typées Staff) ---
    // Ces méthodes utilisent la requête JOIN SELECT_STAFF_BASE pour garantir un objet Staff complet.

    @Override
    public Optional<Staff> findByLogin(String login) {
        String sql = SELECT_STAFF_BASE + " WHERE U.login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapStaff(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par login", e);
        }
    }

    @Override
    public Optional<Staff> findByCin(String cin) {
        String sql = SELECT_STAFF_BASE + " WHERE U.cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapStaff(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par CIN", e);
        }
    }

    // --- 3. Implémentation des Méthodes Staff Spécifiques ---

    @Override
    public List<Staff> findByDateRecrutement(LocalDate dateRecrutement) {
        String sql = SELECT_STAFF_BASE + " WHERE S.dateRecrutement = ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateRecrutement));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par date de recrutement", e);
        }
        return out;
    }

    @Override
    public List<Staff> findBySoldeCongeLessThanEqual(Integer maxSolde) {
        String sql = SELECT_STAFF_BASE + " WHERE S.soldeConge <= ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, maxSolde);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par solde de congés", e);
        }
        return out;
    }

    @Override
    public List<Staff> findBySalaireBetween(Double minSalaire, Double maxSalaire) {
        String sql = SELECT_STAFF_BASE + " WHERE S.salaire BETWEEN ? AND ?";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, minSalaire);
            ps.setDouble(2, maxSalaire);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de Staff par plage de salaire", e);
        }
        return out;
    }

    // --- 4. Implémentation des Méthodes de Gestion Administrative ---

    @Override
    public void updateSalaireAndPrime(Long staffId, Double nouveauSalaire, Double nouvellePrime) {
        // COALESCE permet de garder la valeur actuelle si le paramètre est null
        String sql = "UPDATE Staff SET salaire = COALESCE(?, salaire), prime = COALESCE(?, prime) WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // setObject(index, value, type) permet de gérer correctement les valeurs Double nulles
            ps.setObject(1, nouveauSalaire, Types.DOUBLE);
            ps.setObject(2, nouvellePrime, Types.DOUBLE);
            ps.setLong(3, staffId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du Salaire et de la Prime pour Staff ID: " + staffId, e);
        }
    }

    @Override
    public void updateSoldeConge(Long staffId, Integer nouveauSolde) {
        String sql = "UPDATE Staff SET soldeConge = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, nouveauSolde);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du Solde de Congés pour Staff ID: " + staffId, e);
        }
    }
}