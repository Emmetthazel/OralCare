package ma.oralCare.mvc.ui1.secretaire;

import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * DossierMedicalSecretairePanel : Vue restreinte du dossier médical.
 * Accès uniquement en LECTURE SEULE pour les secrétaires.
 * Les informations confidentielles (notes privées, diagnostics complexes) sont masquées.
 */
public class DossierMedicalSecretairePanel extends JPanel {

    private final MainFrame mainFrame;
    private Long currentPatientId;

    // Composants d'information (Lecture Seule)
    private JTextField txtNom, txtPrenom, txtAge, txtSexe, txtTel, txtEmail, txtAdresse, txtMedecinRef;
    private JTable tableHistorique;
    private DefaultTableModel modelHistorique;

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
     * Section Nord : Identité du patient (Champs non éditables)
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
        txtAdresse = createReadOnlyField(); txtMedecinRef = createReadOnlyField();

        // Labels avec style
        headerPanel.add(new JLabel("Nom:")); headerPanel.add(txtNom);
        headerPanel.add(new JLabel("Prénom:")); headerPanel.add(txtPrenom);
        headerPanel.add(new JLabel("Âge:")); headerPanel.add(txtAge);
        headerPanel.add(new JLabel("Sexe:")); headerPanel.add(txtSexe);
        headerPanel.add(new JLabel("Téléphone:")); headerPanel.add(txtTel);
        headerPanel.add(new JLabel("Email:")); headerPanel.add(txtEmail);
        headerPanel.add(new JLabel("Adresse:")); headerPanel.add(txtAdresse);
        headerPanel.add(new JLabel("Médecin Réf:")); headerPanel.add(txtMedecinRef);

        add(headerPanel, BorderLayout.NORTH);
    }

    /**
     * Section Centrale : Historique des actes et prescriptions (Sans détails cliniques sensibles)
     */
    private void setupCenter() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new TitledBorder(new LineBorder(BORDER_COLOR), "Historique des Passages & Prescriptions"));

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
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
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
        txtAdresse.setText(""); txtMedecinRef.setText("");
        modelHistorique.setRowCount(0);
    }
}