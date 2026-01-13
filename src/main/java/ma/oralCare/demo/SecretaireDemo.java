package ma.oralCare.demo;

import ma.oralCare.factory.SecretaireModuleFactory;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;

import javax.swing.*;

/**
 * Classe de démonstration pour le module secrétaire
 */
public class SecretaireDemo {

    public static void main(String[] args) {
        // Configuration du Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lancer le dashboard secrétaire
        SwingUtilities.invokeLater(() -> {
            try {
                SecretaireDashboard dashboard = new SecretaireDashboard();
                dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                dashboard.setSize(1200, 800);
                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);
                
                System.out.println("=== Démonstration du Module Secrétaire ===");
                System.out.println("1. Connectez-vous avec un login et mot de passe");
                System.out.println("2. Les données seront récupérées depuis la base de données");
                System.out.println("3. Les interfaces sont liées en temps réel");
                System.out.println("4. Utilisez la factory pour gérer les dépendances");
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Erreur lors du démarrage: " + e.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
