package ma.oralCare.mvc.ui.secretaire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import ma.oralCare.mvc.ui.secretaire.dialogs.PatientDialog;

public class PatientPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public PatientPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Barre supérieure : Recherche et Ajout
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        searchField = new JTextField("Rechercher par CIN ou Nom...");
        JButton btnAdd = new JButton("+ Nouveau Patient");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);

        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(btnAdd, BorderLayout.EAST);

        // Tableau : Colonnes basées sur le SQL (id_entite, nom, prenom, sexe, telephone, assurance)
        String[] columns = {"ID", "Nom", "Prénom", "Sexe", "Téléphone", "Assurance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(30);

        btnAdd.addActionListener(e -> {
            PatientDialog dialog = new PatientDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter Patient");
            dialog.setVisible(true);
        });

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(patientTable), BorderLayout.CENTER);
    }
}