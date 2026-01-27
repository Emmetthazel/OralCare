package ma.oralCare.mvc.ui.mainframe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;

/**
 * TableCellRenderer pour boutons de taille fixe centrés
 * Le bouton ne remplit pas la cellule et a des dimensions fixes
 */
public class FixedSizeButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    
    public FixedSizeButtonRenderer() {
        setOpaque(false);
        setLayout(new GridBagLayout()); // GridBagLayout pour un centrage parfait
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                  boolean hasFocus, int row, int column) {
        removeAll(); // Nettoyer avant d'ajouter
        
        FixedSizeButton button = new FixedSizeButton("  Voir Détails");
        
        // Centrer le bouton avec GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        
        add(button, gbc);
        return this;
    }
}

/**
 * Bouton personnalisé avec taille fixe et icône œil
 */
class FixedSizeButton extends JButton {
    
    public FixedSizeButton(String text) {
        super(text);
        // Taille réduite pour s'adapter au texte
        setPreferredSize(new Dimension(100, 28));
        setMaximumSize(new Dimension(100, 28));
        setMinimumSize(new Dimension(100, 28));
        
        // Configuration du style
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 11)); // Police plus petite
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
        
        // Forcer le layout manager à respecter la taille
        setLayout(null);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100, 28); // Taille réduite
    }
    
    @Override
    public Dimension getMaximumSize() {
        return new Dimension(100, 28);
    }
    
    @Override
    public Dimension getMinimumSize() {
        return new Dimension(100, 28);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Gradient horizontal Cyan (#00B4D8) → Bleu (#4361EE)
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0x00B4D8), 100, 0, new Color(0x4361EE));
        g2.setPaint(gradient);
        
        // Coins arrondis prononcés (12px, plus petit)
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, 100, 28, 12, 12);
        g2.fill(roundedRect);
        
        // Dessiner l'icône œil blanche
        drawWhiteEyeIcon(g2);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    private void drawWhiteEyeIcon(Graphics2D g2) {
        // Position de l'icône (à gauche, adaptée à la nouvelle taille)
        int iconX = 15;
        int iconY = 28 / 2; // Centré verticalement
        int eyeWidth = 12; // Plus petit
        int eyeHeight = 8;  // Plus petit
        
        g2.setColor(Color.WHITE);
        
        // Forme d'œil stylisée (ellipse)
        RoundRectangle2D eyeShape = new RoundRectangle2D.Float(
            iconX - eyeWidth/2, iconY - eyeHeight/2, 
            eyeWidth, eyeHeight, 4, 4 // Coins plus petits
        );
        g2.fill(eyeShape);
        
        // Iris (cercle central)
        Ellipse2D iris = new Ellipse2D.Float(
            iconX - 3, iconY - 3, 
            6, 6 // Plus petit
        );
        g2.setColor(new Color(0x4361EE)); // Bleu du gradient
        g2.fill(iris);
        
        // Pupille (petit cercle noir)
        Ellipse2D pupil = new Ellipse2D.Float(
            iconX - 2, iconY - 2, 
            4, 4 // Plus petit
        );
        g2.setColor(Color.BLACK);
        g2.fill(pupil);
        
        // Reflet (petit point blanc)
        Ellipse2D highlight = new Ellipse2D.Float(
            iconX - 1, iconY - 2, 
            1, 1 // Plus petit
        );
        g2.setColor(Color.WHITE);
        g2.fill(highlight);
    }
}

/**
 * TableCellEditor pour les boutons de taille fixe
 */
class FixedSizeButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private JPanel panel;
    private FixedSizeButton button;
    
    public FixedSizeButtonEditor() {
        panel = new JPanel(new GridBagLayout()); // GridBagLayout pour un centrage parfait
        panel.setOpaque(false);
        
        button = new FixedSizeButton("  Voir Détails");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Centrer le bouton avec GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        
        panel.add(button, gbc);
        
        button.addActionListener(e -> {
            fireEditingStopped();
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
                                                  boolean isSelected, int row, int column) {
        return panel;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "Voir Détails";
    }
}
