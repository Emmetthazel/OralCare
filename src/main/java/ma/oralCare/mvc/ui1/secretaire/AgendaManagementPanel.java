package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui1.MainFrame;
import ma.oralCare.mvc.ui1.secretaire.dialog.*;
import ma.oralCare.mvc.utils.StatutTranslator;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Panel de gestion de l'agenda pour la secrétaire.
 */
public class AgendaManagementPanel extends JPanel {
    private final MainFrame mainFrame;
    private JTable tableRDV;
    private DefaultTableModel model;

    public AgendaManagementPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        setupTopPanel();
        setupCenterTable();
        setupBottomActions();

        refreshData();
    }

    // --- BARRE SUPÉRIEURE : TITRE UNIQUE ---
    private void setupTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📅 Gestion des Rendez-vous");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(44, 62, 80));
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
    }

    // --- CENTRE : TABLEAU DES RENDEZ-VOUS ---
    private void setupCenterTable() {
        String[] columns = {"ID", "Date", "Heure", "Patient", "Médecin", "Motif", "Statut"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableRDV = new JTable(model);
        tableRDV.setRowHeight(35);
        tableRDV.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));

        // Application du rendu de couleur selon le statut
        tableRDV.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                String statut = t.getValueAt(r, 6).toString();

                if (!s) { // Si la ligne n'est pas sélectionnée
                    switch (statut) {
                        case "CONFIRMED": comp.setBackground(new Color(210, 255, 210)); break; // Vert
                        case "PENDING": comp.setBackground(new Color(255, 240, 200)); break;   // Orange/Jaune
                        case "CANCELLED": comp.setBackground(new Color(255, 210, 210)); break; // Rouge
                        case "COMPLETED": comp.setBackground(new Color(210, 230, 255)); break; // Bleu
                        default: comp.setBackground(Color.WHITE);
                    }
                }
                return comp;
            }
        });

        add(new JScrollPane(tableRDV), BorderLayout.CENTER);
    }

    // --- BAS : ACTIONS ---
    private void setupBottomActions() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        bottomPanel.setOpaque(false);

        JButton btnAdd = new JButton("➕ Nouveau RDV");
        JButton btnEdit = new JButton("📝 Modifier");
        JButton btnCancel = new JButton("❌ Annuler");
        JButton btnDossier = new JButton("📂 Voir Dossier");
        JButton btnAgenda = new JButton("📅 Dispos Médecins");
        JButton btnFileAttente = new JButton("⏳ File d'Attente");
        JButton btnEmail = new JButton("📧 Envoyer Email");

        // ActionListeners
        btnAdd.addActionListener(e -> {
            // Rediriger vers l'agenda visuel pour créer un RDV
            mainFrame.showView("VISUAL_AGENDA");
        });

        btnEdit.addActionListener(e -> handleAction("EDIT"));
        btnCancel.addActionListener(e -> handleAction("CANCEL"));
        btnDossier.addActionListener(e -> handleAction("DOSSIER"));

        btnAgenda.addActionListener(e -> {
            new AgendaMensuelDialog(mainFrame).setVisible(true);
        });

        btnFileAttente.addActionListener(e -> {
            mainFrame.showView("FILE_ATTENTE");
        });

        btnEmail.addActionListener(e -> handleAction("EMAIL"));

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnEdit);
        bottomPanel.add(btnCancel);
        bottomPanel.add(new JSeparator(JSeparator.VERTICAL));
        bottomPanel.add(btnDossier);
        bottomPanel.add(btnAgenda);
        bottomPanel.add(btnFileAttente);
        bottomPanel.add(btnEmail);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- LOGIQUE DE DONNÉES ---

    public void refreshData() {
        model.setRowCount(0);

        // Récupérer l'ID du médecin connecté depuis la session
        Long medecinId = getConnectedMedecinId();
        
        String sql = "SELECT r.id_entite, r.date, r.heure, p.nom as p_nom, u.nom as m_nom, r.motif, r.statut " +
                "FROM RDV r " +
                "JOIN DossierMedicale d ON r.dossier_medicale_id = d.id_entite " +
                "JOIN Patient p ON d.patient_id = p.id_entite " +
                "LEFT JOIN Medecin m ON d.medecin_id = m.id_entite " +
                "LEFT JOIN utilisateur u ON m.id_entite = u.id_entite " +
                "WHERE d.medecin_id = ? " +  // Filtrer par médecin connecté
                "ORDER BY r.date DESC, r.heure ASC";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String statutTraduit = StatutTranslator.traduireStatutRDV(rs.getString("statut"));
                model.addRow(new Object[]{
                        rs.getLong("id_entite"),
                        rs.getDate("date"),
                        rs.getTime("heure"),
                        rs.getString("p_nom"),
                        "Dr. " + rs.getString("m_nom"),
                        rs.getString("motif"),
                        statutTraduit
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur de chargement de l'agenda : " + e.getMessage());
        }
    }
    
    /**
     * Récupère l'ID du médecin connecté depuis la session
     * @return L'ID du médecin connecté
     */
    private Long getConnectedMedecinId() {
        try {
            // Récupérer l'ID du médecin depuis la base de données
            // On suppose qu'il y a un seul médecin dans le cabinet
            String sql = "SELECT m.id_entite FROM Medecin m LIMIT 1";
            try (Connection conn = SessionFactory.getInstance().getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong("id_entite");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1L; // Valeur par défaut si aucun médecin trouvé
    }

    private void handleAction(String action) {
        int row = tableRDV.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un rendez-vous.");
            return;
        }

        Long rdvId = (Long) tableRDV.getValueAt(row, 0);

        switch (action) {
            case "EDIT":
                new RendezVousDialog(mainFrame, rdvId).setVisible(true);
                refreshData();
                break;
            case "CANCEL":
                if (JOptionPane.showConfirmDialog(this, "Annuler ce rendez-vous ?") == JOptionPane.YES_OPTION) {
                    updateStatus(rdvId, "CANCELLED");
                }
                break;
            case "DOSSIER":
                // Logique pour ouvrir le dossier associé au patient du RDV
                JOptionPane.showMessageDialog(this, "Ouverture du dossier médical...");
                break;
            case "EMAIL":
                // Récupérer l'email du patient et ouvrir le client email
                try (Connection conn = SessionFactory.getInstance().getConnection()) {
                    String sql = "SELECT p.nom, p.prenom, u.email " +
                            "FROM RDV r " +
                            "JOIN DossierMedicale dm ON r.dossier_medicale_id = dm.id_entite " +
                            "JOIN Patient p ON dm.patient_id = p.id_entite " +
                            "LEFT JOIN utilisateur u ON p.id_entite = u.id_entite " +
                            "WHERE r.id_entite = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setLong(1, rdvId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String email = rs.getString("email");
                        String nom = rs.getString("nom") + " " + rs.getString("prenom");
                        if (email != null && !email.isEmpty()) {
                            // Ouvrir le client email par défaut
                            String subject = "Rappel Rendez-vous - Cabinet Dentaire";
                            String body = "Bonjour " + nom + ",\n\nRappel de votre rendez-vous.";
                            try {
                                java.awt.Desktop.getDesktop().mail(
                                        new java.net.URI("mailto:" + email + "?subject=" + 
                                        URLEncoder.encode(subject, "UTF-8") + 
                                        "&body=" + URLEncoder.encode(body, "UTF-8"))
                                );
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(this, 
                                        "Impossible d'ouvrir le client email : " + ex.getMessage(),
                                        "Erreur", JOptionPane.ERROR_MESSAGE);
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                    "Aucune adresse email trouvée pour " + nom,
                                    "Information", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, 
                            "Erreur lors de l'envoi de l'email : " + e.getMessage(),
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                break;
        }
    }

    private void updateStatus(Long id, String status) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE RDV SET statut = ? WHERE id_entite = ?")) {
            ps.setString(1, status);
            ps.setLong(2, id);
            ps.executeUpdate();
            refreshData();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}