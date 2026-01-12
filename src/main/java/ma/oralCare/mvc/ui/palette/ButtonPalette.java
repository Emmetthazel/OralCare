package ma.oralCare.mvc.ui.palette;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class ButtonPalette {

    /**
     * Configuration de base pour tous les boutons
     */
    private static JButton baseButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FontsPalette.BUTTON);
        btn.setFocusPainted(false);      // Supprime le cadre de focus en pointillés
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);            // Transparent par défaut
        btn.setBorderPainted(false);     // Pas de bordure par défaut
        btn.setContentAreaFilled(false); // Pas de fond par défaut
        btn.setMargin(new Insets(0, 0, 0, 0)); // Supprime les marges système (Windows/Mac)
        return btn;
    }

    /**
     * 🔹 Bouton de NAVIGATION (Sidebar)
     * Aligné à gauche, colle au bord du panel, texte avec retrait de 20px.
     */
    public static JButton nav(String text) {
        JButton btn = baseButton(text);
        btn.setForeground(ColorPalette.SECONDARY_TEXT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        // Retrait interne de 20px à gauche pour aligner le texte avec le Logo et Profil
        // Le 0 à gauche du panel parent (sidebar) permettra au bleu de "coller" au bord
        btn.setBorder(new EmptyBorder(12, 20, 12, 10));

        // Permet au bouton de s'étirer sur toute la largeur de la sidebar
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Gestion des effets visuels (Hover)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // On applique l'effet seulement si le bouton n'est pas déjà sélectionné
                if (btn.getBackground() != ColorPalette.PRIMARY) {
                    btn.setContentAreaFilled(true);
                    btn.setOpaque(true);
                    btn.setBackground(ColorPalette.SIDEBAR_BUTTON_HOVER);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.getBackground() != ColorPalette.PRIMARY) {
                    btn.setContentAreaFilled(false);
                    btn.setOpaque(false);
                }
            }
        });
        return btn;
    }

    /**
     * 🔹 Bouton PRIMAIRE (ex: Se connecter)
     */
    public static JButton primary(String text) {
        JButton btn = baseButton(text);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(ColorPalette.BUTTON_LOGIN);
        btn.setForeground(ColorPalette.TEXT);
        btn.setBorderPainted(true);
        btn.setBorder(new LineBorder(ColorPalette.BUTTON_LOGIN, 1, true));
        btn.setPreferredSize(new Dimension(140, 40));
        return btn;
    }

    /**
     * 🔹 Bouton SECONDAIRE (ex: Se déconnecter / Annuler)
     */
    public static JButton secondary(String text) {
        JButton btn = baseButton(text);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(ColorPalette.BUTTON_CANCEL_BG);
        btn.setForeground(ColorPalette.BUTTON_CANCEL_TEXT);
        btn.setBorderPainted(true);
        btn.setBorder(new LineBorder(ColorPalette.BUTTON_CANCEL_BORDER, 1, true));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    /**
     * 🔹 Méthode générique pour les boutons d'action du Dashboard
     */
    private static JButton custom(String text, Color bg, Color fg, int width, int height) {
        JButton btn = baseButton(text);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setPreferredSize(new Dimension(width, height));
        btn.setBorder(new LineBorder(bg, 1, true));
        return btn;
    }

    public static JButton dashboardActionBlue(String text) {
        return custom(text, ColorPalette.ACTION_BLUE, Color.WHITE, 200, 120);
    }

    public static JButton dashboardActionPurple(String text) {
        return custom(text, ColorPalette.ACTION_PURPLE, Color.WHITE, 200, 120);
    }

    public static JButton dashboardActionGreen(String text) {
        return custom(text, ColorPalette.ACTION_GREEN, Color.WHITE, 200, 120);
    }

    public static JButton dashboardActionYellow(String text) {
        return custom(text, ColorPalette.ACTION_YELLOW, Color.WHITE, 200, 120);
    }
}