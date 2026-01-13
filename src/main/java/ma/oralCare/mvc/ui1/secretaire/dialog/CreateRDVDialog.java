package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.entities.enums.StatutRDV;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateRDVDialog : Dialog pour créer un nouveau rendez-vous
 * Champ patient avec saisie libre + recherche automatique
 */
public class CreateRDVDialog extends JDialog {
    
    private final MainFrame mainFrame;
    private LocalDate selectedDate;
    private int selectedHour;
    private boolean confirmed = false;
    
    // Composants
    private JTextField txtPatient;
    private JComboBox<String> cboPatients;
    private JTextArea txtMotif;
    private JComboBox<String> cboStatut;
    private JSpinner spDuree;
    private JPanel patientPanel;
    private JPanel patientListPanel;
    
    // Données
    private List<PatientSuggestion> patientSuggestions;
    private JList<PatientSuggestion> suggestionList;
    private JScrollPane suggestionScrollPane;
    private JPopupMenu suggestionPopup;
    
    // IDs
    private Long selectedPatientId;
    private Long selectedMedecinId;
    
    public CreateRDVDialog(MainFrame mainFrame, LocalDate date, int hour) {
        super(mainFrame, "📅 Nouveau Rendez-vous", true);
        this.mainFrame = mainFrame;
        this.selectedDate = date;
        this.selectedHour = hour;
        this.patientSuggestions = new ArrayList<>();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadPatients();
        
        // Pré-remplir avec les données
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtPatient.setToolTipText("Tapez le nom, prénom, CIN ou téléphone du patient");
        cboStatut.setSelectedIndex(0); // "En attente" par défaut
        
        setSize(600, 500);
        setLocationRelativeTo(mainFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
    }
    
    private void initializeComponents() {
        // Champ patient avec recherche améliorée
        patientPanel = new JPanel(new BorderLayout());
        patientPanel.setBorder(BorderFactory.createTitledBorder("Patient *"));
        
        txtPatient = new JTextField(30);
        txtPatient.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPatient.setToolTipText("Tapez le nom, prénom, CIN ou téléphone du patient");
        
        // Bouton de recherche
        JButton btnSearchPatient = new JButton("🔍 Rechercher");
        btnSearchPatient.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnSearchPatient.setBackground(new Color(52, 152, 219));
        btnSearchPatient.setForeground(Color.WHITE);
        btnSearchPatient.setFocusPainted(false);
        btnSearchPatient.addActionListener(e -> searchPatients());
        
        patientPanel.add(txtPatient, BorderLayout.CENTER);
        patientPanel.add(btnSearchPatient, BorderLayout.EAST);
        
        // Liste de sélection de patients
        patientListPanel = new JPanel(new BorderLayout());
        patientListPanel.setBorder(BorderFactory.createTitledBorder("Liste des Patients"));
        
        cboPatients = new JComboBox<>();
        cboPatients.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cboPatients.setToolTipText("Sélectionnez un patient dans la liste");
        cboPatients.addActionListener(e -> {
            if (cboPatients.getSelectedIndex() > 0) {
                String selectedPatient = (String) cboPatients.getSelectedItem();
                if (selectedPatient != null && !selectedPatient.equals("-- Sélectionner un patient --")) {
                    // Extraire l'ID du patient depuis le format "Nom Prénom (ID: X)"
                    selectedPatientId = extractPatientId(selectedPatient);
                    txtPatient.setText(selectedPatient.split(" \\(ID: ")[0]);
                }
            }
        });
        
        patientListPanel.add(cboPatients, BorderLayout.CENTER);
        
        // Liste de suggestions
        suggestionList = new JList<>();
        suggestionList.setVisibleRowCount(5);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionScrollPane = new JScrollPane(suggestionList);
        suggestionScrollPane.setPreferredSize(new Dimension(300, 100));
        
        suggestionPopup = new JPopupMenu();
        suggestionPopup.add(suggestionScrollPane);
        
        // Motif
        txtMotif = new JTextArea(3, 30);
        txtMotif.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtMotif.setLineWrap(true);
        txtMotif.setWrapStyleWord(true);
        txtMotif.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Motif"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Statut avec libellés français
        cboStatut = new JComboBox<>();
        cboStatut.addItem("En attente");
        cboStatut.addItem("Confirmé");
        cboStatut.addItem("En cours");
        cboStatut.addItem("Terminé");
        cboStatut.addItem("Annulé");
        cboStatut.setSelectedIndex(0); // "En attente" par défaut
        
        // Durée
        SpinnerNumberModel durationModel = new SpinnerNumberModel(30, 15, 180, 15);
        spDuree = new JSpinner(durationModel);
        spDuree.setFont(new Font("SansSerif", Font.PLAIN, 14));
        spDuree.setBorder(BorderFactory.createTitledBorder("Durée (minutes)"));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel informations avec style amélioré
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Patient (première ligne, span 2 colonnes)
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        infoPanel.add(patientPanel, gbc);
        
        // Liste des patients (deuxième ligne, span 2 colonnes)
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.weightx = 1.0;
        infoPanel.add(patientListPanel, gbc);
        
        // Statut (troisième ligne, colonne 1)
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.5;
        cboStatut.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cboStatut.setBorder(BorderFactory.createTitledBorder("Statut"));
        infoPanel.add(cboStatut, gbc);
        
        // Durée (troisième ligne, colonne 2)
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.5;
        infoPanel.add(spDuree, gbc);
        
        // Motif (quatrième ligne, span 2 colonnes)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(new JScrollPane(txtMotif), gbc);
        
        // Informations du RDV
        JPanel rdvInfoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        rdvInfoPanel.setBorder(BorderFactory.createTitledBorder("Informations du rendez-vous"));
        rdvInfoPanel.add(new JLabel("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        rdvInfoPanel.add(new JLabel("Heure: " + String.format("%02d:00", selectedHour)));
        rdvInfoPanel.add(new JLabel("Cabinet: Cabinet Dentaire OralCare"));
        rdvInfoPanel.add(new JLabel("Secrétaire: " + getConnectedSecretaireName()));
        
        // Panel boutons avec style amélioré
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        
        JButton btnCreate = new JButton("✅ Créer le RDV");
        btnCreate.setBackground(new Color(46, 204, 113));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreate.setPreferredSize(new Dimension(150, 40));
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.addActionListener(e -> createRDV());
        
        JButton btnCancel = new JButton("❌ Annuler");
        btnCancel.setBackground(new Color(231, 76, 60));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(120, 40));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnCreate);
        
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(rdvInfoPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        // Recherche patient en temps réel
        txtPatient.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                searchPatients();
            }
        });
        
