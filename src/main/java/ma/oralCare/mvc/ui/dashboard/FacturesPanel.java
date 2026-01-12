package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion des factures (Figure 15).
 * Le bouton est désormais placé sous le titre conformément à la maquette.
 */
public class FacturesPanel extends JPanel {

    public FacturesPanel() {
        // Layout principal avec espacement vertical de 25px
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Factures");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter Facture");
        btnAjouter.setMaximumSize(new Dimension(180, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre les deux
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES FACTURES (Colonnes Figure 15) ---
        String[] columns = {
                "Nom", "Prénom", "Montant global", "Date d'émission",
                "Status", "Montant A payer", "Reste", "Actions"
        };

        // Données fictives basées sur votre aperçu
        Object[][] data = {
                {"El Amrani", "Yassine", "1200 DH", "28/12/2025", "Partiel", "500 DH", "700 DH", "✏️ 🗑️ ⬇️"},
                {"Mansouri", "Sanaa", "400 DH", "30/12/2025", "Payé", "400 DH", "0 DH", "✏️ 🗑️ ⬇️"}
        };

        // Utilisation de la CustomTable (Centrage Header + Cellules inclus)
        CustomTable table = new CustomTable(columns, data);
        table.setShowVerticalLines(true); // Lignes verticales présentes sur la Figure 15

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Ajout des composants au panel principal
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
}