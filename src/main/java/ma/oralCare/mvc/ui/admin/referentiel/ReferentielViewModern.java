package ma.oralCare.mvc.ui.admin.referentiel;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.FormeMedicament;
import ma.oralCare.entities.enums.NiveauDeRisque;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Vue moderne de gestion des référentiels médicaux avec design Material Design
 */
public class ReferentielViewModern extends JPanel {

    private final SystemReferentielController controller;
    private DefaultTableModel medicModel, antecedentModel, acteModel;
    
    // Palette de couleurs moderne médicale
    private static final Color PRIMARY_BLUE = new Color(41, 98, 255);
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

    public ReferentielViewModern(SystemReferentielController controller) {
        this.controller = controller;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_LIGHT);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        // Header moderne avec bannière
        add(createModernHeader(), BorderLayout.NORTH);

        // Onglets modernisés
        JTabbedPane tabbedPane = createModernTabbedPane();
        tabbedPane.addTab("💊 Médicaments", createReferencePanel("MEDIC", "Catalogue Pharmaceutique",
                new String[]{"Date", "Nom", "Laboratoire", "Type", "Forme", "Remb.", "Prix (DH)"}, MEDICAMENT_GREEN));
        tabbedPane.addTab("🧬 Antécédents", createReferencePanel("ANTECEDENT", "Référentiel des Antécédents",
                new String[]{"Date", "Nom", "Catégorie", "Risque"}, ANTECEDENT_PURPLE));
        tabbedPane.addTab("🦷 Actes", createReferencePanel("ACTE", "Catalogue des Actes",
                new String[]{"Date", "Libellé", "Catégorie", "Prix de Base"}, ACTE_CYAN));

