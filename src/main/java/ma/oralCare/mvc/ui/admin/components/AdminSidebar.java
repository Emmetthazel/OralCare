package ma.oralCare.mvc.ui.admin.components;

import ma.oralCare.mvc.ui.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class AdminSidebar extends JPanel {

    // --- STYLE VISUEL (COULEURS CLAIRES) ---
    private final Color BG_COLOR = new Color(251, 251, 251);
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Color ACCENT_BG = new Color(232, 236, 251);      // Bleu clair sélection
    private final Color ACCENT_TEXT = new Color(58, 86, 189);      // Bleu texte actif
    private final Color SUPPORT_BG = new Color(34, 68, 184);       // Bleu foncé bouton spécial

    private JPanel menuContainer;
    private final MainFrame mainFrame;
    private JPanel activeBtn = null;

    public AdminSidebar(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setPreferredSize(new Dimension(260, 0));
        setBackground(BG_COLOR);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        // 1. SECTION HAUTE (Menu principal avec vos IDs)
        menuContainer = new JPanel();
        menuContainer.setOpaque(false);
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBorder(new EmptyBorder(20, 15, 10, 15));

        setupMenuButtons();
        add(menuContainer, BorderLayout.NORTH);

        // 2. SECTION BASSE (Support + Déconnexion)
        setupBottomSection();
    }

    private void setupMenuButtons() {
        /** * IDs ANALYSÉS DE VOTRE ANCIEN CODE :
         * Ces IDs doivent correspondre au CardLayout de votre MainFrame
         */
        addMenuButton(menuContainer, "Tableau de Bord", "DASHBOARD", "🏠", false);
        addMenuButton(menuContainer, "Gestion Utilisateurs", "USERS", "👥", false);

        addMenuButton(menuContainer, "Données Référentielles", "REF_DATA", "📊", false);
        addMenuButton(menuContainer, "Sécurité & Audit", "SECURITY", "�", false);
    }

    private void setupBottomSection() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(0, 15, 30, 15));

        // Bouton Support (Style plein bleu)
        addMenuButton(bottomPanel, "Support Technique", "SUPPORT", "🎧", true);

        bottomPanel.add(Box.createVerticalStrut(8));

        // Bouton Déconnexion (Utilise la logique handleLogout)
        addMenuButton(bottomPanel, "Déconnexion", "LOGOUT", "⚙️", false);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addMenuButton(JPanel container, String text, String viewID, String icon, boolean isSpecial) {
        JPanel btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSpecial) {
                    g2.setColor(SUPPORT_BG);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                } else if (activeBtn == this) {
                    g2.setColor(ACCENT_BG);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(230, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 16));
        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Couleurs de texte
        if (isSpecial) {
            lblText.setForeground(Color.WHITE);
            lblIcon.setForeground(Color.WHITE);
        } else {
            lblText.setForeground(activeBtn == btn ? ACCENT_TEXT : TEXT_COLOR);
        }

        btn.add(lblIcon);
        btn.add(lblText);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (viewID.equals("LOGOUT")) {
                    handleLogout();
                } else if (!viewID.equals("SUPPORT")) {
                    // Passage de l'ID à la MainFrame
                    mainFrame.changePage(viewID);
                    updateActiveStyle(btn, lblText, lblIcon);
                }
            }
        });

        container.add(btn);
        container.add(Box.createVerticalStrut(5));

        if (viewID.equals("DASHBOARD")) {
            activeBtn = btn;
            lblText.setForeground(ACCENT_TEXT);
        }
    }

    private void updateActiveStyle(JPanel selectedBtn, JLabel selectedLabel, JLabel selectedIcon) {
        activeBtn = selectedBtn;
        // Reset tous les labels du menu
        for (Component c : menuContainer.getComponents()) {
            if (c instanceof JPanel) {
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JLabel) sub.setForeground(TEXT_COLOR);
                }
            }
        }
        selectedLabel.setForeground(ACCENT_TEXT);
        selectedIcon.setForeground(ACCENT_TEXT);
        repaint();
    }

    private void handleLogout() {
        int choice = JOptionPane.showConfirmDialog(mainFrame,
                "Voulez-vous quitter la session administrateur ?",
                "Déconnexion", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            mainFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                new ma.oralCare.mvc.ui.auth.LoginFrame().setVisible(true);
            });
        }
    }
}