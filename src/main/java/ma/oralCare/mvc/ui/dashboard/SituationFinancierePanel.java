package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion de la situation financière (Figure 20).
 * Le bouton d'action est désormais placé sous le titre pour une cohérence visuelle totale.
 */
public class SituationFinancierePanel extends JPanel {

    public SituationFinancierePanel() {
        // Layout principal avec espacement vertical de 25px
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Situation Financière");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnCreer = ButtonPalette.primary("Créer situation financière");
        // Utilisation de setMaximumSize pour garantir la taille dans un BoxLayout
        btnCreer.setMaximumSize(new Dimension(230, 40));
        btnCreer.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre le titre et le bouton
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnCreer);

        // --- TABLEAU (Colonnes selon Figure 20) ---
        String[] columns = {"Nom", "Prénom", "Status", "Note", "N° Facture", "Actions"};

        Object[][] data = {
                {"Berrada", "Ahmed", "Payé", "Consultation simple", "FAC-2025-001", "✏️  🗑️"},
                {"Alaoui", "Sami", "En attente", "Détartrage complet", "FAC-2025-002", "✏️  🗑️"}
        };

        // Utilisation de la CustomTable (Centrage Header + Cellules inclus)
        CustomTable table = new CustomTable(columns, data);
        table.setShowVerticalLines(true); // Grille visible selon maquette (Figure 20)

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}