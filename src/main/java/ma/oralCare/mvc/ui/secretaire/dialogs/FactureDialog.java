package ma.oralCare.mvc.ui.secretaire.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FactureDialog extends JDialog {
    // Champs Système (Automatiques - Lecture seule)
    private JTextField txtTotalBase = new JTextField(10);    // Somme des 'prix_de_base' (Tarif National)
    private JTextField txtTotalPatient = new JTextField(10); // Somme des 'prix_de_patient' (Dû après assurance)
    private JTextField txtPartAssurance = new JTextField(10); // Calculé : Base - Patient

    // Champs de Saisie (Action de la secrétaire)
    private JTextField txtRemise = new JTextField("0.00", 10);
    private JTextField txtMontantVerse = new JTextField("0.00", 10);

    // Résultats et Statuts
    private JLabel lblResteAFixer = new JLabel("0.00 DH");
    private JComboBox<String> comboMode = new JComboBox<>(new String[]{"CASH", "CHECK", "CARD"});
    private JComboBox<String> comboStatut = new JComboBox<>(new String[]{"PAID", "PENDING", "OVERDUE", "CANCELLED"});

    private double montantPatientInitial = 0.0;
    private boolean validated = false;

    /**
     * @param totalBase Somme des prix de base de la table 'acte'
     * @param totalPatient Somme des 'prix_de_patient' de la table 'intervention_medecin'
     */
    public FactureDialog(Frame parent, String nomPatient, double totalBase, double totalPatient) {
        super(parent, "Règlement Facture : " + nomPatient, true);
        this.montantPatientInitial = totalPatient;

        setLayout(new BorderLayout(15, 15));
        setResizable(false);

        // --- 1. ENTÊTE : RÉCAPITULATIF SYSTÈME ---
        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 8));
        headerPanel.setBorder(BorderFactory.createTitledBorder("Détails des Actes (via Médecin)"));
        headerPanel.setBackground(new Color(245, 246, 250));

        txtTotalBase.setText(String.format("%.2f", totalBase));
        txtTotalBase.setEditable(false);
        txtTotalPatient.setText(String.format("%.2f", totalPatient));
        txtTotalPatient.setEditable(false);
        txtPartAssurance.setText(String.format("%.2f", totalBase - totalPatient));
        txtPartAssurance.setEditable(false);
        txtPartAssurance.setForeground(new Color(41, 128, 185));

        headerPanel.add(new JLabel(" Total Tarif Base :")); headerPanel.add(txtTotalBase);
        headerPanel.add(new JLabel(" Couverture Assurance :")); headerPanel.add(txtPartAssurance);
        headerPanel.add(new JLabel(" Net Dû Patient :")); headerPanel.add(txtTotalPatient);

        // --- 2. CENTRE : SAISIE ENCAISSEMENT ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addFormRow(centerPanel, "Remise Exceptionnelle (DH) :", txtRemise, gbc, row++);
        addFormRow(centerPanel, "Montant Encaissé (Versé) :", txtMontantVerse, gbc, row++);
        addFormRow(centerPanel, "Mode de Paiement :", comboMode, gbc, row++);
        addFormRow(centerPanel, "Statut de Facture :", comboStatut, gbc, row++);

        // Affichage du Reste
        gbc.gridx = 0; gbc.gridy = row;
        centerPanel.add(new JLabel("RESTE À PAYER :"), gbc);
        gbc.gridx = 1;
        lblResteAFixer.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblResteAFixer.setForeground(new Color(192, 57, 43));
        centerPanel.add(lblResteAFixer, gbc);

        // --- 3. ACTIONS ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Enregistrer & Générer Reçu");
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(200, 40));

        btnSave.addActionListener(e -> {
            validated = true;
            setVisible(false);
        });
        actionPanel.add(btnSave);

        // --- LOGIQUE DE CALCUL TEMPS RÉEL ---
        KeyAdapter calcAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { calculerReste(); }
        };
        txtRemise.addKeyListener(calcAdapter);
        txtMontantVerse.addKeyListener(calcAdapter);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        calculerReste();
        pack();
        setLocationRelativeTo(parent);
    }

    private void addFormRow(JPanel p, String label, JComponent comp, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        p.add(comp, gbc);
    }

    private void calculerReste() {
        try {
            double remise = txtRemise.getText().isEmpty() ? 0 : Double.parseDouble(txtRemise.getText());
            double verse = txtMontantVerse.getText().isEmpty() ? 0 : Double.parseDouble(txtMontantVerse.getText());

            double netAPayer = montantPatientInitial - remise;
            double reste = netAPayer - verse;

            lblResteAFixer.setText(String.format("%.2f DH", Math.max(0, reste)));

            // Mise à jour automatique du statut selon le reste
            if (reste <= 0) {
                comboStatut.setSelectedItem("PAID");
                lblResteAFixer.setForeground(new Color(39, 174, 96));
            } else {
                comboStatut.setSelectedItem("PENDING");
                lblResteAFixer.setForeground(new Color(192, 57, 43));
            }
        } catch (NumberFormatException e) {
            lblResteAFixer.setText("Erreur format");
        }
    }

    public boolean isConfirmed() { return validated; }
    public double getVerse() { return Double.parseDouble(txtMontantVerse.getText()); }
}