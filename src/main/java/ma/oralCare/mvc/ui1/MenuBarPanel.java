package ma.oralCare.mvc.ui1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * MenuBarPanel : Barre de menus universelle.
 * Utilise l'interface Navigatable pour fonctionner avec n'importe quelle Frame.
 */
public class MenuBarPanel extends JMenuBar {

    private final Navigatable parentFrame;
    private final String userName;
    private final String role;

    public MenuBarPanel(Navigatable frame, String role, String userName) {
        this.parentFrame = frame;
        this.role = role;
        this.userName = userName;

        // --- STYLE DE LA BARRE ---
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // --- MENUS ---
        setupAccountMenu();
        setupDisplayMenu();
        setupHelpMenu();
    }

    private void setupAccountMenu() {
        JMenu accountMenu = new JMenu("Compte");
        accountMenu.setMnemonic(KeyEvent.VK_C);

        JMenuItem profileItem = new JMenuItem("Modifier profil");
        profileItem.addActionListener(e -> parentFrame.showView("PROFILE"));

        JMenuItem passwordItem = new JMenuItem("Changer mot de passe");

        JMenuItem logoutItem = new JMenuItem("Déconnexion");
        logoutItem.addActionListener(e -> handleLogout());

        accountMenu.add(profileItem);
        accountMenu.add(passwordItem);
        accountMenu.addSeparator();
        accountMenu.add(logoutItem);

        add(accountMenu);
    }

    private void setupDisplayMenu() {
        JMenu displayMenu = new JMenu("Affichage");
        displayMenu.add(new JMenuItem("Changer thème clair / sombre"));
        displayMenu.add(new JMenuItem("Réorganiser panneaux"));
        add(displayMenu);
    }

    private void setupHelpMenu() {
        JMenu helpMenu = new JMenu("?");
        JMenuItem aboutItem = new JMenuItem("À propos");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog((Component) parentFrame,
                "OralCare v1.2.0\nLogiciel de gestion de cabinet dentaire\n© 2026",
                "À propos", JOptionPane.INFORMATION_MESSAGE));

        helpMenu.add(new JMenuItem("Aide en ligne"));
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);
        add(helpMenu);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog((Component) parentFrame,
                "Voulez-vous fermer votre session ?", "Déconnexion",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            parentFrame.dispose();
            System.out.println("Session fermée pour : " + userName);
        }
    }

    public void adaptToWidth(int width) {}
}