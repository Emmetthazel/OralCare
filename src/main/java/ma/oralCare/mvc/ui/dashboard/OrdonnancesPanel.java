package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion des ordonnances (Figure 18).
 * Le bouton d'action est désormais placé sous le titre.
 */
public class OrdonnancesPanel extends JPanel {

    public OrdonnancesPanel() {
        // Layout principal avec espacement vertical de 25px
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Ordonnances");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter ordonnance");
        btnAjouter.setMaximumSize(new Dimension(200, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre les deux
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES ORDONNANCES (Colonnes Figure 18) ---
        String[] columns = {"Nom", "Prénom", "Date de prescription", "Médicaments", "Actions"};

        // Données d'exemple basées sur la Figure 18
        Object[][] data = {
                {"Berrada", "Ahmed", "30/12/2025", "Amoxicilline, Paracétamol", "✏️  🗑️"},
                {"Alaoui", "Sami", "28/12/2025", "Ibuprofène", "✏️  🗑️"}
        };

        // Utilisation de la CustomTable (Centrage automatique inclus)
        CustomTable table = new CustomTable(columns, data);

        // La Figure 18 montre des lignes verticales
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}