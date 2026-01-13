package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.mvc.ui.components.JDatePicker;
import ma.oralCare.service.modules.RDV.dto.RDVCreateRequest;
import ma.oralCare.service.modules.RDV.dto.RDVUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Dialogue pour l'ajout et la modification des rendez-vous
 */
public class RDVDialog extends JDialog {

    private boolean confirmed = false;
    private RDV existingRDV;
    
    // Champs du formulaire
    private JComboBox<Long> patientCombo;
    private JComboBox<Long> medecinCombo;
    private ma.oralCare.mvc.ui.components.JDatePicker datePicker;
    private JTextField timeField;
    private JTextField motifField;
    private JTextArea notesArea;
    private JComboBox<String> statutCombo;
    
    // Services
    private ma.oralCare.mvc.controllers.RDV.api.RDVController rdvController;

    public RDVDialog(JFrame parent, String title, RDV existingRDV) {
        super(parent, title, true);
        this.existingRDV = existingRDV;
        this.rdvController = getRDVController();
        
        initializeComponents();
        setupLayout();
        
        if (existingRDV != null) {
            populateFields();
        }
        
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise tous les composants du dialogue
     */
    private void initializeComponents() {
        // Combo boxes
        patientCombo = new JComboBox<>();
        medecinCombo = new JComboBox<>();
        loadPatients();
        loadMedecins();
        
        // Champs texte
        timeField = new JTextField(10);
        motifField = new JTextField(30);
        
        // Date picker
        datePicker = new JDatePicker(existingRDV != null ? existingRDV.getDate() : LocalDate.now());
        
        // Combo box du statut
        statutCombo = new JComboBox<>(new String[]{"PLANIFIE", "CONFIRME", "ANNULE", "EN_COURS", "TERMINE"});
        
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

        // Médecin
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Médecin*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(medecinCombo, gbc);

        // Date
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Date*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(datePicker, gbc);

        // Heure
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Heure*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(timeField, gbc);

        // Motif
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Motif*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(motifField, gbc);

        // Statut
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(statutCombo, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
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
     * Charge la liste des médecins
     */
    private void loadMedecins() {
        try {
            List<Medecin> medecins = getMedecins();
            
            medecinCombo.removeAllItems();
            for (Medecin medecin : medecins) {
                medecinCombo.addItem(medecin.getIdEntite());
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des médecins: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remplit les champs avec les données du RDV existant
     */
    private void populateFields() {
        if (existingRDV != null) {
            // Get patient ID from dossierMedicale
            if (existingRDV.getDossierMedicale() != null && existingRDV.getDossierMedicale().getPatient() != null) {
                patientCombo.setSelectedItem(existingRDV.getDossierMedicale().getPatient().getIdEntite());
            }
            
            // Get medecin from consultation or use a default/placeholder
            // Note: RDV entity doesn't have direct medecin ID, this needs to be implemented based on your business logic
            // For now, we'll skip medecin selection
            
            datePicker.setDate(existingRDV.getDate());
            timeField.setText(existingRDV.getHeure().toString());
            motifField.setText(existingRDV.getMotif());
            statutCombo.setSelectedItem(existingRDV.getStatut().name());
            notesArea.setText(existingRDV.getNoteMedecin());
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
        
        if (medecinCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (datePicker.getDate() == null) {
            JOptionPane.showMessageDialog(this, "La date est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (timeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'heure est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (motifField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le motif est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            LocalTime.parse(timeField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Le format de l'heure est invalide (HH:mm)", "Erreur", JOptionPane.ERROR_MESSAGE);
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
    public RDVCreateRequest getRDVCreateRequest() {
        RDVCreateRequest request = new RDVCreateRequest();
        request.setPatientId((Long) patientCombo.getSelectedItem());
        request.setMedecinId((Long) medecinCombo.getSelectedItem());
        request.setDate(datePicker.getDate());
        request.setHeureDebut(LocalTime.parse(timeField.getText().trim()));
        request.setHeureFin(LocalTime.parse(timeField.getText().trim()).plusMinutes(30));
        request.setMotif(motifField.getText().trim());
        request.setNotes(notesArea.getText().trim());
        request.setStatut((String) statutCombo.getSelectedItem());
        
        return request;
    }

    /**
     * Retourne la requête de mise à jour
     */
    public RDVUpdateRequest getRDVUpdateRequest() {
        RDVUpdateRequest request = new RDVUpdateRequest();
        request.setPatientId((Long) patientCombo.getSelectedItem());
        request.setMedecinId((Long) medecinCombo.getSelectedItem());
        request.setDate(datePicker.getDate());
        request.setHeureDebut(LocalTime.parse(timeField.getText().trim()));
        request.setHeureFin(LocalTime.parse(timeField.getText().trim()).plusMinutes(30));
        request.setMotif(motifField.getText().trim());
        request.setNotes(notesArea.getText().trim());
        request.setStatut((String) statutCombo.getSelectedItem());
        
        return request;
    }

    // Méthodes utilitaires pour accéder aux services (à implémenter selon votre architecture)
    private ma.oralCare.mvc.controllers.RDV.api.RDVController getRDVController() {
        // Implémenter selon votre architecture
        return null;
    }
    
    private List<Patient> getPatients() {
        // Implémenter selon votre architecture
        return List.of();
    }
    
    private List<Medecin> getMedecins() {
        // Implémenter selon votre architecture
        return List.of();
    }
}
