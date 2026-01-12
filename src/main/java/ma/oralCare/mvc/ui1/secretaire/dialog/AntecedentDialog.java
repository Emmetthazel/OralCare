package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialogue de gestion des antécédents médicaux pour un patient.
 * Utilise la table de jointure Patient_Antecedent du schéma.
 */
public class AntecedentDialog extends JDialog {
    private final Long patientId;
    private JPanel listPanel;
    private List<JCheckBox> checkBoxes = new ArrayList<>();

    public AntecedentDialog(Frame parent, Long patientId) {
        super(parent, "Gestion des Antécédents", true);
        this.patientId = patientId;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Panel de titre
        JPanel headerPanel = new JPanel();
        headerPanel.add(new JLabel("Cochez les antécédents du patient :"));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerPanel, BorderLayout.NORTH);

        // Panel de liste avec BoxLayout pour alignement vertical
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        loadAntecedents();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de boutons
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Enregistrer");
        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> save());

        southPanel.add(btnCancel);
        southPanel.add(btnSave);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void loadAntecedents() {
        // SQL corrigé pour correspondre à votre schéma (id_entite et nom)
        String sql = "SELECT a.id_entite, a.nom, " +
                "(SELECT 1 FROM Patient_Antecedent pa WHERE pa.patient_id = ? AND pa.antecedent_id = a.id_entite) as is_linked " +
                "FROM Antecedent a ORDER BY a.nom ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("id_entite");
                String nom = rs.getString("nom");
                boolean isLinked = rs.getInt("is_linked") == 1;

                JCheckBox cb = new JCheckBox(nom);
                cb.putClientProperty("id", id);
                cb.setSelected(isLinked);
                cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
                cb.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                checkBoxes.add(cb);
                listPanel.add(cb);
            }

            if (checkBoxes.isEmpty()) {
                listPanel.add(new JLabel("<html><i>Aucun antécédent configuré dans le système.</i></html>"));
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void save() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Supprimer les anciens liens pour ce patient
                String sqlDelete = "DELETE FROM Patient_Antecedent WHERE patient_id = ?";
                try (PreparedStatement del = conn.prepareStatement(sqlDelete)) {
                    del.setLong(1, patientId);
                    del.executeUpdate();
                }

                // 2. Insérer les nouveaux liens cochés
                String sqlInsert = "INSERT INTO Patient_Antecedent (patient_id, antecedent_id) VALUES (?, ?)";
                try (PreparedStatement ins = conn.prepareStatement(sqlInsert)) {
                    for (JCheckBox cb : checkBoxes) {
                        if (cb.isSelected()) {
                            ins.setLong(1, patientId);
                            ins.setLong(2, (Long) cb.getClientProperty("id"));
                            ins.addBatch();
                        }
                    }
                    ins.executeBatch();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Antécédents mis à jour avec succès.");
                dispose();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }
}