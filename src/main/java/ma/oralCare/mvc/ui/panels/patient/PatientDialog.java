package ma.oralCare.mvc.ui.panels.patient;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.ui.components.JDatePicker;
import ma.oralCare.service.modules.patient.dto.PatientCreateRequest;
import ma.oralCare.service.modules.patient.dto.PatientUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

/**
 * Dialogue pour l'ajout et la modification des patients
 */
public class PatientDialog extends JDialog {

    private boolean confirmed = false;
    private Patient existingPatient;
    
    // Champs du formulaire
    private JTextField nomField;
    private JTextField prenomField;
    private JDatePicker dateNaissancePicker;
    private JComboBox<String> sexeCombo;
    private JTextField telephoneField;
    private JTextField emailField;
    private JComboBox<String> assuranceCombo;
    
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
        nomField = new JTextField(30);
        prenomField = new JTextField(30);
        telephoneField = new JTextField(20);
        emailField = new JTextField(30);
        
        // Date de naissance
        dateNaissancePicker = new JDatePicker(existingPatient != null ? existingPatient.getDateDeNaissance() : LocalDate.now().minusYears(30));
        
        // Combo boxes
        sexeCombo = new JComboBox<>(new String[]{"MASCULIN", "FEMININ"});
        assuranceCombo = new JComboBox<>(new String[]{"CNSS", "AMO", "RAMED", "Prive", "Autre"});
        
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
        panel.add(new JLabel("Téléphone*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(telephoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Assurance:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(assuranceCombo, gbc);

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
            sexeCombo.setSelectedItem(existingPatient.getSexe().toString());
            telephoneField.setText(existingPatient.getTelephone());
            emailField.setText(existingPatient.getEmail());
            
            if (existingPatient.getAssurance() != null) {
                assuranceCombo.setSelectedItem(existingPatient.getAssurance().name());
            }
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
        
        if (dateNaissancePicker.getDate().isAfter(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "La date de naissance ne peut pas être dans le futur", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (telephoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le téléphone est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!isValidPhoneNumber(telephoneField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Le format du téléphone est invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (emailField.getText().trim().length() > 0 && !isValidEmail(emailField.getText().trim())) {
            JOptionPane.showMessageDialog(this, "Le format de l'email est invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    /**
     * Vérifie si un numéro de téléphone est valide
     */
    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^[0-9]{10}$") || phone.matches("^[0-9]{9}$");
    }

    /**
     * Vérifie si un email est valide
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
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
        request.setTelephone(telephoneField.getText().trim());
        request.setEmail(emailField.getText().trim());
        request.setAssurance((String) assuranceCombo.getSelectedItem());
        
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
        request.setTelephone(telephoneField.getText().trim());
        request.setEmail(emailField.getText().trim());
        request.setAssurance((String) assuranceCombo.getSelectedItem());
        
        return request;
    }

    // Méthode utilitaire pour accéder au contrôleur (à implémenter selon votre architecture)
    private ma.oralCare.mvc.controllers.patient.api.PatientController getPatientController() {
        // Implémenter selon votre architecture
        return null;
    }
}
