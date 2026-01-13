package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.secretaire.dialog.ExportRapportDialog;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.math.BigDecimal;
import java.util.Vector;

/**
 * Panel de consultation des situations financières pour la secrétaire.
 * Permet de consulter la situation financière d'un patient ou de lister toutes les situations.
 */
public class SituationFinanciereSecretairePanel extends JPanel {

    private final MainFrame mainFrame;
    private JTable tableSituations;
    private DefaultTableModel situationModel;
    private JTable tableFactures;
    private DefaultTableModel factureModel;
    private JTextField txtSearch;
    private JComboBox<String> cbStatut;
    private JLabel lblSummary;

    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(192, 57, 43);

    public SituationFinanciereSecretairePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 246, 250));

        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();

        refreshData();
    }

    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setOpaque(false);

        // Titre
        JLabel lblTitle = new JLabel("💰 Situations Financières");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ACCENT_COLOR);
        northPanel.add(lblTitle, BorderLayout.WEST);

        // Barre de recherche et filtres
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Rechercher par nom de patient");
        txtSearch.addActionListener(e -> refreshData());

        cbStatut = new JComboBox<>(new String[]{"Tous", "ACTIVE", "ARCHIVED", "CLOSED"});
        cbStatut.addActionListener(e -> refreshData());

        JButton btnSearch = new JButton("🔍 Rechercher");
        btnSearch.addActionListener(e -> refreshData());

        searchPanel.add(new JLabel("Patient:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Statut:"));
        searchPanel.add(cbStatut);
        searchPanel.add(btnSearch);

        northPanel.add(searchPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);

        // Panel supérieur : Liste des situations financières
        JPanel topPanel = createStyledPanel("Liste des Situations Financières");
        String[] sitCols = {"ID", "Patient", "Total Actes (DH)", "Payé (DH)", "Crédit (DH)", "Statut", "Date Création"};
        situationModel = new DefaultTableModel(sitCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableSituations = new JTable(situationModel);
        tableSituations.setRowHeight(35);
        tableSituations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSituations.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadFacturesForSelectedSituation();
            }
        });

        // Rendu des couleurs selon le statut
        tableSituations.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && column == 5) { // Colonne Statut
                    String statut = value.toString();
                    switch (statut) {
                        case "ACTIVE":
                            comp.setBackground(new Color(210, 255, 210));
                            break;
                        case "ARCHIVED":
                            comp.setBackground(new Color(240, 240, 240));
                            break;
                        case "CLOSED":
                            comp.setBackground(new Color(255, 240, 200));
                            break;
                        default:
                            comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        topPanel.add(new JScrollPane(tableSituations), BorderLayout.CENTER);
        splitPane.setTopComponent(topPanel);

        // Panel inférieur : Factures de la situation sélectionnée
        JPanel bottomPanel = createStyledPanel("Factures Associées");
        String[] facCols = {"ID", "Date Facture", "Total (DH)", "Payé (DH)", "Reste (DH)", "Statut"};
        factureModel = new DefaultTableModel(facCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableFactures = new JTable(factureModel);
        tableFactures.setRowHeight(30);

        bottomPanel.add(new JScrollPane(tableFactures), BorderLayout.CENTER);
        splitPane.setBottomComponent(bottomPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setOpaque(false);
        southPanel.setBorder(new TitledBorder("Résumé"));

        lblSummary = new JLabel("<html><b>Résumé :</b> Sélectionnez une situation financière pour voir les détails</html>");
        lblSummary.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        southPanel.add(lblSummary, BorderLayout.CENTER);

        JButton btnExport = new JButton("📄 Exporter Rapport");
        btnExport.setBackground(SUCCESS_COLOR);
        btnExport.setForeground(Color.WHITE);
        btnExport.addActionListener(e -> {
            // Ouvrir le dialogue d'export
            new ExportRapportDialog(mainFrame, "SITUATION_FINANCIERE").setVisible(true);
        });
        southPanel.add(btnExport, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));
        return panel;
    }

    public void refreshData() {
        situationModel.setRowCount(0);
        factureModel.setRowCount(0);
        lblSummary.setText("<html><b>Résumé :</b> Sélectionnez une situation financière pour voir les détails</html>");

        String search = txtSearch.getText().trim();
        String statutFilter = cbStatut.getSelectedItem().toString();

        String sql = "SELECT sf.id_entite, p.nom, p.prenom, sf.totale_des_actes, sf.totale_paye, " +
                "sf.credit, sf.statut, b.date_creation " +
                "FROM SituationFinanciere sf " +
                "JOIN BaseEntity b ON sf.id_entite = b.id_entite " +
                "JOIN DossierMedicale dm ON sf.dossier_medicale_id = dm.id_entite " +
                "JOIN Patient p ON dm.patient_id = p.id_entite " +
                "WHERE 1=1";

        if (!search.isEmpty()) {
            sql += " AND (p.nom LIKE ? OR p.prenom LIKE ?)";
        }

        if (!statutFilter.equals("Tous")) {
            sql += " AND sf.statut = ?";
        }

        sql += " ORDER BY b.date_creation DESC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (!search.isEmpty()) {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }
            if (!statutFilter.equals("Tous")) {
                ps.setString(paramIndex, statutFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BigDecimal totalActes = rs.getBigDecimal("totale_des_actes");
                BigDecimal totalPaye = rs.getBigDecimal("totale_paye");
                BigDecimal credit = rs.getBigDecimal("credit");

                situationModel.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        totalActes != null ? String.format("%.2f", totalActes) : "0.00",
                        totalPaye != null ? String.format("%.2f", totalPaye) : "0.00",
                        credit != null ? String.format("%.2f", credit) : "0.00",
                        rs.getString("statut"),
                        rs.getDate("date_creation")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des situations financières : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadFacturesForSelectedSituation() {
        int row = tableSituations.getSelectedRow();
        if (row == -1) {
            factureModel.setRowCount(0);
            return;
        }

        Long situationId = (Long) situationModel.getValueAt(row, 0);
        String patientName = (String) situationModel.getValueAt(row, 1);
        String totalActes = (String) situationModel.getValueAt(row, 2);
        String totalPaye = (String) situationModel.getValueAt(row, 3);
        String credit = (String) situationModel.getValueAt(row, 4);

        // Mise à jour du résumé
        lblSummary.setText(String.format(
                "<html><b>Résumé pour %s :</b> Total Actes: %s DH | Payé: %s DH | <span style='color:%s;'>Crédit: %s DH</span></html>",
                patientName, totalActes, totalPaye,
                credit.startsWith("-") ? SUCCESS_COLOR.getRGB() : DANGER_COLOR.getRGB(),
                credit
        ));

        // Chargement des factures
        factureModel.setRowCount(0);
        String sql = "SELECT f.id_entite, f.date_facture, f.totale_facture, f.totale_paye, f.reste, f.statut " +
                "FROM Facture f " +
                "WHERE f.situation_financiere_id = ? " +
                "ORDER BY f.date_facture DESC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, situationId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BigDecimal totalFacture = rs.getBigDecimal("totale_facture");
                BigDecimal totalPayeFact = rs.getBigDecimal("totale_paye");
                BigDecimal reste = rs.getBigDecimal("reste");

                factureModel.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getTimestamp("date_facture"),
                        totalFacture != null ? String.format("%.2f", totalFacture) : "0.00",
                        totalPayeFact != null ? String.format("%.2f", totalPayeFact) : "0.00",
                        reste != null ? String.format("%.2f", reste) : "0.00",
                        rs.getString("statut")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des factures : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
