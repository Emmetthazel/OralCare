package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.secretaire.dialog.RendezVousDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

public class VisualAgendaPanel extends JPanel {
    private final MainFrame mainFrame;
    private LocalDate startOfWeek;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM");

    private JPanel gridBody;
    private JLabel lblCurrentRange;

    private final LocalTime START_HOUR = LocalTime.of(8, 0);
    private final LocalTime END_HOUR = LocalTime.of(18, 30);
    private final int SLOT_MINUTES = 30;

    public VisualAgendaPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        setupToolbar();
        setupCalendarStructure();
        refreshAgenda();
    }

    private void setupToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBorder(new EmptyBorder(10, 10, 10, 10));
        toolbar.setBackground(new Color(245, 245, 245));

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        navPanel.setOpaque(false);

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        JButton btnToday = new JButton("Aujourd'hui");
        JButton btnRefresh = new JButton("Actualiser 🔄");

        lblCurrentRange = new JLabel();
        lblCurrentRange.setFont(new Font("SansSerif", Font.BOLD, 16));

        btnPrev.addActionListener(e -> { startOfWeek = startOfWeek.minusWeeks(1); refreshAgenda(); });
        btnNext.addActionListener(e -> { startOfWeek = startOfWeek.plusWeeks(1); refreshAgenda(); });
        btnToday.addActionListener(e -> {
            startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            refreshAgenda();
        });
        btnRefresh.addActionListener(e -> refreshAgenda());

        navPanel.add(btnPrev); navPanel.add(btnToday); navPanel.add(btnNext);
        navPanel.add(Box.createHorizontalStrut(20)); navPanel.add(lblCurrentRange);

        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem("Confirmé", new Color(180, 240, 180)));
        legendPanel.add(createLegendItem("En attente", new Color(255, 240, 150)));
        legendPanel.add(createLegendItem("Annulé", new Color(255, 200, 200)));
        legendPanel.add(createLegendItem("Terminé", new Color(190, 220, 255)));

        toolbar.add(navPanel, BorderLayout.WEST);
        toolbar.add(btnRefresh, BorderLayout.CENTER);
        toolbar.add(legendPanel, BorderLayout.EAST);
        add(toolbar, BorderLayout.NORTH);
    }

    private void setupCalendarStructure() {
        JPanel container = new JPanel(new BorderLayout());
        JPanel dayHeader = new JPanel(new GridLayout(1, 8));
        dayHeader.setBackground(new Color(230, 230, 230));
        dayHeader.setPreferredSize(new Dimension(0, 40));

        String[] days = {"HEURE", "LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI", "DIMANCHE"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
            dayHeader.add(lbl);
        }

        gridBody = new JPanel();
        gridBody.setBackground(new Color(220, 220, 220));
        JScrollPane scroll = new JScrollPane(gridBody);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        container.add(dayHeader, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);
        add(container, BorderLayout.CENTER);
    }

    public void refreshAgenda() {
        gridBody.removeAll();
        gridBody.setLayout(new GridLayout(0, 8, 1, 1));

        Map<String, AppointmentData> appointments = loadWeeklyAppointments();

        LocalTime current = START_HOUR;
        while (!current.isAfter(END_HOUR)) {
            JLabel lblTime = new JLabel(current.toString(), SwingConstants.CENTER);
            lblTime.setOpaque(true);
            lblTime.setBackground(new Color(245, 245, 245));
            gridBody.add(lblTime);

            for (int i = 0; i < 7; i++) {
                LocalDate date = startOfWeek.plusDays(i);
                String key = date.toString() + "_" + current.toString().substring(0, 5);
                gridBody.add(createSlot(date, current, appointments.get(key)));
            }
            current = current.plusMinutes(SLOT_MINUTES);
        }
        gridBody.revalidate();
        gridBody.repaint();
    }

    private JPanel createSlot(LocalDate date, LocalTime time, AppointmentData data) {
        JPanel slot = new JPanel(new BorderLayout());

        if (data != null) {
            // COLORATION DU BACKGROUND DE LA CASE (SLOT)
            Color bgColor = getStatusColor(data.status);
            slot.setBackground(bgColor);
            slot.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));

            // Texte HTML forcé en noir
            String displayText = "<html><center><div style='color: black; font-family: Arial;'>"
                    + "<b>" + data.patientLastName.toUpperCase() + "</b><br>"
                    + data.patientFirstName + "<br>"
                    + "<small>(" + data.status + ")</small></div></center></html>";

            JButton btn = new JButton(displayText);
            btn.setContentAreaFilled(false); // Rend le bouton transparent pour voir le fond du JPanel
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);

            btn.addActionListener(e -> {
                new RendezVousDialog(mainFrame, data.id).setVisible(true);
                refreshAgenda();
            });
            slot.add(btn);
        } else {
            // Case libre
            slot.setBackground(Color.WHITE);
            slot.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));

            JButton btnAdd = new JButton("+");
            btnAdd.setForeground(new Color(220, 220, 220));
            btnAdd.setContentAreaFilled(false);
            btnAdd.setBorderPainted(false);
            btnAdd.addActionListener(e -> {
                RendezVousDialog dialog = new RendezVousDialog(mainFrame, null);
                dialog.setScheduledDateTime(date, time);
                dialog.setVisible(true);
                refreshAgenda();
            });
            slot.add(btnAdd);
        }
        return slot;
    }

    private Map<String, AppointmentData> loadWeeklyAppointments() {
        Map<String, AppointmentData> map = new HashMap<>();
        String sql = "SELECT r.id_entite, r.date, r.heure, r.statut, p.nom, p.prenom FROM RDV r " +
                "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                "JOIN Patient p ON d.patient_id = p.id_entite " +
                "WHERE r.date BETWEEN ? AND ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startOfWeek));
            ps.setDate(2, java.sql.Date.valueOf(startOfWeek.plusDays(6)));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String key = rs.getDate("date").toString() + "_" + rs.getTime("heure").toString().substring(0, 5);
                map.put(key, new AppointmentData(rs.getLong("id_entite"), rs.getString("nom"), rs.getString("prenom"), rs.getString("statut")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "CONFIRMED": return new Color(180, 240, 180);
            case "PENDING":   return new Color(255, 240, 150);
            case "CANCELLED": return new Color(255, 200, 200);
            case "COMPLETED": return new Color(190, 220, 255);
            default: return new Color(245, 245, 245);
        }
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(15, 15));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        p.add(box);
        p.add(new JLabel(text));
        return p;
    }

    private static class AppointmentData {
        Long id; String patientLastName, patientFirstName, status;
        AppointmentData(Long id, String nom, String prenom, String status) {
            this.id = id; this.patientLastName = nom; this.patientFirstName = prenom; this.status = status;
        }
    }
}