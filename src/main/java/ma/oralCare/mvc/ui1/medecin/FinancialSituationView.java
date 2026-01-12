package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FinancialSituationView extends JPanel {

    private JTable tableSituations;
    private DefaultTableModel situationModel;
    private JTable tableFactures;
    private DefaultTableModel factureModel;

    private JLabel lblSummary;
    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);

    public FinancialSituationView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- ENTÊTE ---
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("<html>👤 Patient : <b>Sara K.</b></html>"));
        headerPanel.add(new JLabel("<html>📅 Consultation : <b>Contrôle</b></html>"));
        headerPanel.add(new JLabel("<html>Statut : <span style='color:orange;'>EN COURS</span></html>"));
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTRE : LISTE DES SITUATIONS ---
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        centerPanel.setOpaque(false);

        // 1. Liste Globale
        JPanel listPanel = createStyledPanel("Historique des Situations Financières");
        String[] sitCols = {"N°", "Date CS", "Total Actes", "Payé", "Reste", "Statut", "Action"};
        situationModel = new DefaultTableModel(sitCols, 0);
        tableSituations = new JTable(situationModel);
        listPanel.add(new JScrollPane(tableSituations), BorderLayout.CENTER);

        // Boutons Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.setOpaque(false);
        JButton btnAdd = new JButton("+ Créer Situation");
        JButton btnInvoice = new JButton("📄 Générer Facture");
        stylePrimaryButton(btnAdd);
        styleAccentButton(btnInvoice);
        btnPanel.add(btnAdd);
        btnPanel.add(btnInvoice);
        listPanel.add(btnPanel, BorderLayout.SOUTH);

        // 2. Détails & Factures Liées
        JPanel detailPanel = createStyledPanel("Détail Situation Financière & Factures");

        lblSummary = new JLabel("<html><b>Résumé sélection :</b> Total 1000 DH | Payé 500 DH | <span style='color:red;'>Reste 500 DH</span></html>");
        lblSummary.setBorder(new EmptyBorder(0, 0, 10, 0));
        detailPanel.add(lblSummary, BorderLayout.NORTH);

        String[] facCols = {"N° Facture", "Date", "Montant", "Payé", "Reste"};
        factureModel = new DefaultTableModel(facCols, 0);
        tableFactures = new JTable(factureModel);
        detailPanel.add(new JScrollPane(tableFactures), BorderLayout.CENTER);

        centerPanel.add(listPanel);
        centerPanel.add(detailPanel);
        add(centerPanel, BorderLayout.CENTER);

        loadMockData();
    }

    private void loadMockData() {
        situationModel.addRow(new Object[]{"01", "08/01/2026", "1000.00 DH", "500.00 DH", "500.00 DH", "PENDING", "▶ Voir"});
        situationModel.addRow(new Object[]{"02", "02/01/2026", "500.00 DH", "500.00 DH", "0.00 DH", "PAID", "▶ Voir"});

        factureModel.addRow(new Object[]{"FAC-2026-001", "08/01/2026", "1000.00", "500.00", "500.00"});
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
        b.setBackground(SUCCESS_COLOR);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }
}