package ma.oralCare.mvc.ui.admin.user;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.mvc.ui1.Navigatable;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Vue d'administration : Gestion du personnel par Cabinet.
 */
public class UserListView extends JPanel {

    private final Map<String, JPanel> detailPanels = new HashMap<>();
    private final UserManagementController controller;
    private final CardLayout cardLayout;
    private final JPanel cards;
    private JPanel mainContent;
    private JTextField searchField;
    private String selectedEmail = null;

    // Palette de couleurs
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(192, 57, 43);
    private final Color WARNING_COLOR = new Color(243, 156, 18);
    private final Color LIGHT_BG = new Color(245, 246, 250);

    public UserListView(UserManagementController controller) {
        this.controller = controller;
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JPanel listView = new JPanel(new BorderLayout());
        listView.add(createSearchPanel(), BorderLayout.NORTH);

        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        listView.add(scrollPane, BorderLayout.CENTER);
        listView.add(createFooterPanel(), BorderLayout.SOUTH);

        cards.add(listView, "LIST");
        add(cards, BorderLayout.CENTER);

        renderHierarchy();
    }

    // --- RENDU DE LA HIERARCHIE ---

    public void renderHierarchy() {
        mainContent.removeAll();
        detailPanels.clear();

        Map<String, List<UserStaffDTO>> data = controller.loadHierarchy(searchField.getText());

        data.forEach((cabinetName, staffList) -> {
            addCabinetHeader(cabinetName);

            // Section MÉDECINS
            addRoleSubtitle(cabinetName, "MÉDECINS");
            staffList.stream()
                    .filter(u -> "MÉDECIN".equals(u.getRole()) && u.getEmail() != null)
                    .forEach(this::addUserRow);

            // Section SECRÉTAIRES
            addRoleSubtitle(cabinetName, "SECRÉTAIRES");
            staffList.stream()
                    .filter(u -> "SECRÉTAIRE".equals(u.getRole()) && u.getEmail() != null)
                    .forEach(this::addUserRow);

            mainContent.add(Box.createVerticalStrut(15));
        });

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void addUserRow(UserStaffDTO dto) {
        JPanel rowContainer = new JPanel();
        rowContainer.setLayout(new BoxLayout(rowContainer, BoxLayout.Y_AXIS));
        rowContainer.setBackground(Color.WHITE);
        rowContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel row = new JPanel(new BorderLayout());
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(5, 50, 5, 20));

        JLabel info = new JLabel(String.format("• %-25s | %s", dto.getNomComplet(), dto.getEmail()));
        info.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        JButton btnSuppr = createStyledButton("Suppr", DANGER_COLOR);
        btnSuppr.addActionListener(e -> {
            controller.deleteUser(dto.getEmail());
            renderHierarchy();
        });

        row.add(info, BorderLayout.CENTER);
        actions.add(btnSuppr);
        row.add(actions, BorderLayout.EAST);

        row.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) toggleEditPanel(dto.getEmail(), rowContainer);
            }
            public void mousePressed(java.awt.event.MouseEvent e) {
                selectedEmail = dto.getEmail();
                resetRowsBackground();
                row.setBackground(new Color(232, 244, 253));
            }
        });

        rowContainer.add(row);
        mainContent.add(rowContainer);
    }

    private void toggleEditPanel(String email, JPanel container) {
        if (detailPanels.containsKey(email)) {
            container.remove(detailPanels.get(email));
            detailPanels.remove(email);
        } else {
            Utilisateur u = controller.getUserDetails(email);
            String password = controller.getUserPassword(email);
            
            JPanel detailPanel = new JPanel(new BorderLayout());
            detailPanel.setBackground(new Color(248, 249, 250));
            detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));

            // Panel principal avec GridBagLayout pour un meilleur alignement
            JPanel mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBackground(new Color(248, 249, 250));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 5, 3, 5);
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Ajouter tous les champs de l'utilisateur
            int row = 0;
            
            // Login
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("🔑 Login:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField loginField = new JTextField(u.getLogin(), 20);
            loginField.setEditable(false);
            loginField.setBackground(Color.WHITE);
            mainPanel.add(loginField, gbc);
            row++;

            // Mot de passe
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("🔐 Mot de passe:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField passwordField = new JTextField(password, 20);
            passwordField.setEditable(false);
            passwordField.setBackground(new Color(255, 255, 200));
            if (password.startsWith("[HASHÉ")) {
                passwordField.setForeground(Color.RED);
            } else {
                passwordField.setForeground(new Color(0, 128, 0));
            }
            mainPanel.add(passwordField, gbc);
            row++;

            // Nom
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("👤 Nom:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fNom = new JTextField(u.getNom(), 20);
            fNom.setBackground(Color.WHITE);
            mainPanel.add(fNom, gbc);
            row++;

            // Prénom
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("👤 Prénom:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fPrenom = new JTextField(u.getPrenom(), 20);
            fPrenom.setBackground(Color.WHITE);
            mainPanel.add(fPrenom, gbc);
            row++;

            // Email
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("📧 Email:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fEmail = new JTextField(u.getEmail(), 20);
            fEmail.setEditable(false);
            fEmail.setBackground(Color.WHITE);
            mainPanel.add(fEmail, gbc);
            row++;

            // Téléphone
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("📱 Téléphone:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fTel = new JTextField(u.getTel(), 20);
            fTel.setBackground(Color.WHITE);
            mainPanel.add(fTel, gbc);
            row++;

            // CIN
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("🆔 CIN:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fCin = new JTextField(u.getCin(), 20);
            fCin.setBackground(Color.WHITE);
            mainPanel.add(fCin, gbc);
            row++;

            // Date de naissance
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("🎂 Date naissance:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fDateNaissance = new JTextField(
                u.getDateNaissance() != null ? u.getDateNaissance().toString() : "N/A", 20);
            fDateNaissance.setEditable(false);
            fDateNaissance.setBackground(Color.WHITE);
            mainPanel.add(fDateNaissance, gbc);
            row++;

            // Sexe
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("⚧ Sexe:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            JTextField fSexe = new JTextField(u.getSexe() != null ? u.getSexe().getLibelle() : "N/A", 20);
            fSexe.setBackground(Color.WHITE);
            mainPanel.add(fSexe, gbc);
            row++;

            // Adresse
            gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
            mainPanel.add(new JLabel("🏠 Adresse:"), gbc);
            gbc.gridx = 1; gbc.weightx = 0.7;
            String adresse = u.getAdresse() != null ? u.getAdresse().getAdresseComplete() : "N/A";
            JTextField fAdresse = new JTextField(adresse, 20);
            fAdresse.setEditable(false);
            fAdresse.setBackground(Color.WHITE);
            mainPanel.add(fAdresse, gbc);
            row++;

            // Panel des boutons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            buttonPanel.setBackground(new Color(248, 249, 250));
            
            JButton btnSave = createStyledButton("💾 Sauver", SUCCESS_COLOR);
            JButton btnCancel = createStyledButton("❌ Fermer", DANGER_COLOR);
            JButton btnResetPwd = createStyledButton("🔄 Réinitialiser MDP", WARNING_COLOR);
            
            btnSave.addActionListener(e -> {
                String ville = u.getAdresse() != null ? u.getAdresse().getVille() : "N/A";
                controller.updateUser(email, fNom.getText(), fPrenom.getText(), fTel.getText(), fCin.getText(), ville);
                container.remove(detailPanel);
                detailPanels.remove(email);
                mainContent.revalidate();
                mainContent.repaint();
            });
            
            btnCancel.addActionListener(e -> {
                container.remove(detailPanel);
                detailPanels.remove(email);
                mainContent.revalidate();
                mainContent.repaint();
            });
            
            btnResetPwd.addActionListener(e -> {
                String newPwd = controller.resetPassword(email);
                if (newPwd != null) {
                    passwordField.setText(newPwd);
                    passwordField.setForeground(new Color(0, 128, 0));
                }
            });
            
            buttonPanel.add(btnSave);
            buttonPanel.add(btnCancel);
            buttonPanel.add(btnResetPwd);

            // Assemblage final
            detailPanel.add(mainPanel, BorderLayout.CENTER);
            detailPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            container.add(detailPanel);
            detailPanels.put(email, detailPanel);
        }
        mainContent.revalidate();
        mainContent.repaint();
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, int row, String label, Component comp) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        p.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        p.add(comp, gbc);
    }

    private JPanel createFooterPanel() {
        JPanel f = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        f.setBackground(LIGHT_BG);
        JButton btnReset = createStyledButton("Réinitialiser MDP", WARNING_COLOR);
        btnReset.addActionListener(e -> {
            if (selectedEmail != null) controller.resetPassword(selectedEmail);
            else JOptionPane.showMessageDialog(this, "Sélectionnez un utilisateur.");
        });
        f.add(btnReset);
        return f;
    }

    private JPanel createSearchPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(LIGHT_BG);
        p.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel searchLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchLeft.setOpaque(false);
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { renderHierarchy(); }
        });
        searchLeft.add(new JLabel("🔍 Recherche :"));
        searchLeft.add(searchField);

        JPanel actionRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRight.setOpaque(false);

        JButton btnAddCabinet = createStyledButton("+ Nouveau Cabinet", new Color(41, 128, 185));

        // Logic de navigation vers le formulaire plein écran
        btnAddCabinet.addActionListener(e -> {
            // Remonter vers MainFrame via l'interface Navigatable
            Component parent = SwingUtilities.getAncestorOfClass(Navigatable.class, this);
            if (parent instanceof Navigatable nav) {
                nav.showView("FORM_CABINET");
            } else {
                // Fallback si Navigatable n'est pas trouvé
                System.err.println("Erreur: MainFrame n'implémente pas Navigatable ou n'est pas accessible.");
            }
        });

        actionRight.add(btnAddCabinet);
        p.add(searchLeft, BorderLayout.WEST);
        p.add(actionRight, BorderLayout.EAST);
        return p;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setForeground(color);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color), BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addCabinetHeader(String name) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        p.setBackground(new Color(236, 240, 241));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JLabel lbl = new JLabel("Cabinet : " + name);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.add(lbl);
        mainContent.add(p);
    }

    private void addRoleSubtitle(String cabinetName, String title) {
        JPanel h = new JPanel(new BorderLayout());
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(10, 25, 5, 20));
        h.setAlignmentX(Component.LEFT_ALIGNMENT);
        h.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        JLabel l = new JLabel(title);
        l.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        l.setForeground(Color.GRAY);
        JButton btnAdd = createStyledButton("+ Ajouter", SUCCESS_COLOR);
        btnAdd.addActionListener(e -> showCreationDialog(cabinetName, title));
        h.add(l, BorderLayout.WEST); h.add(btnAdd, BorderLayout.EAST);
        mainContent.add(h);
    }

    private void resetRowsBackground() {
        for (Component c : mainContent.getComponents()) {
            if (c instanceof JPanel) {
                for (Component sub : ((JPanel) c).getComponents()) {
                    if (sub instanceof JPanel) sub.setBackground(Color.WHITE);
                }
            }
        }
    }

    private void showCreationDialog(String cabinetName, String roleType) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNom = new JTextField(15);
        JTextField txtPrenom = new JTextField(15);
        JTextField txtEmail = new JTextField(15);
        JTextField txtCin = new JTextField(15);
        JTextField txtTel = new JTextField(15);
        JTextField txtPays = new JTextField("Maroc");
        JTextField txtVille = new JTextField(15);
        JComboBox<String> comboSexe = new JComboBox<>(new String[]{"MALE", "FEMALE"});
        JTextField txtSpec = new JTextField(15);

        JTextField txtLoginPreview = new JTextField("p.nom (auto)");
        txtLoginPreview.setEditable(false);
        txtLoginPreview.setForeground(Color.BLUE);
        txtLoginPreview.setBackground(new Color(236, 240, 241));

        java.awt.event.KeyAdapter loginSync = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                String p = txtPrenom.getText().trim().toLowerCase();
                String n = txtNom.getText().trim().toLowerCase();
                if (!p.isEmpty() && !n.isEmpty()) {
                    txtLoginPreview.setText(p.substring(0, 1) + "." + n);
                }
            }
        };
        txtNom.addKeyListener(loginSync);
        txtPrenom.addKeyListener(loginSync);

        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Aujourd'hui");
        p.put("text.month", "Mois");
        p.put("text.year", "Année");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        String generatedPassword = controller.generateAndSaveNewPassword("TEMP_PREVIEW");
        JTextField txtPwd = new JTextField(generatedPassword);
        txtPwd.setEditable(false);
        txtPwd.setForeground(new Color(39, 174, 113));
        txtPwd.setFont(new Font("Monospaced", Font.BOLD, 13));

        int r = 0;
        addFormRow(panel, gbc, r++, "Nom :", txtNom);
        addFormRow(panel, gbc, r++, "Prénom :", txtPrenom);
        addFormRow(panel, gbc, r++, "Login généré :", txtLoginPreview);
        addFormRow(panel, gbc, r++, "Email (Contact) :", txtEmail);
        addFormRow(panel, gbc, r++, "Mot de passe :", txtPwd);
        addFormRow(panel, gbc, r++, "CIN :", txtCin);
        addFormRow(panel, gbc, r++, "Tél :", txtTel);
        addFormRow(panel, gbc, r++, "Sexe :", comboSexe);
        addFormRow(panel, gbc, r++, "Date Naissance :", datePicker);
        addFormRow(panel, gbc, r++, "Pays :", txtPays);
        addFormRow(panel, gbc, r++, "Ville :", txtVille);
        addFormRow(panel, gbc, r++, (roleType.contains("MÉD") ? "Spécialité :" : "N° CNSS :"), txtSpec);

        boolean success = false;
        while (!success) {
            int result = JOptionPane.showConfirmDialog(this, panel,
                    "👤 Création : " + roleType + " - " + cabinetName,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nom, Prénom et Email sont obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
                    continue;
                }
                try {
                    Map<String, String> data = new HashMap<>();
                    data.put("nom", txtNom.getText().trim());
                    data.put("prenom", txtPrenom.getText().trim());
                    data.put("email", txtEmail.getText().trim());
                    data.put("cin", txtCin.getText().trim());
                    data.put("tel", txtTel.getText().trim());
                    data.put("sexe", (String) comboSexe.getSelectedItem());
                    data.put("pays", txtPays.getText().trim());
                    data.put("ville", txtVille.getText().trim());
                    data.put("password", generatedPassword);
                    data.put(roleType.contains("MÉD") ? "specialite" : "numCnss", txtSpec.getText().trim());

                    if (model.getValue() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        data.put("dateNaissance", sdf.format(model.getValue()));
                    }

                    controller.addNewUser(cabinetName, roleType, data);
                    JOptionPane.showMessageDialog(this, "Compte créé avec succès !");
                    success = true;
                    renderHierarchy();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                break;
            }
        }
    }

    // Formatter pour le DatePicker
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "dd-MM-yyyy";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }
}
