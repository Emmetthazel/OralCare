package ma.oralCare.mvc.ui.secretaire.dialogs;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.enums.Assurance;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class PatientDialog extends JDialog {
    // Champs de texte
    private JTextField txtNom = new JTextField(20);
    private JTextField txtPrenom = new JTextField(20);
    private JSpinner dateSpinner;
    private JTextField txtEmail = new JTextField(20);
    private JTextField txtTelephone = new JTextField(20);
    private JTextArea txtAdresse = new JTextArea(3, 20);

    // Enums conformes au schema.sql
    private JComboBox<Sexe> comboSexe = new JComboBox<>(Sexe.values());
    private JComboBox<Assurance> comboAssurance = new JComboBox<>(Assurance.values());

    // Gestion des antécédents (Table Patient_Antecedent)
    private JList<String> listAntecedents;
    private DefaultListModel<String> anteModel;

    private boolean validated = false;

    public PatientDialog(Frame parent, String title) {
        super(parent, title, true);
        setLayout(new BorderLayout());
        setResizable(false);

        // Panneau de formulaire principal
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date de Naissance
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        // Initialisation des antécédents (Exemples basés sur le SQL)
        anteModel = new DefaultListModel<>();
        anteModel.addElement("Diabète");
        anteModel.addElement("Hypertension");
        anteModel.addElement("Allergie Pénicilline");
        anteModel.addElement("Hépatite");
        listAntecedents = new JList<>(anteModel);
        listAntecedents.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAntecedents.setVisibleRowCount(4);

        // Ajout des composants
        int row = 0;
        addFormField(formPanel, "Nom :", txtNom, gbc, row++);
        addFormField(formPanel, "Prénom :", txtPrenom, gbc, row++);
        addFormField(formPanel, "Date Naissance :", dateSpinner, gbc, row++);
        addFormField(formPanel, "Sexe :", comboSexe, gbc, row++);
        addFormField(formPanel, "Téléphone :", txtTelephone, gbc, row++);
        addFormField(formPanel, "Email :", txtEmail, gbc, row++);
        addFormField(formPanel, "Assurance :", comboAssurance, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtAdresse), gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Antécédents :"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(listAntecedents), gbc);

        // Boutons d'action
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(46, 204, 113));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> setVisible(false));
        btnSave.addActionListener(e -> {
            if (validateForm()) {
                validated = true;
                setVisible(false);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void addFormField(JPanel panel, String label, JComponent field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private boolean validateForm() {
        if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom et le prénom sont obligatoires.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public Patient getPatientData() {
        if (!validated) return null;

        LocalDate dob = ((Date)dateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return Patient.builder()
                .nom(txtNom.getText().trim())
                .prenom(txtPrenom.getText().trim())
                .dateDeNaissance(dob)
                .sexe((Sexe) comboSexe.getSelectedItem())
                .email(txtEmail.getText().trim())
                .telephone(txtTelephone.getText().trim())
                .assurance((Assurance) comboAssurance.getSelectedItem())
                .adresse(txtAdresse.getText().trim())
                .build();
    }

    public List<String> getSelectedAntecedents() {
        return listAntecedents.getSelectedValuesList();
    }
}