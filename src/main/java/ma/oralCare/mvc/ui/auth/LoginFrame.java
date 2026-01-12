package ma.oralCare.mvc.ui.auth;

import ma.oralCare.conf.SessionFactory;
import java.sql.Connection;
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
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginFrame extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(33, 150, 243);
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    private static final String ADMIN_EMAIL = "admin@cabinet.ma";
    private static final String APP_NAME = "Cabinet Dentaire";

    private final JTextField txtLogin;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnQuit;
    private final JLabel lblForgot;
    private final JLabel lblError;

    public LoginFrame() {
        txtLogin = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin = new JButton("Se connecter");
        btnQuit = new JButton("Quitter");
        lblForgot = new JLabel("Mot de passe oublié ?");
        lblError = new JLabel();

        initializeFrame();
        setupComponents();
        setupLayout();
        setupEventListeners();
    }

    private void initializeFrame() {
        setTitle(APP_NAME + " - Connexion");
        setSize(450, 350);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(0, 10));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private void setupComponents() {
        lblForgot.setForeground(PRIMARY_COLOR);
        lblForgot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lblForgot.setFont(lblForgot.getFont().deriveFont(Font.PLAIN, 12f));

        lblError.setForeground(ERROR_COLOR);
        lblError.setFont(lblError.getFont().deriveFont(Font.PLAIN, 12f));
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        lblError.setVisible(false);

        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(120, 35));

        btnQuit.setPreferredSize(new Dimension(100, 35));

        txtLogin.setBorder(createFieldBorder(Color.LIGHT_GRAY));
        txtPassword.setBorder(createFieldBorder(Color.LIGHT_GRAY));
    }

    private void setupEventListeners() {
        btnQuit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Quitter ?", "Confirmation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        lblForgot.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().mail(new URI("mailto:" + ADMIN_EMAIL));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Contactez : " + ADMIN_EMAIL);
                }
            }
        });
    }

    public void showErrorMessage(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
        txtLogin.setBorder(createFieldBorder(ERROR_COLOR));
        txtPassword.setBorder(createFieldBorder(ERROR_COLOR));
    }

    public void clearErrorMessage() {
        lblError.setVisible(false);
        txtLogin.setBorder(createFieldBorder(Color.LIGHT_GRAY));
        txtPassword.setBorder(createFieldBorder(Color.LIGHT_GRAY));
    }

    private javax.swing.border.Border createFieldBorder(Color color) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        JLabel title = new JLabel("Système de Gestion " + APP_NAME);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(title);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 10, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 10, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Login :"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Mot de passe :"), gbc);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(lblForgot, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(lblError, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(btnLogin);
        panel.add(btnQuit);
        return panel;
    }

    // --- Getters ---
    public JTextField getTxtLogin() { return txtLogin; }
    public JPasswordField getTxtPassword() { return txtPassword; }
    public JButton getBtnLogin() { return btnLogin; }

    // =========================================================================
    // ✅ MAIN DE TEST (Lancement du module Auth complet)
    // =========================================================================
    // =========================================================================
    // ✅ MAIN DE TEST (Lancement du module Auth complet)
    // =========================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Style visuel du système
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // 1. Initialisation de la couche données
                // ✅ PLUS BESOIN de récupérer la connexion ici !
                // ✅ Le constructeur est maintenant vide car le repo est autonome
                UtilisateurRepository repo = new UtilisateurRepositoryImpl();

                // 2. Initialisation des services
                PasswordEncoder encoder = new PasswordEncoderImpl();
                CredentialsValidator validator = new CredentialsValidatorImpl();
                AuthService authService = new AuthServiceImpl(repo, encoder, validator);

                // 3. Initialisation du MVC
                LoginFrame view = new LoginFrame();
                new AuthController(view, authService);

                // 4. Affichage
                view.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erreur au démarrage : " + e.getMessage());
            }
        });
    }
}