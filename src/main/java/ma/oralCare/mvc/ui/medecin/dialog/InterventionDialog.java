package ma.oralCare.mvc.ui.medecin.dialog;

import javax.swing.*;
import java.awt.*;

public class InterventionDialog extends JDialog {
    private JComboBox<String> comboActes; // Liste chargée depuis la table 'acte'
    private JTextField txtNumDent = new JTextField(5);
    private JTextArea txtNote = new JTextArea(3, 20);
    private JTextField txtPrixBase = new JTextField(10);
    private JTextField txtPrixPatient = new JTextField(10);
    private boolean validated = false;

    public InterventionDialog(Frame parent) {
        super(parent, "Saisie d'un Acte Médical", true);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuration des prix (Lecture seule pour la base)
        txtPrixBase.setEditable(false);
        txtPrixBase.setText("1000.00"); // Exemple par défaut

        int r = 0;
        addLabel("Sélectionner l'acte :", form, gbc, r);
        comboActes = new JComboBox<>(new String[]{"Détartrage", "Plombage", "Implant", "Extraction"});
        addComponent(comboActes, form, gbc, r++);

        addLabel("Numéro de la dent (FDI) :", form, gbc, r);
        addComponent(txtNumDent, form, gbc, r++);

        addLabel("Prix de Base (Référentiel) :", form, gbc, r);
        addComponent(txtPrixBase, form, gbc, r++);

        addLabel("Prix appliqué au Patient :", form, gbc, r);
        addComponent(txtPrixPatient, form, gbc, r++);

        addLabel("Notes / Observations :", form, gbc, r);
        addComponent(new JScrollPane(txtNote), form, gbc, r++);

        JButton btnOk = new JButton("Ajouter l'intervention");
        btnOk.setBackground(new Color(39, 174, 96));
        btnOk.setForeground(Color.WHITE);
        btnOk.addActionListener(e -> { validated = true; setVisible(false); });

        add(form, BorderLayout.CENTER);
        add(btnOk, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addLabel(String t, JPanel p, GridBagConstraints gbc, int r) {
        gbc.gridx = 0; gbc.gridy = r; p.add(new JLabel(t), gbc);
    }

    private void addComponent(JComponent c, JPanel p, GridBagConstraints gbc, int r) {
        gbc.gridx = 1; gbc.gridy = r; p.add(c, gbc);
    }

    public boolean isValidated() { return validated; }
}