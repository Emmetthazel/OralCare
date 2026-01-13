package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PrescriptionView extends JPanel {

    private JTable tableOrdonnances;
    private DefaultTableModel ordonnanceModel;
    private JTable tablePrescriptions;
    private DefaultTableModel prescriptionModel;

    private JTextArea txtObservations;
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public PrescriptionView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- ENTÊTE (Infos Patient) ---
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("<html>👤 Patient : <b>-</b></html>"));
        headerPanel.add(new JLabel("<html>📅 Consultation : <b>-</b></html>"));
        headerPanel.add(new JLabel("<html>Statut : <span style='color:green;'>-</span></html>"));
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTRE : SPLIT PANE (Liste en haut, Détails en bas) ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);

        // 1. Partie Haute : Liste des Ordonnances
        JPanel listPanel = createStyledPanel("Liste des Ordonnances");
        String[] ordCols = {"N°", "Date", "Médecin", "Nb Prescriptions", "Action"};
        ordonnanceModel = new DefaultTableModel(ordCols, 0);
        tableOrdonnances = new JTable(ordonnanceModel);
        listPanel.add(new JScrollPane(tableOrdonnances), BorderLayout.CENTER);

        // Boutons de gestion
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        JButton btnAdd = new JButton("+ Créer Ordonnance");
        JButton btnDel = new JButton("Supprimer");
        stylePrimaryButton(btnAdd);
        btnPanel.add(btnAdd);
        btnPanel.add(btnDel);
        listPanel.add(btnPanel, BorderLayout.SOUTH);

        // 2. Partie Basse : Détails de l'Ordonnance sélectionnée
        JPanel detailPanel = createStyledPanel("Détails Ordonnance & Prescriptions");

        // Observations en haut du détail
        txtObservations = new JTextArea(2, 20);
        txtObservations.setBorder(BorderFactory.createTitledBorder("Observations Médicales"));
        detailPanel.add(txtObservations, BorderLayout.NORTH);

        // Table des prescriptions
        String[] presCols = {"Médicament", "Quantité", "Fréquence", "Durée"};
        prescriptionModel = new DefaultTableModel(presCols, 0);
        tablePrescriptions = new JTable(prescriptionModel);
        detailPanel.add(new JScrollPane(tablePrescriptions), BorderLayout.CENTER);

        // Actions Impression
        JButton btnPrint = new JButton("🖨 Imprimer l'Ordonnance");
        styleAccentButton(btnPrint);
        detailPanel.add(btnPrint, BorderLayout.SOUTH);

        centerPanel.add(listPanel);
        centerPanel.add(detailPanel);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));
        return p;
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(ACCENT_COLOR);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
    }

    private void styleAccentButton(JButton b) {
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}