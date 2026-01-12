package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.secretaire.dialog.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

/**
 * Module de gestion des patients avec indicateurs métier (Assurance, RDV jour, Statut).
 */
public class PatientManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable tablePatients;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    public PatientManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        setupTopBar();
        setupTable();
        setupBottomActions();

        refreshTable();
    }

    private void setupTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 "));
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Recherche par Nom, CIN ou Téléphone");
        txtSearch.addActionListener(e -> refreshTable());
        searchPanel.add(txtSearch);

        JButton btnAdd = new JButton("＋ Nouveau Patient");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            new PatientDialog(mainFrame, null).setVisible(true);
            refreshTable();
        });

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        // Colonnes enrichies selon les standards métier
        String[] columns = {"ID", "CIN", "Nom & Prénom", "Téléphone", "Assurance", "RDV Aujourd'hui", "Dossier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablePatients = new JTable(tableModel);
        tablePatients.setRowHeight(40);
        tablePatients.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // --- RENDU VISUEL DES COLONNES ---

        // 1. Rendu Assurance (Index 4)
        tablePatients.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = (v != null) ? v.toString() : "NONE";
                if (val.equalsIgnoreCase("NONE") || val.isEmpty()) {
                    lbl.setText("⚠ Absence");
                    lbl.setForeground(new Color(231, 76, 60));
                } else {
                    lbl.setText("✓ " + val);
                    lbl.setForeground(new Color(39, 174, 96));
                }
                return lbl;
            }
        });

        // 2. Rendu RDV Aujourd'hui (Index 5)
        tablePatients.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && (boolean)v) {
                    lbl.setText("🕘 OUI");
                    lbl.setForeground(new Color(41, 128, 185));
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setText("-");
                    lbl.setForeground(Color.LIGHT_GRAY);
                }
                return lbl;
            }
        });

        // Double clic pour ouvrir le dossier
        tablePatients.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleAction("DOSSIER");
            }
        });

        add(new JScrollPane(tablePatients), BorderLayout.CENTER);
    }

    private void setupBottomActions() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);

        JButton btnEdit = new JButton("📝 Fiche");
        JButton btnHistory = new JButton("📋 Antécédents");
        JButton btnDossier = new JButton("📂 Dossier Médical");
        JButton btnRDV = new JButton("📅 Planifier RDV");

        btnEdit.addActionListener(e -> handleAction("EDIT"));
        btnHistory.addActionListener(e -> handleAction("HISTORY"));
        btnDossier.addActionListener(e -> handleAction("DOSSIER"));
        btnRDV.addActionListener(e -> handleAction("RDV"));

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnHistory);
        bottomPanel.add(btnDossier);
        bottomPanel.add(new JSeparator(JSeparator.VERTICAL));
        bottomPanel.add(btnRDV);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        String filter = txtSearch.getText().trim();

        // Requête complexe pour récupérer les infos patient ET la présence de RDV aujourd'hui
        String sql = "SELECT p.id_entite, p.cin, p.nom, p.prenom, p.telephone, p.assurance, " +
                "(SELECT COUNT(*) FROM RDV r JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                " WHERE d.patient_id = p.id_entite AND r.date = CURDATE()) as has_rdv " +
                "FROM Patient p";

        if (!filter.isEmpty()) {
            sql += " WHERE p.nom LIKE ? OR p.prenom LIKE ? OR p.cin LIKE ?";
        }
        sql += " ORDER BY p.nom ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filter.isEmpty()) {
                String p = "%" + filter + "%";
                ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getString("cin"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("assurance"),
                        rs.getInt("has_rdv") > 0, // Boolean pour le rendu
                        "Complet" // Placeholder statut dossier
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void handleAction(String action) {
        int row = tablePatients.getSelectedRow();
        if (row == -1) return;

        Long id = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        switch (action) {
            case "EDIT": new PatientDialog(mainFrame, id).setVisible(true); break;
            case "HISTORY": new AntecedentDialog(mainFrame, id).setVisible(true); break;
            case "DOSSIER": new DossierMedicalDialog(mainFrame, id, name).setVisible(true); break;
            case "RDV": new RendezVousDialog(mainFrame, null).setVisible(true); break;
        }
        refreshTable();
    }
}