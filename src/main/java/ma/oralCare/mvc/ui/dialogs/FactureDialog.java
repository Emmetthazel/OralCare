package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dialogue pour l'ajout et la modification des factures
 */
public class FactureDialog extends JDialog {

    private boolean confirmed = false;
    private Facture existingFacture;
    
    // Champs du formulaire
    private JComboBox<Long> patientCombo;
    private JTextField numeroField;
    private ma.oralCare.mvc.ui.components.JDatePicker dateFacturePicker;
    private JTextField totalField;
    private JComboBox<String> statutCombo;
    private JTextArea notesArea;
    
    // Services
    private ma.oralCare.mvc.controllers.facture.api.FactureController factureController;

    public FactureDialog(JFrame parent, String title, Facture existingFacture, ma.oralCare.mvc.controllers.facture.api.FactureController factureController) {
        super(parent, title, true);
        this.existingFacture = existingFacture;
        this.factureController = factureController;
        
        initializeComponents();
        setupLayout();
        
        if (existingFacture != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise tous les composants du dialogue
     */
    private void initializeComponents() {
        // Combo box des patients
        patientCombo = new JComboBox<>();
        loadPatients();
        
        // Champs texte
        numeroField = new JTextField(20);
        totalField = new JTextField(15);
        
        // Date picker
        dateFacturePicker = new ma.oralCare.mvc.ui.components.JDatePicker(
            existingFacture != null ? existingFacture.getDateFacture().toLocalDate() : LocalDate.now());
        
        // Combo box du statut
        statutCombo = new JComboBox<>(new String[]{"EN_ATTENTE", "VALIDEE", "PAYEE", "PARTIELLEMENT_PAYEE", "ANNULEE"});
        
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

        // Patient
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(patientCombo, gbc);

        // Numéro
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Numéro:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(numeroField, gbc);

        // Date de facture
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(dateFacturePicker, gbc);

        // Total
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Total*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(totalField, gbc);

        // Statut
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(statutCombo, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(new JScrollPane(notesArea), gbc);

        return panel;
    }

    /**
     * Charge la liste des patients
     */
    private void loadPatients() {
        try {
            List<Patient> patients = getPatients();
            
            patientCombo.removeAllItems();
            for (Patient patient : patients) {
                patientCombo.addItem(patient.getIdEntite());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des patients: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remplit les champs avec les données de la facture existante
     */
    private void populateFields() {
        if (existingFacture != null) {
            // Get patient ID from situationFinanciere
            if (existingFacture.getSituationFinanciere() != null && existingFacture.getSituationFinanciere().getDossierMedicale() != null && existingFacture.getSituationFinanciere().getDossierMedicale().getPatient() != null) {
                patientCombo.setSelectedItem(existingFacture.getSituationFinanciere().getDossierMedicale().getPatient().getIdEntite());
            }
            
            // Numero field doesn't exist in entity, use ID as fallback
            numeroField.setText("#" + existingFacture.getIdEntite());
            
            // Convert LocalDateTime to LocalDate
            if (existingFacture.getDateFacture() != null) {
                dateFacturePicker.setDate(existingFacture.getDateFacture().toLocalDate());
            }
            
            totalField.setText(existingFacture.getTotaleFacture().toString());
            
            // Statut is already a string thanks to getStatut() method
            if (existingFacture.getStatut() != null) {
                statutCombo.setSelectedItem(existingFacture.getStatut());
            }
            
            // Notes field doesn't exist in entity
            notesArea.setText("");
        }
    }

    /**
     * Valide le formulaire
     */
    private boolean validateForm() {
        if (patientCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un patient", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (dateFacturePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date de facture est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            BigDecimal total = new BigDecimal(totalField.getText().trim());
            if (total.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Le total doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le format du total est invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
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
    public FactureCreateRequest getFactureCreateRequest() {
        FactureCreateRequest request = new FactureCreateRequest();
        request.setPatientId((Long) patientCombo.getSelectedItem());
        request.setNumero(numeroField.getText().trim());
        request.setDateFacture(dateFacturePicker.getDate());
        request.setTotaleFacture(new BigDecimal(totalField.getText().trim()));
        request.setStatut((String) statutCombo.getSelectedItem());
        request.setNotes(notesArea.getText().trim());
        
        return request;
    }

    /**
     * Retourne la requête de mise à jour
     */
    public FactureUpdateRequest getFactureUpdateRequest() {
        FactureUpdateRequest request = new FactureUpdateRequest();
        request.setNumero(numeroField.getText().trim());
        request.setDateFacture(dateFacturePicker.getDate());
        request.setTotaleFacture(new BigDecimal(totalField.getText().trim()));
        request.setStatut((String) statutCombo.getSelectedItem());
        request.setNotes(notesArea.getText().trim());
        
        return request;
    }

    /**
     * Retourne la liste des patients
     */
    private List<Patient> getPatients() {
        try {
            return factureController.getPatients();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des patients: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
            return List.of();
        }
    }
}
