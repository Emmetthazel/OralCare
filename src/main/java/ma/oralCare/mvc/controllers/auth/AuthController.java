package ma.oralCare.mvc.controllers.auth;

import ma.oralCare.mvc.ui.auth.LoginFrame;
import ma.oralCare.service.modules.auth.api.AuthService;
import ma.oralCare.service.modules.auth.dto.AuthRequest;
import ma.oralCare.service.modules.auth.dto.AuthResult;
import ma.oralCare.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;

/**
 * Contrôleur gérant le processus d'authentification et l'aiguillage
 * vers les différentes interfaces selon le rôle de l'utilisateur.
 */
public class AuthController {

    private final LoginFrame view;
    private final AuthService authService;

    public AuthController(LoginFrame view, AuthService authService) {
        this.view = view;
        this.authService = authService;
        initController();
    }

    private void initController() {
        // Clic sur le bouton de connexion
        view.getBtnLogin().addActionListener(e -> handleLogin());

        // Validation par la touche "Entrée" sur le champ mot de passe
        view.getTxtPassword().addActionListener(e -> handleLogin());

        // Optionnel : Validation par la touche "Entrée" sur le champ login
        view.getTxtLogin().addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String login = view.getTxtLogin().getText().trim();
        String password = new String(view.getTxtPassword().getPassword());

        // 1. Validation basique des champs
        if (login.isEmpty() || password.isEmpty()) {
            view.showErrorMessage("Veuillez remplir tous les champs.");
            return;
        }

        view.clearErrorMessage(); // Nettoie les erreurs précédentes

        try {
            // 2. Appel au service d'authentification
            AuthResult result = authService.authenticate(new AuthRequest(login, password));

            if (result.isAuthenticated() && result.getPrincipal() != null) {
                openMainApplication(result.getPrincipal());
            } else {
                // Affiche l'erreur renvoyée par le service (ex: "Mot de passe incorrect")
                view.showErrorMessage(result.getMessage());
            }
        } catch (Exception e) {
            view.showErrorMessage("Erreur technique : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Oriente l'utilisateur vers son espace de travail selon son rôle.
     */
    private void openMainApplication(UserPrincipal principal) {
        // Fermer la fenêtre de login avant d'ouvrir la suite
        view.dispose();

        // Sécurité : Vérification de la présence de rôles
        if (principal.getRoles() == null || principal.getRoles().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Aucun rôle assigné à cet utilisateur.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roleStr = principal.getRoles().get(0).toUpperCase();
        String login = principal.getLogin();

        System.out.println("[AUTH] Utilisateur connecté : " + login + " | Rôle : " + roleStr);

        // --- AIGUILLAGE SELON LE RÔLE ---

        if (roleStr.contains("ADMIN")) {
            SwingUtilities.invokeLater(() -> {
                ma.oralCare.mvc.ui.MainFrame adminApp = new ma.oralCare.mvc.ui.MainFrame(principal);
                adminApp.setVisible(true);
            });

        } else if (roleStr.contains("DOCTOR") || roleStr.contains("MEDECIN")) {
            // ✅ Espace Médecin (ui1.medecin)
            SwingUtilities.invokeLater(() -> {
                ma.oralCare.mvc.ui1.medecin.MainFrame medecinApp = new ma.oralCare.mvc.ui1.medecin.MainFrame(principal);
                medecinApp.setVisible(true);
            });

        } else if (roleStr.contains("SECRETARY") || roleStr.contains("SECRETAIRE") || roleStr.contains("RECEPTIONIST")) {
            // ✅ Espace Secrétaire (ui1)
            SwingUtilities.invokeLater(() -> {
                ma.oralCare.mvc.ui1.MainFrame secretaireApp = new ma.oralCare.mvc.ui1.MainFrame(roleStr, login);
                secretaireApp.setVisible(true);
            });

        } else {
            // Rôle non reconnu
            JOptionPane.showMessageDialog(null,
                    "Accès refusé : Votre rôle [" + roleStr + "] n'est pas configuré pour accéder à l'application.",
                    "Erreur d'accès",
                    JOptionPane.WARNING_MESSAGE);

            // Retour au login en cas d'erreur de rôle
            SwingUtilities.invokeLater(() -> {
                new ma.oralCare.mvc.ui.auth.LoginFrame().setVisible(true);
            });
        }
    }
}