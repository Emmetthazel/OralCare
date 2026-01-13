package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientListView extends JPanel {

    private JTable tablePatients;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbSexe, cbAssurance, cbStatut;
    private JLabel lblDetails;


    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color BORDER_COLOR = new Color(220, 220, 220);

    public PatientListView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- 1. SECTION HAUTE (Titre + Barre d'outils) ---
        JPanel northPanel = new JPanel(new BorderLayout(10, 10));
        northPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("👥 Gestion des Patients");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        northPanel.add(lblTitle, BorderLayout.NORTH);

        // BARRE D'OUTILS (BorderLayout pour séparer gauche et droite)
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Zone Gauche : Filtres (Taille automatique selon le texte)
        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftGroup.setOpaque(false);

        txtSearch = new JTextField(12);
        cbSexe = new JComboBox<>(new String[]{"Sexe: Tous", "M", "F"});
        cbAssurance = new JComboBox<>(new String[]{"Assurance: Toutes", "CNOPS", "CNSS", "RMA", "SAHAM"});
        cbStatut = new JComboBox<>(new String[]{"Statut: Tous", "Actif", "Inactif"});

        cbSexe.setBackground(Color.WHITE);
        cbAssurance.setBackground(Color.WHITE);
        cbStatut.setBackground(Color.WHITE);

        leftGroup.add(new JLabel("🔍"));
        leftGroup.add(txtSearch);
        leftGroup.add(cbSexe);
        leftGroup.add(cbAssurance);
        leftGroup.add(cbStatut);

        // Zone Droite : Bouton Rechercher à l'extrême droite
        JPanel rightGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightGroup.setOpaque(false);

        JButton btnSearch = new JButton("🔍 Rechercher");
        stylePrimaryButton(btnSearch); // Style avec texte sombre
        rightGroup.add(btnSearch);

        toolbar.add(leftGroup, BorderLayout.WEST);
        toolbar.add(rightGroup, BorderLayout.EAST);

        northPanel.add(toolbar, BorderLayout.CENTER);
        add(northPanel, BorderLayout.NORTH);

        // --- 2. CENTRE : TABLE DES PATIENTS ---
        String[] columns = {"N°", "Nom & Prénom", "Date Naissance", "Sexe", "Assurance", "Statut", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablePatients = new JTable(tableModel);
        tablePatients.setRowHeight(40);
        tablePatients.getTableHeader().setBackground(new Color(240, 240, 240));
        tablePatients.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablePatients.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablePatients);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. BAS : ZONE DÉTAILS ET ACTIONS ---
        JPanel southPanel = new JPanel(new BorderLayout(15, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));

        // Label HTML pour l'affichage amélioré
        lblDetails = new JLabel("<html><i style='color: gray;'>Veuillez sélectionner un patient...</i></html>");
        lblDetails.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        southPanel.add(lblDetails, BorderLayout.CENTER);

        // Boutons d'action
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);

        JButton btnDossier = new JButton("📁 Ouvrir Dossier Médical");
        JButton btnConsult = new JButton("➕ Créer Consultation");
        JButton btnEdit = new JButton("✏ Modifier");

        styleSecondaryButton(btnEdit);
        styleAccentButton(btnConsult);
        stylePrimaryButton(btnDossier);

        actionPanel.add(btnEdit);
        actionPanel.add(btnConsult);
        actionPanel.add(btnDossier);

        southPanel.add(actionPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);

        tablePatients.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDetailsArea();
            }
        });

        btnDossier.addActionListener(e -> {
            int row = tablePatients.getSelectedRow();
            if (row != -1) {
                // Ces données proviendront normalement de vos objets DAO/Service
                String nom = tableModel.getValueAt(row, 1).toString();
                String cin = ""; // Colonne cin dans table Patient
                String assurance = tableModel.getValueAt(row, 4).toString(); // Enum CNOPS, CNSS, etc.
                String sexe = tableModel.getValueAt(row, 3).toString(); // MALE, FEMALE

                // Les données de la table SituationFinanciere seront récupérées depuis la base de données
                double totalActes = 0.00;
                double totalPaye = 0.00;
                double credit = 0.00;

                MainFrame topFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                topFrame.getMedicalRecordDetailView().loadPatientData(
                        nom, "", cin, assurance, sexe, "12/01/2026", totalActes, totalPaye, credit
                );
                topFrame.showView("Dossiers Médicaux");
            }
        });
    }

    private void updateDetailsArea() {
        int row = tablePatients.getSelectedRow();
        if (row != -1) {
            String nom  = tableModel.getValueAt(row, 1).toString();
            String dn   = tableModel.getValueAt(row, 2).toString();
            String sexe = tableModel.getValueAt(row, 3).toString();
            String ass  = tableModel.getValueAt(row, 4).toString();

            String htmlText =
                    "<html>" +
                            "<b>Patient:</b> " + nom + "<br>" +
                            "<b>Né(e) le:</b> " + dn + "<br>" +
                            "<b>Sexe:</b> " + sexe + "<br>" +
                            "<b>Tél:</b> -<br>" +
                            "<b>Assurance:</b> " + ass +
                    "</html>";

            lblDetails.setText(htmlText);
        } else {
            lblDetails.setText("<html><i>Veuillez sélectionner un patient...</i></html>");
        }
    }



    // --- HELPERS DE STYLE ---

    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(41, 128, 185));
        b.setForeground(TEXT_COLOR); // Texte sombre demandé
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleAccentButton(JButton b) {
        b.setBackground(new Color(39, 174, 96));
        b.setForeground(TEXT_COLOR); // Texte sombre demandé
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(TEXT_COLOR); // Texte sombre demandé
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }
}