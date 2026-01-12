package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.secretaire.dialog.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Panel de gestion de l'agenda pour la secrétaire.
 */
public class AgendaManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable tableRDV;
    private DefaultTableModel model;
    private JComboBox<String> cbFilterMedecin;

    public AgendaManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        setupTopPanel();
        setupCenterTable();
        setupBottomActions();

        refreshData();
    }

    // --- BARRE SUPÉRIEURE : FILTRES ---
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setOpaque(false);

        topPanel.add(new JLabel("👩‍⚕️ Filtrer par Médecin :"));
        cbFilterMedecin = new JComboBox<>();
        cbFilterMedecin.addItem("Tous les médecins");
        loadMedecinsInCombo();

        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.addActionListener(e -> refreshData());

        topPanel.add(cbFilterMedecin);
        topPanel.add(btnRefresh);
        add(topPanel, BorderLayout.NORTH);
    }

    // --- CENTRE : TABLEAU DES RENDEZ-VOUS ---
    private void setupCenterTable() {
        String[] columns = {"ID", "Date", "Heure", "Patient", "Médecin", "Motif", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableRDV = new JTable(model);
        tableRDV.setRowHeight(35);
        tableRDV.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Application du rendu de couleur selon le statut
        tableRDV.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                String statut = t.getValueAt(r, 6).toString();

                if (!s) { // Si la ligne n'est pas sélectionnée
                    switch (statut) {
                        case "CONFIRMED": comp.setBackground(new Color(210, 255, 210)); break; // Vert
                        case "PENDING": comp.setBackground(new Color(255, 240, 200)); break;   // Orange/Jaune
                        case "CANCELLED": comp.setBackground(new Color(255, 210, 210)); break; // Rouge
                        case "COMPLETED": comp.setBackground(new Color(210, 230, 255)); break; // Bleu
                        default: comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        add(new JScrollPane(tableRDV), BorderLayout.CENTER);
    }

    // --- BAS : ACTIONS ---
    private void setupBottomActions() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setOpaque(false);

        JButton btnAdd = new JButton("➕ Nouveau RDV");
        JButton btnEdit = new JButton("📝 Modifier");
        JButton btnCancel = new JButton("❌ Annuler");
        JButton btnDossier = new JButton("📂 Voir Dossier");
        JButton btnAgenda = new JButton("📅 Dispos Médecins");

        // ActionListeners
        btnAdd.addActionListener(e -> {
            new RendezVousDialog(mainFrame, null).setVisible(true);
            refreshData();
        });

        btnEdit.addActionListener(e -> handleAction("EDIT"));
        btnCancel.addActionListener(e -> handleAction("CANCEL"));
        btnDossier.addActionListener(e -> handleAction("DOSSIER"));

        btnAgenda.addActionListener(e -> {
            new AgendaMensuelDialog(mainFrame).setVisible(true);
        });

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnCancel);
        bottomPanel.add(new JSeparator(JSeparator.VERTICAL));
        bottomPanel.add(btnDossier);
        bottomPanel.add(btnAgenda);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- LOGIQUE DE DONNÉES ---

    private void loadMedecinsInCombo() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            String sql = "SELECT u.nom FROM Medecin m JOIN utilisateur u ON m.id_entite = u.id_entite";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                cbFilterMedecin.addItem("Dr. " + rs.getString("nom"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void refreshData() {
        model.setRowCount(0);
        String filter = cbFilterMedecin.getSelectedItem().toString();

        String sql = "SELECT r.id_entite, r.date, r.heure, p.nom as p_nom, u.nom as m_nom, r.motif, r.statut " +
                "FROM RDV r " +
                "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                "JOIN Patient p ON d.patient_id = p.id_entite " +
                "LEFT JOIN Medecin m ON d.medecin_id = m.id_entite " +
                "LEFT JOIN utilisateur u ON m.id_entite = u.id_entite ";

        if (!filter.equals("Tous les médecins")) {
            sql += " WHERE u.nom = '" + filter.replace("Dr. ", "") + "'";
        }

        sql += " ORDER BY r.date DESC, r.heure ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("p_nom"),
                        "Dr. " + rs.getString("m_nom"),
                        rs.getString("motif"),
                        rs.getString("statut")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement de l'agenda : " + e.getMessage());
        }
    }

    private void handleAction(String action) {
        int row = tableRDV.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rendez-vous.");
            return;
        }

        Long rdvId = (Long) tableRDV.getValueAt(row, 0);

        switch (action) {
            case "EDIT":
                new RendezVousDialog(mainFrame, rdvId).setVisible(true);
                refreshData();
                break;
            case "CANCEL":
                if (JOptionPane.showConfirmDialog(this, "Annuler ce rendez-vous ?") == JOptionPane.YES_OPTION) {
                    updateStatus(rdvId, "CANCELLED");
                }
                break;
            case "DOSSIER":
                // Logique pour ouvrir le dossier associé au patient du RDV
                JOptionPane.showMessageDialog(this, "Ouverture du dossier médical...");
                break;
        }
    }

    private void updateStatus(Long id, String status) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE RDV SET statut = ? WHERE id_entite = ?")) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
            refreshData();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}