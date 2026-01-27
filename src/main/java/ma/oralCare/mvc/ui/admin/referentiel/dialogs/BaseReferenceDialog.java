package ma.oralCare.mvc.ui.admin.referentiel.dialogs;

import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Classe de base élégante pour tous les dialogues de référentiel médical
 * Design moderne avec animations et effets visuels sophistiqués
 */
public abstract class BaseReferenceDialog extends JDialog {
    
    protected final SystemReferentielController controller;
    
    // Palette de couleurs médicales élégantes
    private static final Color PRIMARY_BLUE = new Color(41, 98, 255);
    private static final Color SECONDARY_BLUE = new Color(30, 80, 200);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color BACKGROUND_LIGHT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    
    public BaseReferenceDialog(Component parent, SystemReferentielController controller, String title) {
        super(SwingUtilities.getWindowAncestor(parent), title, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
    }
    
    protected void initComponents() {
        setSize(650, 550);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Panel principal avec ombre
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(SHADOW_COLOR);
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
                
                // Fond principal
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header élégant
        mainPanel.add(createElegantHeader(), BorderLayout.NORTH);
        
        // Formulaire avec scroll stylisé
        JScrollPane scrollPane = createElegantScrollPane(createFormPanel());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer élégant
        mainPanel.add(createElegantFooter(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Crée un header élégant avec dégradé médical et icône
     */
    private JPanel createElegantHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé médical sophistiqué
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_BLUE,
                    getWidth(), getHeight(), SECONDARY_BLUE
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Ligne de séparation subtile
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.drawLine(20, getHeight()-1, getWidth()-20, getHeight()-1);
                
                g2d.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(25, 30, 20, 30));
        header.setPreferredSize(new Dimension(0, 80));
        
        // Titre avec icône médicale
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);
        
        // Icône médicale stylisée
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int size = 24;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                // Croix médicale
                g2d.fillRoundRect(x + size/4, y, size/2, size, 6, 6);
                g2d.fillRoundRect(x, y + size/4, size, size/2, 6, 6);
                
                g2d.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(30, 30));
        
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(WHITE);
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }
    
    /**
     * Crée un footer élégant avec boutons modernes
     */
    private JPanel createElegantFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 25));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 30, 25, 30));
        
        JButton btnCancel = createElegantButton("Annuler", DANGER_COLOR, false);
        btnCancel.addActionListener(e -> {
            dispose();
        });
        
        JButton btnSave = createElegantButton("💾 Enregistrer", SUCCESS_COLOR, true);
        btnSave.addActionListener(e -> {
            if (validateAndSave()) {
                dispose();
            }
        });
        
        footer.add(btnCancel);
        footer.add(btnSave);
        
        return footer;
    }
    
    /**
     * Crée un bouton élégant avec effets hover et animations
     */
    protected JButton createElegantButton(String text, Color color, boolean filled) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec effet hover
                Color bgColor = filled ? color : WHITE;
                if (isHovered) {
                    bgColor = filled ? adjustColor(color, 15) : new Color(color.getRed(), color.getGreen(), color.getBlue(), 25);
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure
                if (!filled) {
                    g2d.setColor(color);
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                }
                
                g2d.dispose();
            }
            
            @Override
            public void setFont(Font font) {
                super.setFont(font);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(filled ? WHITE : color);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(12, 24, 12, 24));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ((JButton) evt.getSource()).repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ((JButton) evt.getSource()).repaint();
            }
        });
        
        return button;
    }
    
    /**
     * Crée un scroll pane élégant
     */
    private JScrollPane createElegantScrollPane(JComponent view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        
        // Style de la barre de défilement
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(200, 200, 200));
                g2d.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);
                
                g2d.dispose();
            }
        });
        
        return scrollPane;
    }
    
    /**
     * Ajoute un champ de formulaire élégant (label + input)
     */
    protected JTextField addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, int cols) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(createElegantLabel(label), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField field = createElegantTextField(cols);
        panel.add(field, gbc);
        
        return field;
    }
    
    /**
     * Crée un label élégant
     */
    protected JLabel createElegantLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(8, 0, 8, 0));
        return label;
    }
    
    /**
     * Crée un champ de texte élégant
     */
    protected JTextField createElegantTextField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 14, 10, 14)
        ));
        field.setBackground(WHITE);
        field.setOpaque(true);
        
        // Effet focus
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                    new EmptyBorder(9, 13, 9, 13)
                ));
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    new EmptyBorder(10, 14, 10, 14)
                ));
            }
        });
        
        return field;
    }
    
    /**
     * Style une combobox élégante
     */
    protected void styleElegantComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(TEXT_PRIMARY);
        combo.setBackground(WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        combo.setOpaque(true);
    }
    
    /**
     * Crée une checkbox élégante
     */
    protected JCheckBox createElegantCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        checkBox.setForeground(TEXT_PRIMARY);
        checkBox.setBackground(WHITE);
        checkBox.setOpaque(true);
        checkBox.setIcon(new javax.swing.plaf.metal.MetalCheckBoxIcon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                JCheckBox cb = (JCheckBox) c;
                int size = 16;
                
                // Fond
                g2d.setColor(cb.isSelected() ? PRIMARY_BLUE : WHITE);
                g2d.fillRoundRect(x, y, size, size, 4, 4);
                
                // Bordure
                g2d.setColor(cb.isSelected() ? PRIMARY_BLUE : BORDER_COLOR);
                g2d.drawRoundRect(x, y, size, size, 4, 4);
                
                // Check
                if (cb.isSelected()) {
                    g2d.setColor(WHITE);
                    g2d.setStroke(new BasicStroke(2.0f));
                    g2d.drawLine(x + 4, y + size/2, x + size/2 - 1, y + size - 4);
                    g2d.drawLine(x + size/2, y + size - 4, x + size - 4, y + 4);
                }
                
                g2d.dispose();
            }
            
            @Override
            public int getIconWidth() {
                return 16;
            }
            
            @Override
            public int getIconHeight() {
                return 16;
            }
        });
        
        return checkBox;
    }
    
    /**
     * Affiche un message d'erreur élégant
     */
    protected void showElegantError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Erreur de validation", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Affiche un message de succès élégant
     */
    protected void showElegantSuccess(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Succès", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Ajuste une couleur
     */
    private Color adjustColor(Color color, int amount) {
        int r = Math.max(0, Math.min(255, color.getRed() + amount));
        int g = Math.max(0, Math.min(255, color.getGreen() + amount));
        int b = Math.max(0, Math.min(255, color.getBlue() + amount));
        return new Color(r, g, b);
    }
    
    // Méthodes abstraites à implémenter
    protected abstract JPanel createFormPanel();
    protected abstract boolean validateAndSave();
}
