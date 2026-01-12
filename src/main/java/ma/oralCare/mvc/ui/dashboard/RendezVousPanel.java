package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Panel de gestion des rendez-vous pour les médecins (Figure 17).
 * Le bouton d'action est désormais placé sous le titre pour respecter la structure visuelle.
 */
public class RendezVousPanel extends JPanel {

    public RendezVousPanel() {
        // Mise en page cohérente avec les autres modules (espacement vertical de 25px)
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS)); // Alignement vertical
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Rendez-vous");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter rendez-vous");
        // Utilisation de setMaximumSize pour garantir la taille dans un BoxLayout
        btnAjouter.setMaximumSize(new Dimension(200, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT); // Aligne à gauche sous le titre

        // Assemblage de l'en-tête avec un espace de 15px entre les deux
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES RENDEZ-VOUS (Colonnes Figure 17) ---
        String[] columns = {"Date", "Heure", "Motif", "Actions"};

        // Données d'exemple pour l'aperçu
        Object[][] data = {
                {"30/12/2025", "10:30", "Consultation de suivi", "✏️  🗑️"},
                {"31/12/2025", "14:00", "Détartrage", "✏️  🗑️"},
                {"02/01/2026", "09:00", "Extraction simple", "✏️  🗑️"}
        };

        // Utilisation de la table personnalisée avec style et centrage automatique
        CustomTable table = new CustomTable(columns, data);

        // La maquette montre des lignes de séparation verticales pour ce tableau
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);


    }
}