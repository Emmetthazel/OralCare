package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;

public class AddRDVDialog extends JDialog {
    // --- CONFIGURATION ---
    private final int HEURE_DEBUT = 7;
    private final int HEURE_FIN = 19;
    private final int HAUTEUR_HEURE = 60;
    private final Color BLEU_SELECTION = new Color(26, 115, 232, 200);
    private final Color ROUGE_OCCUPE = new Color(211, 47, 47, 180);

    private Map<String, Patient> patientsTest;
    private Map<String, List<Consultation>> consultationsParPatient;
    private Map<Integer, List<Intervention>> interventionsParConsultation;
    private List<TimeSlot> agendaTest;

    private JComboBox<String> patientCombo, consultationCombo, interventionCombo;
    private JTextField dureeField, dateField, heureField;
    private JTextArea noteSecretariat, resumeArea;
    private JLabel remarqueLabel, semaineLabel;
    private JLayeredPane layeredPaneAgenda;
    private JPanel blocSelectionActuel = null;

    private Patient patientSelectionne;
    private Intervention interventionSelectionnee;
    private LocalDate dateDebutSemaine, dateSelectionnee;
    private LocalTime heureSelectionnee;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public AddRDVDialog(JFrame parent) {
        super(parent, "Création de Rendez-Vous - Cabinet Dentaire", true);
        initialiserDonneesTest();
        setupUI();
        chargerSemaineCourante();
    }

    private void initialiserDonneesTest() {
        patientsTest = new LinkedHashMap<>();
        patientsTest.put("Ahmed Alami", new Patient("P-101", "Ahmed Alami", false, "Consultation #124 en cours"));
        patientsTest.put("Marie Dupont", new Patient("P-102", "Marie Dupont", false, "Suivi orthodontie"));
        patientsTest.put("Nouveau Patient", new Patient("P-000", "Nouveau Patient", true, "Nouveau dossier"));

        consultationsParPatient = new HashMap<>();
        consultationsParPatient.put("P-101", Arrays.asList(new Consultation(124, "Traitement carie", true)));
        interventionsParConsultation = new HashMap<>();
        interventionsParConsultation.put(124, Arrays.asList(new Intervention("Détartrage", 30), new Intervention("Extraction", 45)));
        agendaTest = new ArrayList<>();
        dateDebutSemaine = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
    }

