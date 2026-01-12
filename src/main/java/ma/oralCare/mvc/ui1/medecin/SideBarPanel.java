package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SideBarPanel : Barre de navigation latérale pour le profil Médecin.
 * Le titre "ORAL CARE" a été supprimé pour un look plus épuré.
 */
public class SideBarPanel extends JPanel {

    // Couleurs de style
    private final Color BACKGROUND_COLOR = new Color(44, 62, 80); // Bleu nuit
    private final Color HOVER_COLOR = new Color(52, 73, 94);
    private final Color TEXT_COLOR = Color.WHITE;
    private final Color ACCENT_COLOR = new Color(41, 128, 185); // Bleu de sélection

    private JPanel menuContainer;
    private MainFrame mainFrame;

    public SideBarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(250, 0));
        setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout());

        // --- 1. SECTION HAUTE (LOGO SUPPRIMÉ) ---
        // On ne crée plus le headerPanel avec lblLogo.
        // On remplace par un espace vide (strut) pour ne pas coller au bord haut.
        add(Box.createVerticalStrut(20), BorderLayout.NORTH);

        // 2. Conteneur des boutons
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        // Marge de 20px en haut pour l'esthétique
        menuContainer.setBorder(new EmptyBorder(20, 0, 10, 0));

        setupMenuButtons();

        add(new JScrollPane(menuContainer) {{
            setOpaque(false);
            getViewport().setOpaque(false);
            setBorder(null);
            // Cacher la barre de défilement pour un look plus "App"
            getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        }}, BorderLayout.CENTER);

        // 3. Bouton Déconnexion
        setupLogoutButton();
    }

    private void setupMenuButtons() {
        // Les noms correspondent exactement aux clés CardLayout de votre MainFrame
        addMenuButton("Dashboard");
        addMenuButton("Mes RDV");
        addMenuButton("Patients");
        addMenuButton("Dossiers Médicaux");
        addMenuButton("Consultations");
        addMenuButton("Actes & Soins");
        addMenuButton("Ordonnances");
        addMenuButton("Certificats Médicaux");
        addMenuButton("Situations Financières");
    }

    private void addMenuButton(String text) {
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
                mainFrame.showView(text);
                highlightButton(btn); // Optionnel : pour marquer le bouton actif
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if(btn.getBackground() != ACCENT_COLOR) {
                    btn.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(btn.getBackground() != ACCENT_COLOR) {
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
        btnLogout.setPreferredSize(new Dimension(250, 60));
        btnLogout.setBackground(new Color(192, 57, 43)); // Rouge discret
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(mainFrame,
                    "Voulez-vous vraiment vous déconnecter ?",
                    "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                // Ici, vous pourriez ré-ouvrir la fenêtre de Login
                System.exit(0);
            }
        });

        add(btnLogout, BorderLayout.SOUTH);
    }
}