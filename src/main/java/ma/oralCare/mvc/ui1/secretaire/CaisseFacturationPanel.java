package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.utils.StatutTranslator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CaisseFacturationPanel extends JPanel {

    private final MainFrame mainFrame;
    private JTable tableFactures;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton btnModifier, btnAnnuler;
    private JPopupMenu suggestionPopup;
    private JList<String> suggestionList;
    private DefaultListModel<String> suggestionModel;

    public CaisseFacturationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();

        refreshTable(null); // Chargement initial
    }

    // --- 1. BARRE DE RECHERCHE (NORTH) ---
    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout(10, 0));
        northPanel.setOpaque(false);

        // Champ de recherche avec icône
        JPanel searchBox = new JPanel(new BorderLayout(5, 0));
        searchBox.setOpaque(false);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(5, 10, 5, 10)));

        setPlaceholder(searchField, "Rechercher patient ou ID facture...");

        // Système de suggestions
        setupSuggestions();

        searchBox.add(new JLabel("🔍"), BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);

        northPanel.add(searchBox, BorderLayout.WEST);
        add(northPanel, BorderLayout.NORTH);
    }

    // --- 2. TABLEAU CENTRAL (CENTER) ---
    private void setupCenterPanel() {
        String[] columns = {"ID", "Patient", "Consultation", "Total (MAD)", "Payé", "Reste", "Statut", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableFactures = new JTable(tableModel);
        tableFactures.setRowHeight(35);
        tableFactures.setIntercellSpacing(new Dimension(0, 0));
        tableFactures.setShowGrid(false);
        tableFactures.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Design du header
        JTableHeader header = tableFactures.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(245, 245, 245));

        // Rendu des couleurs par statut
        tableFactures.setDefaultRenderer(Object.class, new StatusCellRenderer());

        // Activation des boutons sur sélection
        tableFactures.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = tableFactures.getSelectedRow() != -1;
            btnModifier.setEnabled(selected);
            btnAnnuler.setEnabled(selected);
        });

        JScrollPane scrollPane = new JScrollPane(tableFactures);
        scrollPane.setBorder(new LineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);
    }

    // --- 3. BOUTONS D'ACTION (SOUTH) ---
    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        southPanel.setOpaque(false);

        JButton btnNouvelle = createStyledButton("➕ Nouvelle Facture", new Color(46, 204, 113));
        btnModifier = createStyledButton("📝 Modifier Paiement", new Color(52, 152, 219));
        btnAnnuler = createStyledButton("❌ Annuler", new Color(231, 76, 60));

        btnModifier.setEnabled(false);
        btnAnnuler.setEnabled(false);

        btnNouvelle.addActionListener(e -> openFactureDialog(null));
        btnModifier.addActionListener(e -> {
            Long id = (Long) tableFactures.getValueAt(tableFactures.getSelectedRow(), 0);
            openFactureDialog(id);
        });
        btnAnnuler.addActionListener(e -> deleteSelectedFacture());

        JButton btnExport = createStyledButton("📄 Exporter Rapport", new Color(39, 174, 96));
        btnExport.addActionListener(e -> {
            new ma.oralCare.mvc.ui1.secretaire.dialog.ExportRapportDialog(mainFrame, "CAISSE").setVisible(true);
        });

        southPanel.add(btnNouvelle);
        southPanel.add(btnModifier);
        southPanel.add(btnAnnuler);
        southPanel.add(new JSeparator(JSeparator.VERTICAL));
        southPanel.add(btnExport);
        add(southPanel, BorderLayout.SOUTH);
    }

    // --- LOGIQUE DE DONNÉES SQL ---
    public void refreshTable(String filter) {
        tableModel.setRowCount(0);
        String sql = "SELECT f.id_entite, p.nom, p.prenom, c.libelle, f.totale_facture, f.totale_paye, f.reste, f.statut, f.date_facture " +
                "FROM Facture f " +
                "JOIN Patient p ON f.patient_id = p.id_entite " +
                "JOIN Consultation c ON f.consultation_id = c.id_entite ";

        if (filter != null && !filter.isEmpty()) {
            sql += "WHERE p.nom LIKE ? OR p.prenom LIKE ? OR CAST(f.id_entite AS CHAR) LIKE ?";
        }
        sql += " ORDER BY f.date_facture DESC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (filter != null && !filter.isEmpty()) {
                String q = "%" + filter + "%";
                ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getLong("id_entite"));
                row.add(rs.getString("nom").toUpperCase() + " " + rs.getString("prenom"));
                row.add(rs.getString("libelle"));
                row.add(rs.getDouble("totale_facture"));
                row.add(rs.getDouble("totale_paye"));
                row.add(rs.getDouble("reste"));
                row.add(StatutTranslator.traduireStatutPaiement(rs.getString("statut"))); // Statut traduit
                row.add(rs.getTimestamp("date_facture"));
                tableModel.addRow(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- RECHERCHE ET SUGGESTIONS ---
    private void setupSuggestions() {
        suggestionPopup = new JPopupMenu();
        suggestionModel = new DefaultListModel<>();
        suggestionList = new JList<>(suggestionModel);
        suggestionPopup.add(new JScrollPane(suggestionList));
        suggestionPopup.setFocusable(false);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { handleSearch(); }
            public void removeUpdate(DocumentEvent e) { handleSearch(); }
            public void changedUpdate(DocumentEvent e) { handleSearch(); }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                String selection = suggestionList.getSelectedValue();
                if (selection != null) {
                    searchField.setText(selection.split(" - ")[0]); // On prend le nom
                    suggestionPopup.setVisible(false);
                }
            }
        });
    }

    private void handleSearch() {
        String text = searchField.getText().trim();
        if (text.length() >= 2 && !text.contains("Rechercher")) {
            updateSuggestionModel(text);
            suggestionPopup.setPopupSize(searchField.getWidth(), Math.min(suggestionModel.size() * 25 + 5, 200));
            if (!suggestionModel.isEmpty()) suggestionPopup.show(searchField, 0, searchField.getHeight());
            refreshTable(text);
        } else if (text.isEmpty()) {
            suggestionPopup.setVisible(false);
            refreshTable(null);
        }
    }

    private void updateSuggestionModel(String text) {
        suggestionModel.clear();
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            String sql = "SELECT nom, prenom FROM Patient WHERE nom LIKE ? OR prenom LIKE ? LIMIT 5";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, text + "%"); ps.setString(2, text + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) suggestionModel.addElement(rs.getString("nom") + " " + rs.getString("prenom"));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- ACTIONS ---
    private void openFactureDialog(Long idFacture) {
        // Ici vous appelleriez votre JDialog (ex: FactureDialog)
        // new FactureDialog(mainFrame, idFacture).setVisible(true);
        // refreshTable(null);
        JOptionPane.showMessageDialog(this, "Ouverture du formulaire Facture...");
    }

    private void deleteSelectedFacture() {
        int row = tableFactures.getSelectedRow();
        Long id = (Long) tableFactures.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Annuler définitivement la facture #" + id + " ?", "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE Facture SET statut = 'CANCELLED' WHERE id_entite = ?");
                ps.setLong(1, id);
                ps.executeUpdate();
                refreshTable(null);
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // --- RENDU VISUEL ---
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            Component cell = super.getTableCellRendererComponent(t, v, s, f, r, c);
            String statut = t.getValueAt(r, 6).toString();

            if (!s) {
                cell.setBackground(r % 2 == 0 ? Color.WHITE : new Color(252, 252, 252));
                if (c == 6) { // Colonne Statut
                    switch (statut) {
                        case "PAID": cell.setForeground(new Color(46, 204, 113)); break;
                        case "PENDING": cell.setForeground(new Color(230, 126, 34)); break;
                        case "OVERDUE": cell.setForeground(new Color(231, 76, 60)); break;
                        case "CANCELLED": cell.setForeground(Color.GRAY); break;
                    }
                } else { cell.setForeground(Color.BLACK); }
            }
            return cell;
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void setPlaceholder(JTextField f, String p) {
        f.setText(p); f.setForeground(Color.GRAY);
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { if(f.getText().equals(p)) { f.setText(""); f.setForeground(Color.BLACK); } }
            @Override public void focusLost(FocusEvent e) { if(f.getText().isEmpty()) { f.setText(p); f.setForeground(Color.GRAY); } }
        });
    }
}