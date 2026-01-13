package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.patient.Patient;
import javax.swing.*;
import java.awt.*;

/**
 * Dialogue pour afficher les détails d'un patient
 */
public class PatientDetailsDialog extends JDialog {

    public PatientDetailsDialog(JFrame parent, Patient patient) {
        super(parent, "Détails du Patient", true);
        
        initializeComponents(patient);
        setupLayout();
        
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise les composants avec les données du patient
     */
    private void initializeComponents(Patient patient) {
        // Créer les labels avec les informations du patient
        String[] labels = {
            "ID: " + (patient.getIdEntite() != null ? patient.getIdEntite() : "N/A"),
            "Nom: " + (patient.getNom() != null ? patient.getNom() : "N/A"),
            "Prénom: " + (patient.getPrenom() != null ? patient.getPrenom() : "N/A"),
            "Date de naissance: " + (patient.getDateDeNaissance() != null ? patient.getDateDeNaissance().toString() : "N/A"),
            "Sexe: " + (patient.getSexe() != null ? patient.getSexe().name() : "N/A"),
            "Téléphone: " + (patient.getTelephone() != null ? patient.getTelephone() : "N/A"),
            "Email: " + (patient.getEmail() != null ? patient.getEmail() : "N/A"),
            "Assurance: " + (patient.getAssurance() != null ? patient.getAssurance().name() : "N/A")
        };

        JPanel panel = new JPanel(new GridLayout(labels.length, 1, 5, 5));
        for (String label : labels) {
            panel.add(new JLabel(label));
        }

        // Bouton de fermeture
        JButton fermerButton = new JButton("Fermer");
        fermerButton.addActionListener(e -> setVisible(false));

        add(panel, BorderLayout.CENTER);
        add(fermerButton, BorderLayout.SOUTH);
    }

    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        // La disposition est gérée dans initializeComponents()
    }
}
