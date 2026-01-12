package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Dialogue de gestion du Dossier Médical.
 * Vérifie l'existence d'un dossier pour un patient et permet sa création.
 */
public class DossierMedicalDialog extends JDialog {
    private final Long patientId;
    private JLabel lblStatus, lblDossierID;
    private JButton btnCreate;

    public DossierMedicalDialog(Frame parent, Long patientId, String patientName) {
        super(parent, "Dossier Médical : " + patientName, true);
        this.patientId = patientId;

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        lblStatus = new JLabel("Vérification du dossier en cours...");
        lblDossierID = new JLabel("");
        lblStatus.setFont(new Font("SansSerif", Font.PLAIN, 14));

        infoPanel.add(lblStatus);
        infoPanel.add(lblDossierID);

        btnCreate = new JButton("Créer le Dossier Médical");
        btnCreate.setBackground(new Color(46, 204, 113));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setVisible(false);
        btnCreate.addActionListener(e -> createNewDossier());

        add(infoPanel, BorderLayout.CENTER);
        add(btnCreate, BorderLayout.SOUTH);

        checkDossierExists();
    }

    private void checkDossierExists() {
        // Selon votre schema.sql : la table DossierMedicale a id_entite et patient_id
        String sql = "SELECT id_entite FROM DossierMedicale WHERE patient_id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                lblStatus.setText("✅ Dossier Médical : ACTIF");
                lblDossierID.setText("Identifiant Dossier : " + rs.getLong("id_entite"));
                btnCreate.setVisible(false);
            } else {
                lblStatus.setText("❌ Aucun dossier trouvé pour ce patient.");
                lblDossierID.setText("Le patient doit avoir un dossier pour les consultations.");
                btnCreate.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de vérification : " + e.getMessage());
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
                // Note : medecin_id est laissé à NULL par défaut selon votre schéma
                String sqlInsert = "INSERT INTO DossierMedicale (id_entite, patient_id) VALUES (?, ?)";
                PreparedStatement psDossier = conn.prepareStatement(sqlInsert);
                psDossier.setLong(1, newDossierId);
                psDossier.setLong(2, patientId);
                psDossier.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Dossier médical créé avec succès !");
                checkDossierExists(); // Rafraîchir l'affichage
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la création : " + e.getMessage());
        }
    }
}