package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MesRDVPanel extends JPanel {

    private JTable rdvTable;
    private DefaultTableModel tableModel;
    private JPanel detailsPanel;
    private JLabel lblPatient, lblHeure, lblMotif, lblStatut, lblConsultation;

    public MesRDVPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Header de navigation temporelle ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);

        // Titre + Aujourd'hui
        JLabel lblTitle = new JLabel("Mes RDV - Aujourd’hui");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setForeground(new Color(44, 62, 80));
        topPanel.add(lblTitle, BorderLayout.WEST);

        // Boutons navigation temps
        JPanel timeNavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        timeNavPanel.setBackground(Color.WHITE);
        timeNavPanel.add(new JButton("Aujourd’hui"));
        timeNavPanel.add(new JButton("Semaine"));
        timeNavPanel.add(new JButton("Mois"));
        topPanel.add(timeNavPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- Table des RDV ---
        String[] columns = {"Heure", "Patient", "Motif", "Statut", "Consultation", "Action"};
        Object[][] data = {
                {"09:00", "Amine H.", "Douleur molaire", "Confirmé", "Non créée", "▶"},
                {"10:00", "Sara K.", "Contrôle", "Confirmé", "En cours", "▶"},
                {"11:30", "Yassine B.", "Extraction", "En attente", "Non créée", "▶"},
                {"14:00", "Lina A.", "Détartrage", "Confirmé", "Terminée", "▶"}
        };
        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // table non éditable
            }
        };
        rdvTable = new JTable(tableModel);
        rdvTable.setRowHeight(30);
        rdvTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rdvTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        rdvTable.setSelectionBackground(new Color(41, 128, 185, 50));
        rdvTable.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Scroll pane pour table
        JScrollPane scrollPane = new JScrollPane(rdvTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Panel détails RDV ---
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new GridLayout(6, 2, 5, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Détails du RDV"));
        detailsPanel.setPreferredSize(new Dimension(250, 0));

        lblPatient = new JLabel("Patient : ");
        lblHeure = new JLabel("Heure : ");
        lblMotif = new JLabel("Motif : ");
        lblStatut = new JLabel("Statut : ");
        lblConsultation = new JLabel("Consultation : ");

        detailsPanel.add(lblPatient);
        detailsPanel.add(new JLabel()); // placeholder
        detailsPanel.add(lblHeure);
        detailsPanel.add(new JLabel());
        detailsPanel.add(lblMotif);
        detailsPanel.add(new JLabel());
        detailsPanel.add(lblStatut);
        detailsPanel.add(new JLabel());
        detailsPanel.add(lblConsultation);
        detailsPanel.add(new JLabel());

        // Boutons d'action
        JPanel actionPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        actionPanel.add(new JButton("Démarrer Consultation"));
        actionPanel.add(new JButton("Ouvrir Consultation"));
        actionPanel.add(new JButton("Voir Dossier Médical"));
        detailsPanel.add(actionPanel);

        add(detailsPanel, BorderLayout.EAST);

        // --- Sélection d'une ligne pour afficher détails (UI seulement) ---
        rdvTable.getSelectionModel().addListSelectionListener(e -> {
            int row = rdvTable.getSelectedRow();
            if (row >= 0) {
                lblPatient.setText("Patient : " + rdvTable.getValueAt(row, 1));
                lblHeure.setText("Heure : " + rdvTable.getValueAt(row, 0));
                lblMotif.setText("Motif : " + rdvTable.getValueAt(row, 2));
                lblStatut.setText("Statut : " + rdvTable.getValueAt(row, 3));
                lblConsultation.setText("Consultation : " + rdvTable.getValueAt(row, 4));
            }
        });
    }
}
