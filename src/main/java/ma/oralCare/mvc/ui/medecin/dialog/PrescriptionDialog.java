package ma.oralCare.mvc.ui.medecin.dialog;

import javax.swing.*;
import java.awt.*;

public class PrescriptionDialog extends JDialog {
    // Champs basés sur la table Prescription
    private JComboBox<String> comboMedicaments = new JComboBox<>(new String[]{"Paracétamol", "Amoxicilline", "Ibuprofène"});
    private JSpinner spinQuantite = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
    private JTextField txtFrequence = new JTextField("3 fois par jour", 15);
    private JSpinner spinDuree = new JSpinner(new SpinnerNumberModel(7, 1, 30, 1));
    private boolean validated = false;

    public PrescriptionDialog(Frame parent) {
        super(parent, "Nouvelle Prescription", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Médicament :"), gbc);
        gbc.gridx = 1; add(comboMedicaments, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("Quantité (Boîtes) :"), gbc);
        gbc.gridx = 1; add(spinQuantite, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("Fréquence :"), gbc);
        gbc.gridx = 1; add(txtFrequence, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Durée (jours) :"), gbc);
        gbc.gridx = 1; add(spinDuree, gbc);

        JButton btnOk = new JButton("Ajouter");
        btnOk.addActionListener(e -> { validated = true; setVisible(false); });
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; add(btnOk, gbc);

        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isValidated() { return validated; }
}