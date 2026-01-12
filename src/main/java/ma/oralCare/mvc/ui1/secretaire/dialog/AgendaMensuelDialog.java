package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.conf.SessionFactory;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AgendaMensuelDialog extends JDialog {
    private JComboBox<String> cbMedecin, cbMois;
    private JSpinner spinAnnee;
    private JPanel daysPanel;
    private List<JCheckBox> dayCheckBoxes = new ArrayList<>();
    private String[] joursSemaine = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

    public AgendaMensuelDialog(Frame parent) {
        super(parent, "Disponibilité du Médecin", true);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        setupUI();
        loadMedecins();
    }

    private void setupUI() {
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Médecin :"));
        cbMedecin = new JComboBox<>();
        form.add(cbMedecin);

        form.add(new JLabel("Mois :"));
        cbMois = new JComboBox<>(new String[]{"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"});
        form.add(cbMois);

        form.add(new JLabel("Année :"));
        spinAnnee = new JSpinner(new SpinnerNumberModel(2025, 2025, 2100, 1));
        form.add(spinAnnee);

        add(form, BorderLayout.NORTH);

        daysPanel = new JPanel();
        daysPanel.setLayout(new BoxLayout(daysPanel, BoxLayout.Y_AXIS));
        daysPanel.setBorder(BorderFactory.createTitledBorder("Jours Non Disponibles"));
        for (String jour : joursSemaine) {
            JCheckBox cb = new JCheckBox(jour);
            dayCheckBoxes.add(cb);
            daysPanel.add(cb);
        }
        add(new JScrollPane(daysPanel), BorderLayout.CENTER);

        JButton btnSave = new JButton("Enregistrer l'Agenda");
        btnSave.addActionListener(e -> saveAgenda());
        add(btnSave, BorderLayout.SOUTH);
    }

    private void loadMedecins() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT u.id_entite, u.nom FROM Medecin m JOIN utilisateur u ON m.id_entite = u.id_entite");
            while (rs.next()) {
                cbMedecin.addItem(rs.getLong("id_entite") + " - " + rs.getString("nom"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void saveAgenda() {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. BaseEntity
                PreparedStatement psB = conn.prepareStatement("INSERT INTO BaseEntity (date_creation) VALUES (NOW())", Statement.RETURN_GENERATED_KEYS);
                psB.executeUpdate();
                ResultSet rs = psB.getGeneratedKeys(); rs.next();
                long agendaId = rs.getLong(1);

                // 2. AgendaMensuel
                String sqlA = "INSERT INTO AgendaMensuel (id_entite, mois, annee, medecin_id) VALUES (?, ?, ?, ?)";
                PreparedStatement psA = conn.prepareStatement(sqlA);
                psA.setLong(1, agendaId);
                psA.setString(2, cbMois.getSelectedItem().toString());
                psA.setInt(3, (Integer) spinAnnee.getValue());
                psA.setLong(4, Long.parseLong(cbMedecin.getSelectedItem().toString().split(" - ")[0]));
                psA.executeUpdate();

                // 3. Jours non disponibles
                PreparedStatement psJ = conn.prepareStatement("INSERT INTO AgendaMensuel_JourNonDisponible (agenda_id, jour_non_disponible) VALUES (?, ?)");
                for (JCheckBox cb : dayCheckBoxes) {
                    if (cb.isSelected()) {
                        psJ.setLong(1, agendaId);
                        psJ.setString(2, cb.getText());
                        psJ.addBatch();
                    }
                }
                psJ.executeBatch();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Agenda mis à jour !");
                dispose();
            } catch (SQLException ex) { conn.rollback(); throw ex; }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage()); }
    }
}