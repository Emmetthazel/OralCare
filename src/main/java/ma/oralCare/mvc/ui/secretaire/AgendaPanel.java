package ma.oralCare.mvc.ui.secretaire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import ma.oralCare.mvc.ui.secretaire.dialogs.RDVDialog;

public class AgendaPanel extends JPanel {
    private JTable rdvTable;
    private DefaultTableModel tableModel;

    public AgendaPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Agenda des Rendez-vous");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JButton btnAddRDV = new JButton("Planifier RDV");
        btnAddRDV.setBackground(new Color(52, 152, 219));
        btnAddRDV.setForeground(Color.WHITE);

        header.add(title, BorderLayout.WEST);
        header.add(btnAddRDV, BorderLayout.EAST);

        // Colonnes basées sur la table RDV (date, heure, motif, statut)
        String[] columns = {"ID", "Date", "Heure", "Motif", "Statut", "Dossier ID"};
        tableModel = new DefaultTableModel(columns, 0);
        rdvTable = new JTable(tableModel);

        btnAddRDV.addActionListener(e -> {
            RDVDialog dialog = new RDVDialog((Frame) SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
        });

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(rdvTable), BorderLayout.CENTER);
    }
}