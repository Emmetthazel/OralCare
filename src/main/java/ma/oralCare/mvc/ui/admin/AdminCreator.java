package ma.oralCare.mvc.ui.admin;

import ma.oralCare.conf.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class AdminCreator {
    public static void main(String[] args) {
        String login = "admin";
        String passwordClair = "admin123";
        // Hachage du mot de passe avec BCrypt
        String passwordHashe = BCrypt.hashpw(passwordClair, BCrypt.gensalt());

        try (Connection cnx = SessionFactory.getInstance().getConnection()) {
            cnx.setAutoCommit(false); // Début de la transaction

            try {
                // 1. S'assurer que le rôle ADMIN existe (obligatoire pour la table utilisateur_role)
                long roleId = getOrCreateRole(cnx, "ADMIN");

                // 2. Créer l'entrée dans BaseEntity
                long idEntite = -1;
                String sqlBase = "INSERT INTO BaseEntity (date_creation) VALUES (NOW())";
                try (PreparedStatement ps = cnx.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) idEntite = rs.getLong(1);
                }

                // 3. Créer l'entrée dans la table utilisateur
                String sqlUser = """
                    INSERT INTO utilisateur (id_entite, nom, prenom, login, mot_de_pass) 
                    VALUES (?, ?, ?, ?, ?)
                """;
                try (PreparedStatement ps = cnx.prepareStatement(sqlUser)) {
                    ps.setLong(1, idEntite);
                    ps.setString(2, "SYSTEM"); // Nom par défaut
                    ps.setString(3, "Admin");  // Prénom par défaut
                    ps.setString(4, login);
                    ps.setString(5, passwordHashe);
                    ps.executeUpdate();
                }

                // 4. Créer l'entrée dans la table Admin (Héritage spécialisé)
                String sqlAdmin = "INSERT INTO Admin (id_entite) VALUES (?)";
                try (PreparedStatement ps = cnx.prepareStatement(sqlAdmin)) {
                    ps.setLong(1, idEntite);
                    ps.executeUpdate();
                }

                // 5. Lier l'utilisateur au rôle dans utilisateur_role
                String sqlUserRole = "INSERT INTO utilisateur_role (utilisateur_id, role_id) VALUES (?, ?)";
                try (PreparedStatement ps = cnx.prepareStatement(sqlUserRole)) {
                    ps.setLong(1, idEntite);
                    ps.setLong(2, roleId);
                    ps.executeUpdate();
                }

                cnx.commit(); // Validation de toutes les étapes
                System.out.println("✅ SUCCÈS : Administrateur créé avec succès !");
                System.out.println("Login : " + login);
                System.out.println("ID Entité : " + idEntite);

            } catch (Exception e) {
                cnx.rollback(); // Annulation en cas d'erreur
                throw e;
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR lors de la création de l'admin :");
            e.printStackTrace();
        }
    }

    private static long getOrCreateRole(Connection cnx, String libelle) throws SQLException {
        // Vérifier si le rôle existe
        String select = "SELECT id_entite FROM role WHERE libelle = ?";
        try (PreparedStatement ps = cnx.prepareStatement(select)) {
            ps.setString(1, libelle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getLong(1);
        }

        // Sinon, le créer
        long idRole = -1;
        String sqlBase = "INSERT INTO BaseEntity (date_creation) VALUES (NOW())";
        try (PreparedStatement ps = cnx.prepareStatement(sqlBase, Statement.RETURN_GENERATED_KEYS)) {
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) idRole = rs.getLong(1);
        }

        String sqlRole = "INSERT INTO role (id_entite, libelle) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sqlRole)) {
            ps.setLong(1, idRole);
            ps.setString(2, libelle);
            ps.executeUpdate();
        }
        return idRole;
    }
}