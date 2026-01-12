package ma.oralCare.mvc.ui.secretaire;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale pour l'interface Secrétaire.
 * Gère la navigation entre le Dashboard, la gestion des patients, l'agenda et la caisse.
 */
public class MainSecretaireFrame extends JFrame {
    private JPanel sideMenu;
    private JPanel contentArea;
    private CardLayout cardLayout; // Moteur de basculement entre les vues

    public MainSecretaireFrame() {
        setTitle("Gestion Cabinet Dentaire - Espace Secrétaire");
        setSize(1300, 850); // Légèrement agrandi pour le confort
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Initialisation du Layout de contenu (Zone Droite)
        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        // 2. Ajout des panneaux (Pages) au CardLayout
        // Chaque nom ("Patients", "Agenda", etc.) doit correspondre à l'action des boutons
        contentArea.add(new DashboardPanel(), "Dashboard");        contentArea.add(new PatientPanel(), "Patients");
        contentArea.add(new AgendaPanel(), "Agenda");
        contentArea.add(new CaissePanel(), "Caisse"); // Ajouté conformément à votre demande

        // 3. Barre Latérale (Menu Gauche)
        sideMenu = new JPanel();
        sideMenu.setBackground(new Color(45, 52, 54));
        sideMenu.setPreferredSize(new Dimension(260, 800));
        sideMenu.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

        // Titre du Menu / Logo
        JLabel menuTitle = new JLabel("ORAL CARE");
        menuTitle.setForeground(new Color(236, 240, 241));
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        menuTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));
        sideMenu.add(menuTitle);

        // 4. Création des boutons de navigation
        // L'argument cardName doit être identique aux noms ajoutés dans contentArea
        addButton("Tableau de Bord", "Dashboard");
        addButton("Gestion Patients", "Patients");
        addButton("Agenda & RDV", "Agenda");
        addButton("Caisse & Facturation", "Caisse");

        // Ajout des composants à la fenêtre
        add(sideMenu, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    /**
     * Méthode utilitaire pour créer un bouton de menu stylisé avec action CardLayout
     */
    private void addButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(240, 50));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(45, 52, 54));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Action : affiche le panel correspondant au nom fourni
        btn.addActionListener(e -> cardLayout.show(contentArea, cardName));

        // Effet visuel au survol (Hover effect)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(63, 72, 75));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(45, 52, 54));
            }
        });

        sideMenu.add(btn);
    }

    public static void main(String[] args) {
        // Application du thème système (Windows, Mac ou Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new MainSecretaireFrame().setVisible(true);
        });
    }
}