package ma.oralCare.mvc.ui.medecin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientHistoryPanel extends JPanel {
    private JTable tableHistory;
    private DefaultTableModel modelHistory;
    private JTextArea txtDetails = new JTextArea();

    public PatientHistoryPanel() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // --- BARRE DE RECHERCHE ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Rechercher Patient (CIN/Nom) :"));
        searchPanel.add(new JTextField(20));
        searchPanel.add(new JButton("🔍"));

        // --- TABLEAU DES CONSULTATIONS PASSÉES ---
        String[] columns = {"Date", "Type", "Médecin", "Montant Payé", "Statut"};
        modelHistory = new DefaultTableModel(columns, 0);
        tableHistory = new JTable(modelHistory);

        // Données fictives issues de la table 'Consultation' et 'Facture'
        modelHistory.addRow(new Object[]{"15/11/2025", "Suivi", "Dr. Alami", "200.00 DH", "PAID"});
        modelHistory.addRow(new Object[]{"02/01/2026", "Urgence", "Dr. Alami", "500.00 DH", "PAID"});

        // --- ZONE DE DÉTAILS (Ce qui a été fait précisément) ---
        txtDetails.setEditable(false);
        txtDetails.setBorder(BorderFactory.createTitledBorder("Détail des actes effectués lors de la séance"));
        txtDetails.setText("Date : 15/11/2025\nActe : Extraction Dent 18\nObservation : Cicatrisation normale prévue.");

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(tableHistory), new JScrollPane(txtDetails));
        split.setDividerLocation(200);

        add(searchPanel, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
    }
}