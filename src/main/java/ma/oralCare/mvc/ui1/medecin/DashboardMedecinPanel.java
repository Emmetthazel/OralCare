package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

/**
 * DashboardMedecinPanel : Interface de pilotage pour le médecin.
 * Version synchronisée avec le schéma SQL (id_entite, RDV, Facture).
 */
public class DashboardMedecinPanel extends JPanel {

    private final MainFrame mainFrame;
    private JPanel gridPanel;

    // Palette de couleurs professionnelles
    private final Color COLOR_PRIMARY = new Color(41, 128, 185);   // Bleu
    private final Color COLOR_SUCCESS = new Color(39, 174, 96);   // Vert
    private final Color COLOR_WARNING = new Color(230, 126, 34);  // Orange
    private final Color COLOR_CARD_BG = Color.WHITE;

    public DashboardMedecinPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(236, 240, 241)); // Gris clair de fond
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- TITRE DU DASHBOARD ---
        JLabel lblHeader = new JLabel("Tableau de Bord Médical");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblHeader.setBorder(new EmptyBorder(0, 0, 25, 0));
        add(lblHeader, BorderLayout.NORTH);

        // --- GRILLE DES CARTES (3 colonnes) ---
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
     * Charge les données en respectant scrupuleusement le schema.sql
     */
    public void refreshData() {
        gridPanel.removeAll();

        // 1. Cartes Statistiques (KPIs)
        // Table RDV (majuscules) et colonne 'date'
        addStatCard("RDV Aujourd'hui",
                getQueryCount("SELECT COUNT(*) FROM RDV WHERE date = CURDATE()"),
                "📅", COLOR_WARNING, "RDV");

        // Table Facture et colonne 'totale_facture'
        addStatCard("Revenus (Mois)",
                getQuerySum("SELECT SUM(totale_facture) FROM Facture WHERE MONTH(date_facture) = MONTH(CURDATE())") + " DH",
                "💰", COLOR_SUCCESS, "CAISSE");

        // Table Patient
        addStatCard("Total Patients",
                getQueryCount("SELECT COUNT(*) FROM Patient"),
                "👤", COLOR_PRIMARY, "PATIENTS");

        // 2. Listes d'activités (Exemples avec jointures id_entite)
        addListCard("Prochains RDV",
                "SELECT p.nom, r.heure FROM RDV r " +
                        "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                        "JOIN Patient p ON d.patient_id = p.id_entite " +
                        "WHERE r.date >= CURDATE() ORDER BY r.date, r.heure LIMIT 5");

        addListCard("Actes récents",
                "SELECT a.libelle, i.prix_de_patient FROM intervention_medecin i " +
                        "JOIN acte a ON i.acte_id = a.id_entite " +
                        "ORDER BY i.id_entite DESC LIMIT 5");

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    /**
     * Crée une carte de statistique stylisée
     */
    private void addStatCard(String title, String value, String icon, Color color, String targetView) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 45));
        lblIcon.setForeground(color);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblValue.setForeground(new Color(44, 62, 80));

        infoPanel.add(lblTitle);
        infoPanel.add(lblValue);

        JButton btnDetails = new JButton("Gérer");
        btnDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetails.addActionListener(e -> mainFrame.showView(targetView));

        card.add(lblIcon, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnDetails, BorderLayout.SOUTH);

        gridPanel.add(card);
    }

    /**
     * Crée une carte affichant une liste de résultats SQL
     */
    private void addListCard(String title, String sql) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(300, 200));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblTitle.setBorder(new EmptyBorder(0, 0, 12, 0));
        lblTitle.setForeground(COLOR_PRIMARY);
        card.add(lblTitle, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Construction d'une ligne d'affichage (colonne 1 + colonne 2)
                String line = " • " + rs.getString(1);
                if (rs.getMetaData().getColumnCount() > 1) {
                    line += " (" + rs.getString(2) + ")";
                }
                model.addElement(line);
            }
            if(model.isEmpty()) model.addElement("Aucune donnée disponible");

        } catch (SQLException e) {
            model.addElement("⚠️ Erreur SQL : Voir console");
            e.printStackTrace();
        }

        JList<String> list = new JList<>(model);
        list.setFixedCellHeight(32);
        list.setFont(new Font("SansSerif", Font.PLAIN, 13));
        card.add(new JScrollPane(list), BorderLayout.CENTER);

        gridPanel.add(card);
    }

    // --- UTILS JDBC VIA SESSIONFACTORY ---

    private String getQueryCount(String sql) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.valueOf(rs.getInt(1));
        } catch (SQLException e) {
            System.err.println("Erreur Count SQL: " + e.getMessage());
        }
        return "0";
    }

    private String getQuerySum(String sql) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return String.format("%.0f", rs.getDouble(1));
        } catch (SQLException e) {
            System.err.println("Erreur Sum SQL: " + e.getMessage());
        }
        return "0";
    }
}