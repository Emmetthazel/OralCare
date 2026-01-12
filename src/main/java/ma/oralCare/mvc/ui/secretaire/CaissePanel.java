package ma.oralCare.mvc.ui.secretaire;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import ma.oralCare.mvc.ui.secretaire.dialogs.FactureDialog;

public class CaissePanel extends JPanel {
    private JTable factureTable;
    private DefaultTableModel tableModel;

    public CaissePanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestion de la Caisse & Facturation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JButton btnNewFacture = new JButton("Générer Facture / Règlement");
        btnNewFacture.setBackground(new Color(230, 126, 34)); // Orange
        btnNewFacture.setForeground(Color.WHITE);
        btnNewFacture.setFont(new Font("Segoe UI", Font.BOLD, 14));

        header.add(title, BorderLayout.WEST);
        header.add(btnNewFacture, BorderLayout.EAST);

        // --- TABLEAU (Aligné sur prix_de_base et prix_de_patient du SQL) ---
        String[] columns = {
                "ID Consultation", "Patient", "Total Actes (Base)", "Net à Payer (Patient)", "Versé", "Statut"
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        factureTable = new JTable(tableModel);
        factureTable.setRowHeight(35);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Simulation de données provenant de Consultation + Intervention_Medecin
        // Exemple 1 : Total Base 1200, Patient paie 400 (Assurance couvre 800)
        tableModel.addRow(new Object[]{"CONS-2026-001", "Ahmed Benani", 1200.00, 400.00, 0.00, "PENDING"});
        // Exemple 2 : Patient sans assurance, paie le total
        tableModel.addRow(new Object[]{"CONS-2026-002", "Sara Idrissi", 500.00, 500.00, 500.00, "PAID"});

        // --- LOGIQUE DU BOUTON ---
        btnNewFacture.addActionListener(e -> {
            int selectedRow = factureTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une ligne dans le tableau.");
                return;
            }

            // Récupération des données typées depuis le modèle
            String patientNom = (String) tableModel.getValueAt(selectedRow, 1);
            double totalBase = (double) tableModel.getValueAt(selectedRow, 2);
            double totalPatient = (double) tableModel.getValueAt(selectedRow, 3);

            Window parent = SwingUtilities.getWindowAncestor(this);

            // APPEL CORRIGÉ : Respecte la logique Prix de Base vs Prix Patient
            FactureDialog dialog = new FactureDialog(
                    (Frame) parent,
                    patientNom,
                    totalBase,
                    totalPatient
            );

            dialog.setVisible(true);

            if(dialog.isConfirmed()) {
                // Ici, on déclencherait la mise à jour de la table 'Facture'
                // et de la 'SituationFinanciere' via le Controller/Service
                System.out.println("Règlement enregistré pour : " + patientNom);
                refreshTable();
            }
        });

        // --- FOOTER (Situation Financière Globale) ---
        JPanel footer = new JPanel(new GridLayout(1, 2));
        footer.setBackground(new Color(245, 246, 250));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblCredit = new JLabel("Crédit Client Global : 400.00 DH");
        lblCredit.setForeground(Color.RED);
        lblCredit.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblTotalRecette = new JLabel("Recette Journée : 500.00 DH", SwingConstants.RIGHT);
        lblTotalRecette.setForeground(new Color(39, 174, 96));
        lblTotalRecette.setFont(new Font("Segoe UI", Font.BOLD, 14));

        footer.add(lblCredit);
        footer.add(lblTotalRecette);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(factureTable), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        // Logique pour appeler le DAO et rafraîchir le JTable
        System.out.println("Rafraîchissement des données depuis SQL...");
    }
}