package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion des antécédents pour les médecins (Figure 22).
 * Le bouton d'action est désormais placé sous le titre.
 */
public class AntecedentsPanel extends JPanel {

    public AntecedentsPanel() {
        // Layout principal avec espacement vertical de 25px
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Antécédants");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter antécédent");
        btnAjouter.setMaximumSize(new Dimension(200, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre les deux
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES ANTÉCÉDANTS ---
        // Configuration des colonnes selon la Figure 22
        String[] columns = {"Nom", "Prénom", "Type", "Description", "Actions"};

        // Données d'exemple
        Object[][] data = {
                {"Berrada", "Ahmed", "Allergie", "Pénicilline", "✏️  🗑️"},
                {"Alaoui", "Sami", "Chronique", "Diabète Type 2", "✏️  🗑️"}
        };

        // Utilisation de la table personnalisée (Style et centrage inclus)
        CustomTable table = new CustomTable(columns, data);

        // La maquette montre des lignes verticales pour ce tableau spécifique
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}