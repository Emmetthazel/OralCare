package ma.oralCare.mvc.ui.admin.referentiel;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;
import ma.oralCare.mvc.ui.admin.referentiel.dialogs.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

public class ReferentielView extends JPanel {

    private final SystemReferentielController controller;
    private DefaultTableModel medicModel, antecedentModel, acteModel;
    
    // =========================================================================
    // 🎨 PALETTE DE COULEURS MÉDICALE MODERNE
    // =========================================================================
    private static final Color PRIMARY_BLUE = new Color(41, 98, 255);
    private static final Color SECONDARY_BLUE = new Color(0, 72, 186);
    private static final Color MEDICAMENT_GREEN = new Color(40, 167, 69);
    private static final Color ANTECEDENT_PURPLE = new Color(102, 16, 242);
    private static final Color ACTE_CYAN = new Color(23, 162, 184);
    private static final Color BACKGROUND_LIGHT = new Color(248, 250, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color BORDER_COLOR = new Color(222, 226, 230);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);

    public ReferentielView(SystemReferentielController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Onglets Material Design avec soulignement actif
        JTabbedPane tabbedPane = createMaterialTabbedPane();
        tabbedPane.addTab("Médicaments", createReferencePanel("MEDIC", "Catalogue Pharmaceutique",
                new String[]{"Date", "Nom", "Laboratoire", "Type", "Forme", "Remb.", "Prix (DH)"}, MEDICAMENT_GREEN));
        tabbedPane.addTab("Antécédents", createReferencePanel("ANTECEDENT", "Référentiel des Antécédents",
                new String[]{"Date", "Nom", "Catégorie", "Risque"}, ANTECEDENT_PURPLE));
        tabbedPane.addTab("Actes", createReferencePanel("ACTE", "Catalogue des Actes",
                new String[]{"Date", "Libellé", "Catégorie", "Prix de Base"}, ACTE_CYAN));

        add(tabbedPane, BorderLayout.CENTER);
        refreshAll();
    }

    /**
     * 🎨 Crée un JTabbedPane Material Design avec soulignement actif de 2px
     */
    private JTabbedPane createMaterialTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Variable pour suivre l'onglet sélectionné
        final int[] selectedIndex = {0};
        
        // Écouter les changements de sélection
        tabbedPane.addChangeListener(e -> {
            if (e.getSource() instanceof JTabbedPane) {
                selectedIndex[0] = ((JTabbedPane) e.getSource()).getSelectedIndex();
                tabbedPane.repaint(); // Forcer le redessin
            }
        });
        
