package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.function.Consumer;

public class SidebarPanel extends JPanel {
    private final JPanel menuPanel;
    private final String userName;
    private final String userRole;

    public SidebarPanel(String userName, String userRole, String[] navItems, Consumer<String> onNavClick) {
        this.userName = userName;
        this.userRole = userRole;

        setPreferredSize(new Dimension(260, 0));
        setBackground(ColorPalette.SIDEBAR_BG); // Fond sombre
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(25, 0, 25, 0));

        // Logo
        JLabel logo = new JLabel("OralCare");
        logo.setForeground(Color.WHITE);
        logo.setFont(FontsPalette.TITLE);
        logo.setBorder(new EmptyBorder(0, 20, 40, 0));
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(logo);

        // Menu
        menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));

        for (String item : navItems) {
            // Utilise ButtonPalette.nav pour un style cohérent
            JButton b = ButtonPalette.nav(">   " + item);
            b.setAlignmentX(Component.LEFT_ALIGNMENT);
            b.addActionListener(e -> {
                resetNavButtons();
                highlightButton(b);
                onNavClick.accept(item);
            });
            menuPanel.add(b);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 2)));

            // Sélection par défaut du Dashboard
            if (item.equals("Dashboard")) highlightButton(b);
        }
        add(menuPanel);

        add(Box.createVerticalGlue());
        add(createProfilePanel());
    }

    /**
     * Applique le style de sélection (Bleu primaire avec texte blanc)
     */
    private void highlightButton(JButton b) {
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBackground(ColorPalette.PRIMARY); // Fond bleu
        b.setForeground(Color.WHITE); // Texte blanc pour contraste maximal
    }

    /**
     * Réinitialise les boutons au style gris clair sur fond sombre
     */
    private void resetNavButtons() {
        for (Component comp : menuPanel.getComponents()) {
            if (comp instanceof JButton btn) {
                btn.setOpaque(false);
                btn.setContentAreaFilled(false);
                btn.setBackground(ColorPalette.SIDEBAR_BG);
                // Utilise la nouvelle couleur de texte gris clair
                btn.setForeground(ColorPalette.SECONDARY_TEXT);
            }
        }
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout(10, 0));
        // Fond légèrement différent pour la zone profil
        profilePanel.setBackground(new Color(45, 50, 60));
        profilePanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        profilePanel.setMaximumSize(new Dimension(260, 75));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel profileInfo = new JPanel(new GridLayout(2, 1, 0, 0));
        profileInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(FontsPalette.BUTTON);

        JLabel roleLabel = new JLabel(userRole);
        // Utilise le gris clair pour le rôle
        roleLabel.setForeground(ColorPalette.SECONDARY_TEXT);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        profileInfo.add(nameLabel);
        profileInfo.add(roleLabel);
        profilePanel.add(profileInfo, BorderLayout.CENTER);

        // Bouton de déconnexion
        LogoutButton logoutBtn = new LogoutButton();
        logoutBtn.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Quitter la session ?", "Déconnexion", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                SwingUtilities.getWindowAncestor(this).dispose();
            }
        });
        profilePanel.add(logoutBtn, BorderLayout.EAST);

        return profilePanel;
    }

    private static class LogoutButton extends JButton {
        private boolean isHovered = false;

        public LogoutButton() {
            setPreferredSize(new Dimension(32, 32));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Utilise la couleur DANGER de la palette
            if (isHovered) {
                g2.setColor(ColorPalette.DANGER.brighter());
            } else {
                g2.setColor(ColorPalette.DANGER);
            }
            g2.fill(new Ellipse2D.Double(x, y, size, size));

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            int padding = size / 3;
            g2.drawLine(x + padding, y + padding, x + size - padding, y + size - padding);
            g2.drawLine(x + size - padding, y + padding, x + padding, y + size - padding);

            g2.dispose();
        }
    }
}