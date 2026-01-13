package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.secretaire.DossierMedicalSecretairePanel;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Dialogue de gestion du Dossier Médical.
 * Vérifie l'existence d'un dossier pour un patient et permet sa création et consultation.
 */
public class DossierMedicalDialog extends JDialog {
    private final Long patientId;
    private final String patientName;
    private final Frame parentFrame;
    private JLabel lblStatus, lblDossierID, lblPatientInfo;
    private JButton btnCreate, btnViewDetails;
    private Long dossierId = null;
    private boolean confirmed = false;

    public DossierMedicalDialog(Frame parent, Long patientId, String patientName) {
        super(parent, "Dossier Médical : " + patientName, true);
        this.parentFrame = parent;
        this.patientId = patientId;
        this.patientName = patientName;

        setSize(500, 350);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        setupUI();
        checkDossierExists();
    }

    private void setupUI() {
        // Panel d'informations patient
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientPanel.setBorder(BorderFactory.createTitledBorder("Informations Patient"));
        patientPanel.setBackground(new Color(240, 248, 255));
        
        lblPatientInfo = new JLabel(" " + patientName);
        lblPatientInfo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblPatientInfo.setForeground(new Color(41, 128, 185));
        patientPanel.add(lblPatientInfo);

        // Panel d'état du dossier
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("État du Dossier Médical"));
        
        lblStatus = new JLabel(" Vérification du dossier en cours...");
        lblDossierID = new JLabel("");
        JLabel lblInstructions = new JLabel(" Double-cliquez sur un patient pour accéder à son dossier complet");
        
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblDossierID.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblInstructions.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblInstructions.setForeground(Color.GRAY);

        infoPanel.add(lblStatus);
        infoPanel.add(lblDossierID);
        infoPanel.add(lblInstructions);

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnCreate = new JButton(" Créer le Dossier Médical");
        btnCreate.setBackground(new Color(46, 204, 113));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnCreate.setPreferredSize(new Dimension(200, 35));
        btnCreate.setVisible(false);
        btnCreate.addActionListener(e -> createNewDossier());

        btnViewDetails = new JButton(" Voir Dossier Complet");
        btnViewDetails.setBackground(new Color(52, 152, 219));
        btnViewDetails.setForeground(Color.WHITE);
        btnViewDetails.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnViewDetails.setPreferredSize(new Dimension(200, 35));
        btnViewDetails.setVisible(false);
        btnViewDetails.addActionListener(e -> viewDossierDetails());

        JButton btnClose = new JButton(" Fermer");
        btnClose.setBackground(new Color(231, 76, 60));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnClose.setPreferredSize(new Dimension(120, 35));
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnClose);

        // Assemblage
        add(patientPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void checkDossierExists() {
        // Selon votre schema.sql : la table DossierMedicale a id_entite et patient_id
        String sql = "SELECT id_entite, date_creation FROM DossierMedicale WHERE patient_id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                dossierId = rs.getLong("id_entite");
                Timestamp creationDate = rs.getTimestamp("date_creation");
                
                lblStatus.setText(" Dossier Médical : ACTIF");
                lblStatus.setForeground(new Color(39, 174, 96));
                lblStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                lblDossierID.setText(" Identifiant Dossier : " + dossierId + " | Créé le : " + 
                    new SimpleDateFormat("dd/MM/yyyy HH:mm").format(creationDate));
                lblDossierID.setForeground(new Color(52, 152, 219));
                
                btnCreate.setVisible(false);
                btnViewDetails.setVisible(true);
            } else {
                lblStatus.setText(" Aucun dossier trouvé pour ce patient");
                lblStatus.setForeground(new Color(231, 76, 60));
                lblStatus.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                lblDossierID.setText(" Le patient doit avoir un dossier pour les consultations et suivis");
                lblDossierID.setForeground(Color.ORANGE);
                
                btnCreate.setVisible(true);
                btnViewDetails.setVisible(false);
            }
        } catch (SQLException e) {
            lblStatus.setText(" Erreur de vérification : " + e.getMessage());
            lblStatus.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Erreur de vérification : " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createNewDossier() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Créer l'entrée dans BaseEntity
                PreparedStatement psBase = conn.prepareStatement(
                        "INSERT INTO BaseEntity (date_creation) VALUES (NOW())",
                        Statement.RETURN_GENERATED_KEYS);
                psBase.executeUpdate();

                ResultSet rsKeys = psBase.getGeneratedKeys();
                rsKeys.next();
                long newDossierId = rsKeys.getLong(1);

                // 2. Créer l'entrée dans DossierMedicale (id_entite, patient_id)
                String sqlInsert = "INSERT INTO DossierMedicale (id_entite, patient_id) VALUES (?, ?)";
                PreparedStatement psDossier = conn.prepareStatement(sqlInsert);
                psDossier.setLong(1, newDossierId);
                psDossier.setLong(2, patientId);
                psDossier.executeUpdate();

                conn.commit();
                
                confirmed = true; // Marquer comme confirmé
                
                JOptionPane.showMessageDialog(this, 
                    " Dossier médical créé avec succès !\n\n" +
                    " Identifiant : " + newDossierId + "\n" +
                    " Patient : " + patientName + "\n" +
                    " Date : " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                    
                checkDossierExists(); // Rafraîchir l'affichage
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création : " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDossierDetails() {
        if (dossierId != null) {
            // Ouvrir l'interface complète du dossier médical
            SwingUtilities.invokeLater(() -> {
                try {
                    // Vérifier que parentFrame est bien un MainFrame
                    if (parentFrame instanceof ma.oralCare.mvc.ui1.MainFrame) {
                        ma.oralCare.mvc.ui1.MainFrame mainFrame = (ma.oralCare.mvc.ui1.MainFrame) parentFrame;
                        
                        // Créer et afficher le panneau de dossier médical complet
                        DossierMedicalSecretairePanel dossierPanel = new DossierMedicalSecretairePanel(mainFrame);
                        
                        // Créer une nouvelle fenêtre pour le dossier médical
                        JFrame dossierFrame = new JFrame(" Dossier Médical Complet - " + patientName);
                        dossierFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        dossierFrame.setSize(900, 700);
                        dossierFrame.setLocationRelativeTo(parentFrame);
                        dossierFrame.add(dossierPanel);
                        dossierFrame.setVisible(true);
                        
                        // Fermer ce dialogue
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Erreur : La fenêtre parent n'est pas de type MainFrame", 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                        "Erreur lors de l'ouverture du dossier : " + e.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}