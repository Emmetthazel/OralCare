package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import javax.swing.DefaultListModel;

/**
 * Dashboard dédié à la secrétaire.
 * Centralise les statistiques du jour, les alertes de paiement et les flux patients.
 */
public class DashboardSecretairePanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel gridPanel;

    // Palette de couleurs "Professional Care"
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);   // Bleu Médical
    private final Color COLOR_SUCCESS = new Color(39, 174, 96);    // Vert (Validé)
    private final Color COLOR_WARNING = new Color(243, 156, 18);   // Orange (Alerte)
    private final Color COLOR_DANGER = new Color(192, 57, 43);     // Rouge (Retard/Impayé)
    private final Color COLOR_CARD_BG = Color.WHITE;

    public DashboardSecretairePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245)); // Fond neutre
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblHeader = new JLabel("Tableau de Bord Secrétariat");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblHeader.setForeground(new Color(44, 62, 80));

        JButton btnRefresh = new JButton("🔄 Actualiser les données");
        btnRefresh.addActionListener(e -> refreshData());

        headerPanel.add(lblHeader, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(headerPanel, BorderLayout.NORTH);

        // --- CONTENU ---
        gridPanel = new JPanel(new GridLayout(0, 3, 20, 20));
        gridPanel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        refreshData();
    }

    /**
     * Rafraîchit l'intégralité du dashboard en interrogeant la base de données.
     */
    public void refreshData() {
        gridPanel.removeAll();

        // 1. Ligne des KPIs (Indicateurs Clés)
        addStatCard("Patients Aujourd'hui",
                getQueryCount("SELECT COUNT(*) FROM RDV WHERE date = CURDATE()"),
                "📅", COLOR_PRIMARY, "RDV");

        addStatCard("Dossiers Incomplets",
                getQueryCount("SELECT COUNT(*) FROM Patient WHERE assurance IS NULL OR assurance = ''"),
                "⚠️", COLOR_WARNING, "PATIENTS");

        addStatCard("Paiements en Attente",
                getQueryCount("SELECT COUNT(*) FROM facture WHERE statut = 'PARTIALLY_PAID' OR statut = 'UNPAID'"),
                "💰", COLOR_DANGER, "CAISSE");

        // 2. Ligne des Flux de travail (Listes détaillées)
        // Flux des prochains RDV (Jointure Patient <-> Dossier <-> RDV)
        addListCard("Flux Patients (Aujourd'hui)",
                "SELECT p.nom, r.heure FROM RDV r " +
                        "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                        "JOIN Patient p ON d.patient_id = p.id_entite " +
                        "WHERE r.date = CURDATE() ORDER BY r.heure ASC LIMIT 6");

        // Nouveaux inscrits
        addListCard("Dernières Inscriptions",
                "SELECT nom, prenom FROM Patient ORDER BY id_entite DESC LIMIT 6");

        // Rappels (RDV sans note médicale ou statut spécifique)
        addListCard("Actions Requises",
                "SELECT p.nom, r.statut FROM RDV r " +
                        "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                        "JOIN Patient p ON d.patient_id = p.id_entite " +
                        "WHERE r.statut = 'PENDING' AND r.date < CURDATE() LIMIT 6");

        // Widget File d'Attente
        addFileAttenteWidget();

        // Widget Notifications
        addNotificationsWidget();

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void addFileAttenteWidget() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitle = new JLabel("⏳ File d'Attente");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT p.nom, r.heure FROM RDV r " +
                             "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                             "JOIN Patient p ON d.patient_id = p.id_entite " +
                             "WHERE r.date = CURDATE() AND r.statut = 'CONFIRMED' " +
                             "AND NOT EXISTS (SELECT 1 FROM Consultation c WHERE c.dossier_medicale_id = d.id_entite AND c.date = CURDATE()) " +
                             "ORDER BY r.heure ASC LIMIT 5")) {
            while (rs.next()) {
                model.addElement(" • " + rs.getString("nom") + " [" + rs.getTime("heure") + "]");
            }
            if (model.isEmpty()) model.addElement("Aucun patient en attente");
        } catch (SQLException e) {
            model.addElement("Erreur de chargement");
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnGo = new JButton("Gérer →");
        btnGo.addActionListener(e -> mainFrame.showView("FILE_ATTENTE"));
        card.add(btnGo, BorderLayout.SOUTH);

        gridPanel.add(card);
    }

    private void addNotificationsWidget() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitle = new JLabel("🔔 Notifications");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT n.titre, n.type, COALESCE(nu.est_lu, FALSE) as lue " +
                            "FROM Notification n " +
                            "LEFT JOIN notification_utilisateur nu ON n.id_entite = nu.notification_id " +
                            "WHERE COALESCE(nu.est_lu, FALSE) = FALSE " +
                            "ORDER BY n.date DESC, n.time DESC LIMIT 5")) {
            while (rs.next()) {
                String icon = rs.getBoolean("lue") ? "✓" : "●";
                model.addElement(icon + " [" + rs.getString("type") + "] " + rs.getString("titre"));
            }
            if (model.isEmpty()) model.addElement("Aucune notification");
        } catch (SQLException e) {
            model.addElement("Erreur de chargement");
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(new JScrollPane(list), BorderLayout.CENTER);

        JButton btnGo = new JButton("Voir tout →");
        btnGo.addActionListener(e -> mainFrame.showView("NOTIFICATIONS"));
        card.add(btnGo, BorderLayout.SOUTH);

        gridPanel.add(card);
    }

    private void addStatCard(String title, String value, String icon, Color color, String targetView) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 35));
        lblIcon.setForeground(color);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblValue.setForeground(new Color(52, 73, 94));

        infoPanel.add(lblTitle);
        infoPanel.add(lblValue);

        JButton btnGo = new JButton("Consulter →");
        btnGo.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnGo.setFocusPainted(false);
        btnGo.addActionListener(e -> mainFrame.showView(targetView));

        card.add(lblIcon, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnGo, BorderLayout.SOUTH);

        gridPanel.add(card);
    }

    private void addListCard(String title, String sql) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        card.add(lblTitle, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String val = " • " + rs.getString(1);
                if (rs.getMetaData().getColumnCount() > 1) {
                    val += " [" + rs.getString(2) + "]";
                }
                model.addElement(val);
            }
            if (model.isEmpty()) model.addElement("Aucune donnée pour cette période");

        } catch (SQLException e) {
            model.addElement("Erreur : " + e.getMessage());
        }

        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 12));
        list.setSelectionBackground(new Color(235, 245, 251));
        card.add(new JScrollPane(list), BorderLayout.CENTER);

        gridPanel.add(card);
    }

    private String getQueryCount(String sql) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (SQLException e) { e.printStackTrace(); }
        return "0";
    }
}