package ma.oralCare.mvc.ui.medecin;

import javax.swing.*;
import java.awt.*;

public class PatientFolderFrame extends JDialog {

    public PatientFolderFrame(Frame parent, String patientName) {
        super(parent, "Dossier Médical - " + patientName, true);
        setSize(1100, 700);
        setLocationRelativeTo(parent);

        JTabbedPane tabs = new JTabbedPane();

        // Onglet 1 : Infos Générales & Antécédents
        tabs.addTab("Fiche Patient", createGeneralInfoPanel());

        // Onglet 2 : Historique Clinique (Consultations passées)
        tabs.addTab("Historique des Soins", new PatientHistoryPanel());

        // Onglet 3 : Imagerie
        tabs.addTab("Radiologie", new ma.oralCare.mvc.ui.medecin.components.RadiologiePanel());

        add(tabs);
    }

    private JPanel createGeneralInfoPanel() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Colonne gauche : État civil
        JPanel infoCivil = new JPanel(new GridLayout(0, 1));
        infoCivil.add(new JLabel("NOM : Ahmed Benani"));
        infoCivil.add(new JLabel("CIN : AB12345"));
        infoCivil.add(new JLabel("Tél : 0661223344"));
        infoCivil.setBorder(BorderFactory.createTitledBorder("Informations"));

        // Colonne droite : Alertes Médicales
        JPanel alerts = new JPanel();
        alerts.setBackground(new Color(255, 235, 238));
        alerts.add(new JLabel("⚠️ ALLERGIE PÉNICILLINE"));
        alerts.setBorder(BorderFactory.createTitledBorder("Alertes Critiques"));

        p.add(infoCivil);
        p.add(alerts);
        return p;
    }
}