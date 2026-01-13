package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.SideBarPanel;
import ma.oralCare.mvc.utils.StatutTranslator;
import ma.oralCare.mvc.ui1.secretaire.dialog.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

/**
 * Module de gestion des patients avec indicateurs métier (Assurance, RDV jour, Statut).
 */
public class PatientManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable tablePatients;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    
    // Boutons d'action pour gérer l'état
    private JButton btnEdit, btnDossier, btnRDV;

    public PatientManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        setupTopBar();
        setupTable();
        setupBottomActions();

        refreshTable();
    }

    private void setupTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("🔍 "));
        txtSearch = new JTextField(20);
        txtSearch.setToolTipText("Recherche par Nom, CIN ou Téléphone");
        txtSearch.addActionListener(e -> refreshTable(true)); // Préserver la sélection pendant la recherche
        searchPanel.add(txtSearch);

        JButton btnAdd = new JButton("＋ Nouveau Patient");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            new PatientDialog(mainFrame, null).setVisible(true);
            refreshTable(false); // Ne pas préserver la sélection après ajout
        });

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void setupTable() {
        // Colonnes enrichies selon les standards métier
        String[] columns = {"ID", "CIN", "Nom & Prénom", "Téléphone", "Assurance", "RDV Aujourd'hui", "Dossier"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablePatients = new JTable(tableModel);
        tablePatients.setRowHeight(40);
        tablePatients.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // --- RENDU VISUEL DES COLONNES ---

        // 1. Rendu Assurance (Index 4)
        tablePatients.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                String val = (v != null) ? v.toString() : "NONE";
                if (val.equalsIgnoreCase("NONE") || val.isEmpty()) {
                    lbl.setText("⚠ Absence");
                    lbl.setForeground(new Color(231, 76, 60));
                } else {
                    lbl.setText("✓ " + val);
                    lbl.setForeground(new Color(39, 174, 96));
                }
                return lbl;
            }
        });

        // 2. Rendu RDV Aujourd'hui (Index 5)
        tablePatients.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                if (v != null && (boolean)v) {
                    lbl.setText("🕘 OUI");
                    lbl.setForeground(new Color(41, 128, 185));
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                } else {
                    lbl.setText("-");
                    lbl.setForeground(Color.LIGHT_GRAY);
                }
                return lbl;
            }
        });

        // Double clic pour ouvrir le dossier
        tablePatients.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) handleAction("DOSSIER");
            }
        });

        add(new JScrollPane(tablePatients), BorderLayout.CENTER);
    }

    private void setupBottomActions() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setOpaque(false);

        btnEdit = new JButton("📝 Fiche");
        btnDossier = new JButton("📂 Dossier Médical");
        btnRDV = new JButton("📅 Planifier RDV");

        // État initial : désactivé car aucun patient sélectionné
        updateButtonStates(false);

        btnEdit.addActionListener(e -> handleAction("EDIT"));
        btnDossier.addActionListener(e -> handleAction("DOSSIER"));
        btnRDV.addActionListener(e -> handleAction("RDV"));

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDossier);
        bottomPanel.add(new JSeparator(JSeparator.VERTICAL));
        bottomPanel.add(btnRDV);

        add(bottomPanel, BorderLayout.SOUTH);
        
        // Ajouter un listener pour gérer la sélection
        tablePatients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = tablePatients.getSelectedRow() != -1;
                updateButtonStates(hasSelection);
            }
        });
    }
    
    private void updateButtonStates(boolean hasSelection) {
        btnEdit.setEnabled(hasSelection);
        btnDossier.setEnabled(hasSelection);
        btnRDV.setEnabled(hasSelection);
        
        // Couleur différente quand désactivé
        Color disabledColor = new Color(200, 200, 200);
        Color enabledColor = new Color(52, 152, 219);
        
        btnEdit.setBackground(hasSelection ? enabledColor : disabledColor);
        btnDossier.setBackground(hasSelection ? new Color(46, 204, 113) : disabledColor);
        btnRDV.setBackground(hasSelection ? new Color(241, 196, 15) : disabledColor);
        
        // Texte indicatif quand désactivé
        if (!hasSelection) {
            btnDossier.setText("📂 Dossier Médical (Sélectionner un patient)");
            btnEdit.setText("📝 Fiche (Sélectionner un patient)");
            btnRDV.setText("📅 Planifier RDV (Sélectionner un patient)");
        } else {
            btnDossier.setText("📂 Dossier Médical");
            btnEdit.setText("📝 Fiche");
            btnRDV.setText("📅 Planifier RDV");
        }
    }

    public void refreshTable() {
        refreshTable(true); // Par défaut, préserver la sélection
    }
    
    /**
     * Rafraîchit la table des patients avec option de préserver la sélection
     * @param preserveSelection Si true, préserve la sélection actuelle
     */
    public void refreshTable(boolean preserveSelection) {
        // Sauvegarder la sélection actuelle si demandé
        Long selectedPatientId = null;
        if (preserveSelection && tablePatients.getSelectedRow() != -1) {
            selectedPatientId = (Long) tableModel.getValueAt(tablePatients.getSelectedRow(), 0);
        }
        
        tableModel.setRowCount(0);
        String filter = txtSearch.getText().trim();

        // Requête complexe pour récupérer les infos patient ET la présence de RDV aujourd'hui
        String sql = "SELECT p.id_entite, p.cin, p.nom, p.prenom, p.telephone, p.assurance, " +
                "(SELECT COUNT(*) FROM RDV r JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                " WHERE d.patient_id = p.id_entite AND r.date = CURDATE()) as has_rdv " +
                "FROM Patient p";

        if (!filter.isEmpty()) {
            sql += " WHERE p.nom LIKE ? OR p.prenom LIKE ? OR p.cin LIKE ?";
        }
        sql += " ORDER BY p.nom ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (!filter.isEmpty()) {
                String p = "%" + filter + "%";
                ps.setString(1, p); 
                ps.setString(2, p); 
                ps.setString(3, p);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getString("cin"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getString("telephone"),
                        rs.getString("assurance"),
                        rs.getInt("has_rdv") > 0, // Boolean pour le rendu
                        StatutTranslator.traduireStatut("COMPLETED") // Statut traduit
                });
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        
        // Restaurer la sélection si demandé
        if (preserveSelection && selectedPatientId != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (selectedPatientId.equals(tableModel.getValueAt(i, 0))) {
                    tablePatients.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private void handleAction(String action) {
        int row = tablePatients.getSelectedRow();
        if (row == -1) return;

        Long id = (Long) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 2);

        switch (action) {
            case "EDIT": 
                new PatientDialog(mainFrame, id).setVisible(true); 
                refreshTable(false); // Ne pas préserver la sélection après modification
                break;
            case "DOSSIER": 
                // Naviguer vers l'interface DossierMedicalPanel existante et charger le patient
                mainFrame.showView("DOSSIERS");
                // Charger le patient dans le dossier médical
                JPanel dossierPanel = mainFrame.getDossierMedicalPanel();
                if (dossierPanel instanceof DossierMedicalSecretairePanel) {
                    String patientName = getPatientNameFromTable(id);
                    ((DossierMedicalSecretairePanel) dossierPanel).loadPatientFromSelection(id, patientName);
                }
                break;
            case "RDV": 
                new RendezVousDialog(mainFrame, null).setVisible(true); 
                refreshTable(false); // Ne pas préserver la sélection après ajout
                break;
        }
        
        // Ne pas appeler refreshTable() ici pour préserver l'état des boutons
        // La table sera rafraîchie uniquement quand nécessaire (ajout/modification de patient)
    }
    
    /**
     * Charge le patient sélectionné dans l'interface DossierMedicalPanel existante
     */
    private void loadPatientInDossierMedical(Long patientId, String patientName) {
        try {
            // 1. Naviguer vers la vue DOSSIERS
            mainFrame.showView("DOSSIERS");
            
            // 2. Mettre en évidence le bouton "Dossiers Médicaux" dans le sidebar
            SideBarPanel sideBar = mainFrame.getSideBarPanel();
            if (sideBar != null) {
                sideBar.highlightButtonByViewID("DOSSIERS");
            }
            
            // 3. Obtenir le panel de dossier médical et charger le patient sélectionné
            SwingUtilities.invokeLater(() -> {
                try {
                    // Utiliser la nouvelle méthode pour accéder au panel de dossier médical
                    JPanel dossierPanel = mainFrame.getDossierMedicalPanel();
                    
                    if (dossierPanel != null) {
                        // Charger le patient selon le type de panel
                        if (dossierPanel instanceof DossierMedicalPanel) {
                            ((DossierMedicalPanel) dossierPanel).loadPatientFromSelection(patientId, patientName);
                        } else if (dossierPanel instanceof DossierMedicalSecretairePanel) {
                            ((DossierMedicalSecretairePanel) dossierPanel).loadPatientFromSelection(patientId, patientName);
                        }
                        
                        // Afficher un message de confirmation
                        JOptionPane.showMessageDialog(mainFrame, 
                            "📂 Dossier médical chargé pour : " + patientName, 
                            "Chargement réussi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, 
                            "Erreur : Aucun panel de dossier médical trouvé. Vérifiez la configuration.", 
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Erreur lors du chargement du dossier : " + e.getMessage(), 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la navigation : " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Récupère le nom du patient depuis la table en utilisant son ID
     * @param patientId L'ID du patient
     * @return Le nom complet du patient
     */
    private String getPatientNameFromTable(Long patientId) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Long id = (Long) tableModel.getValueAt(i, 0);
            if (patientId.equals(id)) {
                return (String) tableModel.getValueAt(i, 2); // Colonne "Nom & Prénom"
            }
        }
        return "Patient inconnu";
    }
}
