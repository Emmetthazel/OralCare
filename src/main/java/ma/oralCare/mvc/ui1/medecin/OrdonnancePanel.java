package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrdonnancePanel extends JPanel {

    private Long patientId;
    private JTable tableOrdonnances;
    private JTable tablePrescriptions;
    private DefaultTableModel modelOrdonnances;
    private DefaultTableModel modelPrescriptions;

    // Labels de détails pour mise à jour dynamique
    private JLabel lblDateEmission, lblConsultation, lblNotes;

    public OrdonnancePanel(Long patientId) {
        this.patientId = patientId;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initComponents();
        if (patientId != null) {
            loadPatientData(patientId);
        }
    }

    private void initComponents() {
        // --- 1. BARRE DE RECHERCHE INTERNE ---
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.setOpaque(false);
        searchBar.add(new JLabel("Filtrer historique : "));
        searchBar.add(new JTextField(20));
        add(searchBar, BorderLayout.NORTH);

        // --- 2. CONTENU (DIVISÉ EN DEUX) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(180);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);

        // A. TABLEAU PRINCIPAL
        String[] colsMain = {"Date Ordonnance", "Consultation (Motif)", "Nb Médicaments", "Résumé"};
        modelOrdonnances = new DefaultTableModel(colsMain, 0);
        tableOrdonnances = new JTable(modelOrdonnances);

        JScrollPane scrollMain = new JScrollPane(tableOrdonnances);
        scrollMain.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(new Color(200, 200, 200)), "Historique des Ordonnances"));

        // B. ZONE DE DÉTAILS
        JPanel detailContainer = new JPanel(new BorderLayout(5, 5));
        detailContainer.setOpaque(false);
        detailContainer.setBorder(BorderFactory.createTitledBorder("Détails de l'ordonnance sélectionnée"));

        // Infos textuelles
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        lblDateEmission = new JLabel("Date émission : - ");
        lblConsultation = new JLabel("Consultation : - ");
        lblNotes = new JLabel("Notes : - ");

        infoPanel.add(lblDateEmission);
        infoPanel.add(lblConsultation);
        infoPanel.add(lblNotes);

        detailContainer.add(infoPanel, BorderLayout.NORTH);

        // Tableau des prescriptions
        String[] colsPresc = {"Médicament", "Dosage", "Durée", "Fréquence"};
        modelPrescriptions = new DefaultTableModel(colsPresc, 0);
        tablePrescriptions = new JTable(modelPrescriptions);

        JScrollPane scrollPresc = new JScrollPane(tablePrescriptions);
        detailContainer.add(scrollPresc, BorderLayout.CENTER);

        // Assemblage
        splitPane.setTopComponent(scrollMain);
        splitPane.setBottomComponent(detailContainer);
        add(splitPane, BorderLayout.CENTER);

        // Événement de sélection
        tableOrdonnances.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetails();
            }
        });
    }

    /**
     * Appelé par DossierMedicalMedecinPanel lors d'un changement de patient
     */
    public void loadPatientData(Long patientId) {
        this.patientId = patientId;
        modelOrdonnances.setRowCount(0);
        modelPrescriptions.setRowCount(0);

        if (patientId == null) return;

        // Les données seront récupérées depuis la base de données via le controller/service
    }

    private void updateDetails() {
        int row = tableOrdonnances.getSelectedRow();
        if (row != -1) {
            String date = tableOrdonnances.getValueAt(row, 0).toString();
            String motif = tableOrdonnances.getValueAt(row, 1).toString();

            lblDateEmission.setText("Date émission : " + date);
            lblConsultation.setText("Consultation : " + motif);
            lblNotes.setText("Notes : -");

            // Les prescriptions détaillées seront récupérées depuis la base de données via le controller/service
            modelPrescriptions.setRowCount(0);
        }
    }
}