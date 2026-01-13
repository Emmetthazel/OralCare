package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.utils.StatutTranslator;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * DossierMedicalSecretairePanel : Vue restreinte du dossier médical.
 * Accès uniquement en LECTURE SEULE pour les secrétaires.
 * Les informations confidentielles (notes privées, diagnostics complexes) sont masquées.
 */
public class DossierMedicalSecretairePanel extends JPanel {

    private final MainFrame mainFrame;
    private Long currentPatientId;

    // Composants d'information (Lecture Seule)
    private JTextField txtNom, txtPrenom, txtAge, txtSexe, txtTel, txtEmail, txtAdresse;
    private JTextField txtCabinet, txtDateCreation;
    private JTable tableHistorique, tableAntecedents;
    private DefaultTableModel modelHistorique, modelAntecedents;

    // Style
    private final Color HEADER_BG = new Color(245, 246, 250);
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color BORDER_COLOR = new Color(220, 221, 225);

    public DossierMedicalSecretairePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        setupHeader();  // Informations administratives
        setupCenter();  // Historique des soins (Vue simplifiée)
        setupFooter();  // Actions autorisées
    }

    /**
     * Section Nord : Identité du patient et informations du cabinet (Champs non éditables)
     */
    private void setupHeader() {
        JPanel headerPanel = new JPanel(new GridLayout(2, 4, 15, 10));
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(new LineBorder(BORDER_COLOR), "Fiche Administrative du Patient",
                        TitledBorder.LEFT, TitledBorder.TOP, new Font("SansSerif", Font.BOLD, 13)),
                new EmptyBorder(10, 15, 10, 15)
        ));

        // Initialisation des champs
        txtNom = createReadOnlyField(); txtPrenom = createReadOnlyField();
        txtAge = createReadOnlyField(); txtSexe = createReadOnlyField();
        txtTel = createReadOnlyField(); txtEmail = createReadOnlyField();
        txtAdresse = createReadOnlyField(); txtCabinet = createReadOnlyField();
        txtDateCreation = createReadOnlyField();

        // Labels avec style
        headerPanel.add(new JLabel("Nom:")); headerPanel.add(txtNom);
        headerPanel.add(new JLabel("Prénom:")); headerPanel.add(txtPrenom);
        headerPanel.add(new JLabel("Âge:")); headerPanel.add(txtAge);
        headerPanel.add(new JLabel("Sexe:")); headerPanel.add(txtSexe);
        headerPanel.add(new JLabel("Téléphone:")); headerPanel.add(txtTel);
        headerPanel.add(new JLabel("Email:")); headerPanel.add(txtEmail);
        headerPanel.add(new JLabel("Adresse:")); headerPanel.add(txtAdresse);
        headerPanel.add(new JLabel("Cabinet:")); headerPanel.add(txtCabinet);
        headerPanel.add(new JLabel("Date Création:")); headerPanel.add(txtDateCreation);

        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Section Centrale : Onglets pour Historique et Antécédents
     */
    private void setupCenter() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        // Onglet 1 : Historique des consultations
        JPanel historiquePanel = createHistoriquePanel();
        tabbedPane.addTab("📋 Historique des Consultations", historiquePanel);
        
        // Onglet 2 : Antécédents médicaux
        JPanel antecedentsPanel = createAntecedentsPanel();
        tabbedPane.addTab("🏥 Antécédents Médicaux", antecedentsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Crée le panneau d'historique des consultations
     */
    private JPanel createHistoriquePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new TitledBorder(new LineBorder(BORDER_COLOR), "Historique des Passages & Prescriptions"));

        // Colonnes limitées : on ne montre pas le "Diagnostic" ni les "Notes"
        String[] columns = {"Date", "Type de Consultation", "Acte Réalisé", "Prescription délivrée", "Statut"};
        modelHistorique = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Lecture seule totale
        };

        tableHistorique = new JTable(modelHistorique);
        tableHistorique.setRowHeight(30);
        tableHistorique.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tableHistorique);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
    
    /**
     * Crée le panneau des antécédents médicaux
     */
    private JPanel createAntecedentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new TitledBorder(new LineBorder(BORDER_COLOR), "Antécédents Médicaux du Patient"));

        // Colonnes pour les antécédents
        String[] columns = {"Date", "Nom", "Catégorie", "Niveau de risque"};
        modelAntecedents = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Lecture seule totale
        };

        tableAntecedents = new JTable(modelAntecedents);
        tableAntecedents.setRowHeight(30);
        tableAntecedents.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tableAntecedents);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Section Sud : Boutons pour l'administration
     */
    private void setupFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setOpaque(false);

        JButton btnImprimer = new JButton("🖨️ Imprimer Dossier Patient");
        btnImprimer.setBackground(new Color(52, 152, 219));
        btnImprimer.setForeground(Color.WHITE);
        btnImprimer.setFocusPainted(false);
        btnImprimer.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnImprimer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImprimer.setPreferredSize(new Dimension(220, 40));

        btnImprimer.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Génération du PDF pour le patient...");
            // Logique d'exportation ici
        });

        footerPanel.add(btnImprimer);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Met à jour les données du panel pour un patient spécifique.
     */
    public void refreshData(Long patientId) {
        this.currentPatientId = patientId;
        if (patientId == null) {
            clearFields();
            return;
        }
        // Simulation de chargement SQL (via SessionFactory)
        // LoadPatientAdminInfo(patientId);
        // LoadPublicHistory(patientId);
    }
    
    /**
     * Charge un patient spécifique depuis l'extérieur (similaire à DossierMedicalPanel)
     * @param patientId L'ID du patient à charger
     * @param patientName Le nom du patient pour l'affichage
     */
    public void loadPatientFromSelection(Long patientId, String patientName) {
        this.currentPatientId = patientId;
        
        // Charger les informations du patient depuis la base de données
        SwingUtilities.invokeLater(() -> {
            try {
                // Obtenir la connexion et charger les informations
                try (Connection conn = SessionFactory.getInstance().getConnection()) {
                    loadPatientInfo(conn);
                    loadPatientHistory(conn);
                    loadAntecedents(conn); // Charger les antécédents
                }
            } catch (Exception e) {
                // Afficher un message d'erreur dans la console mais pas d'alerte utilisateur
                System.err.println("Erreur lors du chargement du dossier: " + e.getMessage());
                
                // Afficher des informations par défaut pour éviter l'interface vide
                txtNom.setText(patientName != null ? patientName.split(" ")[0] : "Erreur");
                txtPrenom.setText(patientName != null && patientName.contains(" ") ? patientName.split(" ", 2)[1] : "");
                txtAge.setText("Inconnu");
                txtSexe.setText("Inconnu");
                txtTel.setText("Inconnu");
                txtEmail.setText("Inconnu");
                txtAdresse.setText("Inconnu");
                txtCabinet.setText("Cabinet Dentaire");
                txtDateCreation.setText("Inconnue");
                
                // Ajouter une ligne informative dans l'historique
                modelHistorique.addRow(new Object[]{
                    "Erreur", "Erreur de chargement", "Veuillez réessayer", "Données indisponibles", "Erreur"
                });
                
                // Ajouter une ligne informative dans les antécédents
                modelAntecedents.addRow(new Object[]{
                    "Erreur", "Erreur de chargement", "Veuillez réessayer", "Données indisponibles"
                });
            }
        });
    }
    
    /**
     * Charge les informations administratives du patient et du cabinet
     */
    private void loadPatientInfo(Connection conn) throws SQLException {
        // Charger les informations du patient
        String patientSql = "SELECT * FROM Patient WHERE id_entite = ?";
        try (PreparedStatement ps = conn.prepareStatement(patientSql)) {
            ps.setLong(1, currentPatientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtNom.setText(rs.getString("nom"));
                    txtPrenom.setText(rs.getString("prenom"));
                    txtSexe.setText(rs.getString("sexe"));
                    txtTel.setText(rs.getString("telephone") != null ? rs.getString("telephone") : "");
                    txtEmail.setText(rs.getString("email") != null ? rs.getString("email") : "");
                    txtAdresse.setText(rs.getString("adresse") != null ? rs.getString("adresse") : "");
                    
                    // Calculer l'âge
                    Date birth = rs.getDate("date_de_naissance");
                    if (birth != null) {
                        int age = java.time.Period.between(birth.toLocalDate(), java.time.LocalDate.now()).getYears();
                        txtAge.setText(age + " ans");
                    }
                }
            }
        }
        
        // Charger les informations du cabinet et du dossier médical
        try {
            // Simplifier la requête pour ne plus utiliser la table Cabinet ni la date_creation
            String dossierSql = "SELECT dm.id_entite " +
                              "FROM DossierMedicale dm " +
                              "WHERE dm.patient_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(dossierSql)) {
                ps.setLong(1, currentPatientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtCabinet.setText("Cabinet Dentaire OralCare"); // Valeur fixe
                        txtDateCreation.setText("Dossier existant"); // Valeur fixe
                    } else {
                        // Pas de dossier trouvé
                        txtCabinet.setText("Cabinet Dentaire OralCare");
                        txtDateCreation.setText("Non créé");
                    }
                }
            }
        } catch (SQLException e) {
            // En cas d'erreur, afficher des valeurs par défaut
            txtCabinet.setText("Cabinet Dentaire OralCare");
            txtDateCreation.setText("Inconnue");
            System.err.println("Erreur lors du chargement des infos cabinet: " + e.getMessage());
        }
    }
    
    /**
     * Charge l'historique public du patient (sans informations confidentielles)
     */
    private void loadPatientHistory(Connection conn) throws SQLException {
        modelHistorique.setRowCount(0);
        
        try {
            // D'abord vérifier si le patient a un dossier médical
            Long dossierId = null;
            String checkDossierSql = "SELECT id_entite FROM DossierMedicale WHERE patient_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkDossierSql)) {
                ps.setLong(1, currentPatientId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dossierId = rs.getLong("id_entite");
                    }
                }
            }
            
            if (dossierId == null) {
                // Pas de dossier médical, ajouter une ligne informative
                modelHistorique.addRow(new Object[]{
                    "-", "Aucun dossier", "Aucun acte", "Aucune prescription", "Nouveau patient"
                });
                return;
            }
            
            // Charger les consultations et actes publics avec une requête plus simple
            String sql = "SELECT c.date, 'Consultation' as type, " +
                        "COALESCE(a.libelle, 'Consultation générale') as acte, " +
                        "'Prescription disponible' as prescription, 'Terminé' as statut " +
                        "FROM Consultation c " +
                        "LEFT JOIN intervention_medecin im ON c.id_entite = im.consultation_id " +
                        "LEFT JOIN acte a ON im.acte_id = a.id_entite " +
                        "WHERE c.dossier_medicale_id = ? " +
                        "ORDER BY c.date DESC LIMIT 10";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, dossierId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        String statutTraduit = StatutTranslator.traduireStatutDossier(rs.getString("statut"));
                        modelHistorique.addRow(new Object[]{
                            rs.getDate("date") != null ? rs.getDate("date").toString() : "-",
                            rs.getString("type"),
                            rs.getString("acte"),
                            rs.getString("prescription"),
                            statutTraduit
                        });
                    }
                    
                    // Si aucune donnée trouvée, ajouter une ligne informative
                    if (!hasData) {
                        modelHistorique.addRow(new Object[]{
                            "-", "Aucune consultation", "Aucun acte", "Aucune prescription", "Première visite"
                        });
                    }
                }
            }
            
        } catch (SQLException e) {
            // En cas d'erreur, ajouter une ligne informative plutôt que de lancer l'exception
            modelHistorique.addRow(new Object[]{
                "Erreur", "Erreur de chargement", "Erreur", "Erreur", "Erreur"
            });
            // Ne pas lancer l'exception pour éviter l'alerte
            System.err.println("Erreur lors du chargement de l'historique: " + e.getMessage());
        }
    }
    
    /**
     * Charge les antécédents médicaux du patient
     */
    private void loadAntecedents(Connection conn) throws SQLException {
        modelAntecedents.setRowCount(0);
        
        try {
            // Requête pour charger les antécédents du patient
            String sql = "SELECT a.date_creation, a.nom, a.categorie, " +
                        "a.niveau_de_risque " +
                        "FROM Antecedent a " +
                        "INNER JOIN patient_antecedent pa ON a.id_entite = pa.antecedent_id " +
                        "WHERE pa.patient_id = ? " +
                        "ORDER BY a.date_creation DESC";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, currentPatientId);
                try (ResultSet rs = ps.executeQuery()) {
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        modelAntecedents.addRow(new Object[]{
                            rs.getDate("date_creation") != null ? rs.getDate("date_creation").toString() : "-",
                            rs.getString("nom"),
                            rs.getString("categorie") != null ? rs.getString("categorie") : "Non spécifié",
                            rs.getString("niveau_de_risque") != null ? rs.getString("niveau_de_risque") : "Non spécifié"
                        });
                    }
                    
                    // Si aucune donnée trouvée, ajouter une ligne informative
                    if (!hasData) {
                        modelAntecedents.addRow(new Object[]{
                            "-", "Aucun antécédent", "Aucun antécédent enregistré", "Non spécifié"
                        });
                    }
                }
            }
            
        } catch (SQLException e) {
            // En cas d'erreur, ajouter une ligne informative plutôt que de lancer l'exception
            modelAntecedents.addRow(new Object[]{
                "Erreur", "Erreur de chargement", "Erreur", "Erreur"
            });
            // Ne pas lancer l'exception pour éviter l'alerte
            System.err.println("Erreur lors du chargement des antécédents: " + e.getMessage());
        }
    }

    private JTextField createReadOnlyField() {
        JTextField f = new JTextField();
        f.setEditable(false);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        f.setForeground(TEXT_COLOR);
        return f;
    }

    private void clearFields() {
        txtNom.setText(""); txtPrenom.setText(""); txtAge.setText("");
        txtSexe.setText(""); txtTel.setText(""); txtEmail.setText("");
        txtAdresse.setText(""); txtCabinet.setText(""); txtDateCreation.setText("");
        modelHistorique.setRowCount(0);
        modelAntecedents.setRowCount(0); // Vider aussi les antécédents
    }
}
