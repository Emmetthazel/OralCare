package ma.oralCare.mvc.ui.medecin;

import ma.oralCare.mvc.ui.medecin.components.SchemaDentairePanel;
import ma.oralCare.mvc.ui.medecin.dialog.InterventionDialog;
import ma.oralCare.mvc.ui.medecin.dialog.PrescriptionDialog;
import ma.oralCare.mvc.ui.medecin.dialog.CertificatDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ConsultationPanel extends JPanel {
    private DefaultTableModel modelInterventions;
    private JTable tableInterventions;
    private JTextArea txtObs = new JTextArea(4, 20);

    public ConsultationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- BARRE D'ACTIONS RAPIDES (HAUT) ---
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnOrdo = new JButton("💊 Ordonnance");
        JButton btnCertif = new JButton("📜 Certificat");
        JButton btnRadio = new JButton("📸 Radios");

        btnOrdo.addActionListener(e -> new PrescriptionDialog((Frame)null).setVisible(true));
        btnCertif.addActionListener(e -> new CertificatDialog((Frame)null).setVisible(true));

        actionBar.add(new JLabel("Actions : "));
        actionBar.add(btnOrdo);
        actionBar.add(btnCertif);
        actionBar.add(btnRadio);

        // --- ZONE CENTRALE : ODONTOGRAMME ---
        SchemaDentairePanel odontogramme = new SchemaDentairePanel(e -> {
            String numDent = e.getActionCommand();
            ouvrirDialogueIntervention(numDent);
        });

        // --- ZONE BASSE : TABLEAU DES ACTES ---
        String[] cols = {"Dent", "Acte", "Prix (DH)", "Notes"};
        modelInterventions = new DefaultTableModel(cols, 0);
        tableInterventions = new JTable(modelInterventions);

        JPanel pnlTable = new JPanel(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createTitledBorder("Interventions de la séance"));
        pnlTable.add(new JScrollPane(tableInterventions), BorderLayout.CENTER);

        // --- PANNEAU DROIT : ANTÉCÉDENTS ---
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setPreferredSize(new Dimension(250, 0));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Antécédents du Patient"));
        JList<String> listAnte = new JList<>(new String[]{"Allergie Pénicilline", "Diabète Type 2"});
        listAnte.setForeground(Color.RED);
        pnlRight.add(new JScrollPane(listAnte));

        // Assemblage
        JPanel centerContainer = new JPanel(new GridLayout(2, 1));
        centerContainer.add(odontogramme);
        centerContainer.add(pnlTable);

        add(actionBar, BorderLayout.NORTH);
        add(centerContainer, BorderLayout.CENTER);
        add(pnlRight, BorderLayout.EAST);

        JButton btnTerminer = new JButton("Clôturer la Consultation");
        btnTerminer.setBackground(new Color(39, 174, 96));
        btnTerminer.setForeground(Color.WHITE);
        add(btnTerminer, BorderLayout.SOUTH);
    }

    private void ouvrirDialogueIntervention(String numDent) {
        InterventionDialog diag = new InterventionDialog((Frame) SwingUtilities.getWindowAncestor(this));
        // Optionnel : passer le numDent au dialogue
        diag.setVisible(true);
        if (diag.isValidated()) {
            modelInterventions.addRow(new Object[]{numDent, "Soin Dentaire", "500.00", "Observation..."});
        }
    }
}