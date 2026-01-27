package ma.oralCare.mvc.ui.auth;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.controllers.auth.AuthController;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;
import ma.oralCare.repository.modules.users.impl.UtilisateurRepositoryImpl;
import ma.oralCare.service.modules.auth.api.AuthService;
import ma.oralCare.service.modules.auth.api.CredentialsValidator;
import ma.oralCare.service.modules.auth.api.PasswordEncoder;
import ma.oralCare.service.modules.auth.impl.AuthServiceImpl;
import ma.oralCare.service.modules.auth.impl.CredentialsValidatorImpl;
import ma.oralCare.service.modules.auth.impl.PasswordEncoderImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URI;

public class LoginFrame extends JFrame {

    // Couleurs modernes - exactement comme React
    private static final Color PRIMARY_BLUE = new Color(59, 130, 246);    // blue-500
    private static final Color PRIMARY_CYAN = new Color(6, 182, 212);     // cyan-500
    private static final Color BG_BLUE_50 = new Color(239, 246, 255);     // blue-50
    private static final Color BG_CYAN_50 = new Color(236, 254, 255);     // cyan-50
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(31, 41, 55);         // gray-800
    private static final Color TEXT_MEDIUM = new Color(75, 85, 99);       // gray-600
    private static final Color TEXT_LIGHT = new Color(107, 114, 128);     // gray-500
    private static final Color BORDER = new Color(209, 213, 219);         // gray-300
    private static final Color BORDER_LIGHT = new Color(243, 244, 246);   // gray-100
    private static final Color ERROR = new Color(239, 68, 68);            // red-500

    private static final String ADMIN_EMAIL = "admin@cabinet.ma";
    private static final String APP_NAME = "DentalCare Pro";

    private final JTextField txtLogin;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnTogglePassword;
    private final JCheckBox chkRemember;
    private final JLabel lblForgot;
    private final JLabel lblSupport;
    private boolean passwordVisible = false;

    public LoginFrame() {
        txtLogin = new JTextField(20);
        txtPassword = new JPasswordField(20);
        btnLogin = new JButton("Se connecter");
        btnTogglePassword = new JButton();
        chkRemember = new JCheckBox("Se souvenir de moi");
        lblForgot = new JLabel("<html><u>Mot de passe oublié ?</u></html>");
        lblSupport = new JLabel("<html><u>Contacter le support</u></html>");

        initializeFrame();
        styleComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeFrame() {
        setTitle(APP_NAME + " - Connexion");
        setSize(600, 850);
        setMinimumSize(new Dimension(550, 750));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Fond avec dégradé
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Dégradé diagonal blue-50 -> white -> cyan-50
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, BG_BLUE_50, w, h, BG_CYAN_50);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        setContentPane(contentPane);
    }