    private void setupUI() {
        setSize(1250, 850);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- FORMULAIRE & RÉSUMÉ ---
        JPanel topPanel = new JPanel(new BorderLayout(15, 0));
        topPanel.setPreferredSize(new Dimension(0, 380));

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(new TitledBorder("Détails du Rendez-vous"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 2, 10); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;

        patientCombo = new JComboBox<>(patientsTest.keySet().toArray(new String[0]));
        patientCombo.addActionListener(e -> onPatientSelectionne());
        leftPanel.add(new JLabel("Patient:"), gbc);
        gbc.gridy = 1; leftPanel.add(patientCombo, gbc);

        remarqueLabel = new JLabel(" ");
        remarqueLabel.setForeground(new Color(180, 0, 0));
        gbc.gridy = 2; leftPanel.add(remarqueLabel, gbc);

        consultationCombo = new JComboBox<>();
        consultationCombo.addActionListener(e -> onConsultationSelectionnee());
        gbc.gridy = 3; leftPanel.add(new JLabel("Consultation:"), gbc);
        gbc.gridy = 4; leftPanel.add(consultationCombo, gbc);

        interventionCombo = new JComboBox<>();
        interventionCombo.addActionListener(e -> onInterventionSelectionnee());
        gbc.gridy = 5; leftPanel.add(new JLabel("Intervention:"), gbc);
        gbc.gridy = 6; leftPanel.add(interventionCombo, gbc);

        // --- ZONE HEURE MODIFIABLE ---
        JPanel pTime = new JPanel(new GridLayout(1, 3, 5, 0));
        dureeField = createField("Durée (min)", false);
        dateField = createField("Date", false);

        // On rend l'heure éditable pour ton besoin
        heureField = createField("Heure (HH:mm)", true);
        heureField.setToolTipText("Exemple: 10:30");
        ajouterEcouteurHeure(); // <--- LA LOGIQUE DE SYNCHRO

        pTime.add(dureeField); pTime.add(dateField); pTime.add(heureField);
        gbc.gridy = 7; leftPanel.add(new JLabel("Timing (Cliquez agenda OU tapez l'heure) :"), gbc);
        gbc.gridy = 8; leftPanel.add(pTime, gbc);

        gbc.gridy = 9; leftPanel.add(new JLabel("Note secrétariat:"), gbc);
        gbc.gridy = 10; noteSecretariat = new JTextArea(2, 20);
        leftPanel.add(new JScrollPane(noteSecretariat), gbc);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(300, 0));
        resumeArea = new JTextArea(); resumeArea.setEditable(false);
        resumeArea.setBackground(new Color(250, 250, 250));
        resumeArea.setBorder(new TitledBorder("Résumé de la création"));
        rightPanel.add(new JScrollPane(resumeArea));

        topPanel.add(leftPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // --- AGENDA ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(new TitledBorder("Agenda Hebdomadaire (07h - 19h)"));
        JPanel navAgenda = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prevBtn = new JButton("<<"); JButton nextBtn = new JButton(">>");
        semaineLabel = new JLabel("", SwingConstants.CENTER);
        prevBtn.addActionListener(e -> changerSemaine(-7));
        nextBtn.addActionListener(e -> changerSemaine(7));
        navAgenda.add(prevBtn); navAgenda.add(semaineLabel); navAgenda.add(nextBtn);

        layeredPaneAgenda = new JLayeredPane();
        JScrollPane scrollAgenda = new JScrollPane(layeredPaneAgenda);
        scrollAgenda.getVerticalScrollBar().setUnitIncrement(20);
        bottomPanel.add(navAgenda, BorderLayout.NORTH);
        bottomPanel.add(scrollAgenda, BorderLayout.CENTER);

        JButton createBtn = new JButton("✔ Enregistrer le rendez-vous");
        createBtn.setBackground(new Color(40, 167, 69)); createBtn.setForeground(Color.WHITE);
        createBtn.setPreferredSize(new Dimension(0, 45));
        createBtn.addActionListener(e -> enregistrerRDV());

        add(topPanel, BorderLayout.NORTH); add(bottomPanel, BorderLayout.CENTER); add(createBtn, BorderLayout.SOUTH);
        onPatientSelectionne();
    }

    /**
     * SYNCHRONISATION : Quand on tape une heure, le bloc bleu bouge sur l'agenda
     */
    private void ajouterEcouteurHeure() {
        heureField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { verifierEtActionner(); }
            public void removeUpdate(DocumentEvent e) { verifierEtActionner(); }
            public void changedUpdate(DocumentEvent e) { verifierEtActionner(); }

            private void verifierEtActionner() {
                String texte = heureField.getText();
                if (texte.matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) { // Format HH:mm
                    try {
                        LocalTime nouvelleHeure = LocalTime.parse(texte, timeFormatter);
                        if (nouvelleHeure.getHour() >= HEURE_DEBUT && nouvelleHeure.getHour() < HEURE_FIN) {
                            heureSelectionnee = nouvelleHeure;
                            // Si une date est déjà choisie, on rafraîchit le dessin
                            if (dateSelectionnee != null) {
                                long jourIndex = java.time.temporal.ChronoUnit.DAYS.between(dateDebutSemaine, dateSelectionnee);
                                SwingUtilities.invokeLater(() -> mettreAJourDessinBloc((int)jourIndex));
                            }
                        }
                    } catch (Exception ex) {}
                }
            }
        });
    }

    private void mettreAJourDessinBloc(int jourIndex) {
        if (interventionSelectionnee == null) return;
        if (blocSelectionActuel != null) layeredPaneAgenda.remove(blocSelectionActuel);

        blocSelectionActuel = dessinerBloc(
                jourIndex,
                heureSelectionnee.getHour(),
                heureSelectionnee.getMinute(),
                interventionSelectionnee.dureeMinutes,
                BLEU_SELECTION,
                "SÉLECTION"
        );
        updateResume();
    }

    private void selectionnerHeureViaAgenda(int j, int h) {
        if (interventionSelectionnee == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez d'abord une intervention.");
            return;
        }
        dateSelectionnee = dateDebutSemaine.plusDays(j);
        heureSelectionnee = LocalTime.of(h, 0);

        dateField.setText(dateSelectionnee.format(dateFormatter));
        // On met à jour le texte du champ (l'écouteur fera le reste du dessin)
        heureField.setText(heureSelectionnee.format(timeFormatter));

        mettreAJourDessinBloc(j);
    }

    // --- LOGIQUE MÉTIER ---
    private void onPatientSelectionne() {
        String name = (String) patientCombo.getSelectedItem();
        patientSelectionne = patientsTest.get(name);
        remarqueLabel.setText(patientSelectionne.remarque != null ? "⚠ " + patientSelectionne.remarque : " ");
        consultationCombo.removeAllItems();
        if (patientSelectionne.nouveau) { consultationCombo.addItem("Examen initial"); }
        else {
            List<Consultation> l = consultationsParPatient.get(patientSelectionne.id);
            if (l != null) l.forEach(c -> consultationCombo.addItem(c.nom));
            consultationCombo.addItem("Nouvelle Consultation");
        }
        onConsultationSelectionnee();
    }

    private void onConsultationSelectionnee() {
        String sel = (String) consultationCombo.getSelectedItem();
        interventionCombo.removeAllItems();
        if (sel == null) return;
        int cId = 0;
        if (!patientSelectionne.nouveau) {
            List<Consultation> l = consultationsParPatient.get(patientSelectionne.id);
            if (l != null) for(Consultation c : l) if(c.nom.equals(sel)) cId = c.id;
        }
        if (sel.equals("Examen initial")) { interventionCombo.addItem("Consultation Initiale - 30 min"); }
        else if (interventionsParConsultation.containsKey(cId)) {
            interventionsParConsultation.get(cId).forEach(i -> interventionCombo.addItem(i.nom + " - " + i.dureeMinutes + " min"));
        } else {
            interventionCombo.addItem("Détartrage - 30 min");
            interventionCombo.addItem("Extraction - 45 min");
        }
        onInterventionSelectionnee();
    }

    private void onInterventionSelectionnee() {
        String sel = (String) interventionCombo.getSelectedItem();
        if (sel == null) return;
        int d = sel.contains("45") ? 45 : 30;
        interventionSelectionnee = new Intervention(sel, d);
        dureeField.setText(d + " min");
        updateResume();
        // Si on change d'acte, on redessine le bloc car sa hauteur change
        if (dateSelectionnee != null && heureSelectionnee != null) {
            long index = java.time.temporal.ChronoUnit.DAYS.between(dateDebutSemaine, dateSelectionnee);
            mettreAJourDessinBloc((int)index);
        }
    }

    private void chargerSemaineCourante() {
        dateDebutSemaine = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        afficherAgendaSemaine();
    }

    private void changerSemaine(int jours) {
        dateDebutSemaine = dateDebutSemaine.plusDays(jours);
        afficherAgendaSemaine();
    }

    private void afficherAgendaSemaine() {
        layeredPaneAgenda.removeAll();
        int lCol = 140, headerH = 40, nbH = HEURE_FIN - HEURE_DEBUT;
        int hTotale = (nbH * HAUTEUR_HEURE) + headerH, lTotale = lCol * 8;
        semaineLabel.setText("Semaine du " + dateDebutSemaine.format(dateFormatter));

        JPanel container = new JPanel(new BorderLayout());
        container.setBounds(0, 0, lTotale, hTotale);

        JPanel hJours = new JPanel(new GridLayout(1, 8));
        hJours.setPreferredSize(new Dimension(lTotale, headerH));
        hJours.add(new JLabel("Heures", 0));
        for(int i=0; i<7; i++) hJours.add(new JLabel(dateDebutSemaine.plusDays(i).format(DateTimeFormatter.ofPattern("EEE dd/MM")), 0));
        container.add(hJours, BorderLayout.NORTH);

        JPanel grille = new JPanel(new GridLayout(nbH, 8));
        for (int h = HEURE_DEBUT; h < HEURE_FIN; h++) {
            JLabel lbl = new JLabel(h + ":00", 4); lbl.setBorder(new MatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
            grille.add(lbl);
            for (int j = 0; j < 7; j++) {
                JPanel cell = new JPanel(); cell.setBackground(Color.WHITE);
                cell.setBorder(new MatteBorder(1, 1, 0, 0, new Color(245, 245, 245)));
                int fJ = j, fH = h;
                cell.addMouseListener(new MouseAdapter() { public void mousePressed(MouseEvent e) { selectionnerHeureViaAgenda(fJ, fH); } });
                grille.add(cell);
            }
        }
        container.add(grille, BorderLayout.CENTER);

        layeredPaneAgenda.setPreferredSize(new Dimension(lTotale, hTotale));
        layeredPaneAgenda.add(container, JLayeredPane.DEFAULT_LAYER);

        for (TimeSlot ts : agendaTest) {
            if (!ts.date.isBefore(dateDebutSemaine) && ts.date.isBefore(dateDebutSemaine.plusDays(7))) {
                long d = java.time.temporal.ChronoUnit.DAYS.between(dateDebutSemaine, ts.date);
                dessinerBloc((int)d, ts.debut.getHour(), ts.debut.getMinute(), ts.duree, ROUGE_OCCUPE, ts.patientNom);
            }
        }
        layeredPaneAgenda.revalidate(); layeredPaneAgenda.repaint();
    }

    private JPanel dessinerBloc(int j, int h, int m, int dur, Color c, String text) {
        int lCol = 140;
        int x = (j + 1) * lCol, y = ((h - HEURE_DEBUT) * HAUTEUR_HEURE) + 40 + m;
        JPanel b = new JPanel(new BorderLayout()); b.setBackground(c);
        b.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        b.setBounds(x + 1, y, lCol - 2, dur);
        JLabel l = new JLabel(text, 0); l.setForeground(Color.WHITE); l.setFont(new Font("Arial", 1, 10));
        b.add(l);
        layeredPaneAgenda.add(b, JLayeredPane.PALETTE_LAYER);
        layeredPaneAgenda.repaint();
        return b;
    }

    private void updateResume() {
        if (patientSelectionne == null) return;

        // Récupération propre des valeurs pour éviter les décalages vus sur l'image
        String nomPatient = patientSelectionne.nomComplet;
        String consultation = (consultationCombo.getSelectedItem() != null) ? consultationCombo.getSelectedItem().toString() : "-";
        String acte = (interventionSelectionnee != null) ? interventionSelectionnee.nom : "-";
        String dateRdv = dateField.getText().isEmpty() ? "-" : dateField.getText();
        String heureRdv = heureField.getText().isEmpty() ? "-" : heureField.getText();
        String duree = dureeField.getText().isEmpty() ? "-" : dureeField.getText();
        String notes = noteSecretariat.getText();

        // Construction du texte formaté
        StringBuilder sb = new StringBuilder();
        sb.append("--- RÉSUMÉ ---\n");
        sb.append("PATIENT : ").append(nomPatient).append("\n");
        sb.append("CONVENTION : ").append(consultation).append("\n"); // Affiche la convention choisie
        sb.append("ACTE : ").append(acte).append("\n");
        sb.append("DATE : ").append(dateRdv).append("\n");
        sb.append("HEURE : ").append(heureRdv).append("\n");
        sb.append("DURÉE : ").append(duree).append("\n");
        sb.append("NOTES : ").append(notes);

        resumeArea.setText(sb.toString());
    }

    private void enregistrerRDV() {
        if (dateSelectionnee == null || heureSelectionnee == null) {
            JOptionPane.showMessageDialog(this, "Créneau invalide."); return;
        }
        agendaTest.add(new TimeSlot(dateSelectionnee, heureSelectionnee, interventionSelectionnee.dureeMinutes, patientSelectionne.nomComplet));
        JOptionPane.showMessageDialog(this, "Rendez-vous enregistré !");
        dateField.setText(""); heureField.setText("");
        if (blocSelectionActuel != null) layeredPaneAgenda.remove(blocSelectionActuel);
        afficherAgendaSemaine();
    }

    private JTextField createField(String title, boolean editable) {
        JTextField f = new JTextField(); f.setEditable(editable); f.setBorder(new TitledBorder(title)); return f;
    }

    class Patient { String id, nomComplet, remarque; boolean nouveau; Patient(String i, String n, boolean nv, String r){id=i;nomComplet=n;nouveau=nv;remarque=r;} }
    class Consultation { int id; String nom; boolean enCours; Consultation(int i, String n, boolean e){id=i;nom=n;enCours=e;} }
    class Intervention { String nom; int dureeMinutes; Intervention(String n, int d){nom=n;dureeMinutes=d;} }
    class TimeSlot { LocalDate date; LocalTime debut; int duree; String patientNom; TimeSlot(LocalDate d, LocalTime s, int dur, String p){date=d;debut=s;duree=dur;patientNom=p;} }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new AddRDVDialog(null).setVisible(true));
    }
}