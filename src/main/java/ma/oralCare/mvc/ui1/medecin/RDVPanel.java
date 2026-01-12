package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.mvc.controllers.RDV.api.RDVController;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Interface de gestion des rendez-vous pour le médecin.
 * Intègre le filtrage temporel, le suivi des statuts et les actions de consultation.
 */
public class RDVPanel extends JPanel {

    // Composants Table
    private JTable table;
    private DefaultTableModel model;
    private RDVController controller;

    // Éléments UI dynamiques (Mises à jour par le contrôleur)
    private JLabel lblDateFiltre;
    private JLabel lblPatientVal, lblHeureVal, lblMotifVal, lblStatutVal;
    private Long selectedRdvId = null;

    public RDVPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250)); // Gris clair moderne
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Initialisation des trois zones principales
        add(createHeaderSection(), BorderLayout.NORTH);
        add(createTableSection(), BorderLayout.CENTER);
        add(createDetailSection(), BorderLayout.SOUTH);
    }

    public void setController(RDVController controller) {
        this.controller = controller;
    }

    // ============================================================
    // ✅ MÉTHODES DE MISE À JOUR (Appelées par le RDVController)
    // ============================================================

    public void updateTable(List<RDVPanelDTO> data) {
        model.setRowCount(0);
        if (data == null || data.isEmpty()) {
            return;
        }

        for (RDVPanelDTO dto : data) {
            model.addRow(new Object[]{
                    dto.getRdvId(),
                    dto.getHeureFormattee(),
                    dto.getPatientFullname(),
                    dto.getMotif(),
                    dto.getStatut(),
                    dto.getConsultationStatusLabel(),
                    "Actions"
            });
        }
    }

    public void updateDateLabel(LocalDate date) {
        if (lblDateFiltre != null) {
            lblDateFiltre.setText("Date : [ " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ]");
        }
    }

    // ============================================================
    // 🏗️ CONSTRUCTION DES SECTIONS UI
    // ============================================================

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout(0, 10));
        header.setOpaque(false);

        JLabel title = new JLabel("Mes Rendez-vous");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);

        // -- Navigation Temporelle (Gauche) --
        JPanel timeNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timeNav.setOpaque(false);

        JButton btnToday = new JButton("Aujourd'hui");
        JButton btnWeek = new JButton("Semaine");
        JButton btnMonth = new JButton("Mois");

        btnToday.addActionListener(e -> { if(controller != null) controller.refreshView(); });
        btnWeek.addActionListener(e -> { if(controller != null) controller.handleFilterWeek(); });
        btnMonth.addActionListener(e -> { if(controller != null) controller.handleFilterMonth(); });

        timeNav.add(btnToday);
        timeNav.add(btnWeek);
        timeNav.add(btnMonth);

        // -- Filtres et Date (Droite) --
        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filters.setOpaque(false);

        lblDateFiltre = new JLabel("Date : [ " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ]");
        lblDateFiltre.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JComboBox<String> comboStatut = new JComboBox<>(new String[]{"Tous", "CONFIRMED", "PENDING", "CANCELLED"});
        comboStatut.addActionListener(e -> {
            if(controller != null) controller.handleFilterStatut((String) comboStatut.getSelectedItem());
        });

        filters.add(lblDateFiltre);
        filters.add(new JLabel("Statut :"));
        filters.add(comboStatut);

        toolbar.add(timeNav, BorderLayout.WEST);
        toolbar.add(filters, BorderLayout.EAST);

        header.add(title, BorderLayout.NORTH);
        header.add(toolbar, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createTableSection() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        String[] cols = {"ID", "Heure", "Patient", "Motif", "Statut", "Consultation", "Action"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Cache la colonne ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDetailView();
        });

        container.add(new JScrollPane(table), BorderLayout.CENTER);
        return container;
    }

    private JPanel createDetailSection() {
        JPanel detailWrapper = new JPanel(new BorderLayout());
        detailWrapper.setBackground(Color.WHITE);
        detailWrapper.setPreferredSize(new Dimension(0, 180));
        detailWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("DÉTAILS DU RENDEZ-VOUS SÉLECTIONNÉ");
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        title.setForeground(new Color(127, 140, 141));
        detailWrapper.add(title, BorderLayout.NORTH);

        // Zone d'informations
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);

        lblPatientVal = new JLabel("Veuillez sélectionner un rendez-vous...");
        lblHeureVal = new JLabel("-");
        lblMotifVal = new JLabel("-");
        lblStatutVal = new JLabel("-");

        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        infoPanel.setOpaque(false);
        infoPanel.add(lblPatientVal); infoPanel.add(lblHeureVal);
        infoPanel.add(lblMotifVal); infoPanel.add(lblStatutVal);

        // Zone d'actions (Boutons)
        JPanel actionPanel = new JPanel(new GridBagLayout());
        actionPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JButton btnStart = createStyledButton("Démarrer Consultation", new Color(46, 204, 113));
        JButton btnOpen = createStyledButton("Ouvrir Consultation", new Color(52, 152, 219));
        JButton btnDossier = createStyledButton("Voir Dossier Médical", new Color(52, 73, 94));

        btnStart.addActionListener(e -> {
            if (controller != null && selectedRdvId != null) controller.handleDemarrerConsultation(selectedRdvId);
        });

        gbc.gridx = 0; gbc.gridy = 0; actionPanel.add(btnStart, gbc);
        gbc.gridx = 1; gbc.gridy = 0; actionPanel.add(btnOpen, gbc);
        gbc.gridx = 1; gbc.gridy = 1; actionPanel.add(btnDossier, gbc);

        content.add(infoPanel, BorderLayout.WEST);
        content.add(actionPanel, BorderLayout.EAST);
        detailWrapper.add(content, BorderLayout.CENTER);

        return detailWrapper;
    }

    private void updateDetailView() {
        int row = table.getSelectedRow();
        if (row != -1) {
            selectedRdvId = (Long) model.getValueAt(row, 0);
            lblHeureVal.setText("<html><b>Heure :</b> " + model.getValueAt(row, 1) + "</html>");
            lblPatientVal.setText("<html><b>Patient :</b> " + model.getValueAt(row, 2) + "</html>");
            lblMotifVal.setText("<html><b>Motif :</b> " + model.getValueAt(row, 3) + "</html>");
            lblStatutVal.setText("<html><b>Statut :</b> <font color='blue'>" + model.getValueAt(row, 4) + "</font></html>");
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(190, 38));
        return btn;
    }
}