        // Personnaliser l'apparence des onglets
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, 
                                  Rectangle iconRect, Rectangle textRect) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Rectangle tabRect = rects[tabIndex];
                boolean isSelected = selectedIndex[0] == tabIndex;
                
                // Couleur du texte selon l'état
                Color textColor = TEXT_SECONDARY;
                if (isSelected) {
                    if (tabIndex == 0) textColor = MEDICAMENT_GREEN;
                    else if (tabIndex == 1) textColor = ANTECEDENT_PURPLE;
                    else if (tabIndex == 2) textColor = ACTE_CYAN;
                }
                
                // Dessiner le texte
                g2d.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 13));
                g2d.setColor(textColor);
                String title = tabbedPane.getTitleAt(tabIndex);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(title);
                int textHeight = fm.getHeight();
                int textX = tabRect.x + (tabRect.width - textWidth) / 2;
                int textY = tabRect.y + (tabRect.height - textHeight) / 2 + fm.getAscent();
                g2d.drawString(title, textX, textY);
                
                // Dessiner le soulignement si l'onglet est sélectionné
                if (isSelected) {
                    Color underlineColor = PRIMARY_BLUE;
                    if (tabIndex == 0) underlineColor = MEDICAMENT_GREEN;
                    else if (tabIndex == 1) underlineColor = ANTECEDENT_PURPLE;
                    else if (tabIndex == 2) underlineColor = ACTE_CYAN;
                    
                    g2d.setColor(underlineColor);
                    g2d.fillRect(tabRect.x, tabRect.y + tabRect.height - 2, tabRect.width, 2);
                }
                
                g2d.dispose();
            }
            
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 120; // Largeur fixe pour les onglets
            }
        });
        
        // Configuration Material Design
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(BACKGROUND_LIGHT);
        tabbedPane.setBorder(new EmptyBorder(0, 0, 15, 0));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        // Personnaliser l'apparence des onglets
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTab(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, 
                                  Rectangle iconRect, Rectangle textRect) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Rectangle tabRect = rects[tabIndex];
                boolean isSelected = selectedIndex[0] == tabIndex;
                
                // Couleur du texte selon l'état
                Color textColor = TEXT_SECONDARY;
                if (isSelected) {
                    if (tabIndex == 0) textColor = MEDICAMENT_GREEN;
                    else if (tabIndex == 1) textColor = ANTECEDENT_PURPLE;
                    else if (tabIndex == 2) textColor = ACTE_CYAN;
                }
                
                // Dessiner le texte
                g2d.setFont(new Font("Segoe UI", isSelected ? Font.BOLD : Font.PLAIN, 13));
                g2d.setColor(textColor);
                String title = tabbedPane.getTitleAt(tabIndex);
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(title);
                int textHeight = fm.getHeight();
                int textX = tabRect.x + (tabRect.width - textWidth) / 2;
                int textY = tabRect.y + (tabRect.height - textHeight) / 2 + fm.getAscent();
                g2d.drawString(title, textX, textY);
                
                g2d.dispose();
            }
            
            @Override
            protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                return 120; // Largeur fixe pour les onglets
            }
        });
        
        return tabbedPane;
    }

    // =========================================================================
    // ✅ GESTION DES FORMULAIRES - OPTIMISÉ AVEC DIALOGUES MODERNES
    // =========================================================================

    private void showMedicForm(Medicament m) {
        MedicamentDialog dialog = new MedicamentDialog(this, controller, m);
        dialog.setVisible(true);
        refreshMedicaments();
    }

    private void showAntecedentForm(Antecedent a) {
        AntecedentDialog dialog = new AntecedentDialog(this, controller, a);
        dialog.setVisible(true);
        refreshAntecedents();
    }

    private void showActeForm(Acte act) {
        ActeDialog dialog = new ActeDialog(this, controller, act);
        dialog.setVisible(true);
        refreshActes();
    }

    // =========================================================================
    // ✅ LOGIQUE TABLEAUX ET BOUTONS
    // =========================================================================

    /**
     * 🎨 Crée un panneau de référence médical moderne avec design professionnel
     */
    private JPanel createReferencePanel(String type, String title, String[] columns, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 18, 15, 18));
        
        // Initialisation du modèle de données (logique métier préservée)
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        // Assignation des modèles (logique métier préservée)
        if (type.equals("MEDIC")) medicModel = model;
        else if (type.equals("ANTECEDENT")) antecedentModel = model;
        else acteModel = model;

        // Tableau moderne avec design médical
        JTable table = createModernMedicalTable(model, themeColor);
        
        // Barre de recherche intelligente
        JPanel searchPanel = createModernSearchBar(table, themeColor);
        
        // Toolbar professionnelle
        JPanel toolbar = createModernMedicalToolbar(table, type, themeColor);
        
        // Header de section médical
        JPanel headerPanel = createMedicalSectionHeader(title, themeColor);
        
        // Conteneur principal avec design cohérent
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(createModernScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(toolbar, BorderLayout.SOUTH);
        
        // Card médicale avec élévation subtile
        JPanel cardPanel = createMedicalCard(contentPanel);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void handleEditAction(String type, JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Sélectionnez une ligne."); return; }
        String key = (String) table.getValueAt(row, 1);

        if (type.equals("MEDIC")) {
            controller.loadMedicaments().stream().filter(m -> m.getNom().equals(key)).findFirst().ifPresent(this::showMedicForm);
        } else if (type.equals("ANTECEDENT")) {
            controller.loadAntecedents().stream().filter(a -> a.getNom().equals(key)).findFirst().ifPresent(this::showAntecedentForm);
        } else {
            controller.loadActes().stream().filter(a -> a.getLibelle().equals(key)).findFirst().ifPresent(this::showActeForm);
        }
    }

    // =========================================================================
    // ✅ RAFRAÎCHISSEMENT DES DONNÉES
    // =========================================================================

    private void refreshMedicaments() {
        medicModel.setRowCount(0);
        controller.loadMedicaments().forEach(m -> medicModel.addRow(new Object[]{
                m.getDateCreation(), m.getNom(), m.getLaboratoire(), m.getType(),
                m.getForme() != null ? m.getForme().getLibelle() : "-",
                m.getRemboursable() ? "Oui" : "Non", m.getPrixUnitaire() + " DH"}));
    }

    private void refreshAntecedents() {
        antecedentModel.setRowCount(0);
        controller.loadAntecedents().forEach(a -> antecedentModel.addRow(new Object[]{
                a.getDateCreation(), a.getNom(),
                a.getCategorie() != null ? a.getCategorie().name() : "-",
                a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().getLibelle() : "-"}));
    }

    private void refreshActes() {
        acteModel.setRowCount(0);
        controller.loadActes().forEach(a -> acteModel.addRow(new Object[]{
                a.getDateCreation(), a.getLibelle(), a.getCategorie(), a.getPrixDeBase() + " DH"}));
    }

    private void refreshAll() { refreshMedicaments(); refreshAntecedents(); refreshActes(); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }
    
    // =========================================================================
    // ✅ MÉTHODES MODERNES SUPPLÉMENTAIRES
    // =========================================================================
    
    /**
     * 🎨 Crée un tableau médical moderne avec badges personnalisés pour Remb. et Risque
     */
    private JTable createModernMedicalTable(DefaultTableModel model, Color themeColor) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // Vérifier si c'est une colonne avec badge personnalisé
                String columnName = getColumnName(column);
                
                // Badge pour la colonne 'Remb.'
                if (columnName.equals("Remb.")) {
                    String value = getValueAt(row, column).toString();
                    return createRemboursementBadge(value);
                }
                
                // Badge pour la colonne 'Risque'
                if (columnName.equals("Risque")) {
                    String value = getValueAt(row, column).toString();
                    return createRisqueBadge(value);
                }
                
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    
                    // Fond blanc pour toutes les lignes
                    jc.setBackground(WHITE);
                    
                    // Header médical stylisé
                    if (row == -1) {
                        jc.setBackground(themeColor);
                        jc.setForeground(WHITE);
                        jc.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        ((JLabel) jc).setHorizontalAlignment(SwingConstants.LEFT);
                        jc.setBorder(new EmptyBorder(12, 15, 12, 15));
                    } else {
                        jc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 242, 245)));
                        jc.setForeground(TEXT_PRIMARY);
                    }
                }
                
                return c;
            }
        };
        
        // Configuration médicale professionnelle
        table.setRowHeight(40);
        table.setShowGrid(false); // Pas de grille visible
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 40));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        
        // Personnalisation du header médical
        JTableHeader header = table.getTableHeader();
        header.setOpaque(true);
        header.setBackground(themeColor);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(WHITE);
        header.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        return table;
    }
    
    /**
     * 🎨 Crée un badge de remboursement avec style pastel et forme pil
     */
    private JLabel createRemboursementBadge(String value) {
        JLabel badge = new JLabel(value);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setOpaque(false);
        
        // Personnaliser avec style pil et couleurs pastel
        badge.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Déterminer les couleurs pastel selon la valeur
                Color backgroundColor, textColor;
                if (value.equals("Oui")) {
                    backgroundColor = new Color(220, 255, 220); // Vert très clair
                    textColor = new Color(40, 120, 40); // Vert foncé
                } else {
                    backgroundColor = new Color(240, 240, 240); // Gris très clair
                    textColor = new Color(80, 80, 80); // Gris foncé
                }
                
                // Dessiner le fond avec angles modérés (12px)
                int height = badge.getHeight();
                int width = badge.getWidth(); // Prend toute la largeur de la cellule
                
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, width, height, 12, 12); // Angles de 12px
                
                // Dessiner le texte
                g2d.setColor(textColor);
                g2d.setFont(badge.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = badge.getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int textX = (badge.getWidth() - textWidth) / 2;
                int textY = (badge.getHeight() - textHeight) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
        });
        
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        
        return badge;
    }
    
    /**
     * 🎨 Crée un badge de risque avec dégradé de couleurs
     */
    private JLabel createRisqueBadge(String value) {
        JLabel badge = new JLabel(value);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setOpaque(false);
        
        // Personnaliser avec dégradé et angles arrondis
        badge.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Déterminer la couleur selon le niveau de risque
                Color startColor, endColor;
                if (value.equals("Faible")) {
                    startColor = new Color(40, 167, 69); // Vert
                    endColor = new Color(25, 135, 84);
                } else if (value.equals("Moyen")) {
                    startColor = new Color(255, 193, 7); // Orange
                    endColor = new Color(255, 152, 0);
                } else if (value.equals("Élevé")) {
                    startColor = new Color(220, 53, 69); // Rouge
                    endColor = new Color(200, 35, 51);
                } else {
                    // Valeur par défaut
                    startColor = TEXT_SECONDARY;
                    endColor = TEXT_SECONDARY;
                }
                
                // Dessiner le fond avec dégradé et angles modérés
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, badge.getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, badge.getWidth(), badge.getHeight(), 12, 12);
                
                // Dessiner le texte
                g2d.setColor(Color.WHITE);
                g2d.setFont(badge.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = badge.getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int textX = (badge.getWidth() - textWidth) / 2;
                int textY = (badge.getHeight() - textHeight) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
        });
        
        badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        
        return badge;
    }
    
    /**
     * 🎨 Crée une barre de recherche médicale intelligente avec design professionnel
     */
    private JPanel createModernSearchBar(JTable table, Color themeColor) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        searchPanel.setOpaque(false);
        
        // Champ de recherche médical
        JTextField searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        searchField.setBackground(WHITE);
        
        // Placeholder médical
        searchField.setText("Rechercher un référentiel...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Rechercher un référentiel...")) {
                    searchField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher un référentiel...");
                }
            }
        });
        
        // Listener de recherche intelligent
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterMedicalTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterMedicalTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterMedicalTable(); }
            
            private void filterMedicalTable() {
                String searchText = searchField.getText().toLowerCase();
                if (searchText.equals("rechercher un référentiel...")) searchText = "";
                
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                
                for (int i = 0; i < model.getRowCount(); i++) {
                    boolean found = false;
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object value = model.getValueAt(i, j);
                        if (value != null && value.toString().toLowerCase().contains(searchText)) {
                            found = true;
                            break;
                        }
                    }
                    table.setRowHeight(i, found ? 40 : 0);
                }
            }
        });
        
        searchPanel.add(searchField);
        
        return searchPanel;
    }
    
    /**
     * 🎨 Crée une toolbar médicale professionnelle avec boutons modernes
     */
    private JPanel createModernMedicalToolbar(JTable table, String type, Color themeColor) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        toolbar.setOpaque(false);
        
        // Boutons médicaux modernes sans icônes
        JButton btnAdd = createModernMedicalButton("Ajouter", SUCCESS_COLOR, true);
        JButton btnEdit = createModernMedicalButton("Modifier", themeColor, true);
        JButton btnDelete = createModernMedicalButton("Supprimer", DANGER_COLOR, true);
        
        // Logique métier préservée - Ajout
        btnAdd.addActionListener(e -> {
            if(type.equals("MEDIC")) showMedicForm(new Medicament());
            else if(type.equals("ANTECEDENT")) showAntecedentForm(new Antecedent());
            else showActeForm(new Acte());
        });

        // Logique métier préservée - Modification
        btnEdit.addActionListener(e -> handleEditAction(type, table));

        // Logique métier préservée - Suppression
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                showError("Veuillez sélectionner un élément.");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Supprimer cet élément définitivement ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                String key = (String) table.getValueAt(row, 1);
                Long idToDelete = null;

                if (type.equals("MEDIC")) {
                    idToDelete = controller.loadMedicaments().stream().filter(m -> m.getNom().equals(key)).findFirst().map(m -> m.getIdEntite()).orElse(null);
                } else if (type.equals("ANTECEDENT")) {
                    idToDelete = controller.loadAntecedents().stream().filter(a -> a.getNom().equals(key)).findFirst().map(a -> a.getIdEntite()).orElse(null);
                } else if (type.equals("ACTE")) {
                    idToDelete = controller.loadActes().stream().filter(act -> act.getLibelle().equals(key)).findFirst().map(act -> act.getIdEntite()).orElse(null);
                }

                if (idToDelete != null) {
                    controller.deleteEntity(type, idToDelete);
                    refreshAll();
                }
            }
        });
        
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        
        return toolbar;
    }
    
    /**
     * 🎨 Crée un bouton médical moderne avec angles très arrondis (15px) et icônes minimalistes
     */
    private JButton createModernMedicalButton(String text, Color color, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(isPrimary ? WHITE : color);
        button.setBackground(isPrimary ? color : WHITE);
        
        // Créer des angles très arrondis (15px)
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, isPrimary ? 0 : 1),
            new EmptyBorder(8, 18, 8, 18)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Personnaliser avec angles très arrondis
        button.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                button.setOpaque(false);
            }
            
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dessiner le fond avec angles très arrondis
                g2d.setColor(button.getBackground());
                g2d.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 15, 15);
                
                // Dessiner la bordure si nécessaire
                if (!isPrimary) {
                    g2d.setColor(color);
                    g2d.drawRoundRect(0, 0, button.getWidth() - 1, button.getHeight() - 1, 15, 15);
                }
                
                // Dessiner le texte
                g2d.setColor(button.getForeground());
                g2d.setFont(button.getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = button.getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                int textX = (button.getWidth() - textWidth) / 2;
                int textY = (button.getHeight() - textHeight) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
        });
        
        // Effets hover médicaux professionnels
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(adjustColor(color, 8));
                } else {
                    button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 25));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(isPrimary ? color : WHITE);
            }
        });
        
        return button;
    }
    
    /**
     * 🎨 Crée un header de section médicale professionnel
     */
    private JPanel createMedicalSectionHeader(String title, Color themeColor) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(themeColor);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    /**
     * 🎨 Crée une card médicale avec élévation subtile et design professionnel
     */
    private JPanel createMedicalCard(JPanel content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * 🎨 Crée un scroll pane médical moderne avec design professionnel
     */
    private JScrollPane createModernScrollPane(JComponent view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getVerticalScrollBar().setBlockIncrement(50);
        scrollPane.setBackground(BACKGROUND_LIGHT);
        
        return scrollPane;
    }
    
    /**
     * 🎨 Ajuste une couleur médicale de manière professionnelle
     */
    private Color adjustColor(Color color, int amount) {
        int r = Math.max(0, Math.min(255, color.getRed() + amount));
        int g = Math.max(0, Math.min(255, color.getGreen() + amount));
        int b = Math.max(0, Math.min(255, color.getBlue() + amount));
        return new Color(r, g, b);
    }
}
