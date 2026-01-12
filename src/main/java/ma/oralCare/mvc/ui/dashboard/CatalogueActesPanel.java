package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion du catalogue des actes.
 * Le bouton d'action est désormais placé sous le titre pour respecter la structure visuelle.
 */
public class CatalogueActesPanel extends JPanel {

    public CatalogueActesPanel() {
        // Layout principal avec espacement vertical de 25px
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Catalogue des actes");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter acte");
        btnAjouter.setMaximumSize(new Dimension(180, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre les deux
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES ACTES ---
        String[] columns = {"Nom", "Prénom", "Description", "Type", "Coût", "Actions"};
        Object[][] data = {
                {"Extraction", "Simple", "Extraction dentaire standard", "Chirurgie", "300 DH", "✏️  🗑️"},
                {"Détartrage", "Complet", "Nettoyage profondeur", "Soin", "400 DH", "✏️  🗑️"}
        };

        // Utilisation de la CustomTable (Style et centrage automatique inclus)
        CustomTable table = new CustomTable(columns, data);
        table.setShowVerticalLines(false); // Design épuré sans lignes verticales pour le catalogue

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}