    private void styleComponents() {
        // Champs de texte
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        txtLogin.setFont(fieldFont);
        txtLogin.setBackground(Color.WHITE);
        txtLogin.setForeground(TEXT_DARK);
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 40, 12, 12)
        ));
        txtLogin.setPreferredSize(new Dimension(368, 46));

        txtPassword.setFont(fieldFont);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setForeground(TEXT_DARK);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));
        txtPassword.setPreferredSize(new Dimension(368, 46));
        txtPassword.setEchoChar('•');

        // Bouton toggle password
        btnTogglePassword.setText("👁");
        btnTogglePassword.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnTogglePassword.setForeground(new Color(156, 163, 175)); // gray-400
        btnTogglePassword.setBorderPainted(false);
        btnTogglePassword.setContentAreaFilled(false);
        btnTogglePassword.setFocusPainted(false);
        btnTogglePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTogglePassword.setPreferredSize(new Dimension(30, 30));

        // Bouton de connexion avec dégradé
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setPreferredSize(new Dimension(368, 46));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Checkbox
        chkRemember.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkRemember.setForeground(TEXT_MEDIUM);
        chkRemember.setBackground(CARD_BG);
        chkRemember.setFocusPainted(false);

        // Labels cliquables
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblForgot.setForeground(PRIMARY_BLUE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblSupport.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSupport.setForeground(PRIMARY_BLUE);
        lblSupport.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(10, 40, 10, 40));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setMaximumSize(new Dimension(432, 700));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(createLogoSection());
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(createCardPanel());

        contentPanel.add(Box.createVerticalStrut(10)); // petit espace avant le glue
        contentPanel.add(Box.createVerticalGlue());    // prend tout l'espace restant

        contentPanel.add(createFooterSection());       // footer en bas

        mainPanel.add(contentPanel, new GridBagConstraints());
        add(mainPanel);
    }

    private JPanel createLogoSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Logo carré avec dégradé
        JPanel logoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_BLUE, 80, 80, PRIMARY_CYAN);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, 80, 80, 16, 16);

                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(2, 2, 80, 80, 16, 16);
            }
        };
        logoBox.setPreferredSize(new Dimension(80, 80));
        logoBox.setMaximumSize(new Dimension(80, 80));
        logoBox.setLayout(new GridBagLayout());
        logoBox.setOpaque(false);

        JLabel tooth = new JLabel("🦷");
        tooth.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        tooth.setBorder(BorderFactory.createEmptyBorder(9, 0, 0, 0));

        logoBox.add(tooth);

        JPanel logoWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoWrapper.setOpaque(false);
        logoWrapper.add(logoBox);

        panel.add(logoWrapper);
        panel.add(Box.createVerticalStrut(16));

        // Titre
        JLabel title = new JLabel(APP_NAME);
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Gestion de Cabinet Dentaire");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);

        return panel;
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(4, 4, getWidth() - 4, getHeight() - 4, 16, 16);

                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);

                g2d.setColor(BORDER_LIGHT);
                g2d.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 16, 16);
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setAlignmentX(Component.CENTER_ALIGNMENT); // ⭐⭐ LA LIGNE QUI CENTRE LA CARTE
        card.setBorder(new EmptyBorder(32, 32, 32, 32));
        card.setPreferredSize(new Dimension(432, 500));
        card.setMaximumSize(new Dimension(432, 500));

        card.add(createCardHeader());
        card.add(Box.createVerticalStrut(24));
        card.add(createFormFields());
        card.add(Box.createVerticalStrut(20));
        card.add(createOptionsRow());
        card.add(Box.createVerticalStrut(20));
        card.add(createLoginButton());
        card.add(Box.createVerticalStrut(24));
        card.add(createSeparator());
        card.add(Box.createVerticalStrut(24));
        card.add(createSupportSection());

        return card;
    }

    private JPanel createCardHeader() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Connexion");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Accédez à votre espace professionnel");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);

        // Force l'alignement à gauche
        panel.setMaximumSize(new Dimension(368, 60));

        return panel;
    }

    private JPanel createFormFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Champ utilisateur
        JLabel lblUser = new JLabel("Nom d'utilisateur");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(new Color(55, 65, 81)); // gray-700
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel userFieldPanel = createFieldWithIcon(txtLogin, "👤");
        userFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblUser);
        panel.add(Box.createVerticalStrut(8));
        panel.add(userFieldPanel);
        panel.add(Box.createVerticalStrut(20));

        // Champ mot de passe
        JLabel lblPass = new JLabel("Mot de passe");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(new Color(55, 65, 81));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel passFieldPanel = createPasswordFieldWithIcons();
        passFieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lblPass);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passFieldPanel);

        // Force l'alignement à gauche
        panel.setMaximumSize(new Dimension(368, 200));

        return panel;
    }

    private JPanel createFieldWithIcon(JTextField field, String icon) {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(368, 46));
        panel.setMaximumSize(new Dimension(368, 46));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(new Color(156, 163, 175)); // gray-400
        iconLabel.setBounds(12, 13, 20, 20);

        field.setBounds(0, 0, 368, 46);

        panel.add(field);
        panel.add(iconLabel);

        return panel;
    }

    private JPanel createPasswordFieldWithIcons() {
        JPanel panel = new JPanel(null);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(368, 46));
        panel.setMaximumSize(new Dimension(368, 46));

        JLabel lockIcon = new JLabel("🔒");
        lockIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lockIcon.setForeground(new Color(156, 163, 175));
        lockIcon.setBounds(12, 14, 20, 20);

        txtPassword.setBounds(0, 0, 368, 46);
        btnTogglePassword.setBounds(330, 8, 30, 30);

        panel.add(txtPassword);
        panel.add(lockIcon);
        panel.add(btnTogglePassword);

        return panel;
    }

    private JPanel createOptionsRow() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(368, 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(chkRemember, BorderLayout.WEST);
        panel.add(lblForgot, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLoginButton() {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(368, 46));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btn = new JButton("Se connecter") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dégradé
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_BLUE, getWidth(), 0, PRIMARY_CYAN);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Ombre au hover
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(0, 2, getWidth(), getHeight(), 8, 8);
                }

                // Texte
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(368, 46));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Copier l'action du bouton original
        btn.addActionListener(e -> btnLogin.doClick());

        wrapper.add(btn);
        return wrapper;
    }

    private JPanel createSeparator() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(368, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(229, 231, 235)); // gray-200

        JLabel lblAide = new JLabel(" Aide ");
        lblAide.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblAide.setForeground(TEXT_LIGHT);

        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(229, 231, 235));

        panel.add(sep1);
        panel.add(lblAide);
        panel.add(sep2);

        return panel;
    }

    private JPanel createSupportSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(368, 30));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblText = new JLabel("Besoin d'aide ? ");
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblText.setForeground(TEXT_MEDIUM);

        panel.add(lblText);
        panel.add(lblSupport);

        return panel;
    }

    private JPanel createFooterSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel copyright = new JLabel("© 2026 DentalCare Pro - Tous droits réservés");
        copyright.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        copyright.setForeground(TEXT_LIGHT);
        copyright.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("Version 2.5.1");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        version.setForeground(TEXT_LIGHT);
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(copyright);
        panel.add(Box.createVerticalStrut(4));
        panel.add(version);

        return panel;
    }

    private void setupEventListeners() {
        btnTogglePassword.addActionListener(e -> {
            passwordVisible = !passwordVisible;
            if (passwordVisible) {
                txtPassword.setEchoChar((char) 0);
                btnTogglePassword.setText("🙈");
            } else {
                txtPassword.setEchoChar('•');
                btnTogglePassword.setText("👁");
            }
        });

        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + ADMIN_EMAIL));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Contactez : " + ADMIN_EMAIL, "Mot de passe oublié",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        lblSupport.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + ADMIN_EMAIL));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Contactez : " + ADMIN_EMAIL, "Support",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        };
        txtLogin.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);

        txtLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR, 2),
                BorderFactory.createEmptyBorder(11, 39, 11, 11)
        ));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ERROR, 2),
                BorderFactory.createEmptyBorder(11, 39, 11, 39)
        ));
    }

    public void clearErrorMessage() {
        txtLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 40, 12, 12)
        ));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));
    }

    // Getters
    public JTextField getTxtLogin() { return txtLogin; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }

    // MAIN DE TEST
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                UtilisateurRepository repo = new UtilisateurRepositoryImpl();
                PasswordEncoder encoder = new PasswordEncoderImpl();
                CredentialsValidator validator = new CredentialsValidatorImpl();
                AuthService authService = new AuthServiceImpl(repo, encoder, validator);

                System.out.println("\n=== DIAGNOSTIC DU MOT DE PASSE h.ahlam ===");
                try {
                    var userOpt = repo.findByLogin("h.ahlam");
                    if (userOpt.isPresent()) {
                        var user = userOpt.get();
                        System.out.println("Utilisateur trouvé: " + user.getLogin());
                        System.out.println("Hash actuel: " + user.getMotDePass());

                        String[] testPasswords = {"123", "password", "admin", "secret", "123456", "ahlam"};
                        boolean found = false;
                        for (String pwd : testPasswords) {
                            boolean matches = encoder.matches(pwd, user.getMotDePass());
                            System.out.println("Test '" + pwd + "': " + (matches ? "✅ CORRECT" : "❌ INCORRECT"));

                            if (matches) {
                                System.out.println(">>> MOT DE PASSE TROUVÉ: " + pwd);
                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            System.out.println("Aucun mot de passe ne fonctionne. Création d'un nouveau hash pour '123'...");
                            String newHash = encoder.encode("123");
                            System.out.println("Nouveau hash: " + newHash);

                            try (var conn = ma.oralCare.conf.SessionFactory.getInstance().getConnection();
                                 var stmt = conn.prepareStatement("UPDATE utilisateur SET mot_de_pass = ? WHERE login = ?")) {
                                stmt.setString(1, newHash);
                                stmt.setString(2, "h.ahlam");
                                int rows = stmt.executeUpdate();
                                if (rows > 0) {
                                    System.out.println("✅ Mot de passe mis à jour! Nouveau mot de passe: 123");
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur lors de la mise à jour: " + e.getMessage());
                            }
                        }
                    } else {
                        System.out.println("Utilisateur h.ahlam non trouvé!");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur de diagnostic: " + e.getMessage());
                }
                System.out.println("=== FIN DU DIAGNOSTIC ===\n");

                LoginFrame view = new LoginFrame();
                new AuthController(view, authService);
                view.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur au démarrage : " + e.getMessage());
            }
        });
    }
}