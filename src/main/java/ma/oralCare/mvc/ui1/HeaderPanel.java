package ma.oralCare.mvc.ui1;

import javax.swing.*;
import java.awt.*;

/**
 * HeaderPanel : Bannière supérieure simplifiée.
 * Affiche le module actif, les informations de session et les notifications.
 */
public class HeaderPanel extends JPanel {

    private JLabel lblModuleTitle;
    private JLabel lblUserName;
    private JLabel lblUserRole;
    private JLabel lblNotifications;

    // Palette de couleurs
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private final Color TEXT_DARK = new Color(44, 62, 80);
    private final Color NOTIF_RED = new Color(231, 76, 60);

    public HeaderPanel(String userName, String role) {
        // Configuration structurelle
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(0, 70)); // Hauteur légèrement réduite car moins de données
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // --- SECTION GAUCHE : Titre du Module ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 15));
        leftPanel.setOpaque(false);

        lblModuleTitle = new JLabel("TABLEAU DE BORD");
        lblModuleTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblModuleTitle.setForeground(PRIMARY_BLUE);
        leftPanel.add(lblModuleTitle);

        // --- SECTION CENTRALE : Notifications rapides ---
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 22));
        centerPanel.setOpaque(false);

        lblNotifications = new JLabel("🔔 0 RDV aujourd'hui");
        lblNotifications.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblNotifications.setForeground(NOTIF_RED);
        lblNotifications.setVisible(false); // Masqué par défaut s'il n'y a rien
        centerPanel.add(lblNotifications);

        // --- SECTION DROITE : Identité Utilisateur ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);

        // Bloc texte (Nom et Rôle)
        JPanel userTexts = new JPanel(new GridLayout(2, 1));
        userTexts.setOpaque(false);

        lblUserName = new JLabel(userName);
        lblUserName.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblUserName.setForeground(TEXT_DARK);
        lblUserName.setHorizontalAlignment(SwingConstants.RIGHT);

        lblUserRole = new JLabel(role);
        lblUserRole.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblUserRole.setForeground(Color.GRAY);
        lblUserRole.setHorizontalAlignment(SwingConstants.RIGHT);

        userTexts.add(lblUserName);
        userTexts.add(lblUserRole);

        // Avatar stylisé (Cercle avec initiale)
        JLabel avatar = new JLabel(userName.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(45, 45));
        avatar.setOpaque(true);
        avatar.setBackground(PRIMARY_BLUE);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 20));

        rightPanel.add(userTexts);
        rightPanel.add(avatar);

        // Assemblage final
        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Met à jour le titre du module (appelé par MainFrame lors de la navigation)
     */
    public void setModuleTitle(String title) {
        lblModuleTitle.setText(title.toUpperCase());
    }

    /**
     * Met à jour dynamiquement le compteur de notifications
     */
    public void setNotifications(int count) {
        if (count > 0) {
            lblNotifications.setText("🔔 " + count + " notification(s) en attente");
            lblNotifications.setVisible(true);
        } else {
            lblNotifications.setVisible(false);
        }
        revalidate(); // Force la mise à jour visuelle
    }
}