package ma.oralCare.mvc.ui.mainframe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * TableCellRenderer pour les statuts avec cercle parfait
 * Point parfaitement rond de 8px avec espacement optimal
 */
public class StatusDotRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    private JLabel statusText;
    private Color dotColor;
    
    public StatusDotRenderer() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 6, 0)); // Espacement de 6px entre point et texte
        setOpaque(false);
        
        statusText = new JLabel();
        statusText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(statusText);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                  boolean hasFocus, int row, int column) {
        String status = (value != null) ? value.toString() : "";
        
        if (status.contains("En ligne")) {
            dotColor = new Color(0x10B981); // Vert fluo
            statusText.setText("En ligne");
            statusText.setForeground(new Color(0x059669));
        } else {
            dotColor = new Color(0x9CA3AF); // Gris clair
            statusText.setText("Hors ligne");
            statusText.setForeground(new Color(0x6B7280));
        }
        
        // Padding interne pour éviter que le texte colle aux lignes
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setAlignmentY(Component.CENTER_ALIGNMENT);
        
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Dessiner le cercle parfait de 8px
        int dotX = 12; // Position X du centre du point
        int dotY = getHeight() / 2; // Centré verticalement
        int dotRadius = 4; // Rayon de 4px = diamètre de 8px
        
        g2.setColor(dotColor);
        Ellipse2D dot = new Ellipse2D.Float(dotX - dotRadius, dotY - dotRadius, 
                                            dotRadius * 2, dotRadius * 2);
        g2.fill(dot);
        
        g2.dispose();
    }
}
