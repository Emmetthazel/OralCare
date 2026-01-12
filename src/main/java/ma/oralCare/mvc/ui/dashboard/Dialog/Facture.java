package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Facture extends JFrame {

    // Données provenant de la base de données (à remplacer par vos appels DAO)
    private Patient patient;
    private Consultation consultation;
    private List<Intervention> interventions;
    private SituationFinanciere situationFinanciere;

    // Composants Swing
    private JLabel labelNomPatient;
    private JLabel labelDateNaissance;
    private JLabel labelDossierMedical;
    private JLabel labelConsultation;
    private JLabel labelAssurance;
    private JLabel labelSituationFinanciere;
    private JLabel labelStatutFacture;
    private JLabel labelTotalFacture;
    private JLabel labelTotalPaye;
    private JLabel labelRestePayer;
    private JTable tableInterventions;
    private JTextArea observationsArea;
    private JCheckBox especesCheckBox;
    private JCheckBox carteCheckBox;
    private JCheckBox chequeCheckBox;

    // Formatters
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat factureDateFormat = new SimpleDateFormat("yyyyMMdd");

    public Facture(Patient patient, Consultation consultation,
                   List<Intervention> interventions, SituationFinanciere situationFinanciere) {
        this.patient = patient;
        this.consultation = consultation;
        this.interventions = interventions;
        this.situationFinanciere = situationFinanciere;

        initUI();
        calculerTotaux();
        calculerStatutFacture();
    }

    // Constructeur pour tests
    public Facture() {
        // Données de test
        this.patient = new Patient("RAHMANI Fatima", "15/03/1985",
                "DM-2023-0452", "CNOPS", "ACTIVE");
        this.consultation = new Consultation("CONS-2024-1287", new Date(), "*Contrôle et détartrage*");
        this.interventions = getInterventionsTest();
        this.situationFinanciere = new SituationFinanciere(400.00, new Date());

        initUI();
        calculerTotaux();
        calculerStatutFacture();
    }

    private void initUI() {
        setTitle("Facture - Cabinet Dentaire");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel principal avec marge
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Création des sections
        JPanel headerPanel = createHeaderPanel();
        JPanel patientPanel = createPatientPanel();
        JPanel libelleConsultationPanel = createLibelleConsultationPanel();
        JScrollPane tablePanel = createInterventionsTable();
        JPanel totauxPanel = createTotauxPanel();
        JPanel statutPanel = createStatutPanel();
        JPanel paiementPanel = createPaiementPanel();
        JPanel observationsPanel = createObservationsPanel();
        JPanel footerPanel = createFooterPanel();

        // Ajout des sections au mainPanel avec espacement réduit après libelleConsultationPanel
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(patientPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(libelleConsultationPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 2))); // Espacement minimal
        mainPanel.add(tablePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(totauxPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(statutPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(paiementPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(observationsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(footerPanel);

        // Ajout du défilement
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // Configuration de la fenêtre
        pack();
        setSize(850, 900); // Légèrement réduit car moins d'interventions
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Nom du cabinet
        JLabel cabinetLabel = new JLabel("CABINET DENTAIRE DR. BENALI");
        cabinetLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cabinetLabel.setForeground(new Color(0, 82, 155));
        cabinetLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adresse et contact
        JLabel adresseLabel = new JLabel("123 Avenue Hassan II, Casablanca, Maroc");
        JLabel contactLabel = new JLabel("Tél: +212 5 22 34 56 78 | Email: contact@cabinetdentaire.ma");
        JLabel siteLabel = new JLabel("www.cabinetdentaire.ma");

        adresseLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contactLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        siteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Séparateur
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(500, 2));
        separator.setForeground(new Color(0, 82, 155));

        // Titre FACTURE
        JLabel factureLabel = new JLabel("FACTURE");
        factureLabel.setFont(new Font("Arial", Font.BOLD, 28));
        factureLabel.setForeground(new Color(178, 34, 34));
        factureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Numéro et date de facture
        String dateFacture = dateFormat.format(new Date());
        String numFacture = "FAC-" + factureDateFormat.format(new Date()) + "-001";

        JPanel infoFacturePanel = new JPanel();
        infoFacturePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 40, 5));

        JLabel numLabel = new JLabel("N°: " + numFacture);
        JLabel dateLabel = new JLabel("Date: " + dateFacture);

        numLabel.setFont(new Font("Arial", Font.BOLD, 13));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 13));

        infoFacturePanel.add(numLabel);
        infoFacturePanel.add(dateLabel);

        headerPanel.add(cabinetLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(adresseLabel);
        headerPanel.add(contactLabel);
        headerPanel.add(siteLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(separator);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        headerPanel.add(factureLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoFacturePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(infoFacturePanel);

        return headerPanel;
    }

    private JPanel createPatientPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        // Panel principal des informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(6, 2, 10, 8));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Informations Patient",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Remplissage des informations
        infoPanel.add(createLabelBold("Nom et Prénom:"));
        labelNomPatient = new JLabel(patient.getNom());
        infoPanel.add(labelNomPatient);

        infoPanel.add(createLabelBold("Date de naissance:"));
        labelDateNaissance = new JLabel(patient.getDateNaissance());
        infoPanel.add(labelDateNaissance);

        infoPanel.add(createLabelBold("Dossier médical (ID):"));
        labelDossierMedical = new JLabel(patient.getDossierMedicalId());
        infoPanel.add(labelDossierMedical);

        infoPanel.add(createLabelBold("Consultation (ID):"));
        String consultationText = consultation.getId();
        labelConsultation = new JLabel(consultationText);
        infoPanel.add(labelConsultation);

        infoPanel.add(createLabelBold("Assurance:"));
        labelAssurance = new JLabel(patient.getAssurance());
        labelAssurance.setFont(new Font("Arial", Font.BOLD, 12));
        labelAssurance.setForeground(getCouleurAssurance(patient.getAssurance()));
        infoPanel.add(labelAssurance);

        infoPanel.add(createLabelBold("Situation financière:"));
        labelSituationFinanciere = new JLabel(getTexteSituationFinanciere(patient.getSituationFinanciere()));
        labelSituationFinanciere.setFont(new Font("Arial", Font.BOLD, 12));
        labelSituationFinanciere.setForeground(getCouleurSituationFinanciere(patient.getSituationFinanciere()));
        infoPanel.add(labelSituationFinanciere);

        container.add(infoPanel);

        // Note assurance
        if (!patient.getAssurance().equals("NONE")) {
            JLabel noteAssurance = new JLabel("Note: Fiche assurance à remplir pour remboursement si applicable");
            noteAssurance.setFont(new Font("Arial", Font.ITALIC, 11));
            noteAssurance.setForeground(Color.BLUE);
            noteAssurance.setAlignmentX(Component.LEFT_ALIGNMENT);
            container.add(Box.createRigidArea(new Dimension(0, 5)));
            container.add(noteAssurance);
        }

        return container;
    }

    private JPanel createLibelleConsultationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Consultation: ");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(0, 82, 155));

        JLabel descriptionLabel = new JLabel("*Contrôle et détartrage*");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 12));
        descriptionLabel.setForeground(Color.BLACK);

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    private JScrollPane createInterventionsTable() {
        // Colonnes du tableau
        String[] colonnes = {"#", "Intervention", "Dent", "Prix Final (MAD)"};

        // Préparation des données
        Object[][] donnees = new Object[interventions.size()][4];

        for (int i = 0; i < interventions.size(); i++) {
            Intervention interv = interventions.get(i);
            donnees[i][0] = i + 1;
            donnees[i][1] = interv.getNom();
            donnees[i][2] = interv.getDent() != null && !interv.getDent().isEmpty() ? interv.getDent() : "-";
            donnees[i][3] = String.format("%,.2f", interv.getPrixFinal());
        }

        // Création du tableau
        tableInterventions = new JTable(donnees, colonnes) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return String.class;
            }
        };

        // Configuration du style
        tableInterventions.setRowHeight(30);
        tableInterventions.setFont(new Font("Arial", Font.PLAIN, 12));
        tableInterventions.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tableInterventions.getTableHeader().setBackground(new Color(240, 240, 240));
        tableInterventions.setGridColor(new Color(200, 200, 200));
        tableInterventions.setShowGrid(true);
        tableInterventions.setIntercellSpacing(new Dimension(1, 1));

        // Ajustement des colonnes
        tableInterventions.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableInterventions.getColumnModel().getColumn(1).setPreferredWidth(320);
        tableInterventions.getColumnModel().getColumn(2).setPreferredWidth(80);
        tableInterventions.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Alignement à droite pour la colonne prix
        tableInterventions.getColumnModel().getColumn(3).setCellRenderer(new RightAlignmentRenderer());

        JScrollPane scrollPane = new JScrollPane(tableInterventions);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Interventions réalisées",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        return scrollPane;
    }

    // Renderer pour aligner le texte à droite
    private class RightAlignmentRenderer extends DefaultTableCellRenderer {
        public RightAlignmentRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            ((JLabel) c).setHorizontalAlignment(JLabel.RIGHT);

            if (column == 3) {
                c.setFont(new Font("Arial", Font.BOLD, 12));
                c.setForeground(new Color(0, 100, 0));
            }

            return c;
        }
    }

    private JPanel createTotauxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 15, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Totaux",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Calcul initial
        double totalFacture = calculerTotalFacture();
        double totalPaye = situationFinanciere.getTotalPaye();
        double reste = totalFacture - totalPaye;

        panel.add(createLabelBold("Totale Facture (MAD):"));
        labelTotalFacture = new JLabel(String.format("%,.2f", totalFacture));
        labelTotalFacture.setFont(new Font("Arial", Font.BOLD, 13));
        labelTotalFacture.setForeground(new Color(0, 100, 0));
        panel.add(labelTotalFacture);

        panel.add(createLabelBold("Totale Payée (MAD):"));
        labelTotalPaye = new JLabel(String.format("%,.2f", totalPaye));
        labelTotalPaye.setFont(new Font("Arial", Font.BOLD, 13));
        labelTotalPaye.setForeground(Color.BLUE);
        panel.add(labelTotalPaye);

        panel.add(createLabelBold("Reste à Payer (MAD):"));
        labelRestePayer = new JLabel(String.format("%,.2f", reste));
        labelRestePayer.setFont(new Font("Arial", Font.BOLD, 13));
        labelRestePayer.setForeground(reste > 0 ? new Color(178, 34, 34) : new Color(0, 100, 0));
        panel.add(labelRestePayer);

        return panel;
    }

    private JPanel createStatutPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Statut de la facture",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel("Statut: ");
        label.setFont(new Font("Arial", Font.BOLD, 13));

        labelStatutFacture = new JLabel("");
        labelStatutFacture.setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(label);
        panel.add(labelStatutFacture);

        return panel;
    }

    private JPanel createPaiementPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Mode de paiement",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        especesCheckBox = new JCheckBox("Espèces");
        carteCheckBox = new JCheckBox("Carte Bancaire");
        chequeCheckBox = new JCheckBox("Chèque");

        Font checkboxFont = new Font("Arial", Font.PLAIN, 12);
        especesCheckBox.setFont(checkboxFont);
        carteCheckBox.setFont(checkboxFont);
        chequeCheckBox.setFont(checkboxFont);

        panel.add(especesCheckBox);
        panel.add(carteCheckBox);
        panel.add(chequeCheckBox);

        return panel;
    }

    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 1),
                        "Observations / Notes",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 12),
                        new Color(0, 82, 155)
                ),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Texte par défaut
        String observationText = "Patient sous assurance " + patient.getAssurance() + ".\n";
        if (!patient.getAssurance().equals("NONE")) {
            observationText += "Fiche assurance à compléter pour remboursement.\n";
        }
        observationText += "\nConsultation: Contrôle et détartrage\n";
        observationText += "\nRendez-vous de contrôle dans 6 mois.";

        observationsArea = new JTextArea(observationText, 4, 40);
        observationsArea.setFont(new Font("Arial", Font.PLAIN, 12));
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(new JScrollPane(observationsArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Ligne de séparation
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(150, 150, 150));

        // Panel pour le texte centré
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Message de remerciement
        JLabel remerciement = new JLabel("Merci pour votre confiance !");
        remerciement.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        remerciement.setForeground(new Color(0, 82, 155));
        remerciement.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Mention légale
        JLabel mention = new JLabel("Facture établie conformément aux dispositions légales en vigueur");
        mention.setFont(new Font("Arial", Font.ITALIC, 10));
        mention.setForeground(Color.GRAY);
        mention.setAlignmentX(Component.CENTER_ALIGNMENT);

        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        textPanel.add(remerciement);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(mention);
        textPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(separator, BorderLayout.NORTH);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    private void calculerTotaux() {
        double totalFacture = calculerTotalFacture();
        double totalPaye = situationFinanciere.getTotalPaye();
        double reste = totalFacture - totalPaye;

        labelTotalFacture.setText(String.format("%,.2f", totalFacture));
        labelTotalPaye.setText(String.format("%,.2f", totalPaye));
        labelRestePayer.setText(String.format("%,.2f", reste));

        labelRestePayer.setForeground(reste > 0 ? new Color(178, 34, 34) : new Color(0, 100, 0));
    }

    private void calculerStatutFacture() {
        double totalFacture = calculerTotalFacture();
        double totalPaye = situationFinanciere.getTotalPaye();
        double reste = totalFacture - totalPaye;
        Date aujourdhui = new Date();

        String statut = "";
        Color couleur = Color.BLACK;

        if (reste == 0) {
            statut = "PAYÉE";
            couleur = new Color(0, 100, 0);
        } else if (reste > 0) {
            if (aujourdhui.after(situationFinanciere.getDateLimite())) {
                statut = "EN RETARD";
                couleur = new Color(178, 34, 34);
            } else {
                statut = "EN ATTENTE";
                couleur = new Color(255, 140, 0);
            }
        }

        labelStatutFacture.setText(statut);
        labelStatutFacture.setForeground(couleur);
    }

    private double calculerTotalFacture() {
        double total = 0.0;
        for (Intervention interv : interventions) {
            total += interv.getPrixFinal();
        }
        return total;
    }

    private void imprimerFacture() {
        try {
            boolean complete = tableInterventions.print();
            if (complete) {
                JOptionPane.showMessageDialog(this,
                        "Facture imprimée avec succès",
                        "Impression",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impression annulée",
                        "Impression",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (PrinterException pe) {
            JOptionPane.showMessageDialog(this,
                    "Erreur d'impression: " + pe.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enregistrerFacture() {
        JOptionPane.showMessageDialog(this,
                "Facture enregistrée avec succès",
                "Enregistrement",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Méthodes utilitaires
    private JLabel createLabelBold(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private Color getCouleurAssurance(String assurance) {
        switch(assurance.toUpperCase()) {
            case "CNOPS": return new Color(0, 82, 155);
            case "CNSS": return new Color(139, 0, 0);
            case "RAMED": return new Color(0, 100, 0);
            case "NONE": return Color.BLACK;
            default: return Color.BLACK;
        }
    }

    private String getTexteSituationFinanciere(String situation) {
        switch(situation.toUpperCase()) {
            case "ACTIVE": return "En cours de traitement";
            case "ARCHIVED": return "Archivée";
            case "CLOSED": return "Clôturée / Payée";
            default: return situation;
        }
    }

    private Color getCouleurSituationFinanciere(String situation) {
        switch(situation.toUpperCase()) {
            case "ACTIVE": return new Color(0, 82, 155);
            case "ARCHIVED": return Color.GRAY;
            case "CLOSED": return new Color(0, 100, 0);
            default: return Color.BLACK;
        }
    }

    // Données de test
    private List<Intervention> getInterventionsTest() {
        List<Intervention> interventions = new ArrayList<>();
        interventions.add(new Intervention("Détartrage complet", null, 350.00, 350.00));
        interventions.add(new Intervention("Composite dent 25", "25", 600.00, 600.00));
        interventions.add(new Intervention("Radio panoramique", null, 250.00, 250.00));
        // Pas de "Consultation contrôle" - elle apparaît uniquement dans le libellé
        return interventions;
    }

    // Classes de données
    public static class Patient {
        private String nom;
        private String dateNaissance;
        private String dossierMedicalId;
        private String assurance;
        private String situationFinanciere;

        public Patient(String nom, String dateNaissance, String dossierMedicalId,
                       String assurance, String situationFinanciere) {
            this.nom = nom;
            this.dateNaissance = dateNaissance;
            this.dossierMedicalId = dossierMedicalId;
            this.assurance = assurance;
            this.situationFinanciere = situationFinanciere;
        }

        public String getNom() { return nom; }
        public String getDateNaissance() { return dateNaissance; }
        public String getDossierMedicalId() { return dossierMedicalId; }
        public String getAssurance() { return assurance; }
        public String getSituationFinanciere() { return situationFinanciere; }
    }

    public static class Consultation {
        private String id;
        private Date date;
        private String description;

        public Consultation(String id, Date date, String description) {
            this.id = id;
            this.date = date;
            this.description = description;
        }

        public String getId() { return id; }
        public Date getDate() { return date; }
        public String getDescription() { return description; }
    }

    public static class Intervention {
        private String nom;
        private String dent;
        private double prixDeBase;
        private double prixDePatient;

        public Intervention(String nom, String dent, double prixDeBase,
                            double prixDePatient) {
            this.nom = nom;
            this.dent = dent;
            this.prixDeBase = prixDeBase;
            this.prixDePatient = prixDePatient;
        }

        public String getNom() { return nom; }
        public String getDent() { return dent; }
        public double getPrixFinal() {
            return prixDePatient > 0 ? prixDePatient : prixDeBase;
        }
    }

    public static class SituationFinanciere {
        private double totalPaye;
        private Date dateLimite;

        public SituationFinanciere(double totalPaye, Date dateLimite) {
            this.totalPaye = totalPaye;
            this.dateLimite = dateLimite;
        }

        public double getTotalPaye() { return totalPaye; }
        public Date getDateLimite() { return dateLimite; }
    }

    // Méthode main pour tester
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Facture();
        });
    }
}