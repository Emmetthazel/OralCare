package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import ma.oralCare.mvc.ui1.medecin.OrdonnancePanel;

public class DossierMedicalMedecinPanel extends JPanel {

    private final MainFrame mainFrame;
    private Long currentPatientId;

    // --- COMPOSANTS UI PRINCIPAUX ---
    private JTextField txtSearch, txtNom, txtCIN;
    private JTextArea areaDiagnostic, areaNotes;
    private JLabel lblReste;
    private JTable tableHistorique, tableInterventions;
    private DefaultTableModel modelHist, modelInterv;

    // --- RECHERCHE INTELLIGENTE ---
    private JPopupMenu menuSuggestions;
    private JList<String> listSuggestions;
    private DefaultListModel<String> modelSuggestions;

    // --- STYLE ---
    private final Color ACCENT_BLUE = new Color(41, 128, 185);
    private final Color PANEL_BG = new Color(245, 246, 250);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color PURPLE_COLOR = new Color(142, 68, 173);

    public DossierMedicalMedecinPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        initSuggestionMenu();
        initComponents();
    }

    private void initSuggestionMenu() {
        menuSuggestions = new JPopupMenu();
        modelSuggestions = new DefaultListModel<>();
        listSuggestions = new JList<>(modelSuggestions);
        listSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSuggestions.setCursor(new Cursor(Cursor.HAND_CURSOR));

        listSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selected = listSuggestions.getSelectedValue();
                if (selected != null) {
                    txtSearch.setText(selected.split(" — ")[0]);
                    menuSuggestions.setVisible(false);
                    refreshData(100L); // Simulé
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listSuggestions);
        scroll.setPreferredSize(new Dimension(400, 150));
        scroll.setBorder(null);
        menuSuggestions.add(scroll);
    }

    private void initComponents() {
        setupSearchBar();

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setOpaque(false);

        // Panneau gauche : Info patient et Historique
        JPanel leftPanel = new JPanel(new BorderLayout(0, 10));
        leftPanel.setOpaque(false);
        leftPanel.add(createPatientInfoCard(), BorderLayout.NORTH);
        leftPanel.add(createHistoryPanel(), BorderLayout.CENTER);

        // Panneau droit : Onglets de consultation et Ordonnances
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);
        rightPanel.add(createConsultationTabs(), BorderLayout.CENTER);
        rightPanel.add(createActionButtons(), BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(480);
        contentPanel.add(splitPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
        setupFooter();
    }

    private void setupSearchBar() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        txtSearch = new JTextField(" Rechercher par Nom / CIN / Téléphone...");
        txtSearch.setPreferredSize(new Dimension(0, 40));
        txtSearch.setFont(new Font("SansSerif", Font.ITALIC, 14));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().contains("Rechercher")) {
                    txtSearch.setText("");
                    txtSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
                }
            }
        });

        p.add(txtSearch, BorderLayout.CENTER);
        p.add(createBtn("🔍 Rechercher", ACCENT_BLUE), BorderLayout.EAST);
        p.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(p, BorderLayout.NORTH);
    }

    private void updateSuggestions() {
        String query = txtSearch.getText().trim();
        if (query.length() < 2 || query.contains("Rechercher")) {
            menuSuggestions.setVisible(false);
            return;
        }
        modelSuggestions.clear();
        // Les suggestions seront récupérées depuis la base de données via le controller/service

        if (modelSuggestions.getSize() > 0) {
            menuSuggestions.show(txtSearch, 0, txtSearch.getHeight());
            txtSearch.requestFocus();
        } else {
            menuSuggestions.setVisible(false);
        }
    }

    private JTabbedPane createConsultationTabs() {
        JTabbedPane tabs = new JTabbedPane();

        // ONGLET 1 : ÉDITION
        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BoxLayout(detailPanel, BoxLayout.Y_AXIS));
        areaDiagnostic = new JTextArea(3, 0);
        detailPanel.add(createSection("Diagnostic Médical", new JScrollPane(areaDiagnostic)));
        modelInterv = new DefaultTableModel(new String[]{"Acte", "Dent", "Prix", "Observation"}, 0);
        tableInterventions = new JTable(modelInterv);
        detailPanel.add(createSection("Interventions (Soins Réalisés)", new JScrollPane(tableInterventions)));
        areaNotes = new JTextArea(3, 0);
        detailPanel.add(createSection("Notes Confidentielles", new JScrollPane(areaNotes)));

        tabs.addTab("Édition Consultation", detailPanel);

        // ONGLET 2 : ORDONNANCES (Nouvelle maquette intégrée)
        tabs.addTab("Ordonnances", new OrdonnancePanel(currentPatientId));

        tabs.addTab("Certificats", new CertificatPanel());
        return tabs;
    }

    // --- CLASSE INTERNE : NOUVELLE PANEL ORDONNANCE ---


    // --- MÉTHODES HELPER ---
    private JPanel createPatientInfoCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(PANEL_BG);
        card.setBorder(BorderFactory.createTitledBorder(new LineBorder(ACCENT_BLUE), "FICHE PATIENT"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8); gbc.fill = GridBagConstraints.HORIZONTAL;
        txtNom = createReadOnlyField(); txtCIN = createReadOnlyField();
        gbc.gridx = 0; gbc.gridy = 0; card.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1; card.add(txtNom, gbc);
        gbc.gridx = 2; card.add(new JLabel("CIN:"), gbc);
        gbc.gridx = 3; card.add(txtCIN, gbc);
        lblReste = new JLabel("Reste à payer: 0 DH");
        lblReste.setForeground(new Color(192, 57, 43));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; card.add(lblReste, gbc);
        return card;
    }

    private JPanel createHistoryPanel() {
        modelHist = new DefaultTableModel(new String[]{"Date", "Statut", "Résumé"}, 0);
        tableHistorique = new JTable(modelHist);
        JScrollPane sp = new JScrollPane(tableHistorique);
        sp.setBorder(BorderFactory.createTitledBorder("HISTORIQUE CLINIQUE"));
        JPanel p = new JPanel(new BorderLayout()); p.add(sp); return p;
    }

    private JPanel createActionButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        p.setOpaque(false);
        p.add(createBtn("🦷 Intervention", SUCCESS_COLOR));
        p.add(createBtn("💊 Prescrire", PURPLE_COLOR));
        p.add(createBtn("💾 Enregistrer", ACCENT_BLUE));
        return p;
    }

    private void setupFooter() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT));
        f.setBackground(PANEL_BG);
        f.add(new JLabel(" Connecté : Dr. Cabinet | " + java.time.LocalDate.now()));
        add(f, BorderLayout.SOUTH);
    }

    public void refreshData(Long patientId) {
        this.currentPatientId = patientId;
        if (patientId == null) return;
        txtNom.setText("Patient Sélectionné " + patientId);
    }

    private JTextField createReadOnlyField() {
        JTextField f = new JTextField(10); f.setEditable(false); f.setBorder(null);
        f.setOpaque(false); f.setFont(new Font("SansSerif", Font.BOLD, 12)); return f;
    }

    private JPanel createSection(String title, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(comp, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); return p;
    }

    private JButton createBtn(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setForeground(color); b.setBackground(Color.WHITE);
        b.setOpaque(true); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(color); b.setForeground(Color.WHITE); }
            public void mouseExited(MouseEvent e) { b.setBackground(Color.WHITE); b.setForeground(color); }
        });
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}
