package ma.oralCare.mvc.ui.admin.components;

import ma.oralCare.mvc.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * AdminSidebar : Barre de navigation latérale pour l'Administrateur.
 * Adopte le nouveau style épuré et professionnel sans logo.
 */
public class AdminSidebar extends JPanel {

    // Couleurs de style (Identiques au profil Médecin pour la cohérence)
    private final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Bleu nuit
    private final Color HOVER_COLOR = new Color(52, 73, 94);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(41, 128, 185); // Bleu OralCare

    private JPanel menuContainer;
    private final MainFrame mainFrame;

    public AdminSidebar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(250, 0));
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // --- 1. SECTION HAUTE ---
        // Espace vide pour un look moderne (remplace l'ancien logo)
        add(Box.createVerticalStrut(20), BorderLayout.NORTH);

        // 2. Conteneur des boutons
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(new EmptyBorder(20, 0, 10, 0));

        setupMenuButtons();

        add(new JScrollPane(menuContainer) {{
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(null);
            getVerticalScrollBar().setPreferredSize(new Dimension(0,0)); // Cache le scroll
        }}, BorderLayout.CENTER);

        // 3. Bouton Déconnexion
        setupLogoutButton();
    }

    private void setupMenuButtons() {
        // Liste des modules Administrateur (Texte affiché, ID du CardLayout)
        addMenuButton("Tableau de Bord", "DASHBOARD");
        addMenuButton("Gestion Utilisateurs", "USERS");
        addMenuButton("Gestion des Rôles", "ROLES");
        addMenuButton("Données Référentielles", "REF_DATA");
        addMenuButton("Sécurité & Sauvegarde", "SECURITY");
        addMenuButton("Logs & Audit", "LOGS");
    }

    private void addMenuButton(String text, String viewID) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        btn.setMaximumSize(new Dimension(250, 55));
        btn.setBackground(BACKGROUND_COLOR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.add(lbl);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Appel de la méthode changePage de votre MainFrame
                mainFrame.changePage(viewID);
                highlightButton(btn);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.getBackground() != ACCENT_COLOR) {
                    btn.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.getBackground() != ACCENT_COLOR) {
                    btn.setBackground(BACKGROUND_COLOR);
                }
            }
        });

        menuContainer.add(btn);

        // Sélectionner par défaut le premier bouton (Dashboard)
        if (viewID.equals("DASHBOARD")) {
            highlightButton(btn);
        }
    }

    private void highlightButton(JPanel selectedBtn) {
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(BACKGROUND_COLOR);
            }
        }
        selectedBtn.setBackground(ACCENT_COLOR);
    }

    private void setupLogoutButton() {
        JButton btnLogout = new JButton("🚪 Déconnexion");
        btnLogout.setPreferredSize(new Dimension(250, 60));
        btnLogout.setBackground(new Color(192, 57, 43)); // Rouge discret
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Voulez-vous quitter la session administrateur ?",
                    "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                // Retour au LoginFrame
                SwingUtilities.invokeLater(() -> {
                    ma.oralCare.mvc.ui.auth.LoginFrame loginFrame = new ma.oralCare.mvc.ui.auth.LoginFrame();
                    loginFrame.setVisible(true);
                });
            }
        });

        add(btnLogout, BorderLayout.SOUTH);
    }
}