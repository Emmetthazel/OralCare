package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Dialogue de gestion des patients (Ajout et Modification).
 * Aligné sur le schéma SQL : cin, nom, prenom, sexe, telephone, email, adresse, assurance.
 */
public class PatientDialog extends JDialog {
    private JTextField txtCin, txtNom, txtPrenom, txtTel, txtEmail, txtAdresse;
    private JComboBox<String> cbSexe, cbAssurance;
    private Long patientId;

    public PatientDialog(Frame parent, Long id) {
        super(parent, (id == null ? "Ajouter un Patient" : "Modifier le Patient"), true);
        this.patientId = id;

        setSize(450, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Grille ajustée à 9 lignes (on retire la ville car absente du schéma SQL Patient)
        JPanel formPanel = new JPanel(new GridLayout(9, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("CIN (Obligatoire) :")); txtCin = new JTextField(); formPanel.add(txtCin);
        formPanel.add(new JLabel("Nom :")); txtNom = new JTextField(); formPanel.add(txtNom);
        formPanel.add(new JLabel("Prénom :")); txtPrenom = new JTextField(); formPanel.add(txtPrenom);
        formPanel.add(new JLabel("Sexe :")); cbSexe = new JComboBox<>(new String[]{"MALE", "FEMALE", "OTHER"}); formPanel.add(cbSexe);
        formPanel.add(new JLabel("Téléphone :")); txtTel = new JTextField(); formPanel.add(txtTel);
        formPanel.add(new JLabel("Email :")); txtEmail = new JTextField(); formPanel.add(txtEmail);
        formPanel.add(new JLabel("Adresse :")); txtAdresse = new JTextField(); formPanel.add(txtAdresse);
        formPanel.add(new JLabel("Assurance :")); cbAssurance = new JComboBox<>(new String[]{"NONE", "CNOPS", "CNSS", "RAMED"}); formPanel.add(cbAssurance);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Sauvegarder");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> savePatient());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        if (id != null) loadPatientData();
    }

    private void loadPatientData() {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM Patient WHERE id_entite = ?")) {
            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtCin.setText(rs.getString("cin"));
                txtNom.setText(rs.getString("nom"));
                txtPrenom.setText(rs.getString("prenom"));
                txtTel.setText(rs.getString("telephone"));
                txtEmail.setText(rs.getString("email"));
                txtAdresse.setText(rs.getString("adresse"));
                cbSexe.setSelectedItem(rs.getString("sexe"));
                cbAssurance.setSelectedItem(rs.getString("assurance"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + e.getMessage());
        }
    }

    private void savePatient() {
        if (txtCin.getText().trim().isEmpty() || txtNom.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le CIN et le Nom sont obligatoires.");
            return;
        }

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                if (patientId == null) {
                    // 1. Insertion BaseEntity
                    PreparedStatement psBase = conn.prepareStatement(
                            "INSERT INTO BaseEntity (date_creation) VALUES (NOW())", Statement.RETURN_GENERATED_KEYS);
                    psBase.executeUpdate();
                    ResultSet rsKeys = psBase.getGeneratedKeys();
                    rsKeys.next();
                    long newId = rsKeys.getLong(1);

                    // 2. Insertion Patient (9 colonnes : id + 8 champs)
                    String sqlInsert = "INSERT INTO Patient (id_entite, cin, nom, prenom, sexe, telephone, email, adresse, assurance) VALUES (?,?,?,?,?,?,?,?,?)";
                    PreparedStatement psPat = conn.prepareStatement(sqlInsert);
                    psPat.setLong(1, newId);
                    setStatementParams(psPat, 2);
                    psPat.executeUpdate();
                } else {
                    // Mode Mise à jour (8 champs + WHERE id)
                    String sqlUpdate = "UPDATE Patient SET cin=?, nom=?, prenom=?, sexe=?, telephone=?, email=?, adresse=?, assurance=? WHERE id_entite=?";
                    PreparedStatement psUp = conn.prepareStatement(sqlUpdate);
                    setStatementParams(psUp, 1);
                    psUp.setLong(9, patientId);
                    psUp.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Patient enregistré avec succès !");
                dispose();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + e.getMessage());
        }
    }

    /**
     * Remplit les paramètres du PreparedStatement pour éviter la duplication de code.
     * @param startIndex 2 pour INSERT (après l'ID), 1 pour UPDATE.
     */
    private void setStatementParams(PreparedStatement ps, int startIndex) throws SQLException {
        ps.setString(startIndex, txtCin.getText().trim());
        ps.setString(startIndex + 1, txtNom.getText().trim());
        ps.setString(startIndex + 2, txtPrenom.getText().trim());
        ps.setString(startIndex + 3, cbSexe.getSelectedItem().toString());
        ps.setString(startIndex + 4, txtTel.getText().trim());
        ps.setString(startIndex + 5, txtEmail.getText().trim());
        ps.setString(startIndex + 6, txtAdresse.getText().trim());
        ps.setString(startIndex + 7, cbAssurance.getSelectedItem().toString());
    }
}