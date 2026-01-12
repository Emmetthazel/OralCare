package ma.oralCare.mvc.ui.secretaire.dialogs;

import ma.oralCare.entities.enums.StatutRDV;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

public class RDVDialog extends JDialog {
    // Composants de saisie
    private JTextField txtDossierId = new JTextField(10);
    private JSpinner dateSpinner;
    private JSpinner timeSpinner;
    private JTextField txtMotif = new JTextField(20);

    // Utilisation de l'Enum StatutRDV (PENDING, CONFIRMED, CANCELLED, COMPLETED)
    private JComboBox<StatutRDV> comboStatut = new JComboBox<>(StatutRDV.values());
    private JTextArea txtNote = new JTextArea(4, 20);

    private boolean validated = false;

    public RDVDialog(Frame parent) {
        super(parent, "Planifier un nouveau Rendez-vous", true);
        setLayout(new BorderLayout());
        setResizable(false);

        // Panneau de formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Configuration Date (dd/MM/yyyy)
        dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        // Configuration Heure (HH:mm)
        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));

        // Placement des composants
        int row = 0;
        addLabel("ID Dossier Médical :", formPanel, gbc, row);
        addComponent(txtDossierId, formPanel, gbc, row++);

        addLabel("Date du RDV :", formPanel, gbc, row);
        addComponent(dateSpinner, formPanel, gbc, row++);

        addLabel("Heure :", formPanel, gbc, row);
        addComponent(timeSpinner, formPanel, gbc, row++);

        addLabel("Motif :", formPanel, gbc, row);
        addComponent(txtMotif, formPanel, gbc, row++);

        addLabel("Statut :", formPanel, gbc, row);
        addComponent(comboStatut, formPanel, gbc, row++);

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Note Médecin :"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtNote), gbc);

        // Panneau de boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Planifier");
        btnSave.setBackground(new Color(52, 152, 219)); // Bleu
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> setVisible(false));
        btnSave.addActionListener(e -> {
            if (validateFields()) {
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

    private void addLabel(String text, JPanel p, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        p.add(new JLabel(text), gbc);
    }

    private void addComponent(JComponent comp, JPanel p, GridBagConstraints gbc, int row) {
        gbc.gridx = 1; gbc.gridy = row;
        p.add(comp, gbc);
    }

    private boolean validateFields() {
        if (txtDossierId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'ID du dossier médical est requis.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtMotif.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un motif.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Retourne la date sous forme de LocalDate (Java 8+)
     */
    public LocalDate getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Retourne l'heure sous forme de LocalTime (Java 8+)
     */
    public LocalTime getSelectedTime() {
        Date date = (Date) timeSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    // Getters pour récupérer les données dans le contrôleur
    public boolean isValidated() { return validated; }
    public String getDossierId() { return txtDossierId.getText(); }
    public String getMotif() { return txtMotif.getText(); }
    public StatutRDV getStatut() { return (StatutRDV) comboStatut.getSelectedItem(); }
    public String getNote() { return txtNote.getText(); }
}