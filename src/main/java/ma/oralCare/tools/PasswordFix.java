package ma.oralCare.tools;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.service.modules.auth.impl.PasswordEncoderImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

/**
 * Outil pour vérifier et corriger les mots de passe des utilisateurs
 */
public class PasswordFix {
    
    public static void main(String[] args) {
        try {
            // Initialisation
            UtilisateurRepositoryImpl repo = new UtilisateurRepositoryImpl();
            PasswordEncoderImpl encoder = new PasswordEncoderImpl();
            
            String login = "h.ahlam";
            
            // 1. Vérifier l'utilisateur actuel
            System.out.println("=== VÉRIFICATION DE L'UTILISATEUR ===");
            Optional<Utilisateur> userOpt = repo.findByLogin(login);
            
            if (userOpt.isEmpty()) {
                System.out.println("Utilisateur non trouvé: " + login);
                return;
            }
            
            Utilisateur user = userOpt.get();
            System.out.println("Utilisateur trouvé: " + user.getLogin());
            System.out.println("Nom: " + user.getNom() + " " + user.getPrenom());
            System.out.println("Mot de passe actuel (hash): " + user.getMotDePass());
            
            // 2. Tester différents mots de passe possibles
            String[] passwordsToTry = {"123", "password", "admin", "secret", "123456", "ahlam"};
            
            System.out.println("\n=== TEST DES MOTS DE PASSE ===");
            for (String pwd : passwordsToTry) {
                boolean matches = encoder.matches(pwd, user.getMotDePass());
                System.out.println("Test '" + pwd + "': " + (matches ? "✅ CORRECT" : "❌ INCORRECT"));
                
                if (matches) {
                    System.out.println(">>> MOT DE PASSE TROUVÉ: " + pwd);
                    break;
                }
            }
            
            // 3. Si aucun mot de passe ne fonctionne, en créer un nouveau
            System.out.println("\n=== CRÉATION D'UN NOUVEAU MOT DE PASSE ===");
            String newPassword = "123"; // Mot de passe par défaut
            String hashedPassword = encoder.encode(newPassword);
            
            System.out.println("Nouveau hash pour '" + newPassword + "': " + hashedPassword);
            
            // 4. Mettre à jour la base de données
            Connection conn = null;
            try {
                ma.oralCare.conf.SessionFactory sessionFactory = ma.oralCare.conf.SessionFactory.getInstance();
                conn = sessionFactory.getConnection();
                conn.setAutoCommit(false);
                
                String updateSql = "UPDATE utilisateur SET mot_de_pass = ? WHERE login = ?";
                PreparedStatement stmt = conn.prepareStatement(updateSql);
                stmt.setString(1, hashedPassword);
                stmt.setString(2, login);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    conn.commit();
                    System.out.println("✅ Mot de passe mis à jour avec succès!");
                    System.out.println("✅ Nouveau mot de passe pour " + login + ": " + newPassword);
                } else {
                    conn.rollback();
                    System.out.println("❌ Échec de la mise à jour");
                }
                
                stmt.close();
                
            } catch (Exception e) {
                if (conn != null) {
                    conn.rollback();
                }
                System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
            
            // 5. Vérifier la mise à jour
            System.out.println("\n=== VÉRIFICATION FINALE ===");
            Optional<Utilisateur> updatedUserOpt = repo.findByLogin(login);
            if (updatedUserOpt.isPresent()) {
                Utilisateur updatedUser = updatedUserOpt.get();
                boolean finalCheck = encoder.matches(newPassword, updatedUser.getMotDePass());
                System.out.println("Vérification finale: " + (finalCheck ? "✅ SUCCÈS" : "❌ ÉCHEC"));
                if (finalCheck) {
                    System.out.println("✅ L'utilisateur " + login + " peut maintenant se connecter avec: " + newPassword);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