        // Sélection dans la liste de suggestions
        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && suggestionList.getSelectedValue() != null) {
                PatientSuggestion selected = suggestionList.getSelectedValue();
                txtPatient.setText(selected.displayName);
                selectedPatientId = selected.id;
                suggestionPopup.setVisible(false);
            }
        });
        
        // Cacher le popup quand on clique ailleurs
        txtPatient.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                SwingUtilities.invokeLater(() -> suggestionPopup.setVisible(false));
            }
        });
    }
    
    private void searchPatients() {
        String searchText = txtPatient.getText().trim();
        if (searchText.length() < 2) {
            suggestionPopup.setVisible(false);
            return;
        }
        
        patientSuggestions.clear();
        
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            String sql = "SELECT id_entite, nom, prenom, cin, telephone FROM Patient " +
                        "WHERE nom LIKE ? OR prenom LIKE ? OR cin LIKE ? OR telephone LIKE ? " +
                        "ORDER BY nom, prenom LIMIT 10";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String pattern = "%" + searchText + "%";
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
                ps.setString(4, pattern);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        PatientSuggestion suggestion = new PatientSuggestion(
                            rs.getLong("id_entite"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("cin"),
                            rs.getString("telephone")
                        );
                        patientSuggestions.add(suggestion);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        updateSuggestionList();
    }
    
    private void updateSuggestionList() {
        DefaultListModel<PatientSuggestion> model = new DefaultListModel<>();
        for (PatientSuggestion suggestion : patientSuggestions) {
            model.addElement(suggestion);
        }
        suggestionList.setModel(model);
        
        if (!patientSuggestions.isEmpty()) {
            suggestionPopup.show(txtPatient, 0, txtPatient.getHeight());
            suggestionPopup.pack();
        } else {
            suggestionPopup.setVisible(false);
        }
    }
    
    private void loadPatients() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            String sql = "SELECT id_entite, nom, prenom, cin, telephone FROM Patient " +
                        "ORDER BY nom, prenom";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    // Ajouter l'option par défaut
                    cboPatients.addItem("-- Sélectionner un patient --");
                    
                    while (rs.next()) {
                        Long id = rs.getLong("id_entite");
                        String nom = rs.getString("nom");
                        String prenom = rs.getString("prenom");
                        String displayName = nom + " " + prenom + " (ID: " + id + ")";
                        
                        cboPatients.addItem(displayName);
                        // Stocker l'ID dans les données du composant
                        cboPatients.putClientProperty("patient_id_" + String.valueOf(cboPatients.getItemCount() - 1), id);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Extrait l'ID du patient depuis le format "Nom Prénom (ID: X)"
     */
    private Long extractPatientId(String patientDisplay) {
        try {
            if (patientDisplay.contains("(ID: ")) {
                String idStr = patientDisplay.substring(patientDisplay.indexOf("(ID: ") + 5, patientDisplay.indexOf(")"));
                return Long.parseLong(idStr.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void createRDV() {
        // Validation
        if (selectedPatientId == null) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un patient valide", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (txtMotif.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez saisir un motif", 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Convertir le statut français vers l'enum
        String statutFrancais = (String) cboStatut.getSelectedItem();
        StatutRDV statutEnum = convertirStatutFrancaisVersEnum(statutFrancais);
        
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            // Vérifier si le dossier médical existe
            if (!dossierMedicalExists(conn, selectedPatientId)) {
                // Le dossier médical n'existe pas, proposer de le créer
                int option = JOptionPane.showConfirmDialog(
                    this,
                    "Ce patient n'a pas de dossier médical.\nVoulez-vous créer son dossier médical maintenant ?",
                    "Dossier Médical Manquant",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (option == JOptionPane.YES_OPTION) {
                    // Rediriger vers l'interface de création de dossier médical
                    dispose(); // Fermer le dialog de RDV
                    redirectToDossierMedical(selectedPatientId);
                    return;
                } else {
                    // L'utilisateur ne veut pas créer le dossier
                    JOptionPane.showMessageDialog(this,
                        "Un dossier médical est requis pour créer un rendez-vous.",
                        "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            
            // Le dossier médical existe, créer le RDV
            String rdvSql = "INSERT INTO RDV (date, heure, statut, motif, dossier_medicale_id) " +
                           "VALUES (?, ?, ?, ?, (SELECT id_entite FROM DossierMedicale WHERE patient_id = ?))";
            
            try (PreparedStatement ps = conn.prepareStatement(rdvSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDate(1, java.sql.Date.valueOf(selectedDate));
                ps.setTime(2, java.sql.Time.valueOf(LocalTime.of(selectedHour, 0)));
                ps.setString(3, statutEnum.name());
                ps.setString(4, txtMotif.getText().trim());
                ps.setLong(5, selectedPatientId);
                
                int affectedRows = ps.executeUpdate();
                
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long rdvId = generatedKeys.getLong(1);
                            
                            confirmed = true;
                            JOptionPane.showMessageDialog(this, 
                                "Rendez-vous créé avec succès", 
                                "Succès", 
                                JOptionPane.INFORMATION_MESSAGE);
                            dispose();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la création du rendez-vous: " + e.getMessage(), 
                "Erreur", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Convertit le statut français vers l'enum StatutRDV
     */
    private StatutRDV convertirStatutFrancaisVersEnum(String statutFrancais) {
        switch (statutFrancais) {
            case "En attente": return StatutRDV.PENDING;
            case "Confirmé": return StatutRDV.CONFIRMED;
            case "En cours": return StatutRDV.IN_PROGRESS;
            case "Terminé": return StatutRDV.COMPLETED;
            case "Annulé": return StatutRDV.CANCELLED;
            default: return StatutRDV.PENDING; // Valeur par défaut
        }
    }
    
    private void createConsultation(Connection conn, long rdvId) throws SQLException {
        String consultationSql = "INSERT INTO Consultation (date, dossier_medicale_id, rdv_id) " +
                               "VALUES (?, (SELECT id_entite FROM DossierMedicale WHERE patient_id = ?), ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(consultationSql)) {
            ps.setDate(1, java.sql.Date.valueOf(selectedDate));
            ps.setLong(2, selectedPatientId);
            ps.setLong(3, rdvId);
            ps.executeUpdate();
        }
    }
    
    // Méthodes utilitaires
    private long getConnectedSecretaireId() {
        // À implémenter selon votre système d'authentification
        return 1L; // Placeholder
    }
    
    private String getConnectedSecretaireName() {
        // À implémenter selon votre système d'authentification
        return "Secrétaire Connecté"; // Placeholder
    }
    
    private long getConnectedCabinetId() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            // Récupérer l'ID du cabinet de la secrétaire connectée
            String sql = "SELECT s.cabinet_id FROM Secretaire sec " +
                        "JOIN Staff s ON sec.id_entite = s.id_entite " +
                        "JOIN utilisateur u ON s.id_entite = u.id_entite " +
                        "WHERE u.login = 'h.ahlam' LIMIT 1"; // À adapter avec le vrai login
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong("cabinet_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1L; // Valeur par défaut si erreur
    }
    
    /**
     * Vérifie si le dossier médical existe pour un patient
     */
    private boolean dossierMedicalExists(Connection conn, Long patientId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM DossierMedicale WHERE patient_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    /**
     * Redirige vers la création de dossier médical en arrière-plan
     */
    private void redirectToDossierMedical(Long patientId) {
        try {
            // Créer le dossier médical en arrière-plan
            boolean dossierCree = createDossierMedicalInBackground(patientId);
            
            if (dossierCree) {
                // Succès - afficher message de confirmation
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Dossier médical créé avec succès !\nVous pouvez maintenant créer le rendez-vous.",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                });
                
                // Recréer automatiquement le dialog de RDV
                SwingUtilities.invokeLater(() -> {
                    CreateRDVDialog newRdvDialog = new CreateRDVDialog(mainFrame, selectedDate, selectedHour);
                    newRdvDialog.setVisible(true);
                });
            } else {
                // Erreur lors de la création
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(mainFrame,
                        "Erreur lors de la création automatique du dossier médical.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création du dossier médical: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Crée le dossier médical en arrière-plan sans interface UI
     */
    private boolean createDossierMedicalInBackground(Long patientId) {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Vérifier si le dossier existe déjà
                if (dossierMedicalExists(conn, patientId)) {
                    return true; // Le dossier existe déjà
                }
                
                // 1. Créer l'entrée dans BaseEntity
                PreparedStatement psBase = conn.prepareStatement(
                        "INSERT INTO BaseEntity (date_creation) VALUES (NOW())",
                        Statement.RETURN_GENERATED_KEYS);
                psBase.executeUpdate();
                
                ResultSet rsKeys = psBase.getGeneratedKeys();
                rsKeys.next();
                long newDossierId = rsKeys.getLong(1);
                
                // 2. Créer l'entrée dans DossierMedicale
                String sqlInsert = "INSERT INTO DossierMedicale (id_entite, patient_id) VALUES (?, ?)";
                PreparedStatement psDossier = conn.prepareStatement(sqlInsert);
                psDossier.setLong(1, newDossierId);
                psDossier.setLong(2, patientId);
                psDossier.executeUpdate();
                
                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    // Classe interne pour les suggestions de patients
    private static class PatientSuggestion {
        Long id;
        String nom;
        String prenom;
        String cin;
        String telephone;
        String displayName;
        
        PatientSuggestion(Long id, String nom, String prenom, String cin, String telephone) {
            this.id = id;
            this.nom = nom;
            this.prenom = prenom;
            this.cin = cin;
            this.telephone = telephone;
            this.displayName = nom + " " + prenom + " (" + cin + ")";
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
}
