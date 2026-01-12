/*package ma.oralCare;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui.auth.LoginFrame;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.service.modules.auth.impl.AuthServiceImpl;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class OralCareApp {

    public static void main(String[] args) {

        // --- 1. Test connexion JDBC ---
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            System.out.println("Driver JDBC chargé avec succès : " + conn.getMetaData().getDriverName());
            System.out.println("Nouvelle connexion JDBC établie avec succès !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données.", e);
        }

        // --- 2. Instanciation Repository et Service ---
        UtilisateurRepositoryImpl utilisateurRepo = new UtilisateurRepositoryImpl();
        AuthServiceImpl authService = new AuthServiceImpl(utilisateurRepo);


        // --- 7. Ici tu peux lancer ton UI Login ---
        // Par exemple : LoginUI.launch();
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginUI = new LoginFrame(); // pas besoin de modifier LoginUI
            loginUI.setVisible(true);

            // --- Création du controller pour gérer la logique du login ---
        });
    }
}
*/
