package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Vector;

/**
 * Panel de gestion du Dossier Médical complet avec recherche intelligente.
 */
public class DossierMedicalPanel extends JPanel {

    private final MainFrame mainFrame;
    private Long currentPatientId = null;
    private Long currentDossierId = null;

    // Composants de recherche intelligente
    private JTextField searchField;
    private JPopupMenu suggestionPopup;
    private JList<PatientSuggestion> suggestionList;
    private DefaultListModel<PatientSuggestion> listModel;

    // Labels d'information
    private JLabel lblNom, lblAge, lblSexe, lblCIN, lblTel, lblEmail, lblAdresse, lblAssurance;

    // Tables
    private JTable tableRDV, tableInterventions, tableFactures, tablePrescriptions, tableAntecedents, tableCertificats;

    public DossierMedicalPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        setupNorthPanel();
        setupCenterPanel();
        setupSearchLogic();
    }

    // --- INTERFACE ---

    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout(15, 15));
        northPanel.setOpaque(false);

        // 1. Barre de recherche intelligente
        JPanel searchBarPanel = new JPanel(new BorderLayout(10, 0));
        searchBarPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(450, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        setPlaceholder(searchField, "Rechercher un patient (nom ou CIN)...");

        searchBarPanel.add(new JLabel("🔍"), BorderLayout.WEST);
        searchBarPanel.add(searchField, BorderLayout.CENTER);

        // 2. Fiche Patient
        JPanel infoPanel = new JPanel(new GridLayout(2, 4, 20, 10));
        infoPanel.setBackground(new Color(250, 251, 252));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(new Color(230, 230, 230)), "Informations Patient"),
                new EmptyBorder(10, 15, 10, 15)));

        lblNom = createInfoLabel("Patient: -");
        lblAge = createInfoLabel("Âge: -");
        lblSexe = createInfoLabel("Sexe: -");
        lblCIN = createInfoLabel("CIN: -");
        lblTel = createInfoLabel("Tél: -");
        lblEmail = createInfoLabel("Email: -");
        lblAdresse = createInfoLabel("Adresse: -");
        lblAssurance = createInfoLabel("Assurance: -");

        infoPanel.add(lblNom); infoPanel.add(lblAge); infoPanel.add(lblSexe); infoPanel.add(lblCIN);
        infoPanel.add(lblTel); infoPanel.add(lblEmail); infoPanel.add(lblAdresse); infoPanel.add(lblAssurance);

        northPanel.add(searchBarPanel, BorderLayout.NORTH);
        northPanel.add(infoPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 12));

        tableRDV = createStyledTable();
        tableInterventions = createStyledTable();
        tableFactures = createStyledTable();
        tablePrescriptions = createStyledTable();
        tableAntecedents = createStyledTable();
        tableCertificats = createStyledTable();

        tabs.addTab("📅 RDV & Consultations", new JScrollPane(tableRDV));
        tabs.addTab("🦷 Actes & Soins", new JScrollPane(tableInterventions));
        tabs.addTab("💰 Factures", new JScrollPane(tableFactures));
        tabs.addTab("💊 Ordonnances", new JScrollPane(tablePrescriptions));
        tabs.addTab("⚠️ Antécédents", new JScrollPane(tableAntecedents));
        tabs.addTab("📜 Certificats", new JScrollPane(tableCertificats));

        add(tabs, BorderLayout.CENTER);
    }

    // --- LOGIQUE DE RECHERCHE ---

    private void setupSearchLogic() {
        suggestionPopup = new JPopupMenu();
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionBackground(new Color(0, 120, 215));
        suggestionList.setSelectionForeground(Color.WHITE);

        suggestionPopup.add(new JScrollPane(suggestionList));
        suggestionPopup.setFocusable(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateSuggestions(); }
            public void removeUpdate(DocumentEvent e) { updateSuggestions(); }
            public void changedUpdate(DocumentEvent e) { updateSuggestions(); }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && suggestionPopup.isVisible()) {
                    suggestionList.requestFocus();
                    suggestionList.setSelectedIndex(0);
                }
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectPatient(suggestionList.getSelectedValue());
            }
        });
    }

    private void updateSuggestions() {
        String text = searchField.getText().trim();
        if (text.isEmpty() || text.contains("Rechercher")) {
            suggestionPopup.setVisible(false);
            return;
        }

        listModel.clear();
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            String sql = "SELECT id_entite, nom, prenom, cin FROM Patient WHERE nom LIKE ? OR cin LIKE ? LIMIT 10";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + text + "%");
            ps.setString(2, "%" + text + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listModel.addElement(new PatientSuggestion(
                        rs.getLong("id_entite"),
                        rs.getString("nom").toUpperCase() + " " + rs.getString("prenom") + " (" + rs.getString("cin") + ")"
                ));
            }
            if (!listModel.isEmpty()) {
                suggestionPopup.setPopupSize(searchField.getWidth(), Math.min(listModel.size() * 25 + 10, 200));
                suggestionPopup.show(searchField, 0, searchField.getHeight());
                searchField.requestFocus();
            } else {
                suggestionPopup.setVisible(false);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    private void selectPatient(PatientSuggestion suggestion) {
        if (suggestion == null) return;
        this.currentPatientId = suggestion.id;
        searchField.setText(suggestion.display);
        suggestionPopup.setVisible(false);
        refreshData();
    }

    // --- CHARGEMENT DES DONNÉES ---

    public void refreshData() {
        if (currentPatientId == null) return;
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            loadPatientInfo(conn);
            loadDossierId(conn);
            if (currentDossierId != null) {
                loadRDVs(conn);
                loadInterventions(conn);
                loadFactures(conn);
                loadPrescriptions(conn);
                loadAntecedents(conn);
                loadCertificats(conn);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement SQL: " + e.getMessage());
        }
    }

    private void loadPatientInfo(Connection conn) throws SQLException {
        String sql = "SELECT * FROM Patient WHERE id_entite = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, currentPatientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            lblNom.setText("<html><b>Patient:</b> " + rs.getString("nom").toUpperCase() + " " + rs.getString("prenom") + "</html>");
            lblCIN.setText("<html><b>CIN:</b> " + rs.getString("cin") + "</html>");
            lblSexe.setText("<html><b>Sexe:</b> " + rs.getString("sexe") + "</html>");
            lblTel.setText("<html><b>Tél:</b> " + (rs.getString("telephone") != null ? rs.getString("telephone") : "-") + "</html>");
            lblAssurance.setText("<html><b>Assurance:</b> " + rs.getString("assurance") + "</html>");

            Date birth = rs.getDate("date_de_naissance");
            if (birth != null) {
                int age = Period.between(birth.toLocalDate(), LocalDate.now()).getYears();
                lblAge.setText("<html><b>Âge:</b> " + age + " ans</html>");
            }
        }
    }

    private void loadDossierId(Connection conn) throws SQLException {
        String sql = "SELECT id_entite FROM DossierMedicale WHERE patient_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, currentPatientId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) currentDossierId = rs.getLong("id_entite");
    }

    private void loadRDVs(Connection conn) throws SQLException {
        String sql = "SELECT id_entite, date, heure, motif, statut, note_medecin FROM RDV WHERE dossier_medicale_id = ? ORDER BY date DESC";
        populateTable(tableRDV, conn, sql, new String[]{"ID", "Date", "Heure", "Motif", "Statut", "Note"});
        applyRDVColoration();
    }

    private void loadFactures(Connection conn) throws SQLException {
        String sql = "SELECT id_entite, date_facture, totale_facture, totale_paye, reste, statut FROM Facture WHERE patient_id = ?";
        populateTable(tableFactures, conn, sql, new String[]{"ID", "Date", "Total", "Payé", "Reste", "Statut"});
    }

    private void loadInterventions(Connection conn) throws SQLException {
        String sql = "SELECT a.libelle, i.num_dent, i.prix_de_patient, i.duree_en_minutes FROM intervention_medecin i " +
                "JOIN acte a ON i.acte_id = a.id_entite JOIN Consultation c ON i.consultation_id = c.id_entite " +
                "WHERE c.dossier_medicale_id = ?";
        populateTable(tableInterventions, conn, sql, new String[]{"Acte", "Dent", "Prix (MAD)", "Durée"});
    }

    private void loadPrescriptions(Connection conn) throws SQLException {
        String sql = "SELECT m.nom, p.quantite, p.frequence, p.duree_en_jours FROM Prescription p " +
                "JOIN Medicament m ON p.medicament_id = m.id_entite JOIN Ordonnance o ON p.ordonnance_id = o.id_entite " +
                "WHERE o.dossier_medicale_id = ?";
        populateTable(tablePrescriptions, conn, sql, new String[]{"Médicament", "Qté", "Fréquence", "Durée"});
    }

    private void loadAntecedents(Connection conn) throws SQLException {
        String sql = "SELECT nom, categorie, niveau_de_risque FROM Antecedent a " +
                "JOIN Patient_Antecedent pa ON a.id_entite = pa.antecedent_id WHERE pa.patient_id = ?";
        populateTable(tableAntecedents, conn, sql, new String[]{"Nom", "Catégorie", "Risque"});
    }

    private void loadCertificats(Connection conn) throws SQLException {
        String sql = "SELECT cert.date_debut, cert.date_fin, cert.note_medecin FROM Certificat cert " +
                "JOIN Consultation cons ON cert.consultation_id = cons.id_entite WHERE cons.dossier_medicale_id = ?";
        populateTable(tableCertificats, conn, sql, new String[]{"Début", "Fin", "Note"});
    }

    // --- UTILITAIRES ---

    private void populateTable(JTable table, Connection conn, String sql, String[] cols) throws SQLException {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, (sql.contains("patient_id")) ? currentPatientId : currentDossierId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= cols.length; i++) row.add(rs.getObject(i));
            model.addRow(row);
        }
        table.setModel(model);
        if (cols[0].equals("ID")) {
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
        }
    }

    private void applyRDVColoration() {
        tableRDV.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                Object stat = t.getValueAt(r, 4);
                if (!s && stat != null) {
                    if (stat.toString().equals("CONFIRMED")) comp.setBackground(new Color(220, 255, 220));
                    else if (stat.toString().equals("CANCELLED")) comp.setBackground(new Color(255, 220, 220));
                    else comp.setBackground(Color.WHITE);
                }
                return comp;
            }
        });
    }

    private JTable createStyledTable() {
        JTable table = new JTable();
        table.setRowHeight(30);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        return table;
    }

    private JLabel createInfoLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }

    private void setPlaceholder(JTextField field, String hint) {
        field.setText(hint);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                if (field.getText().equals(hint)) { field.setText(""); field.setForeground(Color.BLACK); }
            }
            @Override public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) { field.setText(hint); field.setForeground(Color.GRAY); }
            }
        });
    }

    // --- CLASSE INTERNE ---
    static class PatientSuggestion {
        long id; String display;
        public PatientSuggestion(long id, String display) { this.id = id; this.display = display; }
        @Override public String toString() { return display; }
    }
}