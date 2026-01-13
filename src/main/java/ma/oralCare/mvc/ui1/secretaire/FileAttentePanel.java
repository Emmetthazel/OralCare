package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

/**
 * Panel de gestion de la file d'attente des patients.
 * Permet de gérer l'ordre d'arrivée et de notifier le médecin.
 */
public class FileAttentePanel extends JPanel {

    private final MainFrame mainFrame;
    private JTable tableAttente;
    private DefaultTableModel model;
    private JButton btnMonter, btnDescendre, btnAppeler, btnRetirer;

    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);

    public FileAttentePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 246, 250));

        setupNorthPanel();
        setupCenterPanel();
        setupSouthPanel();

        refreshData();
    }

    private void setupNorthPanel() {
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("⏳ File d'Attente");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(ACCENT_COLOR);
        northPanel.add(lblTitle, BorderLayout.WEST);

        JButton btnRefresh = new JButton("🔄 Actualiser");
        btnRefresh.addActionListener(e -> refreshData());
        northPanel.add(btnRefresh, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);
    }

    private void setupCenterPanel() {
        JPanel centerPanel = createStyledPanel("Liste d'Attente");
        String[] columns = {"Ordre", "Patient", "Heure Arrivée", "Motif", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableAttente = new JTable(model);
        tableAttente.setRowHeight(40);
        tableAttente.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableAttente.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Rendu visuel
        tableAttente.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected && row == 0) { // Premier patient
                    comp.setBackground(new Color(210, 255, 210)); // Vert clair
                }
                return comp;
            }
        });

        centerPanel.add(new JScrollPane(tableAttente), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void setupSouthPanel() {
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        southPanel.setOpaque(false);

        btnMonter = new JButton("⬆ Monter");
        btnDescendre = new JButton("⬇ Descendre");
        btnAppeler = new JButton("📢 Appeler Patient");
        btnRetirer = new JButton("❌ Retirer");

        btnAppeler.setBackground(SUCCESS_COLOR);
        btnAppeler.setForeground(Color.WHITE);
        btnAppeler.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnMonter.addActionListener(e -> movePatient(-1));
        btnDescendre.addActionListener(e -> movePatient(1));
        btnAppeler.addActionListener(e -> appelerPatient());
        btnRetirer.addActionListener(e -> retirerPatient());

        // Désactiver les boutons si aucune sélection
        tableAttente.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = tableAttente.getSelectedRow() != -1;
            btnMonter.setEnabled(hasSelection);
            btnDescendre.setEnabled(hasSelection);
            btnAppeler.setEnabled(hasSelection);
            btnRetirer.setEnabled(hasSelection);
        });

        southPanel.add(btnMonter);
        southPanel.add(btnDescendre);
        southPanel.add(new JSeparator(JSeparator.VERTICAL));
        southPanel.add(btnAppeler);
        southPanel.add(btnRetirer);

        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));
        return panel;
    }

    public void refreshData() {
        model.setRowCount(0);

        // Récupérer les patients en attente (RDV confirmés pour aujourd'hui, non encore consultés)
        String sql = "SELECT r.id_entite, p.nom, p.prenom, r.heure, r.motif, r.statut " +
                "FROM RDV r " +
                "JOIN DossierMedicale dm ON r.dossier_medicale_id = dm.id_entite " +
                "JOIN Patient p ON dm.patient_id = p.id_entite " +
                "WHERE r.date = CURDATE() " +
                "AND r.statut = 'CONFIRMED' " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM Consultation c " +
                "    WHERE c.dossier_medicale_id = dm.id_entite " +
                "    AND c.date = CURDATE()" +
                ") " +
                "ORDER BY r.heure ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int ordre = 1;
            while (rs.next()) {
                model.addRow(new Object[]{
                        ordre++,
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getTime("heure"),
                        rs.getString("motif"),
                        rs.getString("statut")
                });
            }

            if (model.getRowCount() == 0) {
                model.addRow(new Object[]{"-", "Aucun patient en attente", "-", "-", "-"});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement de la file d'attente : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void movePatient(int direction) {
        int row = tableAttente.getSelectedRow();
        if (row == -1 || row + direction < 0 || row + direction >= model.getRowCount()) {
            return;
        }

        // Échanger les lignes dans le modèle
        Vector<Object> currentRow = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            currentRow.add(model.getValueAt(row, i));
        }

        Vector<Object> targetRow = new Vector<>();
        for (int i = 0; i < model.getColumnCount(); i++) {
            targetRow.add(model.getValueAt(row + direction, i));
        }

        // Mettre à jour l'ordre
        currentRow.set(0, row + direction + 1);
        targetRow.set(0, row + 1);

        // Remplacer les lignes
        for (int i = 0; i < model.getColumnCount(); i++) {
            model.setValueAt(targetRow.get(i), row, i);
            model.setValueAt(currentRow.get(i), row + direction, i);
        }

        // Sélectionner la nouvelle position
        tableAttente.setRowSelectionInterval(row + direction, row + direction);
    }

    private void appelerPatient() {
        int row = tableAttente.getSelectedRow();
        if (row == -1) return;

        String patientName = (String) model.getValueAt(row, 1);
        JOptionPane.showMessageDialog(this,
                "📢 Appel du patient : " + patientName + "\n\nLe patient est invité à se présenter.",
                "Appel Patient",
                JOptionPane.INFORMATION_MESSAGE);

        // Ici, on pourrait envoyer une notification au médecin ou mettre à jour le statut
    }

    private void retirerPatient() {
        int row = tableAttente.getSelectedRow();
        if (row == -1) return;

        String patientName = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Retirer " + patientName + " de la file d'attente ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            model.removeRow(row);
            // Réorganiser les ordres
            for (int i = 0; i < model.getRowCount(); i++) {
                model.setValueAt(i + 1, i, 0);
            }
        }
    }
}
