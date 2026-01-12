package ma.oralCare.mvc.ui.admin;

import ma.oralCare.conf.SessionFactory;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminFixer {
    public static void main(String[] args) {
        String login = "admin";
        String passwordClair = "admin123";

        // 1. Génération du hash via la bibliothèque du projet
        String passwordHashe = BCrypt.hashpw(passwordClair, BCrypt.gensalt());

        System.out.println("Génération du hash pour '" + passwordClair + "'...");
        System.out.println("Hash généré : " + passwordHashe);

        // 2. Mise à jour directe en base de données
        String sql = "UPDATE utilisateur SET mot_de_pass = ? WHERE login = ?";

        try (Connection cnx = SessionFactory.getInstance().getConnection();
             PreparedStatement pstmt = cnx.prepareStatement(sql)) {

            pstmt.setString(1, passwordHashe);
            pstmt.setString(2, login);

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ SUCCÈS : Le mot de passe de l'admin a été mis à jour avec le bon hash !");
            } else {
                System.out.println("❌ ERREUR : Aucun utilisateur trouvé avec le login '" + login + "'.");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR SQL : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
