package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Dialogue pour l'enregistrement des paiements
 */
public class PaiementDialog extends JDialog {

    private boolean confirmed = false;
    private Facture facture;
    
    // Champs du formulaire
    private JTextField montantField;
    private JComboBox<String> modePaiementCombo;
    private ma.oralCare.mvc.ui.components.JDatePicker datePaiementPicker;
    private JTextField referenceField;
    private JTextArea notesArea;
    private JLabel resteLabel; // Store reference to the reste label
    
    // Services
    private ma.oralCare.mvc.controllers.facture.api.FactureController factureController;

    public PaiementDialog(JFrame parent, Facture facture) {
        super(parent, "Enregistrer un paiement", true);
        this.facture = facture;
        this.factureController = getFactureController();
        
        initializeComponents();
        setupLayout();
        calculateResteDu();
    }

    /**
     * Initialise tous les composants du dialogue
     */
    private void initializeComponents() {
        // Champs texte
        montantField = new JTextField(15);
        referenceField = new JTextField(20);
        
        // Date picker
        datePaiementPicker = new ma.oralCare.mvc.ui.components.JDatePicker(LocalDate.now());
        
        // Combo boxes
        modePaiementCombo = new JComboBox<>(new String[]{"ESPECES", "CARTE_BANCAIRE", "CHEQUE", "VIREMENT", "AUTRE"});
        
        // Zone de notes
        notesArea = new JTextArea(3, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        // Labels d'information
        JLabel patientLabel = new JLabel("Patient: " + (facture.getSituationFinanciere() != null && 
            facture.getSituationFinanciere().getDossierMedicale() != null && 
            facture.getSituationFinanciere().getDossierMedicale().getPatient() != null ? 
            facture.getSituationFinanciere().getDossierMedicale().getPatient().getNom() + " " + 
            facture.getSituationFinanciere().getDossierMedicale().getPatient().getPrenom() : ""));
        JLabel numeroFactureLabel = new JLabel("Facture: #" + (facture.getIdEntite() != null ? facture.getIdEntite() : ""));
        JLabel totalFactureLabel = new JLabel("Total: " + (facture.getTotaleFacture() != null ? facture.getTotaleFacture() + " DH" : "0 DH"));
        JLabel totalPayeLabel = new JLabel("Déjà payé: " + (facture.getTotalePaye() != null ? facture.getTotalePaye() + " DH" : "0 DH"));
        JLabel resteLabel = new JLabel("Reste à payer: 0 DH");
        this.resteLabel = resteLabel; // Store reference
        
        // Boutons
        JButton okButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        // Gestionnaires d'événements
        okButton.addActionListener(e -> handleOK());
        cancelButton.addActionListener(e -> handleCancel());
        
        // Panel d'information
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.add(patientLabel);
        infoPanel.add(numeroFactureLabel);
        infoPanel.add(totalFactureLabel);
        infoPanel.add(totalPayeLabel);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Ajouter le panel d'information
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(infoPanel, gbc);

        // Montant
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Montant*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(montantField, gbc);

        // Mode de paiement
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Mode de paiement*:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(modePaiementCombo, gbc);

        // Date de paiement
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Date paiement:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(datePaiementPicker, gbc);

        // Référence
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Référence:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(referenceField, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        mainPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(notesArea), gbc);

        // Reste à payer
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        mainPanel.add(resteLabel, gbc);

        // Ajout au dialogue
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        // La disposition est gérée dans initializeComponents()
    }

    /**
     * Calcule le reste à payer
     */
    private void calculateResteDu() {
        BigDecimal total = facture.getTotaleFacture() != null ? facture.getTotaleFacture() : BigDecimal.ZERO;
        BigDecimal paye = facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO;
        BigDecimal reste = total.subtract(paye);
        
        // Mettre à jour le label du reste
        resteLabel.setText("Reste à payer: " + reste + " DH");
        
        // Limiter le montant maximum au reste
        montantField.setText(reste.toString());
    }

    /**
     * Valide le formulaire
     */
    private boolean validateForm() {
        try {
            BigDecimal montant = new BigDecimal(montantField.getText().trim());
            BigDecimal total = facture.getTotaleFacture() != null ? facture.getTotaleFacture() : BigDecimal.ZERO;
            BigDecimal paye = facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO;
            BigDecimal reste = total.subtract(paye);
            
            if (montant.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Le montant doit être positif", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (montant.compareTo(reste) > 0) {
                JOptionPane.showMessageDialog(this, "Le montant ne peut pas dépasser le reste à payer (" + reste + " DH)", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le format du montant est invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
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
     * Retourne la requête de paiement
     */
    public PaiementRequest getPaiementRequest() {
        PaiementRequest request = new PaiementRequest();
        request.setMontant(new BigDecimal(montantField.getText().trim()));
        request.setModePaiement((String) modePaiementCombo.getSelectedItem());
        request.setDatePaiement(datePaiementPicker.getDate());
        request.setReference(referenceField.getText().trim());
        request.setNotes(notesArea.getText().trim());
        
        return request;
    }

    // Méthode utilitaire pour accéder au contrôleur (à implémenter selon votre architecture)
    private ma.oralCare.mvc.controllers.facture.api.FactureController getFactureController() {
        // Implémenter selon votre architecture
        return null;
    }
}
