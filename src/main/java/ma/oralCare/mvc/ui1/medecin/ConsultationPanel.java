package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConsultationPanel extends JPanel {

    // Composants de données
    private JTable tableActes;
    private DefaultTableModel modelActes;
    private JTextArea txtObservations;

    // Labels dynamiques pour la liaison avec RDV
    private JLabel lblPatientInfo;
    private JLabel lblHeure;
    private JLabel lblTotal, lblPaye, lblReste;
    // ✅ Ajoutez ces getters dans ConsultationPanel.java
    public JButton getBtnAddIntervention() { return (JButton) ((JPanel)getComponent(1)).getComponent(2); } // Selon votre structure
    public JButton getBtnOrdonnance() { return (JButton) ((JPanel)((JPanel)getComponent(2)).getComponent(1)).getComponent(0); }
    public JButton getBtnCertificat() { return (JButton) ((JPanel)((JPanel)getComponent(2)).getComponent(1)).getComponent(1); }
    public JButton getBtnFacture() { return (JButton) ((JPanel)((JPanel)getComponent(2)).getComponent(1)).getComponent(2); }
    public JButton getBtnFinish() { return (JButton) ((JPanel)((JPanel)getComponent(2)).getComponent(1)).getComponent(3); }
    public JTextArea getTxtObservations() { return txtObservations; }

    public ConsultationPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250)); // Gris clair moderne
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. En-tête (Informations du patient en cours)
        add(createHeader(), BorderLayout.NORTH);

        // 2. Zone Centrale (Tableau des interventions + Observations)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);
        centerPanel.add(createInterventionSection(), BorderLayout.CENTER);
        centerPanel.add(createObservationSection(), BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Pied de page (Finances + Boutons d'action)
        add(createFooterSection(), BorderLayout.SOUTH);
    }

    public void chargerRendezVousActif(RDVPanelDTO rdv) {
        if (rdv == null) return;

        // Mise à jour de l'en-tête
        lblPatientInfo.setText("Patient : " + rdv.getPatientFullname().toUpperCase() +
                " | RDV : " + rdv.getMotif() +
                " | Statut : EN COURS");
        lblHeure.setText("⏰ " + rdv.getHeureFormattee());

        // Réinitialisation pour une nouvelle session
        txtObservations.setText("");
        modelActes.setRowCount(0);
        updateFinances(0, 0);

        System.out.println("[UI-CONSULTATION] Chargement réussi pour : " + rdv.getPatientFullname());
    }

    // ============================================================
    // 🏗️ CONSTRUCTION DES SECTIONS UI
    // ============================================================

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 233, 237)),
                new EmptyBorder(15, 20, 15, 20)));

        lblPatientInfo = new JLabel("Aucun patient sélectionné - En attente...");
        lblPatientInfo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPatientInfo.setForeground(new Color(44, 62, 80));

        lblHeure = new JLabel("⏰ --:--");
        lblHeure.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeure.setForeground(new Color(127, 140, 141));

        panel.add(lblPatientInfo, BorderLayout.WEST);
        panel.add(lblHeure, BorderLayout.EAST);
        return panel;
    }

    private JPanel createInterventionSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JLabel title = new JLabel("🦷 INTERVENTIONS & ACTES");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(new Color(52, 73, 94));

        String[] columns = {"Dent", "Acte médical", "Prix (DH)", "Statut", "Avancement"};
        modelActes = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tableActes = new JTable(modelActes);
        tableActes.setRowHeight(38);
        tableActes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Centrer les données du tableau
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tableActes.getColumnCount(); i++) {
            tableActes.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JButton btnAdd = new JButton("+ Ajouter Intervention");
        btnAdd.setBackground(new Color(52, 152, 219));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(tableActes), BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createObservationSection() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel title = new JLabel("📝 Observations médicales :");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtObservations = new JTextArea(4, 20);
        txtObservations.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtObservations.setLineWrap(true);
        txtObservations.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtObservations);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterSection() {
        JPanel mainFooter = new JPanel(new BorderLayout());
        mainFooter.setOpaque(false);

        JPanel financePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
        financePanel.setBackground(new Color(236, 240, 241));
        financePanel.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));

        lblTotal = new JLabel("Total actes : 0 dh");
        lblPaye = new JLabel("Payé : 0 dh");
        lblReste = new JLabel("Reste : 0 dh");

        Font finFont = new Font("Segoe UI", Font.BOLD, 14);
        lblTotal.setFont(finFont);
        lblPaye.setFont(finFont);
        lblReste.setFont(finFont); lblReste.setForeground(new Color(192, 57, 43)); // Rouge pour le reste

        financePanel.add(lblTotal);
        financePanel.add(lblPaye);
        financePanel.add(lblReste);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        actionPanel.setOpaque(false);

        JButton btnOrd = createStyledButton("Ordonnance", new Color(52, 73, 94));
        JButton btnCert = createStyledButton("Certificat", new Color(52, 73, 94));
        JButton btnFacture = createStyledButton("Facture", new Color(52, 73, 94));
        JButton btnFinish = createStyledButton("Terminer Consultation", new Color(46, 204, 113));

        actionPanel.add(btnOrd);
        actionPanel.add(btnCert);
        actionPanel.add(btnFacture);
        actionPanel.add(btnFinish);

        mainFooter.add(financePanel, BorderLayout.NORTH);
        mainFooter.add(actionPanel, BorderLayout.SOUTH);
        return mainFooter;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateFinances(double total, double paye) {
        lblTotal.setText("Total actes : " + total + " dh");
        lblPaye.setText("Payé : " + paye + " dh");
        lblReste.setText("Reste : " + (total - paye) + " dh");
    }
}