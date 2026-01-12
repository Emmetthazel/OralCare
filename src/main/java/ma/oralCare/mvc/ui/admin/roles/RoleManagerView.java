package ma.oralCare.mvc.ui.admin.roles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RoleManagerView extends JPanel {

    private JTable roleTable;
    private DefaultTableModel tableModel;

    public RoleManagerView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // --- 1. ENTÊTE (Cas d'utilisation : Ajouter rôle) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Gestion des Rôles (Médecin / Secrétaire)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // Bouton pour le cas d'utilisation "Ajouter"
        JButton btnAdd = new JButton("+ Créer un Rôle");
        btnAdd.setBackground(new Color(41, 128, 185));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        header.add(title, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- 2. TABLEAU (Cas d'utilisation : Consulter rôle) ---
        // Liste conforme aux acteurs du système
        String[] columns = {"ID", "Libellé du Rôle", "Description des Accès"};
        Object[][] data = {
                {"1", "MEDECIN", "Accès aux dossiers médicaux et catalogue médicaments"},
                {"2", "SECRETAIRE", "Accès à l'agenda, facturation et catalogue ATES"}
        };

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        roleTable = new JTable(tableModel);
        roleTable.setRowHeight(40);
        roleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(roleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. ACTIONS (Cas d'utilisation : Modifier / Supprimer) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        // Bouton pour le cas d'utilisation "Modifier permission"
        JButton btnEdit = new JButton("Modifier Permissions");

        // Bouton pour le cas d'utilisation "Supprimer"
        JButton btnDelete = new JButton("Supprimer");
        btnDelete.setForeground(Color.RED);

        footer.add(btnEdit);
        footer.add(btnDelete);
        add(footer, BorderLayout.SOUTH);
    }
}