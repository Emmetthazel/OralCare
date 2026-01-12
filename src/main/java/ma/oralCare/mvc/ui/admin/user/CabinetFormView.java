package ma.oralCare.mvc.ui.admin.user;

import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.ui1.Navigatable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class CabinetFormView extends JPanel {
    private final UserManagementController controller;
    private final Navigatable navigation;

    // Palette de couleurs alignée sur UserListView
    private final Color LIGHT_BG = new Color(245, 246, 250);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color PRIMARY_BLUE = new Color(41, 128, 185);

    // Champs
    private JTextField txtNom, txtEmail, txtTel1, txtTel2, txtCin, txtSite, txtInsta, txtFb, txtLogo, txtNum, txtRue, txtCP, txtVille, txtPays, txtComp;
    private JTextArea txtDesc;

    public CabinetFormView(UserManagementController controller, Navigatable navigation) {
        this.controller = controller;
        this.navigation = navigation;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initComponents();
    }

    private void initComponents() {
        // --- 1. HEADER (Style Gris Clair identique à UserListView) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(LIGHT_BG);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Configuration : Nouveau Cabinet Médical");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnBack = createStyledButton("⬅ Retour à la liste", PRIMARY_BLUE);
        btnBack.addActionListener(e -> navigation.showView("USERS"));

        header.add(title, BorderLayout.WEST);
        header.add(btnBack, BorderLayout.EAST);

        // --- 2. CORPS DU FORMULAIRE ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Section 1 : Identification (2 colonnes)
        mainContent.add(createSection("Informations Générales", new String[]{"Nom du Cabinet *", "Email Professionnel *", "Téléphone 1 *", "Téléphone 2", "Identifiant (CIN/IF)", "Logo (URL)"},
                new JTextField[]{txtNom = new JTextField(), txtEmail = new JTextField(), txtTel1 = new JTextField(), txtTel2 = new JTextField(), txtCin = new JTextField(), txtLogo = new JTextField()}));

        mainContent.add(Box.createVerticalStrut(20));

        // Section 2 : Adresse (2 colonnes)
        mainContent.add(createSection("Localisation & Adresse", new String[]{"Numéro", "Rue *", "Ville *", "Code Postal *", "Pays *", "Complément"},
                new JTextField[]{txtNum = new JTextField(), txtRue = new JTextField(), txtVille = new JTextField(), txtCP = new JTextField(), txtPays = new JTextField("Maroc"), txtComp = new JTextField()}));

        mainContent.add(Box.createVerticalStrut(20));

        // Section 3 : Digital & Description
        mainContent.add(createSection("Présence Digitale & Description", new String[]{"Site Web", "Instagram", "Facebook"},
                new JTextField[]{txtSite = new JTextField(), txtInsta = new JTextField(), txtFb = new JTextField()}));

        // Ajout de la description à part car c'est un JTextArea
        mainContent.add(new JLabel("  Description du cabinet :"));
        txtDesc = new JTextArea(4, 20);
        mainContent.add(new JScrollPane(txtDesc));

        // --- 3. FOOTER (Action) ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(LIGHT_BG);
        footer.setBorder(new EmptyBorder(10, 20, 10, 20));

        JButton btnSave = createStyledButton("✅ Enregistrer le Cabinet", SUCCESS_COLOR);
        btnSave.setPreferredSize(new Dimension(200, 40));
        btnSave.addActionListener(e -> handleSave());
        footer.add(btnSave);

        add(header, BorderLayout.NORTH);
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    /**
     * Crée une section avec deux colonnes (Label/Input | Label/Input)
     */
    private JPanel createSection(String title, String[] labels, JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)), title, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), PRIMARY_BLUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        for (int i = 0; i < labels.length; i++) {
            // Calcul de la position (2 colonnes)
            int row = i / 2;
            int col = (i % 2) * 2;

            gbc.gridy = row;
            gbc.gridx = col;
            panel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = col + 1;
            panel.add(fields[i], gbc);
        }
        return panel;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setForeground(color);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color), BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void handleSave() {
        if (txtNom.getText().isBlank() || txtEmail.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir au moins le nom et l'email.");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("nom", txtNom.getText());
        data.put("email", txtEmail.getText());
        data.put("tel1", txtTel1.getText());
        data.put("tel2", txtTel2.getText());
        data.put("cin", txtCin.getText());
        data.put("logo", txtLogo.getText());
        data.put("siteWeb", txtSite.getText());
        data.put("instagram", txtInsta.getText());
        data.put("facebook", txtFb.getText());
        data.put("description", txtDesc.getText());
        data.put("numero", txtNum.getText());
        data.put("rue", txtRue.getText());
        data.put("ville", txtVille.getText());
        data.put("codePostal", txtCP.getText());
        data.put("pays", txtPays.getText());
        data.put("complement", txtComp.getText());

        try {
            controller.addNewCabinet(data);
            JOptionPane.showMessageDialog(this, "Cabinet créé avec succès !");
            navigation.showView("USERS");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}