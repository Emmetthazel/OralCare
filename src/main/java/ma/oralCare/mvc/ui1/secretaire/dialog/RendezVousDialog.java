package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Dialogue complet pour la création et modification de rendez-vous.
 * Intègre le pré-remplissage depuis le planning visuel.
 */
public class RendezVousDialog extends JDialog {

    private final MainFrame mainFrame;
    private final Long rdvId; // null pour un nouveau RDV

    private JComboBox<String> comboPatient, comboStatut;
    private JTextField txtDate, txtHeure, txtMotif;
    private JTextArea txtNote;

    // Mapping pour lier le nom sélectionné à l'ID du dossier médical
    private final Map<String, Long> patientDossierMap = new HashMap<>();

    public RendezVousDialog(MainFrame parent, Long rdvId) {
        super(parent, "Détails du Rendez-vous", true);
        this.mainFrame = parent;
        this.rdvId = rdvId;

        setLayout(new BorderLayout());
        setSize(500, 550);
        setLocationRelativeTo(parent);

        setupForm();
        setupButtons();

        loadPatients(); // Remplit la combo box

        if (rdvId != null) {
            loadRDVData();
        } else {
            // Valeurs par défaut (seront écrasées par setScheduledDateTime si appelé)
            txtDate.setText(LocalDate.now().toString());
            txtHeure.setText(LocalTime.now().toString().substring(0, 5));
        }
    }

    /**
     * Méthode cruciale appelée par VisualAgendaPanel pour injecter
     * la date et l'heure du créneau cliqué.
     */
    public void setScheduledDateTime(LocalDate date, LocalTime time) {
        if (txtDate != null) txtDate.setText(date.toString());
        if (txtHeure != null) txtHeure.setText(time.toString().substring(0, 5));
    }

    private void setupForm() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        // Ligne 1 : Patient
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Patient :"), gbc);
        comboPatient = new JComboBox<>();
        gbc.gridx = 1;
        formPanel.add(comboPatient, gbc);

        // Ligne 2 : Date
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Date (AAAA-MM-JJ) :"), gbc);
        txtDate = new JTextField();
        gbc.gridx = 1;
        formPanel.add(txtDate, gbc);

        // Ligne 3 : Heure
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Heure (HH:MM) :"), gbc);
        txtHeure = new JTextField();
        gbc.gridx = 1;
        formPanel.add(txtHeure, gbc);

        // Ligne 4 : Motif
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Motif :"), gbc);
        txtMotif = new JTextField();
        gbc.gridx = 1;
        formPanel.add(txtMotif, gbc);

        // Ligne 5 : Statut
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Statut :"), gbc);
        comboStatut = new JComboBox<>(new String[]{"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"});
        gbc.gridx = 1;
        formPanel.add(comboStatut, gbc);

        // Ligne 6 : Note
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Notes :"), gbc);
        txtNote = new JTextArea(4, 20);
        txtNote.setLineWrap(true);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtNote), gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void setupButtons() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnCancel = new JButton("Annuler");
        JButton btnSave = new JButton("Enregistrer");

        btnSave.setBackground(new Color(41, 128, 185));
        btnSave.setForeground(Color.WHITE);

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> saveRDV());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void loadPatients() {
        String sql = "SELECT p.nom, p.prenom, d.id_entite FROM Patient p " +
                "JOIN DossierMedicale d ON p.id_entite = d.patient_id";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("nom") + " " + rs.getString("prenom");
                patientDossierMap.put(name, rs.getLong("id_entite"));
                comboPatient.addItem(name);
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadRDVData() {
        String sql = "SELECT r.*, p.nom, p.prenom FROM RDV r " +
                "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                "JOIN Patient p ON d.patient_id = p.id_entite WHERE r.id_entite = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, rdvId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                comboPatient.setSelectedItem(rs.getString("nom") + " " + rs.getString("prenom"));
                txtDate.setText(rs.getDate("date").toString());
                txtHeure.setText(rs.getTime("heure").toString().substring(0, 5));
                txtMotif.setText(rs.getString("motif"));
                comboStatut.setSelectedItem(rs.getString("statut"));
                txtNote.setText(rs.getString("note_medecin"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void saveRDV() {
        Long dossierId = patientDossierMap.get((String) comboPatient.getSelectedItem());
        if (dossierId == null) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un patient valide.");
            return;
        }

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            if (rdvId == null) {
                // INSERTION : Créer BaseEntity d'abord
                long newId = System.currentTimeMillis();

                String sqlBase = "INSERT INTO BaseEntity (id_entite, date_creation) VALUES (?, NOW())";
                try (PreparedStatement psB = conn.prepareStatement(sqlBase)) {
                    psB.setLong(1, newId);
                    psB.executeUpdate();
                }

                String sqlRDV = "INSERT INTO RDV (date, heure, motif, statut, note_medecin, dossier_medicale_id, id_entite) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement psR = conn.prepareStatement(sqlRDV)) {
                    psR.setDate(1, Date.valueOf(txtDate.getText()));
                    psR.setTime(2, Time.valueOf(txtHeure.getText() + ":00"));
                    psR.setString(3, txtMotif.getText());
                    psR.setString(4, (String) comboStatut.getSelectedItem());
                    psR.setString(5, txtNote.getText());
                    psR.setLong(6, dossierId);
                    psR.setLong(7, newId);
                    psR.executeUpdate();
                }
            } else {
                // MISE À JOUR
                String sqlUpdate = "UPDATE RDV SET date=?, heure=?, motif=?, statut=?, note_medecin=?, dossier_medicale_id=? WHERE id_entite=?";
                try (PreparedStatement psU = conn.prepareStatement(sqlUpdate)) {
                    psU.setDate(1, Date.valueOf(txtDate.getText()));
                    psU.setTime(2, Time.valueOf(txtHeure.getText() + ":00"));
                    psU.setString(3, txtMotif.getText());
                    psU.setString(4, (String) comboStatut.getSelectedItem());
                    psU.setString(5, txtNote.getText());
                    psU.setLong(6, dossierId);
                    psU.setLong(7, rdvId);
                    psU.executeUpdate();
                }
            }

            conn.commit();
            mainFrame.refreshCurrentView(); // Rafraîchit l'agenda visuel instantanément
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur SQL : " + ex.getMessage());
        }
    }
}