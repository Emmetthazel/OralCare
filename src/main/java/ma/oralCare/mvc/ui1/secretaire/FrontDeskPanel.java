package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

/**
 * FrontDeskPanel : Tour de contrôle de la secrétaire.
 * Centralise les indicateurs, les prochains RDV et les alertes dossiers.
 */
public class FrontDeskPanel extends JPanel {
    private final MainFrame mainFrame;
    private JLabel lblTotalRDV, lblImpayes, lblDossiersIncomplets;
    private JTable tableFlux;
    private DefaultTableModel modelFlux;

    public FrontDeskPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 246, 250));

        setupTopStats();
        setupMainContent();

        refreshAll();
    }

    // --- 1. SECTION HAUTE : CARTES DE STATISTIQUES ---
    private void setupTopStats() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);

        lblTotalRDV = new JLabel("0");
        statsPanel.add(createStatCard("RDV du jour", lblTotalRDV, new Color(41, 128, 185)));

        lblImpayes = new JLabel("0 DH");
        statsPanel.add(createStatCard("Factures en attente", lblImpayes, new Color(231, 76, 60)));

        lblDossiersIncomplets = new JLabel("0");
        statsPanel.add(createStatCard("Alertes Dossiers", lblDossiersIncomplets, new Color(243, 156, 18)));

        add(statsPanel, BorderLayout.NORTH);
    }

    // --- 2. SECTION CENTRALE : FLUX ET ALERTES ---
    private void setupMainContent() {
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setOpaque(false);

        // Zone de gauche : Flux des patients (Tableau compact)
        JPanel fluxContainer = new JPanel(new BorderLayout());
        fluxContainer.setBackground(Color.WHITE);
        fluxContainer.setBorder(BorderFactory.createTitledBorder("📅 Flux des Patients - Prochaines heures"));

        String[] cols = {"Heure", "Patient", "Médecin", "Statut"};
        modelFlux = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableFlux = new JTable(modelFlux);
        tableFlux.setRowHeight(30);
        fluxContainer.add(new JScrollPane(tableFlux), BorderLayout.CENTER);

        // Zone de droite : Alertes rapides
        JPanel alertPanel = new JPanel();
        alertPanel.setLayout(new BoxLayout(alertPanel, BoxLayout.Y_AXIS));
        alertPanel.setPreferredSize(new Dimension(300, 0));
        alertPanel.setOpaque(false);

        alertPanel.add(createAlertBox("🔴 2 patients en retard", "Dr. Alami attend."));
        alertPanel.add(Box.createVerticalStrut(10));
        alertPanel.add(createAlertBox("⚠️ Dossiers sans mutuelle", "Vérifier à l'accueil."));

        centerPanel.add(fluxContainer, BorderLayout.CENTER);
        centerPanel.add(alertPanel, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);
    }

    // --- UTILS : CRÉATION DE COMPOSANTS ---

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, accent),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.PLAIN, 14));
        t.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        valueLabel.setForeground(accent);

        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createAlertBox(String title, String sub) {
        JPanel box = new JPanel(new GridLayout(2, 1));
        box.setBackground(new Color(255, 235, 235));
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        box.setMaximumSize(new Dimension(300, 60));

        JLabel t = new JLabel(title); t.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel s = new JLabel(sub); s.setFont(new Font("SansSerif", Font.PLAIN, 11));

        box.add(t); box.add(s);
        return box;
    }

    // --- LOGIQUE DE RAFRAÎCHISSEMENT ---

    public void refreshAll() {
        refreshStats();
        refreshFlux();
    }

    private void refreshStats() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            // 1. RDV du jour
            PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM RDV WHERE date = CURDATE()");
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) lblTotalRDV.setText(String.valueOf(rs1.getInt(1)));

            // 2. Alertes dossiers (sans assurance)
            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM Patient WHERE assurance IS NULL OR assurance = 'NONE'");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) lblDossiersIncomplets.setText(String.valueOf(rs2.getInt(1)));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void refreshFlux() {
        modelFlux.setRowCount(0);
        String sql = "SELECT r.heure, p.nom, u.nom as m_nom, r.statut FROM RDV r " +
                "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                "JOIN Patient p ON d.patient_id = p.id_entite " +
                "JOIN Medecin m ON d.medecin_id = m.id_entite " +
                "JOIN utilisateur u ON m.id_entite = u.id_entite " +
                "WHERE r.date = CURDATE() ORDER BY r.heure ASC";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                modelFlux.addRow(new Object[]{
                        rs.getTime("heure").toString().substring(0, 5),
                        rs.getString("nom"),
                        "Dr. " + rs.getString("m_nom"),
                        rs.getString("statut")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}