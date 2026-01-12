package ma.oralCare.mvc.ui.admin;

import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController;
import ma.oralCare.mvc.controllers.admin.dto.AdminDashboardDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminDashboard extends JPanel {

    private final AdminDashboardController controller;
    private final JLabel lblDateTime;
    private final JLabel btnNotif;
    private final JLabel lblWelcome;

    // Composants à mettre à jour dynamiquement
    private JLabel lblLogsCount;
    private JLabel lblDbSize;
    private JLabel lblActiveCabinets;
    private JLabel lblCabinetDetail;
    private JTextArea txtAreaLogs;

    public AdminDashboard(String adminName, AdminDashboardController controller) {
        this.controller = controller;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // --- 1️⃣ HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Initialisation temporaire
        lblWelcome = new JLabel("Bonjour...");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblWelcome.setForeground(new Color(44, 62, 80));

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        rightHeader.setOpaque(false);

        lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDateTime.setForeground(new Color(127, 140, 141));
        startClock();

        btnNotif = new JLabel("🔔");
        btnNotif.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        btnNotif.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightHeader.add(lblDateTime);
        rightHeader.add(btnNotif);
        header.add(lblWelcome, BorderLayout.WEST);
        header.add(rightHeader, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 2️⃣ CONTENU CENTRAL ---
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        JPanel cardContainer = new JPanel(new GridLayout(1, 3, 25, 0));
        cardContainer.setOpaque(false);
        cardContainer.setBorder(new EmptyBorder(10, 0, 30, 0));

        lblLogsCount = new JLabel("--");
        cardContainer.add(createStatCard("Logs du Jour", lblLogsCount, "Actions enregistrées aujourd'hui", new Color(52, 152, 219)));

        lblDbSize = new JLabel("-- MB");
        cardContainer.add(createStatCard("Stock DB (MySQL)", lblDbSize, "Taille des données sur le serveur", new Color(46, 204, 113)));

        lblActiveCabinets = new JLabel("--");
        lblCabinetDetail = new JLabel("Calcul de l'activité...");
        cardContainer.add(createStatCardCustom("Cabinets Actifs", lblActiveCabinets, lblCabinetDetail, new Color(231, 76, 60)));

        centerPanel.add(cardContainer, BorderLayout.NORTH);

        // --- 3️⃣ FOOTER ---
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200)), " Dernières actions effectuées (Audit Logs) ");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 14));
        footer.setBorder(titledBorder);

        txtAreaLogs = new JTextArea(8, 20);
        txtAreaLogs.setEditable(false);
        txtAreaLogs.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtAreaLogs.setBackground(Color.WHITE);
        footer.add(new JScrollPane(txtAreaLogs));

        centerPanel.add(footer, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- CHARGEMENT INITIAL ---
        refreshData();
    }

    /**
     * Met à jour l'affichage avec les données réelles du contrôleur.
     */
    public void refreshData() {
        // 1. Appel au contrôleur
        AdminDashboardDTO dto = controller.getDashboardData();

        // 2. ✅ GESTION DE LA CIVILITÉ (M. / Mme Nom)
        if (dto.getAdminName() != null) {
            String prefix = "";
            String gender = dto.getAdminGender(); // Récupère "MALE" ou "FEMALE" depuis le DTO

            if ("MALE".equalsIgnoreCase(gender)) {
                prefix = "M. ";
            } else if ("FEMALE".equalsIgnoreCase(gender)) {
                prefix = "Mme ";
            }

            lblWelcome.setText("Bonjour, " + prefix + dto.getAdminName());
        }

        // 3. Mise à jour des compteurs
        lblLogsCount.setText(String.valueOf(dto.getTodayLogsCount()));
        lblDbSize.setText(dto.getFormattedDatabaseSize());
        lblActiveCabinets.setText(dto.getFormattedActiveCabinets());
        lblCabinetDetail.setText("Sur " + dto.getTotalCabinets() + " cabinets inscrits");

        // 4. Mise à jour des logs
        StringBuilder sb = new StringBuilder();
        if (dto.getLatestActions() == null || dto.getLatestActions().isEmpty()) {
            sb.append(" Aucune action récente enregistrée.");
        } else {
            for (String log : dto.getLatestActions()) {
                sb.append(" ").append(log).append("\n");
            }
        }
        txtAreaLogs.setText(sb.toString());

        // 5. Statut Système
        if ("ECHEC".equals(dto.getSystemStatus())) {
            btnNotif.setForeground(Color.RED);
            btnNotif.setToolTipText("Alerte: Problème système détecté !");
        } else {
            btnNotif.setForeground(new Color(44, 62, 80));
            btnNotif.setToolTipText("Système sain");
        }
    }

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        });
        timer.start();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, String subText, Color accentColor) {
        return createStatCardCustom(title, valueLabel, new JLabel(subText), accentColor);
    }

    private JPanel createStatCardCustom(String title, JLabel valueLabel, JLabel subLabel, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(Color.GRAY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(accentColor);

        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subLabel.setForeground(Color.LIGHT_GRAY);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(subLabel);

        return card;
    }
}