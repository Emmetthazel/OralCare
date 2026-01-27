package ma.oralCare.mvc.ui.admin.referentiel.dialogs;

import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Dialogue Premium pour la gestion des antécédents médicaux
 * Design moderne avec espacements généreux et composants élégants
 */
public class AntecedentDialog extends BaseReferenceDialog {
    
    private JTextField txtNom;
    private JComboBox<CategorieAntecedent> cboCategorie;
    private JComboBox<NiveauDeRisque> cboRisque;
    private final Antecedent antecedent;
    
    // Palette de couleurs Premium
    private static final Color PRIMARY_BLUE = new Color(41, 98, 255);
    private static final Color PRIMARY_BLUE_LIGHT = new Color(100, 149, 255);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color FOCUS_COLOR = PRIMARY_BLUE;
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 20);
    
    public AntecedentDialog(Component parent, SystemReferentielController controller, Antecedent antecedent) {
        super(parent, controller, antecedent == null || antecedent.getIdEntite() == null ? 
              "Nouvel Antécédent" : "Modifier Antécédent");
        this.antecedent = antecedent != null ? antecedent : new Antecedent();
        initComponents();
        loadData();
    }
    
    @Override
    protected JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec coins arrondis
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2d.dispose();
            }
        };
        
        panel.setBackground(Color.WHITE);
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30)); // Marge interne généreuse
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0); // Espacement vertical généreux
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        
        int row = 0;
        
        // Nom/Libellé
        panel.add(createPremiumLabel("🧬 Nom/Libellé"), gbc);
        gbc.gridy = ++row;
        txtNom = createPremiumTextField(30);
        panel.add(txtNom, gbc);
        
        // Catégorie
        gbc.gridy = ++row;
        panel.add(createPremiumLabel("📋 Catégorie"), gbc);
        gbc.gridy = ++row;
        cboCategorie = new JComboBox<>(CategorieAntecedent.values());
        styleElegantComboBox(cboCategorie);
        panel.add(cboCategorie, gbc);
        
        // Niveau de Risque
        gbc.gridy = ++row;
        panel.add(createPremiumLabel("⚠️ Niveau de Risque"), gbc);
        gbc.gridy = ++row;
        cboRisque = new JComboBox<>(NiveauDeRisque.values());
        styleElegantComboBox(cboRisque);
        panel.add(cboRisque, gbc);
        
        return panel;
    }
    
    /**
     * Crée un label Premium avec police moderne et espacement
     */
    private JLabel createPremiumLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(TEXT_SECONDARY);
        label.setBorder(new EmptyBorder(0, 0, 8, 0)); // Espacement avec le champ
        return label;
    }
    
    /**
     * Crée un champ de texte Premium avec bordure inférieure et coins arrondis
     */
    private JTextField createPremiumTextField(int cols) {
        JTextField field = new JTextField(cols) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordure inférieure uniquement
                if (hasFocus()) {
                    g2d.setColor(FOCUS_COLOR);
                    g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                } else {
                    g2d.setColor(BORDER_COLOR);
                    g2d.fillRect(0, getHeight() - 1, getWidth(), 1);
                }
                
                g2d.dispose();
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(12, 16, 12, 16)); // Padding interne généreux
        field.setOpaque(true);
        
        // Effet focus personnalisé
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                field.repaint();
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                field.repaint();
            }
        });
        
        return field;
    }
    
    @Override
    protected void initComponents() {
        setSize(700, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Panel principal avec coins arrondis et ombre
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
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
                
                g2d.dispose();
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header Premium
        mainPanel.add(createPremiumHeader(), BorderLayout.NORTH);
        
        // Formulaire avec scroll stylisé
        JScrollPane scrollPane = createPremiumScrollPane(createFormPanel());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer Premium
        mainPanel.add(createPremiumFooter(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    /**
     * Crée un header Premium avec dégradé doux et icône
     */
    private JPanel createPremiumHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé très doux
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_BLUE,
                    getWidth(), getHeight(), PRIMARY_BLUE_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Ligne de séparation subtile
                g2d.setColor(new Color(255, 255, 255, 150));
                g2d.drawLine(30, getHeight()-1, getWidth()-30, getHeight()-1);
                
                g2d.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(25, 30, 20, 30));
        header.setPreferredSize(new Dimension(0, 80));
        
        // Titre centré avec icône
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setOpaque(false);
        
        // Icône médicale
        JLabel iconLabel = new JLabel("🧬");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(12));
        titlePanel.add(titleLabel);
        
        header.add(titlePanel, BorderLayout.CENTER);
        return header;
    }
    
    /**
     * Crée un footer Premium avec boutons modernes
     */
    private JPanel createPremiumFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 25));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(0, 30, 25, 30));
        
        JButton btnCancel = createGhostButton("Annuler");
        btnCancel.addActionListener(e -> {
            dispose();
        });
        
        JButton btnSave = createPremiumButton("💾 Enregistrer");
        btnSave.addActionListener(e -> {
            if (validateAndSave()) {
                showElegantSuccess("Antécédent enregistré avec succès");
                dispose();
            }
        });
        
        footer.add(btnCancel);
        footer.add(btnSave);
        
        return footer;
    }
    
    /**
     * Crée un bouton Premium avec ombre et effets hover
     */
    private JButton createPremiumButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Détection du hover
                Point mousePos = getMousePosition();
                boolean isHovered = mousePos != null && mousePos.x >= 0 && mousePos.x < getWidth() && 
                                  mousePos.y >= 0 && mousePos.y < getHeight();
                
                // Fond avec effet hover
                Color bgColor = isHovered ? adjustColor(SUCCESS_COLOR, -20) : SUCCESS_COLOR;
                
                // Ombre portée
                if (isHovered) {
                    g2d.setColor(SHADOW_COLOR);
                    g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 20, 20);
                }
                
                // Fond principal
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
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
     * Crée un bouton Ghost (transparent avec effet hover)
     */
    private JButton createGhostButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond transparent ou très clair au hover
                Point mousePos = getMousePosition();
                boolean isHovered = mousePos != null && mousePos.x >= 0 && mousePos.x < getWidth() && 
                                  mousePos.y >= 0 && mousePos.y < getHeight();
                
                if (isHovered) {
                    g2d.setColor(new Color(240, 240, 240));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                
                g2d.dispose();
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(TEXT_SECONDARY);
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
     * Crée un scroll pane Premium
     */
    private JScrollPane createPremiumScrollPane(JComponent view) {
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
     * Ajuste une couleur
     */
    private Color adjustColor(Color color, int amount) {
        int r = Math.max(0, Math.min(255, color.getRed() + amount));
        int g = Math.max(0, Math.min(255, color.getGreen() + amount));
        int b = Math.max(0, Math.min(255, color.getBlue() + amount));
        return new Color(r, g, b);
    }
    
    private void loadData() {
        if (antecedent.getIdEntite() != null) {
            txtNom.setText(antecedent.getNom() != null ? antecedent.getNom() : "");
            cboCategorie.setSelectedItem(antecedent.getCategorie() != null ? antecedent.getCategorie() : CategorieAntecedent.AUTRE);
            cboRisque.setSelectedItem(antecedent.getNiveauDeRisque() != null ? antecedent.getNiveauDeRisque() : NiveauDeRisque.LOW);
        }
    }
    
    @Override
    protected boolean validateAndSave() {
        if (txtNom.getText().trim().isEmpty()) {
            showElegantError("Le nom de l'antécédent est obligatoire");
            return false;
        }
        
        try {
            antecedent.setNom(txtNom.getText().trim());
            antecedent.setCategorie((CategorieAntecedent) cboCategorie.getSelectedItem());
            antecedent.setNiveauDeRisque((NiveauDeRisque) cboRisque.getSelectedItem());
            
            controller.updateAntecedent(antecedent);
            return true;
            
        } catch (Exception e) {
            showElegantError("Erreur lors de l'enregistrement : " + e.getMessage());
            return false;
        }
    }
}
