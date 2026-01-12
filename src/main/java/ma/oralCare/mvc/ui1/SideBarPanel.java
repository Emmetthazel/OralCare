package ma.oralCare.mvc.ui1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SideBarPanel : Barre de navigation latérale.
 * Permet de basculer entre les différents modules de l'application.
 */
public class SideBarPanel extends JPanel {

    private final MainFrame mainFrame;
    private final String userRole;
    private JPanel menuContainer;

    // Couleurs de style
    private final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Bleu nuit professionnel
    private final Color HOVER_COLOR = new Color(52, 73, 94);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(41, 128, 185); // Bleu OralCare

    public SideBarPanel(MainFrame mainFrame, String role) {
        this.mainFrame = mainFrame;
        this.userRole = role;

        setPreferredSize(new Dimension(250, 0));
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // --- 1. LOGO SUPPRIMÉ ---
        // La partie NORTH a été retirée pour supprimer le texte "ORAL CARE"

        // 2. Conteneur des boutons de menu
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        // Ajout d'une marge en haut pour compenser la suppression du logo
        menuContainer.setBorder(new EmptyBorder(30, 0, 10, 0));

        setupMenuButtons();

        add(new JScrollPane(menuContainer) {{
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(null);
        }}, BorderLayout.CENTER);

        // 3. Bouton Déconnexion en bas
        setupLogoutButton();
    }

    private void setupMenuButtons() {
        // --- BOUTONS COMMUNS ---
        addMenuButton("Tableau de Bord", "DASHBOARD");

        // --- BOUTONS SECRÉTAIRE / GESTION ---
        addMenuButton("Gestion Patients", "PATIENTS");
        addMenuButton("Agenda", "VISUAL_AGENDA");
        addMenuButton("Liste des RDV", "RDV");

        // --- BOUTONS MÉDECIN ---
        if (userRole != null && (userRole.equalsIgnoreCase("MEDECIN") || userRole.equalsIgnoreCase("DOCTOR"))) {
            addMenuButton("Consultations", "CONSULTATIONS");
            addMenuButton("Actes & Soins", "ACTES");
        }

        // --- AUTRES MODULES ---
        addMenuButton("Dossiers Médicaux", "DOSSIERS");
        addMenuButton("Caisse & Factures", "CAISSE");

        // --- ADMIN ---
        if (userRole != null && userRole.equalsIgnoreCase("ADMIN")) {
            addMenuButton("Utilisateurs", "ADMIN_USERS");
        }
    }

    private void addMenuButton(String text, String viewID) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        btn.setMaximumSize(new Dimension(250, 50));
        btn.setBackground(BACKGROUND_COLOR);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lbl = new JLabel(text);
        lbl.setForeground(TEXT_COLOR);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.add(lbl);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showView(viewID);
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
        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.setPreferredSize(new Dimension(250, 50));
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(mainFrame, "Quitter l'application ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        add(btnLogout, BorderLayout.SOUTH);
    }
}