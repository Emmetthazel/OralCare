package ma.oralCare.mvc.ui.dialogs;

import ma.oralCare.entities.dossierMedical.Facture;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Dialogue pour afficher les détails d'une facture
 */
public class FactureDetailsDialog extends JDialog {

    public FactureDetailsDialog(JFrame parent, Facture facture) {
        super(parent, "Détails de la Facture", true);
        
        initializeComponents(facture);
        setupLayout();
        
        pack();
        setLocationRelativeTo(parent);
    }

    /**
     * Initialise les composants avec les données de la facture
     */
    private void initializeComponents(Facture facture) {
        // Créer les labels avec les informations de la facture
        String[] labels = {
            "Numéro: " + facture.getNumero(),
            "Date: " + (facture.getDateFacture() != null ? 
                facture.getDateFacture().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"),
            "Patient: " + facture.getPatientNom(),
            "Total: " + (facture.getTotaleFacture() != null ? facture.getTotaleFacture() + " DH" : "0 DH"),
            "Payé: " + (facture.getTotalePaye() != null ? facture.getTotalePaye() + " DH" : "0 DH"),
            "Reste: " + ((facture.getTotaleFacture() != null && facture.getTotalePaye() != null ?
                    facture.getTotaleFacture().subtract(facture.getTotalePaye()) + " DH" : "0 DH")),
            "Statut: " + (facture.getStatut() != null ? facture.getStatut() : "N/A"),
            "Notes: " + "Aucune note" // Facture entity doesn't have notes field
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
