package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AddFactureDialog extends JDialog {
    // Composants du formulaire
    private JComboBox<PatientItem> cmbPatient;
    private JCheckBox chkSelectAllConsultations;
    private JTable tblConsultations;
    private JTextField txtTotaleFacture;
    private JTextField txtTotalePaye;
    private JTextField txtResteAPayer;
    private JButton btnPaiementTotal;

    // Panels pour contrôle de visibilité
    private JPanel consultationsSection;
    private JPanel montantsSection;
    private JPanel apercuSection;

    // Labels pour l'aperçu
    private JLabel lblApercuPatient;
    private JTextArea txtApercuConsultations;
    private JLabel lblApercuTotalFacture;
    private JLabel lblApercuTotalPaye;
    private JLabel lblApercuResteAPayer;
    private JLabel lblApercuStatut;

    // Composant pour l'en-tête de l'aperçu
    private JLabel apercuHeaderLabel;

    // Données chargées
    private List<PatientItem> patients;

    // Couleurs
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_COLOR = new Color(80, 80, 80);
    private static final Color READONLY_BG = new Color(248, 248, 248);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color TABLE_HEADER_BG = new Color(37, 99, 235, 20);
    private static final Color TABLE_SELECTION_BG = new Color(37, 99, 235, 10);

    private boolean validated = false;

    // Classes pour les items des combobox
    public class PatientItem {
        private int id;
        private String nom;
        private String prenom;

        public PatientItem(int id, String nom, String prenom, String cin, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
        }

        public int getId() { return id; }
        public String getNom() { return nom; }
        public String getPrenom() { return prenom; }

        @Override
        public String toString() {
            return nom + " " + prenom;
        }

        public String getInfoComplete() {
            return "Nom complet: " + nom + " " + prenom;
        }
    }

    public AddFactureDialog(Frame parent) {
        super(parent, "Créer une nouvelle facture", true);
        initComponents();
        loadData();
        setSize(900, 800);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main Content Panel
        JPanel contentPanel = createContentPanel();

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Footer Panel with Buttons
        JPanel footerPanel = createFooter();

        add(mainScrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 35, 25, 35));
        contentPanel.setBackground(Color.WHITE);

        // Section: Sélection du patient
        contentPanel.add(createSectionHeader("Sélection du patient"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createPatientSelectionPanel());
        contentPanel.add(Box.createVerticalStrut(25));

        // Section: Consultations (cachée initialement)
        consultationsSection = createConsultationsSection();
        consultationsSection.setVisible(false);
        contentPanel.add(consultationsSection);
        contentPanel.add(Box.createVerticalStrut(25));

        // Section: Montants (cachée initialement)
        montantsSection = createMontantsSection();
        montantsSection.setVisible(false);
        contentPanel.add(montantsSection);
        contentPanel.add(Box.createVerticalStrut(25));

        // Section: Aperçu de facture (cachée initialement)
        apercuSection = createApercuSection();
        apercuSection.setVisible(false);
        contentPanel.add(apercuSection);

        return contentPanel;
    }

    private JPanel createPatientSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Patient ComboBox
        cmbPatient = new JComboBox<>();
        cmbPatient.addItem(new PatientItem(0, "Sélectionner un patient", "", "", ""));
        cmbPatient.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleComponent(cmbPatient);
        cmbPatient.addActionListener(e -> onPatientSelected());

        cmbPatient.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (c instanceof JComponent && value instanceof PatientItem) {
                    PatientItem patient = (PatientItem) value;
                    ((JComponent) c).setToolTipText(patient.getId() > 0 ? patient.getInfoComplete() : "Sélectionnez un patient");
                }
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                setBorder(new EmptyBorder(5, 10, 5, 10));
                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                }
                return c;
            }
        });

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        panel.add(createFieldPanel("Patient *", cmbPatient), gbc);

        return panel;
    }

    private JPanel createConsultationsSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);

        // Section Header
        JPanel headerPanel = createSectionHeader("Consultations du patient");
        sectionPanel.add(headerPanel);
        sectionPanel.add(Box.createVerticalStrut(15));

        // Checkbox "Sélectionner toutes les consultations"
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(Color.WHITE);

        chkSelectAllConsultations = new JCheckBox("Sélectionner toutes les consultations");
        chkSelectAllConsultations.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkSelectAllConsultations.setForeground(PRIMARY_COLOR);
        chkSelectAllConsultations.setBackground(Color.WHITE);
        chkSelectAllConsultations.setFocusPainted(false);
        chkSelectAllConsultations.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkSelectAllConsultations.setEnabled(false);
        chkSelectAllConsultations.addActionListener(e -> onSelectAllConsultations());

        checkboxPanel.add(chkSelectAllConsultations);

        JPanel fieldPanel = createFieldPanel("", checkboxPanel);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sectionPanel.add(fieldPanel);
        sectionPanel.add(Box.createVerticalStrut(10));

        // Table des consultations
        String[] columnNames = {"Sélection", "Date", "Description", "Montant (MAD)", "Statut"};
        Object[][] data = {}; // Vide initialement

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Seule la colonne Sélection est éditable
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 0 && row < getRowCount()) {
                    Object value = super.getValueAt(row, column);
                    if (value == null) {
                        return Boolean.FALSE;
                    }
                    return value;
                }
                return super.getValueAt(row, column);
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 0) {
                    Boolean booleanValue;
                    if (aValue instanceof Boolean) {
                        booleanValue = (Boolean) aValue;
                    } else if (aValue instanceof String) {
                        try {
                            booleanValue = Boolean.valueOf((String) aValue);
                        } catch (Exception e) {
                            booleanValue = Boolean.FALSE;
                        }
                    } else {
                        booleanValue = Boolean.FALSE;
                    }

                    super.setValueAt(booleanValue, row, column);

                    fireTableCellUpdated(row, column);

                    updateSelectAllCheckbox();
                } else {
                    super.setValueAt(aValue, row, column);
                }
            }

            private void updateSelectAllCheckbox() {
                if (chkSelectAllConsultations != null && chkSelectAllConsultations.isEnabled()) {
                    boolean allSelected = true;
                    boolean noneSelected = true;

                    for (int i = 0; i < getRowCount(); i++) {
                        Object value = getValueAt(i, 0);
                        if (value instanceof Boolean) {
                            Boolean isSelected = (Boolean) value;
                            if (isSelected != null && isSelected) {
                                noneSelected = false;
                            } else {
                                allSelected = false;
                            }
                        }
                    }

                    // Enlever temporairement le listener pour éviter une boucle infinie
                    ActionListener[] listeners = chkSelectAllConsultations.getActionListeners();
                    for (ActionListener listener : listeners) {
                        chkSelectAllConsultations.removeActionListener(listener);
                    }

                    if (allSelected && getRowCount() > 0) {
                        chkSelectAllConsultations.setSelected(true);
                        chkSelectAllConsultations.setText("Désélectionner toutes les consultations");
                    } else if (noneSelected) {
                        chkSelectAllConsultations.setSelected(false);
                        chkSelectAllConsultations.setText("Sélectionner toutes les consultations");
                    } else {
                        chkSelectAllConsultations.setSelected(false);
                        chkSelectAllConsultations.setText("Sélectionner toutes les consultations");
                    }

                    // Réajouter le listener
                    chkSelectAllConsultations.addActionListener(e -> onSelectAllConsultations());
                }
            }
        };

        tblConsultations = new JTable(model);
        tblConsultations.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblConsultations.setRowHeight(35);
        tblConsultations.setIntercellSpacing(new Dimension(0, 0));
        tblConsultations.setShowGrid(false);
        tblConsultations.setSelectionBackground(TABLE_SELECTION_BG);

        // IMPORTANT: Configuration pour permettre l'édition des cases à cocher
        tblConsultations.setRowSelectionAllowed(true);
        tblConsultations.setColumnSelectionAllowed(false);
        tblConsultations.setCellSelectionEnabled(false);

        // Configurer le renderer pour la colonne booléenne
        tblConsultations.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            private JCheckBox checkBox = new JCheckBox();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Boolean) {
                    checkBox.setSelected((Boolean) value);
                    checkBox.setHorizontalAlignment(JLabel.CENTER);
                    checkBox.setBackground(isSelected ? TABLE_SELECTION_BG : Color.WHITE);
                    checkBox.setOpaque(true);
                    return checkBox;
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Configurer l'éditeur pour la colonne booléenne
        tblConsultations.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            {
                ((JCheckBox) getComponent()).setHorizontalAlignment(JLabel.CENTER);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                                                         boolean isSelected, int row, int column) {
                Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
                if (component instanceof JCheckBox) {
                    ((JCheckBox) component).setHorizontalAlignment(JLabel.CENTER);
                    ((JCheckBox) component).setBackground(Color.WHITE);
                }
                return component;
            }
        });

        JTableHeader header = tblConsultations.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(PRIMARY_COLOR);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        tblConsultations.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblConsultations.getColumnModel().getColumn(0).setMaxWidth(80);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 1; i < tblConsultations.getColumnCount(); i++) {
            tblConsultations.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        model.addTableModelListener(e -> {
            if (e.getColumn() == 0) {
                SwingUtilities.invokeLater(() -> {
                    onConsultationSelectionChanged();
                });
            }
        });

        // Ajouter un MouseListener pour capturer les clics sur les cases à cocher
        tblConsultations.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleTableClick(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleTableClick(e);
            }

            private void handleTableClick(MouseEvent e) {
                int column = tblConsultations.columnAtPoint(e.getPoint());
                int row = tblConsultations.rowAtPoint(e.getPoint());

                if (row >= 0 && column >= 0 && column == 0) {
                    // Inverser la valeur actuelle
                    Boolean currentValue = (Boolean) model.getValueAt(row, column);
                    model.setValueAt(!currentValue, row, column);

                    // Forcer la mise à jour de l'affichage
                    tblConsultations.repaint();

                    // Mettre à jour l'interface immédiatement
                    SwingUtilities.invokeLater(() -> {
                        onConsultationSelectionChanged();
                    });
                }
            }
        });

        // Permettre la sélection avec la barre d'espace
        tblConsultations.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    int row = tblConsultations.getSelectedRow();
                    int column = tblConsultations.getSelectedColumn();

                    if (row >= 0 && column == 0) {
                        Boolean currentValue = (Boolean) model.getValueAt(row, column);
                        model.setValueAt(!currentValue, row, column);
                        onConsultationSelectionChanged();
                    }
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(tblConsultations);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        tableScrollPane.setPreferredSize(new Dimension(800, 200));

        // Panneau de test/debug
        JPanel debugPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        debugPanel.setBackground(Color.WHITE);
        debugPanel.setBorder(BorderFactory.createTitledBorder("Debug - Test de sélection"));

        JButton btnTestSelectFirst = new JButton("Test: Sélectionner première");
        btnTestSelectFirst.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnTestSelectFirst.addActionListener(e -> {
            if (model.getRowCount() > 0) {
                model.setValueAt(true, 0, 0);
                onConsultationSelectionChanged();
                tblConsultations.repaint();
            }
        });

        JButton btnTestSelectAll = new JButton("Test: Tout sélectionner");
        btnTestSelectAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnTestSelectAll.addActionListener(e -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(true, i, 0);
            }
            onConsultationSelectionChanged();
            tblConsultations.repaint();
        });

        JButton btnTestDeselectAll = new JButton("Test: Tout désélectionner");
        btnTestDeselectAll.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnTestDeselectAll.addActionListener(e -> {
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(false, i, 0);
            }
            onConsultationSelectionChanged();
            tblConsultations.repaint();
        });

        debugPanel.add(btnTestSelectFirst);
        debugPanel.add(btnTestSelectAll);
        debugPanel.add(btnTestDeselectAll);

        // Panneau de validation
        JPanel validationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        validationPanel.setBackground(Color.WHITE);

        JButton btnValiderSelection = createButton("Valider la sélection", new Color(46, 125, 50), Color.WHITE, false);
        btnValiderSelection.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnValiderSelection.setPreferredSize(new Dimension(150, 30));
        btnValiderSelection.setToolTipText("Cliquez pour valider votre sélection de consultations");
        btnValiderSelection.addActionListener(e -> {
            onConsultationSelectionChanged();
        });

        validationPanel.add(btnValiderSelection);

        sectionPanel.add(tableScrollPane);
        sectionPanel.add(Box.createVerticalStrut(5));
        sectionPanel.add(debugPanel);
        sectionPanel.add(validationPanel);

        return sectionPanel;
    }

    private JPanel createMontantsSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);

        JPanel headerPanel = createSectionHeader("Montants");
        sectionPanel.add(headerPanel);
        sectionPanel.add(Box.createVerticalStrut(15));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtTotaleFacture = createStyledTextField("0.00", true);
        txtTotaleFacture.setEditable(false);
        txtTotaleFacture.setToolTipText("Total des consultations sélectionnées");

        txtTotalePaye = createStyledTextField("0.00", false);
        txtTotalePaye.setToolTipText("Montant payé par le patient");

        txtResteAPayer = createStyledTextField("0.00", true);
        txtResteAPayer.setEditable(false);
        txtResteAPayer.setToolTipText("Reste à payer");

        btnPaiementTotal = createButton("Paiement total immédiat", PRIMARY_COLOR, Color.WHITE, false);
        btnPaiementTotal.setPreferredSize(new Dimension(200, 40));
        btnPaiementTotal.setToolTipText("Régler le total de la facture immédiatement");

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Total facture *", txtTotaleFacture), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Total payé *", txtTotalePaye), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Reste à payer", txtResteAPayer), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnPaiementTotal);
        panel.add(createFieldPanel("", buttonPanel), gbc);

        sectionPanel.add(panel);

        txtTotalePaye.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { updateMontantsAndApercu(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateMontantsAndApercu(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateMontantsAndApercu(); }

            private void updateMontantsAndApercu() {
                updateMontantsSection();
                updateApercuSection();
            }
        });

        btnPaiementTotal.addActionListener(e -> {
            try {
                String totalStr = txtTotaleFacture.getText().replace(",", "").replace(" ", "").trim();
                double totalFacture = Double.parseDouble(totalStr);
                txtTotalePaye.setText(String.format("%,.2f", totalFacture));
                updateMontantsSection();
                updateApercuSection();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(AddFactureDialog.this,
                        "Impossible de calculer le total de la facture",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        return sectionPanel;
    }

    private JPanel createApercuSection() {
        JPanel sectionPanel = new JPanel();
        sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
        sectionPanel.setBackground(Color.WHITE);
        sectionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // En-tête avec référence stockée
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.setBackground(Color.WHITE);

        apercuHeaderLabel = new JLabel("Aperçu de la facture");
        apercuHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        apercuHeaderLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(apercuHeaderLabel);

        sectionPanel.add(headerPanel);
        sectionPanel.add(Box.createVerticalStrut(15));

        JPanel gridPanel = new JPanel(new GridBagLayout());
        gridPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblPatient = new JLabel("Patient:");
        lblPatient.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPatient.setForeground(TEXT_COLOR);
        gridPanel.add(lblPatient, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.7;
        lblApercuPatient = new JLabel("Non sélectionné");
        lblApercuPatient.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(lblApercuPatient, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblConsultations = new JLabel("Consultations:");
        lblConsultations.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConsultations.setForeground(TEXT_COLOR);
        gridPanel.add(lblConsultations, gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.7; gbc.fill = GridBagConstraints.BOTH;
        txtApercuConsultations = new JTextArea(3, 30);
        txtApercuConsultations.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtApercuConsultations.setEditable(false);
        txtApercuConsultations.setBackground(READONLY_BG);
        txtApercuConsultations.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        txtApercuConsultations.setText("Aucune consultation sélectionnée");
        gridPanel.add(new JScrollPane(txtApercuConsultations), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblTotalFacture = new JLabel("Total facture:");
        lblTotalFacture.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalFacture.setForeground(TEXT_COLOR);
        gridPanel.add(lblTotalFacture, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.7;
        lblApercuTotalFacture = new JLabel("0.00 MAD");
        lblApercuTotalFacture.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(lblApercuTotalFacture, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        JLabel lblTotalPaye = new JLabel("Total payé:");
        lblTotalPaye.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotalPaye.setForeground(TEXT_COLOR);
        gridPanel.add(lblTotalPaye, gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.7;
        lblApercuTotalPaye = new JLabel("0.00 MAD");
        lblApercuTotalPaye.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(lblApercuTotalPaye, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        JLabel lblResteAPayer = new JLabel("Reste à payer:");
        lblResteAPayer.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResteAPayer.setForeground(TEXT_COLOR);
        gridPanel.add(lblResteAPayer, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 0.7;
        lblApercuResteAPayer = new JLabel("0.00 MAD");
        lblApercuResteAPayer.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gridPanel.add(lblApercuResteAPayer, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.3;
        JLabel lblStatut = new JLabel("Statut:");
        lblStatut.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatut.setForeground(TEXT_COLOR);
        gridPanel.add(lblStatut, gbc);

        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.7;
        lblApercuStatut = new JLabel("En attente");
        lblApercuStatut.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblApercuStatut.setForeground(WARNING_COLOR);
        gridPanel.add(lblApercuStatut, gbc);

        sectionPanel.add(gridPanel);

        return sectionPanel;
    }

    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnAnnuler = createButton("Annuler", Color.WHITE, TEXT_COLOR, true);
        btnAnnuler.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment annuler ? Les données saisies seront perdues.",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        JButton btnCreer = createButton("Créer la facture", PRIMARY_COLOR, Color.WHITE, false);
        btnCreer.setPreferredSize(new Dimension(170, 40));
        btnCreer.addActionListener(e -> validateAndSave());

        footerPanel.add(btnAnnuler);
        footerPanel.add(btnCreer);

        return footerPanel;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean bordered) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", bordered ? Font.PLAIN : Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (bordered) {
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        } else {
            button.setBorderPainted(false);
        }

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bordered) {
                    button.setBackground(new Color(248, 248, 248));
                } else {
                    button.setBackground(new Color(29, 78, 216));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    private JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_COLOR);

        panel.add(label);
        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (!labelText.isEmpty()) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setForeground(TEXT_COLOR);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createVerticalStrut(6));
        }

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (field instanceof JTextField) {
            Dimension fieldSize = new Dimension(350, 45);
            ((JTextField) field).setPreferredSize(fieldSize);
            ((JTextField) field).setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        } else if (field instanceof JComboBox) {
            Dimension fieldSize = new Dimension(350, 45);
            field.setPreferredSize(fieldSize);
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        }

        panel.add(field);

        return panel;
    }

    private JTextField createStyledTextField(String text, boolean readonly) {
        JTextField textField = new JTextField(text);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        if (readonly) {
            textField.setBackground(READONLY_BG);
            textField.setForeground(TEXT_COLOR);
        } else {
            textField.setBackground(Color.WHITE);
            textField.setForeground(Color.BLACK);
        }

        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        if (!readonly) {
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                            new EmptyBorder(9, 11, 9, 11)
                    ));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    textField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                            new EmptyBorder(10, 12, 10, 12)
                    ));
                }
            });
        }

        return textField;
    }

    private void styleComponent(JComponent component) {
        if (component instanceof JComboBox) {
            component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));

            component.setBackground(Color.WHITE);
        }
    }

    private void loadData() {
        patients = java.util.List.of(
                new PatientItem(1, "DUPONT", "Jean", "AB123456", "0612345678"),
                new PatientItem(2, "MARTIN", "Marie", "CD789012", "0623456789"),
                new PatientItem(3, "DURAND", "Pierre", "EF345678", "0634567890"),
                new PatientItem(4, "BERNARD", "Sophie", "GH901234", "0645678901"),
                new PatientItem(5, "PETIT", "Luc", "IJ567890", "0656789012")
        );

        for (PatientItem patient : patients) {
            cmbPatient.addItem(patient);
        }
    }

    private void onPatientSelected() {
        PatientItem selectedPatient = (PatientItem) cmbPatient.getSelectedItem();

        if (selectedPatient == null || selectedPatient.getId() == 0) {
            consultationsSection.setVisible(false);
            montantsSection.setVisible(false);
            apercuSection.setVisible(false);

            DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
            model.setRowCount(0);
            chkSelectAllConsultations.setEnabled(false);
            chkSelectAllConsultations.setSelected(false);

            return;
        }

        consultationsSection.setVisible(true);
        updateConsultationsTable(selectedPatient.getId());
        chkSelectAllConsultations.setEnabled(true);
        montantsSection.setVisible(false);
        apercuSection.setVisible(false);
        updateApercuSection();
    }

    private void updateConsultationsTable(int patientId) {
        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        model.setRowCount(0);

        Object[][] sampleData = {
                {false, "15/01/2024", "Consultation dentaire", "200.00", "Non facturé"},
                {false, "20/01/2024", "Détartrage complet", "350.00", "Non facturé"},
                {false, "25/01/2024", "Soin carie", "600.00", "Non facturé"},
                {false, "30/01/2024", "Contrôle", "150.00", "Non facturé"}
        };

        for (Object[] row : sampleData) {
            model.addRow(row);
        }

        // Forcer le rafraîchissement de l'affichage
        tblConsultations.repaint();
    }

    private void onSelectAllConsultations() {
        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        boolean selectAll = chkSelectAllConsultations.isSelected();

        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(selectAll, i, 0);
        }

        // Forcer le rafraîchissement
        tblConsultations.repaint();

        onConsultationSelectionChanged();
    }

    private void onConsultationSelectionChanged() {
        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        boolean hasSelection = false;
        int selectedCount = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, 0);
            if (value instanceof Boolean) {
                Boolean isSelected = (Boolean) value;
                if (isSelected != null && isSelected) {
                    hasSelection = true;
                    selectedCount++;
                }
            }
        }

        montantsSection.setVisible(hasSelection);
        apercuSection.setVisible(hasSelection);

        if (hasSelection) {
            updateMontantsSection();
        }

        updateApercuSection();

        // Mettre à jour le titre de l'aperçu en utilisant la référence directe
        if (hasSelection && apercuSection.isVisible()) {
            apercuHeaderLabel.setText("Aperçu de la facture (" + selectedCount + " consultation(s))");
        } else if (apercuHeaderLabel != null) {
            apercuHeaderLabel.setText("Aperçu de la facture");
        }
    }

    private void updateMontantsSection() {
        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        double totalFacture = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object selectionValue = model.getValueAt(i, 0);

            if (selectionValue instanceof Boolean) {
                Boolean isSelected = (Boolean) selectionValue;
                if (isSelected != null && isSelected) {
                    Object montantValue = model.getValueAt(i, 3);
                    if (montantValue instanceof String) {
                        String montantStr = ((String) montantValue).replace(" MAD", "").trim();
                        try {
                            // Gérer le format avec point ou virgule comme séparateur décimal
                            montantStr = montantStr.replace(",", "").replace(" ", "");
                            double montant = Double.parseDouble(montantStr);
                            totalFacture += montant;
                        } catch (NumberFormatException e) {
                            System.err.println("Erreur de parsing: " + montantStr);
                            // Ignorer les erreurs de parsing
                        }
                    }
                }
            }
        }

        String totalFormatted = String.format("%,.2f", totalFacture);
        txtTotaleFacture.setText(totalFormatted);
        txtTotaleFacture.setToolTipText("Total des consultations sélectionnées: " + totalFormatted + " MAD");

        double totalPaye = 0.0;
        try {
            String totalPayeStr = txtTotalePaye.getText().replace(" MAD", "").replace(" ", "").trim();
            totalPayeStr = totalPayeStr.replace(",", ".");
            totalPaye = Double.parseDouble(totalPayeStr);
        } catch (NumberFormatException e) {
            // Conserver 0.00
        }

        double resteAPayer = totalFacture - totalPaye;
        if (resteAPayer < 0) resteAPayer = 0;

        String resteFormatted = String.format("%,.2f", resteAPayer);
        txtResteAPayer.setText(resteFormatted);
        txtResteAPayer.setToolTipText("Reste à payer: " + resteFormatted + " MAD");
    }

    private void updateApercuSection() {
        PatientItem selectedPatient = (PatientItem) cmbPatient.getSelectedItem();

        if (selectedPatient == null || selectedPatient.getId() == 0) {
            lblApercuPatient.setText("Non sélectionné");
            txtApercuConsultations.setText("Aucune consultation sélectionnée");
            lblApercuTotalFacture.setText("0.00 MAD");
            lblApercuTotalPaye.setText("0.00 MAD");
            lblApercuResteAPayer.setText("0.00 MAD");
            lblApercuStatut.setText("En attente");
            lblApercuStatut.setForeground(WARNING_COLOR);
            return;
        }

        lblApercuPatient.setText(selectedPatient.getNom() + " " + selectedPatient.getPrenom());

        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        StringBuilder consultationsText = new StringBuilder();
        double totalFacture = 0.0;
        int selectedCount = 0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object selectionValue = model.getValueAt(i, 0);

            if (selectionValue instanceof Boolean) {
                Boolean isSelected = (Boolean) selectionValue;
                if (isSelected != null && isSelected) {
                    selectedCount++;
                    String date = (String) model.getValueAt(i, 1);
                    String description = (String) model.getValueAt(i, 2);
                    String montantStr = (String) model.getValueAt(i, 3);

                    consultationsText.append("• ")
                            .append(date)
                            .append(" - ")
                            .append(description)
                            .append(" (")
                            .append(montantStr)
                            .append(")\n");

                    try {
                        // Gérer le format avec point ou virgule
                        String montantClean = montantStr.replace(" MAD", "").replace(" ", "").trim();
                        montantClean = montantClean.replace(",", ".");
                        totalFacture += Double.parseDouble(montantClean);
                    } catch (NumberFormatException e) {
                        System.err.println("Erreur de parsing dans aperçu: " + montantStr);
                        // Ignorer les erreurs
                    }
                }
            }
        }

        if (selectedCount > 0) {
            txtApercuConsultations.setText(consultationsText.toString());
            txtApercuConsultations.setToolTipText(selectedCount + " consultation(s) sélectionnée(s)");
        } else {
            txtApercuConsultations.setText("Aucune consultation sélectionnée");
            txtApercuConsultations.setToolTipText("Sélectionnez au moins une consultation");
        }

        String totalFactureFormatted = String.format("%,.2f MAD", totalFacture);
        lblApercuTotalFacture.setText(totalFactureFormatted);

        double totalPaye = 0.0;
        try {
            String totalPayeStr = txtTotalePaye.getText().replace(" MAD", "").replace(" ", "").trim();
            totalPayeStr = totalPayeStr.replace(",", ".");
            totalPaye = Double.parseDouble(totalPayeStr);
        } catch (NumberFormatException e) {
            // Conserver 0.00
        }

        String totalPayeFormatted = String.format("%,.2f MAD", totalPaye);
        lblApercuTotalPaye.setText(totalPayeFormatted);

        double resteAPayer = totalFacture - totalPaye;
        if (resteAPayer < 0) resteAPayer = 0;

        String resteFormatted = String.format("%,.2f MAD", resteAPayer);
        lblApercuResteAPayer.setText(resteFormatted);

        if (totalPaye >= totalFacture && totalFacture > 0) {
            lblApercuStatut.setText("Payée");
            lblApercuStatut.setForeground(SUCCESS_COLOR);
        } else if (totalPaye > 0 && totalPaye < totalFacture) {
            lblApercuStatut.setText("Partiellement payée");
            lblApercuStatut.setForeground(new Color(255, 140, 0));
        } else if (totalFacture > 0) {
            lblApercuStatut.setText("En attente de paiement");
            lblApercuStatut.setForeground(WARNING_COLOR);
        } else {
            lblApercuStatut.setText("En attente");
            lblApercuStatut.setForeground(WARNING_COLOR);
        }
    }

    private void validateAndSave() {
        PatientItem selectedPatient = (PatientItem) cmbPatient.getSelectedItem();

        if (selectedPatient == null || selectedPatient.getId() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un patient",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblConsultations.getModel();
        boolean hasSelection = false;
        int selectedCount = 0;
        double totalFacture = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            Object value = model.getValueAt(i, 0);

            if (value instanceof Boolean) {
                Boolean isSelected = (Boolean) value;
                if (isSelected != null && isSelected) {
                    hasSelection = true;
                    selectedCount++;

                    // Calculer le total facture directement depuis la table
                    String montantStr = (String) model.getValueAt(i, 3);
                    try {
                        String montantClean = montantStr.replace(" MAD", "").replace(" ", "").trim();
                        montantClean = montantClean.replace(",", ".");
                        totalFacture += Double.parseDouble(montantClean);
                    } catch (NumberFormatException e) {
                        // Ignorer les erreurs
                    }
                }
            }
        }

        if (!hasSelection) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner au moins une consultation",
                    "Erreur de validation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalPaye = 0.0;
        try {
            String totalPayeStr = txtTotalePaye.getText().replace(" MAD", "").replace(" ", "").trim();
            totalPayeStr = totalPayeStr.replace(",", ".");
            totalPaye = Double.parseDouble(totalPayeStr);
        } catch (NumberFormatException e) {
            // Conserver 0.00
        }

        double resteAPayer = totalFacture - totalPaye;
        if (resteAPayer < 0) resteAPayer = 0;

        // Déterminer le statut
        String statut;
        if (totalPaye >= totalFacture && totalFacture > 0) {
            statut = "Payée";
        } else if (totalPaye > 0 && totalPaye < totalFacture) {
            statut = "Partiellement payée";
        } else if (totalFacture > 0) {
            statut = "En attente de paiement";
        } else {
            statut = "En attente";
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "<html><body style='width: 300px'>" +
                        "<b>Voulez-vous créer cette facture ?</b><br><br>" +
                        "<b>Patient:</b> " + selectedPatient.getNom() + " " + selectedPatient.getPrenom() + "<br>" +
                        "<b>Consultations sélectionnées:</b> " + selectedCount + "<br>" +
                        "<b>Total facture:</b> " + String.format("%,.2f", totalFacture) + " MAD<br>" +
                        "<b>Total payé:</b> " + String.format("%,.2f", totalPaye) + " MAD<br>" +
                        "<b>Reste à payer:</b> " + String.format("%,.2f", resteAPayer) + " MAD<br>" +
                        "<b>Statut:</b> " + statut + "<br><br>" +
                        "Cette opération enregistrera la facture dans le système." +
                        "</body></html>",
                "Confirmation de création",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            validated = true;
            JOptionPane.showMessageDialog(this,
                    "Facture créée avec succès !\n" +
                            "Patient: " + selectedPatient.getNom() + " " + selectedPatient.getPrenom() + "\n" +
                            "Nombre de consultations: " + selectedCount + "\n" +
                            "Total: " + String.format("%,.2f", totalFacture) + " MAD\n" +
                            "L'interface est prête à être connectée aux services métier.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        }
    }

    public boolean isValidated() {
        return validated;
    }

    public PatientItem getSelectedPatient() {
        return (PatientItem) cmbPatient.getSelectedItem();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AddFactureDialog dialog = new AddFactureDialog(frame);
            dialog.setVisible(true);
            if (dialog.isValidated()) {
                System.out.println("Interface validée - Prête pour l'intégration avec les services métier");
            }
            System.exit(0);
        });
    }
}