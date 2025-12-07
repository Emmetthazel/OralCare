package ma.oralCare.repository.modules.auth.impl;

import ma.oralCare.entities.notification.Role;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.repository.modules.auth.api.RoleRepository;
import ma.oralCare.conf.SessionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleRepositoryImpl implements RoleRepository {

    // --- Fonctions utilitaires de conversion (Privilèges) ---

    private String privilegesListToCsv(List<String> privileges) {
        if (privileges == null || privileges.isEmpty()) return "";
        return privileges.stream().collect(Collectors.joining(","));
    }

    private List<String> csvToPrivilegesList(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(csv.split(",")));
    }

    // --- Mappeur d'Entité Role ---
    private Role mapRole(ResultSet rs) throws SQLException {
        // Conversion de String BDD vers Enum Java
        String libelleString = rs.getString("libelle");
        RoleLibelle libelleEnum = RoleLibelle.valueOf(libelleString);

        return Role.builder()
                .id(rs.getLong("id"))
                .libelle(libelleEnum)
                .privileges(csvToPrivilegesList(rs.getString("privileges")))
                // Les relations (utilisateurs) ne sont généralement pas mappées ici dans le Repository JDBC
                .build();
    }


    // --- 1. Opérations CRUD de base ---

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM role ORDER BY libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRole(rs));
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la récupération de tous les rôles", e); }
        return out;
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRole(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche du rôle par ID", e); }
    }

    @Override
    public void create(Role newElement) {
        if (newElement == null) return;

        String sql = "INSERT INTO role (libelle, privileges) VALUES (?, ?)";
        String privilegesCsv = privilegesListToCsv(newElement.getPrivileges());

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Conversion de l'Enum Java vers String BDD
            ps.setString(1, newElement.getLibelle().name());
            ps.setString(2, privilegesCsv);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newElement.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la création du rôle", e); }
    }

    @Override
    public void update(Role newValuesElement) {
        if (newValuesElement == null || newValuesElement.getId() == null) return;

        String sql = "UPDATE role SET libelle = ?, privileges = ? WHERE id = ?";
        String privilegesCsv = privilegesListToCsv(newValuesElement.getPrivileges());

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Conversion de l'Enum Java vers String BDD
            ps.setString(1, newValuesElement.getLibelle().name());
            ps.setString(2, privilegesCsv);
            ps.setLong(3, newValuesElement.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la mise à jour du rôle", e); }
    }

    @Override
    public void delete(Role element) {
        if (element != null && element.getId() != null) deleteById(element.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la suppression du rôle par ID", e); }
    }

    // --- 2. Méthodes de Recherche Spécifiques ---

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        String sql = "SELECT * FROM role WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, libelle);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRole(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche du rôle par libellé", e); }
    }

    @Override
    public List<Role> findByPrivilege(String privilege) {
        // Recherche des privilèges dans la chaîne CSV (couvre le début, le milieu et la fin)
        // La première condition gère le cas où le privilège est le seul ou au début/milieu.
        // La deuxième condition gère le cas où le privilège est le dernier dans la liste.
        String sql = "SELECT * FROM role WHERE privileges LIKE ? OR privileges LIKE ? OR privileges = ?";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%," + privilege + ",%");
            ps.setString(2, privilege + ",%");
            ps.setString(3, privilege);              // Le cas où la liste n'a qu'un seul privilège

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Filtrage en mémoire pour être sûr à 100% (si la requête SQL était imprécise)
                    Role role = mapRole(rs);
                    if (role.getPrivileges().contains(privilege)) {
                        out.add(role);
                    }
                }
            }
        } catch (SQLException e) { throw new RuntimeException("Erreur lors de la recherche des rôles par privilège", e); }
        return out;
    }

    // --- 3. Méthodes de Gestion Spécifiques ---

    @Override
    public Role addPrivilegeToRole(Long roleId, String newPrivilege) {
        Role role = findById(roleId);
        if (role == null || role.getPrivileges().contains(newPrivilege)) return role;

        role.getPrivileges().add(newPrivilege);
        update(role); // Réutilisation de la méthode update CRUD
        return findById(roleId);
    }

    @Override
    public Role removePrivilegeFromRole(Long roleId, String privilegeToRemove) {
        Role role = findById(roleId);
        if (role == null || !role.getPrivileges().contains(privilegeToRemove)) return role;

        role.getPrivileges().remove(privilegeToRemove);
        update(role); // Réutilisation de la méthode update CRUD
        return findById(roleId);
    }
}