        add(tabbedPane, BorderLayout.CENTER);
        refreshAll();
    }

    /**
     * Crée un header moderne avec bannière colorée
     */
    private JPanel createModernHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé moderne pour la bannière
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_BLUE,
                    getWidth(), getHeight(), new Color(0, 72, 186)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(0, 3, getWidth(), getHeight() - 3, 20, 20);
                
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Titre principal
        JLabel titleLabel = new JLabel("🏥 Gestion des Référentiels Médicaux");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Sous-titre
        JLabel subtitleLabel = new JLabel("Administration des médicaments, antécédents et actes médicaux");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        headerPanel.add(textPanel, BorderLayout.WEST);
        
        // Icône décorative
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int size = Math.min(getWidth(), getHeight()) - 10;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Dessiner une icône médicale stylisée
                g2d.drawRoundRect(centerX - size/3, centerY - size/3, size*2/3, size*2/3, 8, 8);
                g2d.drawLine(centerX - size/4, centerY, centerX + size/4, centerY);
                g2d.drawLine(centerX, centerY - size/4, centerX, centerY + size/4);
                
                g2d.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(60, 60));
        iconLabel.setOpaque(false);
        
        headerPanel.add(iconLabel, BorderLayout.EAST);
        
        return headerPanel;
    }

    /**
     * Crée un JTabbedPane moderne avec design Material
     */
    private JTabbedPane createModernTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(BACKGROUND_LIGHT);
        tabbedPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        return tabbedPane;
    }

    /**
     * Crée un panneau de référence moderne avec table stylisé et boutons modernes
     */
    private JPanel createReferencePanel(String type, String title, String[] columns, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Modèle de table
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (type.equals("MEDIC")) medicModel = model;
        else if (type.equals("ANTECEDENT")) antecedentModel = model;
        else acteModel = model;

        // Tableau moderne
        JTable table = createModernTable(model, themeColor);
        
        // Barre de recherche
        JPanel searchPanel = createSearchPanel(table, themeColor);
        
        // Toolbar moderne
        JPanel toolbar = createModernToolbar(table, type, themeColor);
        
        // Header de section
        JPanel headerPanel = createSectionHeader(title, themeColor);
        
        // Conteneur principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(searchPanel, BorderLayout.NORTH);
        contentPanel.add(createModernScrollPane(table), BorderLayout.CENTER);
        contentPanel.add(toolbar, BorderLayout.SOUTH);
        
        JPanel cardPanel = createModernCard(contentPanel);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Crée un tableau moderne avec stylisation avancée
     */
    private JTable createModernTable(DefaultTableModel model, Color themeColor) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    
                    // Alternance de couleurs
                    if (row % 2 == 0) {
                        jc.setBackground(WHITE);
                    } else {
                        jc.setBackground(new Color(248, 250, 252));
                    }
                    
                    // Header stylisé
                    if (row == -1) {
                        jc.setBackground(themeColor);
                        jc.setForeground(WHITE);
                        jc.setFont(new Font("Segoe UI", Font.BOLD, 13));
                        ((JLabel) jc).setHorizontalAlignment(SwingConstants.LEFT);
                        jc.setBorder(new EmptyBorder(15, 15, 15, 15));
                    } else {
                        jc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                        jc.setBorder(new EmptyBorder(12, 15, 12, 15));
                        jc.setForeground(TEXT_PRIMARY);
                    }
                }
                
                return c;
            }
        };
        
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 50));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        
        // Personnaliser le header
        JTableHeader header = table.getTableHeader();
        header.setOpaque(false);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(WHITE);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        return table;
    }
    
    /**
     * Crée une barre de recherche moderne
     */
    private JPanel createSearchPanel(JTable table, Color themeColor) {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setOpaque(false);
        
        JLabel searchLabel = new JLabel("🔍");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchLabel.setForeground(TEXT_SECONDARY);
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        searchField.setBackground(WHITE);
        
        // Placeholder text
        searchField.setText("Rechercher...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Rechercher...")) {
                    searchField.setText("");
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher...");
                }
            }
        });
        
        // Listener de recherche
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            
            private void filterTable() {
                String searchText = searchField.getText().toLowerCase();
                if (searchText.equals("rechercher...")) searchText = "";
                
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
                    table.setRowHeight(i, found ? 45 : 0);
                }
            }
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        
        return searchPanel;
    }
    
    /**
     * Crée une toolbar moderne avec boutons stylisés
     */
    private JPanel createModernToolbar(JTable table, String type, Color themeColor) {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        toolbar.setOpaque(false);
        
        JButton btnAdd = createModernButton("➕ Ajouter", SUCCESS_COLOR, true);
        JButton btnEdit = createModernButton("✏️ Modifier", themeColor, true);
        JButton btnDelete = createModernButton("🗑️ Supprimer", DANGER_COLOR, true);
        
        btnAdd.addActionListener(e -> {
            if(type.equals("MEDIC")) showMedicForm(new Medicament());
            else if(type.equals("ANTECEDENT")) showAntecedentForm(new Antecedent());
            else showActeForm(new Acte());
        });

        btnEdit.addActionListener(e -> handleEditAction(type, table));

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
     * Crée un bouton moderne
     */
    private JButton createModernButton(String text, Color color, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isPrimary ? WHITE : color);
        button.setBackground(isPrimary ? color : WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, isPrimary ? 0 : 1),
            new EmptyBorder(12, 25, 12, 25)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Effet hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isPrimary) {
                    button.setBackground(adjustColor(color, 10));
                } else {
                    button.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(isPrimary ? color : WHITE);
            }
        });
        
        return button;
    }
    
    /**
     * Crée un header de section moderne
     */
    private JPanel createSectionHeader(String title, Color themeColor) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(themeColor);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    /**
     * Crée un panneau de type card avec élévation
     */
    private JPanel createModernCard(JPanel content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Crée un scroll pane moderne
     */
    private JScrollPane createModernScrollPane(JComponent view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        scrollPane.setBackground(BACKGROUND_LIGHT);
        
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

    // =========================================================================
    // ✅ GESTION DES FORMULAIRES (AJOUT & MODIF)
    // =========================================================================

    private void showMedicForm(Medicament m) {
        boolean isNew = (m.getIdEntite() == null);
        JTextField nom = new JTextField(m.getNom());
        JTextField labo = new JTextField(m.getLaboratoire());
        JTextField type = new JTextField(m.getType());

        JComboBox<FormeMedicament> forme = new JComboBox<>(FormeMedicament.values());
        forme.setSelectedItem(m.getForme() != null ? m.getForme() : FormeMedicament.TABLET);

        String prixStr = (m.getPrixUnitaire() != null) ? m.getPrixUnitaire().toString() : "";
        JTextField prix = new JTextField(prixStr);
        JCheckBox remb = new JCheckBox("Remboursable", m.getRemboursable() != null ? m.getRemboursable() : false);

        JPanel container = createMedicContainer(nom, labo, type, forme, prix, remb);
        String title = isNew ? "Ajouter Médicament" : "Modifier Médicament";

        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                m.setNom(nom.getText());
                m.setLaboratoire(labo.getText());
                m.setType(type.getText());
                m.setForme((FormeMedicament) forme.getSelectedItem());
                m.setRemboursable(remb.isSelected());

                String p = prix.getText().trim().replace(",", ".");
                m.setPrixUnitaire(p.isEmpty() ? BigDecimal.ZERO : new BigDecimal(p));

                controller.updateMedicament(m);
                refreshMedicaments();
            } catch (Exception e) { showError("Erreur saisie prix ou données : " + e.getMessage()); }
        }
    }

    private void showAntecedentForm(Antecedent a) {
        boolean isNew = (a.getIdEntite() == null);
        JTextField nom = new JTextField(a.getNom());
        JComboBox<CategorieAntecedent> cat = new JComboBox<>(CategorieAntecedent.values());
        cat.setSelectedItem(a.getCategorie() != null ? a.getCategorie() : CategorieAntecedent.AUTRE);

        JComboBox<NiveauDeRisque> risque = new JComboBox<>(NiveauDeRisque.values());
        risque.setSelectedItem(a.getNiveauDeRisque() != null ? a.getNiveauDeRisque() : NiveauDeRisque.LOW);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(createFieldGroup("Nom / Libellé", nom));
        container.add(createFieldGroup("Catégorie", cat));
        container.add(createFieldGroup("Niveau de Risque", risque));

        String title = isNew ? "Ajouter Antécédent" : "Modifier Antécédent";
        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            a.setNom(nom.getText());
            a.setCategorie((CategorieAntecedent) cat.getSelectedItem());
            a.setNiveauDeRisque((NiveauDeRisque) risque.getSelectedItem());
            controller.updateAntecedent(a);
            refreshAntecedents();
        }
    }

    private void showActeForm(Acte act) {
        boolean isNew = (act.getIdEntite() == null);
        JTextField libelle = new JTextField(act.getLibelle());
        JTextField cat = new JTextField(act.getCategorie());

        String prixStr = (act.getPrixDeBase() != null) ? act.getPrixDeBase().toString() : "";
        JTextField prix = new JTextField(prixStr);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.add(createFieldGroup("Libellé de l'acte", libelle));
        container.add(createFieldGroup("Catégorie", cat));
        container.add(createFieldGroup("Prix de Base (DH)", prix));

        String title = isNew ? "Ajouter Acte" : "Modifier Acte";
        if (JOptionPane.showConfirmDialog(this, container, title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                act.setLibelle(libelle.getText());
                act.setCategorie(cat.getText());
                String p = prix.getText().trim().replace(",", ".");
                act.setPrixDeBase(p.isEmpty() ? BigDecimal.ZERO : new BigDecimal(p));
                controller.updateActe(act);
                refreshActes();
            } catch (Exception e) { showError("Prix invalide."); }
        }
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

    // --- Helpers UI ---
    private JPanel createMedicContainer(JTextField n, JTextField l, JTextField t, JComboBox f, JTextField p, JCheckBox r) {
        JPanel c = new JPanel(); c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.add(createFieldGroup("Nom Commercial", n));
        c.add(createFieldGroup("Laboratoire", l));
        c.add(createFieldGroup("Type / Molécule", t));
        c.add(createFieldGroup("Forme Pharmaceutique", f));
        c.add(createFieldGroup("Prix Unitaire (DH)", p));
        c.add(r); return c;
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        group.add(label, BorderLayout.NORTH);
        group.add(field, BorderLayout.CENTER);
        group.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return group;
    }

    private void refreshAll() { refreshMedicaments(); refreshAntecedents(); refreshActes(); }
    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "Erreur", JOptionPane.ERROR_MESSAGE); }
}
