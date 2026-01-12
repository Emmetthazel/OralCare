package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.dashboard.Dialog.AddPatientDialog;
import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Page de gestion des patients (Module complet).
 * Structure : Titre -> Bouton Ajouter -> Tableau des patients.
 */
public class PatientsPanel extends JPanel {

    public PatientsPanel() {
        // Mise en page standardisée (espacement 25px)
        setLayout(new BorderLayout(0, 25));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE DU MODULE (TITRE AU-DESSUS DU BOUTON) ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Patients");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter patient");
        btnAjouter.setMaximumSize(new Dimension(180, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Action : Ouvrir le dialogue d'ajout
        btnAjouter.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            if (parent instanceof Frame) {
                new AddPatientDialog((Frame) parent).setVisible(true);
            }
        });

        // Assemblage de l'en-tête
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(15));
        topPanel.add(btnAjouter);

        // --- TABLEAU DES PATIENTS (Colonnes Figure 14) ---
        String[] columns = {
                "Nom", "Prénom", "Date Naissance", "Numéro", "Sexe", "Adresse", "Actions"
        };

        // Données d'exemple
        Object[][] data = {
                {"El Amrani", "Yassine", "12/05/1988", "0661223344", "M", "Rabat, Agdal", "✏️  🗑️"},
                {"Bennani", "Sanaa", "24/11/1992", "0670556677", "F", "Salé, Tabriquet", "✏️  🗑️"},
                {"Alami", "Ahmed", "15/01/1975", "0661998877", "M", "Casablanca, Maarif", "✏️  🗑️"}
        };

        // CustomTable gère automatiquement le centrage et le style
        CustomTable table = new CustomTable(columns, data);
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Classe interne CustomTable pour harmoniser l'affichage des patients.
     */
    private static class CustomTable extends JTable {
        public CustomTable(String[] columns, Object[][] data) {
            super(data, columns);

            // Style de l'en-tête (Header)
            JTableHeader header = getTableHeader();
            header.setFont(FontsPalette.LABEL.deriveFont(Font.BOLD));
            header.setBackground(new Color(248, 249, 250));
            header.setForeground(ColorPalette.TEXT);
            header.setPreferredSize(new Dimension(0, 45));
            header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.CARD_BORDER));

            // Style des cellules
            setFont(FontsPalette.LABEL);
            setRowHeight(50);
            setGridColor(ColorPalette.CARD_BORDER);
            setSelectionBackground(new Color(232, 242, 254));
            setSelectionForeground(ColorPalette.TEXT);
            setIntercellSpacing(new Dimension(5, 0));

            // Centrage du texte dans toutes les colonnes
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < getColumnCount(); i++) {
                getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }
}