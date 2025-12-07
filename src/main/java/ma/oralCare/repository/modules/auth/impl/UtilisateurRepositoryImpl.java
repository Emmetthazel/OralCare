package ma.oralCare.repository.modules.auth.impl;

import ma.oralCare.entities.staff.Utilisateur;
import ma.oralCare.entities.common.Adresse;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.notification.Role;
import ma.oralCare.repository.modules.auth.api.UtilisateurRepository;
import ma.oralCare.conf.SessionFactory;
import ma.oralCare.repository.modules.auth.api.RoleRepository;
// Import nécessaire pour la conversion Enum
import ma.oralCare.entities.enums.RoleLibelle;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    // Dépendance simplifiée pour l'exemple JDBC
    private final RoleRepository roleRepository = new ma.oralCare.repository.modules.auth.impl.RoleRepositoryImpl();

    // --- 0. Fonctions Utilitaires de Mappage et d'Hydratation ---

    /** Mappe une adresse à partir d'un ResultSet. Suppose des colonnes préfixées (adresse_) dans la table utilisateur. */
    private Adresse mapAdresse(ResultSet rs) throws SQLException {
        return new Adresse(
                rs.getString("adresse_numero"),
                rs.getString("adresse_rue"),
                rs.getString("adresse_codePostal"),
                rs.getString("adresse_ville"),
                rs.getString("adresse_pays"),
                rs.getString("adresse_complement")
        );
    }

    /** Définit les paramètres de l'adresse dans un PreparedStatement, en commençant à l'index donné. */
    private void setAdresseParams(PreparedStatement ps, Adresse adresse, int startIndex) throws SQLException {
        ps.setString(startIndex, adresse.getNumero());
        ps.setString(startIndex + 1, adresse.getRue());
        ps.setString(startIndex + 2, adresse.getCodePostal());
        ps.setString(startIndex + 3, adresse.getVille());
        ps.setString(startIndex + 4, adresse.getPays());
        ps.setString(startIndex + 5, adresse.getComplement());
    }


    /** Mappe un Utilisateur (sans charger les collections Rôles/Notifications, qui est fait séparément) */
    private Utilisateur mapUtilisateur(ResultSet rs) throws SQLException {
        Utilisateur user = Utilisateur.builder()
                .id(rs.getLong("id"))
                .nom(rs.getString("nom"))
                .email(rs.getString("email"))
                .cin(rs.getString("cin"))
                .tel(rs.getString("tel"))
                // Conversion String BDD -> Enum Sexe
                .sexe(Sexe.valueOf(rs.getString("sexe")))
                .login(rs.getString("login"))
                .motDePass(rs.getString("motDePass"))
                .lastLoginDate(rs.getDate("lastLoginDate") != null ? rs.getDate("lastLoginDate").toLocalDate() : null)
                .dateNaissance(rs.getDate("dateNaissance") != null ? rs.getDate("dateNaissance").toLocalDate() : null)
                // L'attribut estActif est supposé exister pour le CU "Activer/désactiver Compte"
                // .estActif(rs.getBoolean("estActif"))
                .adresse(mapAdresse(rs))
                .build();

        // Charger les rôles (N+1)
        user.setRoles(findRolesByUtilisateurId(user.getId()));

        return user;
    }

    /** Charge les rôles associés à un utilisateur à partir de la table de jointure */
    private List<Role> findRolesByUtilisateurId(Long userId) {
        String sql = "SELECT role_id FROM utilisateur_role WHERE utilisateur_id = ?";
        List<Long> roleIds = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) roleIds.add(rs.getLong("role_id"));
            }
        } catch (SQLException e) {
            // NOTE: On ne lance pas d'erreur critique, on log le problème et renvoie vide
            System.err.println("Erreur lors du chargement des IDs de rôles: " + e.getMessage());
            return new ArrayList<>();
        }

        // Charger les entités Role complètes via RoleRepository
        return roleIds.stream()
                .map(roleRepository::findById)
                .filter(r -> r != null)
                .collect(Collectors.toList());
    }

    // --- 1. Opérations CRUD de base ---

    @Override
    public List<Utilisateur> findAll() {
        // Sélectionne tous les champs nécessaires, y compris ceux pour l'adresse
        String sql = "SELECT * FROM utilisateur";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de tous les utilisateurs", e); }
        return out;
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUtilisateur(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'utilisateur par ID", e); }
    }

    @Override
    public void create(Utilisateur newElement) {
        if (newElement == null) return;

        // Requête d'insertion (14 colonnes de données + 1 colonne estActif si elle existe)
        String sql = "INSERT INTO utilisateur (nom, email, cin, tel, sexe, login, motDePass, dateNaissance, adresse_numero, adresse_rue, adresse_codePostal, adresse_ville, adresse_pays, adresse_complement) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newElement.getNom());
            ps.setString(2, newElement.getEmail());
            ps.setString(3, newElement.getCin());
            ps.setString(4, newElement.getTel());
            ps.setString(5, newElement.getSexe().name()); // Conversion Enum Sexe -> String BDD
            ps.setString(6, newElement.getLogin());
            ps.setString(7, newElement.getMotDePass());
            ps.setDate(8, Date.valueOf(newElement.getDateNaissance()));

            // Paramètres d'Adresse (à partir de l'index 9)
            setAdresseParams(ps, newElement.getAdresse(), 9);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
                // Attribuer les rôles si présents (CU Attribuer Role)
                if (newElement.getRoles() != null) {
                    newElement.getRoles().forEach(role -> {
                        try {
                            createRoleAssociation(newElement.getId(), role.getId());
                        } catch (SQLException ex) {
                            throw new RuntimeException("Erreur d'association de rôle lors de la création", ex);
                        }
                    });
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création de l'utilisateur", e); }
    }

    @Override
    public void update(Utilisateur newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;

        // Mise à jour de toutes les colonnes mutables sauf login et motDePass
        String sql = "UPDATE utilisateur SET nom = ?, email = ?, cin = ?, tel = ?, sexe = ?, dateNaissance = ?, " +
                "adresse_numero = ?, adresse_rue = ?, adresse_codePostal = ?, adresse_ville = ?, adresse_pays = ?, adresse_complement = ? WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, newValuesElement.getNom());
            ps.setString(2, newValuesElement.getEmail());
            ps.setString(3, newValuesElement.getCin());
            ps.setString(4, newValuesElement.getTel());
            ps.setString(5, newValuesElement.getSexe().name());
            ps.setDate(6, Date.valueOf(newValuesElement.getDateNaissance()));

            // Paramètres d'Adresse (à partir de l'index 7)
            setAdresseParams(ps, newValuesElement.getAdresse(), 7);

            ps.setLong(13, newValuesElement.getId());

            ps.executeUpdate();

            // NOTE: La gestion de la relation N:M des rôles (update) est gérée par les méthodes add/removeRoleToUtilisateur,
            // ou par une logique de service qui appelle d'abord la suppression de toutes les associations puis leur recréation.

        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur", e); }
    }

    @Override
    public void delete(Utilisateur element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection()) {
            c.setAutoCommit(false); // Début de transaction pour garantir l'intégrité

            // 1. Supprimer les entrées de la table de jointure (nécessaire pour la contrainte de clé étrangère)
            String deleteRolesSql = "DELETE FROM utilisateur_role WHERE utilisateur_id = ?";
            try (PreparedStatement ps = c.prepareStatement(deleteRolesSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            // 2. Supprimer l'utilisateur lui-même
            String deleteUserSql = "DELETE FROM utilisateur WHERE id = ?";
            try (PreparedStatement ps = c.prepareStatement(deleteUserSql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            c.commit(); // Validation
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur par ID", e);
        }
    }

    // --- 2. Méthodes d'Authentification et de Recherche Spécifiques ---

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'utilisateur par login", e); }
    }

    @Override
    public Optional<Utilisateur> findByCin(String cin) {
        String sql = "SELECT * FROM utilisateur WHERE cin = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche de l'utilisateur par CIN", e); }
    }

    @Override
    public Utilisateur updateMotDePasse(Long userId, String newPassword) {
        // CU Réinitialiser Mot de Passe
        String sql = "UPDATE utilisateur SET motDePass = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            ps.executeUpdate();
            return findById(userId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour du mot de passe", e); }
    }

    @Override
    public Utilisateur updateStatutCompte(Long userId, boolean estActif) {
        // CU Activer/désactiver Compte
        // Supposons que la colonne 'estActif' existe
        String sql = "UPDATE utilisateur SET estActif = ? WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, estActif);
            ps.setLong(2, userId);
            ps.executeUpdate();
            return findById(userId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour du statut du compte", e); }
    }

    @Override
    public List<Utilisateur> findByRoleLibelle(String roleLibelle) {
        // Recherche des utilisateurs par le libellé du rôle (N:M)

        // 1. Trouver l'ID du Rôle à partir du libellé
        Optional<Role> roleOpt = roleRepository.findByLibelle(roleLibelle);
        if (roleOpt.isEmpty()) return new ArrayList<>();
        Long roleId = roleOpt.get().getId();

        // 2. Joindre les tables utilisateur et utilisateur_role
        String sql = "SELECT u.* FROM utilisateur u JOIN utilisateur_role ur ON u.id = ur.utilisateur_id WHERE ur.role_id = ?";
        List<Utilisateur> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapUtilisateur(rs));
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des utilisateurs par rôle", e); }
        return out;
    }

    // --- 3. Méthodes de Gestion des Rôles (Relation N:M) ---

    private void createRoleAssociation(Long userId, Long roleId) throws SQLException {
        // Logique pour l'association dans la table de jointure
        String sql = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        }
    }

    private void deleteRoleAssociation(Long userId, Long roleId) throws SQLException {
        // Logique pour la suppression dans la table de jointure
        String sql = "DELETE FROM utilisateur_role WHERE utilisateur_id = ? AND role_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        }
    }

    @Override
    public Utilisateur addRoleToUtilisateur(Long userId, Long roleId) {
        // CU Attribuer Role
        try {
            createRoleAssociation(userId, roleId);
            return findById(userId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de l'ajout du rôle à l'utilisateur", e); }
    }

    @Override
    public Utilisateur removeRoleFromUtilisateur(Long userId, Long roleId) {
        // CU Modifier permissions
        try {
            deleteRoleAssociation(userId, roleId);
            return findById(userId);
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression du rôle de l'utilisateur", e); }
    }
}