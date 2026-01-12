package ma.oralCare.mvc.ui.admin.referentiel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CabinetManagerPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public CabinetManagerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Barre d'outils
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("➕ Enregistrer Nouveau Cabinet");
        toolbar.add(btnAdd);

        // Table
        String[] columns = {"ID", "Nom du Cabinet", "Ville", "Date d'adhésion"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        btnAdd.addActionListener(e -> showCabinetForm());

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void showCabinetForm() {
        JTextField nameField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField addressField = new JTextField();

        Object[] message = {
                "Nom du Cabinet :", nameField,
                "Ville :", cityField,
                "Adresse Complète :", addressField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Nouveau Client Cabinet", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            // Ici, on enregistre dans la table 'cabinets' de MySQL
            model.addRow(new Object[]{"AUTO", nameField.getText(), cityField.getText(), "Aujourd'hui"});
        }
    }
}