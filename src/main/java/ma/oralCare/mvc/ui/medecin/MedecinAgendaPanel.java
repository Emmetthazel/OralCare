package ma.oralCare.mvc.ui.medecin;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedecinAgendaPanel extends JPanel {
    private JTable tableRDV;
    private DefaultTableModel modelRDV;
    private JLabel lblDateActuelle;

    public MedecinAgendaPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- SECTION HAUTE : NAVIGATION DATE ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel dateNav = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dateNav.setOpaque(false);

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        lblDateActuelle = new JLabel(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy")));
        lblDateActuelle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        dateNav.add(btnPrev);
        dateNav.add(lblDateActuelle);
        dateNav.add(btnNext);

        JButton btnToday = new JButton("Aujourd'hui");
        btnToday.setBackground(new Color(52, 152, 219));
        btnToday.setForeground(Color.WHITE);

        topPanel.add(dateNav, BorderLayout.WEST);
        topPanel.add(btnToday, BorderLayout.EAST);

        // --- SECTION CENTRALE : TABLEAU DES RDV (Table RDV) ---
        String[] columns = {"Heure", "Patient", "Contact", "Motif", "Statut"};
        modelRDV = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableRDV = new JTable(modelRDV);
        tableRDV.setRowHeight(40);
        tableRDV.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableRDV.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Coloration des statuts (CONFIRMED, PENDING, CANCELLED)
        tableRDV.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                c.setHorizontalAlignment(JLabel.CENTER);
                c.setFont(c.getFont().deriveFont(Font.BOLD));

                if ("CONFIRMED".equals(status)) c.setForeground(new Color(39, 174, 96));
                else if ("PENDING".equals(status)) c.setForeground(new Color(230, 126, 34));
                else if ("CANCELLED".equals(status)) c.setForeground(Color.RED);

                return c;
            }
        });

        // Simulation de données issues de la base
        modelRDV.addRow(new Object[]{"09:00", "Amine Tazi", "0661234567", "Consultation Initiale", "CONFIRMED"});
        modelRDV.addRow(new Object[]{"10:30", "Meryem Bennani", "0665554433", "Détartrage", "PENDING"});
        modelRDV.addRow(new Object[]{"11:30", "Driss El Fassi", "0671882299", "Urgence Douleur", "CONFIRMED"});

        // --- SECTION BASSE : GESTION DISPONIBILITÉ (AgendaMensuel) ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JPanel pnlLegende = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLegende.setOpaque(false);
        pnlLegende.add(new JLabel("Note : Les RDV confirmés apparaissent en vert."));

        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlActions.setOpaque(false);

        JButton btnIndisponible = new JButton("Marquer Journée Indisponible");
        btnIndisponible.setBackground(new Color(231, 76, 60));
        btnIndisponible.setForeground(Color.WHITE);

        JButton btnPrintList = new JButton("Imprimer Liste");

        pnlActions.add(btnPrintList);
        pnlActions.add(btnIndisponible);

        bottomPanel.add(pnlLegende, BorderLayout.WEST);
        bottomPanel.add(pnlActions, BorderLayout.EAST);

        // Ajout final au panel principal
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(tableRDV), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}