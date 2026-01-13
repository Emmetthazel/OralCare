package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.service.modules.patient.dto.PatientCreateRequest;
import ma.oralCare.service.modules.patient.dto.PatientUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialogue pour l'ajout et la modification des patients
 */
public class PatientDialog extends JDialog {

    private boolean confirmed = false;
    private Patient existingPatient;
    
    // Champs du formulaire
    private JTextField nomField;
    private JTextField prenomField;
    private ma.oralCare.mvc.ui.components.JDatePicker dateNaissancePicker;
    private JComboBox<String> sexeCombo;
    private JTextField telField;
    private JTextField emailField;
    private JTextField cinField;
    private JTextField numeroField;
    private JTextField rueField;
    private JTextField codePostalField;
    private JTextField villeField;
    private JTextField paysField;
    private JTextField complementField;
    private JComboBox<String> assuranceCombo;
    private JTextField numeroAssuranceField;
    private JTextArea notesArea;
    
    // Services
    private ma.oralCare.mvc.controllers.patient.api.PatientController patientController;

    public PatientDialog(JFrame parent, String title, Patient existingPatient) {
        super(parent, title, true);
        this.existingPatient = existingPatient;
        this.patientController = getPatientController();
        
        initializeComponents();
        setupLayout();
        
        if (existingPatient != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise tous les composants du dialogue
     */
    private void initializeComponents() {
        // Champs texte
        nomField = new JTextField(20);
        prenomField = new JTextField(20);
        telField = new JTextField(15);
        emailField = new JTextField(25);
        cinField = new JTextField(15);
        numeroField = new JTextField(10);
        rueField = new JTextField(25);
        codePostalField = new JTextField(10);
        villeField = new JTextField(20);
        paysField = new JTextField(20);
        complementField = new JTextField(25);
        numeroAssuranceField = new JTextField(15);
        
        // Date picker
        dateNaissancePicker = new ma.oralCare.mvc.ui.components.JDatePicker(
            existingPatient != null && existingPatient.getDateDeNaissance() != null ? 
            existingPatient.getDateDeNaissance() : LocalDate.now().minusYears(25));
        
        // Combo boxes
        sexeCombo = new JComboBox<>(new String[]{"MASCULIN", "FEMININ"});
        assuranceCombo = new JComboBox<>(new String[]{"CNSS", "AMO", "RAMED", "PRIVE", "AUTRE"});
        
        // Zone de notes
        notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        // Boutons
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Gestionnaires d'événements
        okButton.addActionListener(e -> handleOK());
        cancelButton.addActionListener(e -> handleCancel());
        
        // Ajout au dialogue
        add(createMainPanel(), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        // La disposition est gérée dans initializeComponents()
    }

    /**
     * Crée le panel principal du formulaire
     */
    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Informations personnelles
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Nom*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nomField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Prénom*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(prenomField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date de naissance*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(dateNaissancePicker, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Sexe*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(sexeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("CIN*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(cinField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Téléphone*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(telField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(emailField, gbc);

        // Adresse
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Numéro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(numeroField, gbc);

        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Rue:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(rueField, gbc);

        gbc.gridx = 0; gbc.gridy = 9; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Code postal:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(codePostalField, gbc);

        gbc.gridx = 0; gbc.gridy = 10; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Ville:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(villeField, gbc);

        gbc.gridx = 0; gbc.gridy = 11; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Pays:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(paysField, gbc);

        gbc.gridx = 0; gbc.gridy = 12; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Complément:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(complementField, gbc);

        // Assurance
        gbc.gridx = 0; gbc.gridy = 13; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Assurance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(assuranceCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 14; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Numéro assurance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(numeroAssuranceField, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 15; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(new JScrollPane(notesArea), gbc);

        return panel;
    }

    /**
     * Remplit les champs avec les données du patient existant
     */
    private void populateFields() {
        if (existingPatient != null) {
            nomField.setText(existingPatient.getNom());
            prenomField.setText(existingPatient.getPrenom());
            dateNaissancePicker.setDate(existingPatient.getDateDeNaissance());
            sexeCombo.setSelectedItem(existingPatient.getSexe().name());
            telField.setText(existingPatient.getTelephone());
            emailField.setText(existingPatient.getEmail());
            
            // Note: Patient entity doesn't have these fields - they may need to be removed or handled differently
            // cinField.setText(existingPatient.getCin()); // Field doesn't exist
            // numeroField.setText(existingPatient.getNumero()); // Field doesn't exist
            // rueField.setText(existingPatient.getRue()); // Field doesn't exist
            // codePostalField.setText(existingPatient.getCodePostal()); // Field doesn't exist
            // villeField.setText(existingPatient.getVille()); // Field doesn't exist
            // paysField.setText(existingPatient.getPays()); // Field doesn't exist
            // complementField.setText(existingPatient.getComplement()); // Field doesn't exist
            
            assuranceCombo.setSelectedItem(existingPatient.getAssurance().name());
            // numeroAssuranceField.setText(existingPatient.getNumeroAssurance()); // Field doesn't exist
            // notesArea.setText(existingPatient.getNotes()); // Field doesn't exist
        }
    }

    /**
     * Valide le formulaire
     */
    private boolean validateForm() {
        if (nomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le nom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (prenomField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le prénom est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dateNaissancePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de naissance est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dateNaissancePicker.getDate().isAfter(LocalDate.now().minusYears(18))) {
            JOptionPane.showMessageDialog(this, "Le patient doit avoir au moins 18 ans", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (cinField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le CIN est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (telField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le téléphone est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Gère le clic sur OK
     */
    private void handleOK() {
        if (validateForm()) {
            confirmed = true;
            setVisible(false);
        }
    }

    /**
     * Gère le clic sur Annuler
     */
    private void handleCancel() {
        confirmed = false;
        setVisible(false);
    }

    /**
     * Affiche le dialogue et retourne le résultat
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    /**
     * Retourne la requête de création
     */
    public PatientCreateRequest getPatientCreateRequest() {
        PatientCreateRequest request = new PatientCreateRequest();
        request.setNom(nomField.getText().trim());
        request.setPrenom(prenomField.getText().trim());
        request.setDateNaissance(dateNaissancePicker.getDate());
        request.setSexe((String) sexeCombo.getSelectedItem());
        request.setTelephone(telField.getText().trim());
        request.setEmail(emailField.getText().trim());
        request.setCin(cinField.getText().trim());
        request.setNumero(numeroField.getText().trim());
        request.setRue(rueField.getText().trim());
        request.setCodePostal(codePostalField.getText().trim());
        request.setVille(villeField.getText().trim());
        request.setPays(paysField.getText().trim());
        request.setComplement(complementField.getText().trim());
        request.setAssurance((String) assuranceCombo.getSelectedItem());
        // Note: numeroAssurance et notes ne sont pas disponibles dans les DTOs actuels
        
        return request;
    }

    /**
     * Retourne la requête de mise à jour
     */
    public PatientUpdateRequest getPatientUpdateRequest() {
        PatientUpdateRequest request = new PatientUpdateRequest();
        request.setNom(nomField.getText().trim());
        request.setPrenom(prenomField.getText().trim());
        request.setDateNaissance(dateNaissancePicker.getDate());
        request.setSexe((String) sexeCombo.getSelectedItem());
        request.setTelephone(telField.getText().trim());
        request.setEmail(emailField.getText().trim());
        // Note: cin n'est pas disponible dans PatientUpdateRequest
        request.setNumero(numeroField.getText().trim());
        request.setRue(rueField.getText().trim());
        request.setCodePostal(codePostalField.getText().trim());
        request.setVille(villeField.getText().trim());
        request.setPays(paysField.getText().trim());
        request.setComplement(complementField.getText().trim());
        request.setAssurance((String) assuranceCombo.getSelectedItem());
        // Note: numeroAssurance et notes ne sont pas disponibles dans les DTOs actuels
        
        return request;
    }

    // Méthode utilitaire pour accéder au contrôleur (à implémenter selon votre architecture)
    private ma.oralCare.mvc.controllers.patient.api.PatientController getPatientController() {
        // Implémenter selon votre architecture
        return null;
    }
}
