package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Pattern;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

public class AddPatientDialog extends JDialog {
    private JTextField txtCIN;
    private JTextField txtNom;
    private JTextField txtPrenom;
    private JDateChooser dateNaissance;
    private JComboBox<String> cmbSexe;
    private JTextField txtEmail;
    private JTextField txtTelephone;
    private JTextField txtAdresse;
    private JComboBox<String> cmbAssurance;
    private JTextArea txtAntecedents;

    private boolean validated = false;

    // Constantes pour les couleurs
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color TEXT_COLOR = new Color(80, 80, 80);
    private static final Color PLACEHOLDER_COLOR = new Color(150, 150, 150);

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+212|0)[5-7][0-9]{8}$"
    );

    public AddPatientDialog(Frame parent) {
        super(parent, "Nouveau Patient", true);
        initComponents();
        setSize(850, 750);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Main Content Panel
        JPanel contentPanel = createContentPanel();

        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Footer Panel with Buttons
        JPanel footerPanel = createFooter();

        add(mainScrollPane, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(25, 35, 25, 35));
        contentPanel.setBackground(Color.WHITE);

        // Section: Informations personnelles (inclut maintenant l'assurance)
        contentPanel.add(createSectionHeader("Informations personnelles"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createPersonalInfoPanel());
        contentPanel.add(Box.createVerticalStrut(25));

        // Section: Contact
        contentPanel.add(createSectionHeader("Informations de contact"));
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createContactPanel());
        contentPanel.add(Box.createVerticalStrut(25));

        // Section: Antécédents médicaux (l'assurance est maintenant dans Informations personnelles)
        contentPanel.add(createSectionHeader("Antécédents médicaux"));
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createAntecedentsPanel());

        return contentPanel;
    }

    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtNom = createStyledTextField("Entrez le nom");
        txtPrenom = createStyledTextField("Entrez le prénom");
        txtCIN = createStyledTextField("Ex: AB123456");
        txtEmail = createStyledTextField("exemple@email.com");

        dateNaissance = new JDateChooser();
        styleComponent(dateNaissance);

        cmbSexe = new JComboBox<>(new String[]{"Sélectionner", "Homme", "Femme", "Autre"});
        styleComponent(cmbSexe);

        // Créer aussi le comboBox pour assurance ici
        String[] assurances = {"Sélectionner", "CNSS", "CNOPS", "RMA", "Wafa Assurance", "Saham", "Autre", "Aucune"};
        cmbAssurance = new JComboBox<>(assurances);
        cmbAssurance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleComponent(cmbAssurance);

        // Ligne 1: Nom / Prénom
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Nom *", txtNom), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Prénom *", txtPrenom), gbc);

        // Ligne 2: CIN / Email
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.5;
        panel.add(createFieldPanel("CIN *", txtCIN), gbc);

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Email", txtEmail), gbc);

        // Ligne 3: Date / Sexe
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Date de naissance *", dateNaissance), gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Sexe *", cmbSexe), gbc);

        // Ligne 4: Assurance / Vide
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Type d'assurance *", cmbAssurance), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.5;
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(Color.WHITE);
        emptyPanel.setOpaque(false);
        panel.add(emptyPanel, gbc);

        return panel;
    }

    private JPanel createContactPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        txtTelephone = createStyledTextField("0612345678 ou +212612345678");
        txtAdresse = createStyledTextField("Adresse complète");

        // Téléphone à gauche (50%)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Téléphone *", txtTelephone), gbc);

        // Adresse à droite (50%)
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.5;
        panel.add(createFieldPanel("Adresse", txtAdresse), gbc);

        return panel;
    }

    private JPanel createAssurancePanel() {
        String[] assurances = {"Sélectionner", "CNSS", "CNOPS", "RMA", "Wafa Assurance", "Saham", "Autre", "Aucune"};
        cmbAssurance = new JComboBox<>(assurances);
        cmbAssurance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        styleComponent(cmbAssurance);

        // Simple panneau avec le champ aligné à gauche
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);

        JPanel fieldPanel = createFieldPanel("Type d'assurance *", cmbAssurance);
        panel.add(fieldPanel);

        return panel;
    }

    private JPanel createAntecedentsPanel() {
        txtAntecedents = new JTextArea(5, 40);
        txtAntecedents.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtAntecedents.setLineWrap(true);
        txtAntecedents.setWrapStyleWord(true);
        txtAntecedents.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(txtAntecedents);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel hintLabel = new JLabel("Maladies chroniques, allergies, traitements en cours, etc. (optionnel)");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(TEXT_COLOR);
        hintLabel.setBorder(new EmptyBorder(0, 0, 5, 0));

        panel.add(hintLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnAnnuler = createButton("Annuler", Color.WHITE, TEXT_COLOR, true);
        btnAnnuler.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Voulez-vous vraiment annuler ? Les données saisies seront perdues.",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
            }
        });

        JButton btnCreer = createButton("Créer le patient", PRIMARY_COLOR, Color.WHITE, false);
        btnCreer.setPreferredSize(new Dimension(170, 40));
        btnCreer.addActionListener(e -> validateAndSave());

        footerPanel.add(btnAnnuler);
        footerPanel.add(btnCreer);

        return footerPanel;
    }

    private JButton createButton(String text, Color bg, Color fg, boolean bordered) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", bordered ? Font.PLAIN : Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (bordered) {
            button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        } else {
            button.setBorderPainted(false);
        }

        // Effet hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bordered) {
                    button.setBackground(new Color(248, 248, 248));
                } else {
                    button.setBackground(new Color(29, 78, 216));
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bg);
            }
        });

        return button;
    }

    private JPanel createSectionHeader(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(PRIMARY_COLOR);

        panel.add(label);
        return panel;
    }

    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Tous les champs ont la même taille (50% de la largeur)
        Dimension fieldSize = new Dimension(350, 45);

        if (field instanceof JTextField) {
            ((JTextField) field).setPreferredSize(fieldSize);
            ((JTextField) field).setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        } else if (field instanceof JDateChooser) {
            field.setPreferredSize(fieldSize);
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        } else if (field instanceof JComboBox) {
            field.setPreferredSize(fieldSize);
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        }

        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);

        return panel;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));

        // Placeholder effect
        textField.setForeground(PLACEHOLDER_COLOR);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(9, 11, 9, 11)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(PLACEHOLDER_COLOR);
                    textField.setText(placeholder);
                }
                textField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                ));
            }
        });

        return textField;
    }

    private void styleComponent(JComponent component) {
        if (component instanceof JComboBox) {
            component.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));

            component.setBackground(Color.WHITE);
            ((JComboBox<?>) component).setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    setBorder(new EmptyBorder(5, 10, 5, 10));
                    if (isSelected) {
                        setBackground(PRIMARY_COLOR);
                        setForeground(Color.WHITE);
                    }
                    return this;
                }
            });
        }

        if (component instanceof JDateChooser) {
            JDateChooser dateChooser = (JDateChooser) component;

            // Appliquer le même style qu'aux autres champs
            dateChooser.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(10, 12, 10, 12)
            ));

            // Personnaliser le champ texte à l'intérieur du JDateChooser
            JTextFieldDateEditor editor = (JTextFieldDateEditor) dateChooser.getDateEditor();
            editor.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            editor.setBorder(null); // Retirer la bordure interne
            editor.setBackground(Color.WHITE);

            // Focus listener pour le champ texte
            editor.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    dateChooser.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2, true),
                            new EmptyBorder(9, 11, 9, 11)
                    ));
                }

                @Override
                public void focusLost(FocusEvent e) {
                    dateChooser.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                            new EmptyBorder(10, 12, 10, 12)
                    ));
                }
            });

            // Style du bouton calendrier
            try {
                for (int i = 0; i < dateChooser.getComponentCount(); i++) {
                    Component comp = dateChooser.getComponent(i);
                    if (comp instanceof JButton) {
                        JButton calendarButton = (JButton) comp;
                        calendarButton.setBackground(Color.WHITE);
                        calendarButton.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
                        calendarButton.setFocusPainted(false);
                        calendarButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                        calendarButton.setPreferredSize(new Dimension(30, 45));

                        // Hover effect sur le bouton
                        calendarButton.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseEntered(MouseEvent e) {
                                calendarButton.setBackground(new Color(248, 248, 248));
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                calendarButton.setBackground(Color.WHITE);
                            }
                        });
                        break;
                    }
                }

                // Personnaliser les couleurs du calendrier popup
                dateChooser.getJCalendar().setWeekdayForeground(TEXT_COLOR);
                dateChooser.getJCalendar().setSundayForeground(new Color(220, 38, 38));
                dateChooser.getJCalendar().setDecorationBackgroundColor(PRIMARY_COLOR);
                dateChooser.getJCalendar().setWeekOfYearVisible(false);
            } catch (Exception e) {
                // Ignorer si propriétés non disponibles
            }
        }
    }

    private void validateAndSave() {
        StringBuilder errors = new StringBuilder();

        if (isPlaceholder(txtNom) || txtNom.getText().trim().length() < 2)
            errors.append("• Le nom doit contenir au moins 2 caractères\n");

        if (isPlaceholder(txtPrenom) || txtPrenom.getText().trim().length() < 2)
            errors.append("• Le prénom doit contenir au moins 2 caractères\n");

        // Validation CIN
        if (isPlaceholder(txtCIN) || txtCIN.getText().trim().length() < 5)
            errors.append("• Le CIN est obligatoire et doit être valide\n");

        if (dateNaissance.getDate() == null)
            errors.append("• La date de naissance est obligatoire\n");

        if (cmbSexe.getSelectedIndex() == 0)
            errors.append("• Veuillez sélectionner le sexe\n");

        if (isPlaceholder(txtTelephone))
            errors.append("• Le numéro de téléphone est obligatoire\n");

        if (cmbAssurance.getSelectedIndex() == 0)
            errors.append("• Veuillez sélectionner un type d'assurance\n");

        if (errors.length() > 0) {
            JOptionPane.showMessageDialog(this, errors.toString(), "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }

        validated = true;
        JOptionPane.showMessageDialog(this, "Patient créé avec succès !");
        dispose();
    }

    private boolean isPlaceholder(JTextField field) {
        return field.getText().isEmpty() ||
                field.getForeground().equals(PLACEHOLDER_COLOR) ||
                field.getForeground().equals(Color.GRAY);
    }

    // Getters
    public boolean isValidated() {
        return validated;
    }

    public String getNom() {
        return isPlaceholder(txtNom) ? "" : txtNom.getText().trim();
    }

    public String getPrenom() {
        return isPlaceholder(txtPrenom) ? "" : txtPrenom.getText().trim();
    }

    public java.util.Date getDateNaissance() {
        return dateNaissance.getDate();
    }

    public String getSexe() {
        return cmbSexe.getSelectedIndex() == 0 ? "" : (String) cmbSexe.getSelectedItem();
    }

    public String getEmail() {
        return isPlaceholder(txtEmail) ? "" : txtEmail.getText().trim();
    }

    public String getTelephone() {
        return isPlaceholder(txtTelephone) ? "" : txtTelephone.getText().replaceAll("\\s+", "");
    }

    public String getAdresse() {
        return isPlaceholder(txtAdresse) ? "" : txtAdresse.getText().trim();
    }

    public String getAssurance() {
        return cmbAssurance.getSelectedIndex() == 0 ? "" : (String) cmbAssurance.getSelectedItem();
    }

    public String getAntecedents() {
        return txtAntecedents.getText().trim();
    }

    public String getCIN() {
        return isPlaceholder(txtCIN) ? "" : txtCIN.getText().trim();
    }

    // Main pour test
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AddPatientDialog dialog = new AddPatientDialog(frame);
            dialog.setVisible(true);

            if (dialog.isValidated()) {
                System.out.println("Patient créé :");
                System.out.println("Nom: " + dialog.getNom());
                System.out.println("Prénom: " + dialog.getPrenom());
                System.out.println("Date: " + dialog.getDateNaissance());
                System.out.println("Sexe: " + dialog.getSexe());
                System.out.println("Téléphone: " + dialog.getTelephone());
            }

            System.exit(0);
        });
    }
}