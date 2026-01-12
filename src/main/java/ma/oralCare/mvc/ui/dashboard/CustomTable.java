package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Composant de table personnalisé et réutilisable.
 * Gère automatiquement le centrage du header, du contenu et le style visuel.
 */
public class CustomTable extends JTable {

    public CustomTable(String[] columns, Object[][] data) {
        // 1. Modèle de données : Empêche l'édition directe des cellules
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.setModel(model);

        // 2. Application des styles visuels et de l'alignement
        applyStyle();
    }

    private void applyStyle() {
        // --- STYLE GÉNÉRAL DU CORPS DE LA TABLE ---
        setRowHeight(50);
        setFont(FontsPalette.LABEL);
        setGridColor(ColorPalette.CARD_BORDER);
        setShowVerticalLines(false); // Par défaut (peut être modifié via setShowVerticalLines(true))
        setSelectionBackground(new Color(232, 242, 254)); // Bleu très clair au survol
        setSelectionForeground(ColorPalette.TEXT);
        setFocusable(false);
        setIntercellSpacing(new Dimension(0, 0));

        // --- STYLE ET CENTRAGE DE L'EN-TÊTE (HEADER) ---
        JTableHeader header = getTableHeader();
        header.setFont(FontsPalette.BUTTON);
        header.setBackground(Color.WHITE);
        header.setForeground(ColorPalette.TEXT);
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorPalette.CARD_BORDER));

        // Renderer pour centrer le texte du Header
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // --- ALIGNEMENT CENTRÉ DU CONTENU DES CELLULES ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Appliquer le centrage à chaque colonne
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}