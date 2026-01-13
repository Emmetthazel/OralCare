package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TreatmentView extends JPanel {

    private JTable tableInterventions;
    private DefaultTableModel tableModel;
    private JTextArea txtObservations;
    private JLabel lblTotal, lblPaye, lblReste;

    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);

    public TreatmentView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- 1. ENTÊTE : INFOS CONSULTATION ---
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(210, 210, 210)));

        headerPanel.add(createHeaderLabel("👤 Patient : <b>-</b>", "⏰ -"));
        headerPanel.add(createHeaderLabel("🦷 Consultation : <b>-</b>", ""));
        headerPanel.add(createHeaderLabel("Statut : <span style='color:orange;'>-</span>", ""));

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. CENTRE : TABLE DES INTERVENTIONS ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        JLabel lblTableTitle = new JLabel("INTERVENTIONS & ACTES");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        centerPanel.add(lblTableTitle, BorderLayout.NORTH);

        String[] columns = {"Dent", "Acte médical", "Prix patient (DH)", "Statut", "Avancement (%)"};
        tableModel = new DefaultTableModel(columns, 0);
        tableInterventions = new JTable(tableModel);
        tableInterventions.setRowHeight(35);

        JScrollPane scrollPane = new JScrollPane(tableInterventions);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Zone Actions Interventions
        JPanel actionInterventionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionInterventionPanel.setOpaque(false);

        JButton btnAdd = new JButton("+ Ajouter Intervention");
        JButton btnEdit = new JButton("✏ Modifier");
        JButton btnDelete = new JButton("🗑 Supprimer");

        stylePrimaryButton(btnAdd);
        styleSecondaryButton(btnEdit);
        styleSecondaryButton(btnDelete);

        actionInterventionPanel.add(btnAdd);
        actionInterventionPanel.add(btnEdit);
        actionInterventionPanel.add(btnDelete);
        centerPanel.add(actionInterventionPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // --- 3. BAS : OBSERVATIONS & FINANCE ---
        JPanel southPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        southPanel.setOpaque(false);
        southPanel.setPreferredSize(new Dimension(0, 200));

        // Observations
        JPanel obsPanel = new JPanel(new BorderLayout(5, 5));
        obsPanel.setOpaque(false);
        obsPanel.add(new JLabel("📝 Observations du Médecin :"), BorderLayout.NORTH);
        txtObservations = new JTextArea();
        txtObservations.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        obsPanel.add(new JScrollPane(txtObservations), BorderLayout.CENTER);

        // Résumé Financier
        JPanel financePanel = new JPanel(new BorderLayout());
        financePanel.setBackground(Color.WHITE);
        financePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        lblTotal = new JLabel("Total actes : 0 DH");
        lblPaye = new JLabel("Payé : 0 DH");
        lblReste = new JLabel("Reste : 0 DH");

        JPanel labelsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        labelsPanel.setOpaque(false);
        labelsPanel.add(lblTotal);
        labelsPanel.add(lblPaye);
        labelsPanel.add(lblReste);

        JButton btnFacture = new JButton("📄 Générer Facture");
        styleAccentButton(btnFacture);

        financePanel.add(new JLabel("💰 RÉSUMÉ FINANCIER"), BorderLayout.NORTH);
        financePanel.add(labelsPanel, BorderLayout.CENTER);
        financePanel.add(btnFacture, BorderLayout.SOUTH);

        southPanel.add(obsPanel);
        southPanel.add(financePanel);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JLabel createHeaderLabel(String mainText, String subText) {
        JLabel label = new JLabel("<html>" + mainText + " &nbsp;&nbsp; <small style='color:gray;'>" + subText + "</small></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        return label;
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(41, 128, 185));
        b.setForeground(TEXT_COLOR);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(TEXT_COLOR);
        b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
    }

    private void styleAccentButton(JButton b) {
        b.setBackground(new Color(39, 174, 96));
        b.setForeground(TEXT_COLOR);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}