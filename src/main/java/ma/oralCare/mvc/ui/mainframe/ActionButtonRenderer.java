package ma.oralCare.mvc.ui.mainframe;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;

/**
 * TableCellRenderer personnalisé pour les boutons d'action
 * Dimensions fixes : 140px x 35px, centré dans la cellule
 */
public class ActionButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    private CustomActionButton button;
    
    public ActionButtonRenderer() {
        setOpaque(false);
        setLayout(new GridBagLayout()); // Pour centrer le bouton
        
        button = new CustomActionButton("Voir Détails");
        
        // Centrer le bouton dans le panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        add(button, gbc);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                  boolean hasFocus, int row, int column) {
        button.setText("  Voir Détails"); // Espace pour l'icône
        return this;
    }
}

/**
 * JButton personnalisé pour l'éditeur avec icône œil dessinée
 */
class CustomActionButton extends JButton {
    
    public CustomActionButton(String text) {
        super(text);
        setPreferredSize(new Dimension(140, 35)); // Taille fixe
        setMaximumSize(new Dimension(140, 35)); // Taille maximale
        setMinimumSize(new Dimension(140, 35)); // Taille minimale
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setHorizontalAlignment(SwingConstants.CENTER);
        setVerticalAlignment(SwingConstants.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Gradient horizontal Cyan (#00B4D8) → Bleu (#4361EE)
        GradientPaint gradient = new GradientPaint(0, 0, new Color(0x00B4D8), getWidth(), 0, new Color(0x4361EE));
        g2.setPaint(gradient);
        
        // Coins arrondis prononcés (15px)
        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fill(roundedRect);
        
        // Dessiner l'icône œil blanche à gauche du texte
        drawWhiteEyeIcon(g2);
        
        g2.dispose();
        super.paintComponent(g);
    }
    
    private void drawWhiteEyeIcon(Graphics2D g2) {
        // Position de l'icône (à gauche, espacée du bord)
        int iconX = 20;
        int iconY = getHeight() / 2;
        int eyeWidth = 16;
        int eyeHeight = 10;
        
        g2.setColor(Color.WHITE);
        
        // Forme d'œil stylisée (ellipse)
        RoundRectangle2D eyeShape = new RoundRectangle2D.Float(
            iconX - eyeWidth/2, iconY - eyeHeight/2, 
            eyeWidth, eyeHeight, 5, 5
        );
        g2.fill(eyeShape);
        
        // Iris (cercle central)
        Ellipse2D iris = new Ellipse2D.Float(
            iconX - 4, iconY - 4, 
            8, 8
        );
        g2.setColor(new Color(0x4361EE)); // Bleu du gradient pour le contraste
        g2.fill(iris);
        
        // Pupille (petit cercle noir)
        Ellipse2D pupil = new Ellipse2D.Float(
            iconX - 2, iconY - 2, 
            4, 4
        );
        g2.setColor(Color.BLACK);
        g2.fill(pupil);
        
        // Reflet (petit point blanc pour l'effet brillant)
        Ellipse2D highlight = new Ellipse2D.Float(
            iconX - 1, iconY - 3, 
            2, 2
        );
        g2.setColor(Color.WHITE);
        g2.fill(highlight);
    }
}

/**
 * TableCellEditor pour les boutons d'action
 */
class ActionButtonEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private JPanel panel;
    private CustomActionButton button;
    
    public ActionButtonEditor() {
        panel = new JPanel(new GridBagLayout()); // Panel pour centrer le bouton
        panel.setOpaque(false);
        
        button = new CustomActionButton("Voir Détails");
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Centrer le bouton dans le panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        panel.add(button, gbc);
        
        button.addActionListener(e -> {
            fireEditingStopped();
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
                                                  boolean isSelected, int row, int column) {
        button.setText("  Voir Détails"); // Espace pour l'icône
        return panel;
    }
    
    @Override
    public Object getCellEditorValue() {
        return "Voir Détails";
    }
}
