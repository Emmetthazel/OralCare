package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Panel de gestion des notifications et alertes pour la secrétaire.
 */
public class NotificationsPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultListModel<NotificationItem> notificationModel;
    private JList<NotificationItem> notificationList;
    private JComboBox<String> cbType, cbPriorite;
    private JCheckBox chkNonLues;

    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color DANGER_COLOR = new Color(192, 57, 43);

    public NotificationsPanel(MainFrame mainFrame) {
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

        JLabel lblTitle = new JLabel("🔔 Notifications & Alertes");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ACCENT_COLOR);
        northPanel.add(lblTitle, BorderLayout.WEST);

        // Filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setOpaque(false);

        cbType = new JComboBox<>(new String[]{"Tous", "INFO", "WARNING", "ERROR", "SUCCESS"});
        cbPriorite = new JComboBox<>(new String[]{"Toutes", "LOW", "MEDIUM", "HIGH", "URGENT"});
        chkNonLues = new JCheckBox("Non lues uniquement");

        cbType.addActionListener(e -> refreshData());
        cbPriorite.addActionListener(e -> refreshData());
        chkNonLues.addActionListener(e -> refreshData());

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbType);
        filterPanel.add(new JLabel("Priorité:"));
        filterPanel.add(cbPriorite);
        filterPanel.add(chkNonLues);

        northPanel.add(filterPanel, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JPanel centerPanel = createStyledPanel("Liste des Notifications");
        notificationModel = new DefaultListModel<>();
        notificationList = new JList<>(notificationModel);
        notificationList.setCellRenderer(new NotificationCellRenderer());
        notificationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        notificationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    marquerCommeLue();
                }
            }
        });

        centerPanel.add(new JScrollPane(notificationList), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        southPanel.setOpaque(false);

        JButton btnMarquerLue = new JButton("✓ Marquer comme lue");
        JButton btnMarquerToutesLues = new JButton("✓ Tout marquer comme lu");
        JButton btnRefresh = new JButton("🔄 Actualiser");

        btnMarquerLue.addActionListener(e -> marquerCommeLue());
        btnMarquerToutesLues.addActionListener(e -> marquerToutesLues());
        btnRefresh.addActionListener(e -> refreshData());

        southPanel.add(btnRefresh);
        southPanel.add(btnMarquerLue);
        southPanel.add(btnMarquerToutesLues);

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
        notificationModel.clear();

        String typeFilter = cbType.getSelectedItem().toString();
        String prioriteFilter = cbPriorite.getSelectedItem().toString();
        boolean nonLuesSeulement = chkNonLues.isSelected();

        String sql = "SELECT n.id_entite, n.titre, n.message, n.type, n.priorite, n.date, n.time, " +
                "COALESCE(nu.est_lu, FALSE) as lue " +
                "FROM Notification n " +
                "LEFT JOIN notification_utilisateur nu ON n.id_entite = nu.notification_id " +
                "WHERE 1=1";

        if (!typeFilter.equals("Tous")) {
            sql += " AND n.type = ?";
        }
        if (!prioriteFilter.equals("Toutes")) {
            sql += " AND n.priorite = ?";
        }
        if (nonLuesSeulement) {
            sql += " AND COALESCE(nu.est_lu, FALSE) = FALSE";
        }

        sql += " ORDER BY n.date DESC, n.time DESC LIMIT 50";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (!typeFilter.equals("Tous")) {
                ps.setString(paramIndex++, typeFilter);
            }
            if (!prioriteFilter.equals("Toutes")) {
                ps.setString(paramIndex, prioriteFilter);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                NotificationItem item = new NotificationItem(
                        rs.getLong("id_entite"),
                        rs.getString("titre"),
                        rs.getString("message"),
                        rs.getString("type"),
                        rs.getString("priorite"),
                        rs.getDate("date"),
                        rs.getTime("time"),
                        rs.getBoolean("lue")
                );
                notificationModel.addElement(item);
            }

            if (notificationModel.isEmpty()) {
                notificationModel.addElement(new NotificationItem(0L, "Aucune notification", "", "INFO", "LOW", null, null, false));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des notifications : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void marquerCommeLue() {
        NotificationItem item = notificationList.getSelectedValue();
        if (item == null || item.getId() == 0) return;

        // Note: Pour marquer comme lue, il faudrait utiliser notification_utilisateur
        // Pour simplifier, on utilise INSERT ... ON DUPLICATE KEY UPDATE
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            // Récupérer l'ID de l'utilisateur connecté (à adapter selon votre système d'authentification)
            // Pour l'instant, on utilise une valeur par défaut ou on peut laisser cette fonctionnalité pour plus tard
            String sql = "INSERT INTO notification_utilisateur (notification_id, utilisateur_id, est_lu) " +
                    "VALUES (?, 1, TRUE) " +
                    "ON DUPLICATE KEY UPDATE est_lu = TRUE";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setLong(1, item.getId());
            ps.executeUpdate();
            refreshData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void marquerToutesLues() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Marquer toutes les notifications comme lues ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Note: Cette fonctionnalité nécessite l'ID de l'utilisateur connecté
            // Pour l'instant, on laisse cette fonctionnalité pour une implémentation future
            JOptionPane.showMessageDialog(this,
                    "Cette fonctionnalité nécessite l'ID de l'utilisateur connecté.\n" +
                    "À implémenter avec le système d'authentification.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Classe interne pour représenter une notification
    private static class NotificationItem {
        private final Long id;
        private final String titre;
        private final String message;
        private final String type;
        private final String priorite;
        private final java.sql.Date date;
        private final java.sql.Time time;
        private final boolean lue;

        public NotificationItem(Long id, String titre, String message, String type, String priorite,
                               java.sql.Date date, java.sql.Time time, boolean lue) {
            this.id = id;
            this.titre = titre;
            this.message = message;
            this.type = type;
            this.priorite = priorite;
            this.date = date;
            this.time = time;
            this.lue = lue;
        }

        public Long getId() { return id; }
        public String getTitre() { return titre; }
        public String getMessage() { return message; }
        public String getType() { return type; }
        public String getPriorite() { return priorite; }
        public java.sql.Date getDate() { return date; }
        public java.sql.Time getTime() { return time; }
        public boolean isLue() { return lue; }

        @Override
        public String toString() {
            String status = lue ? "✓" : "●";
            String dateStr = date != null ? date.toString() : "";
            String timeStr = time != null ? time.toString() : "";
            return String.format("%s [%s] %s - %s %s", status, type, titre, dateStr, timeStr);
        }
    }

    // Renderer personnalisé pour les notifications
    private class NotificationCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                     boolean isSelected, boolean hasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

            if (value instanceof NotificationItem) {
                NotificationItem item = (NotificationItem) value;

                // Couleur selon le type
                switch (item.getType()) {
                    case "WARNING":
                        setForeground(WARNING_COLOR);
                        break;
                    case "ERROR":
                        setForeground(DANGER_COLOR);
                        break;
                    case "SUCCESS":
                        setForeground(new Color(39, 174, 96));
                        break;
                    default:
                        setForeground(ACCENT_COLOR);
                }

                // Style selon lue/non lue
                if (!item.isLue()) {
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }

                setText(item.toString());
            }

            return this;
        }
    }
}
