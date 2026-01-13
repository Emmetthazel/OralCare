package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.factory.SecretaireModuleFactory;
import ma.oralCare.mvc.controllers.session.api.SessionSecretaireController;
import ma.oralCare.mvc.controllers.users.api.SecretaireController;
import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.service.modules.session.api.SessionSecretaireService;
import ma.oralCare.service.modules.users.api.SecretaireService;
import ma.oralCare.service.modules.dashboard.api.DashboardService;
import ma.oralCare.service.modules.realtime.api.RealTimeService;
import ma.oralCare.service.modules.realtime.dto.RealTimeEvent;
import ma.oralCare.service.modules.realtime.dto.SubscriptionRequest;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SecretaireDashboard extends BaseDashboard {

    private static final String[] NAV_ITEMS = {
            "Dashboard", "Rendez-vous", "Patients", "Agenda Médecin", "Factures", "Consultations", "Certificats"
    };

    // Services et contrôleurs
    private SessionSecretaireService sessionService;
    private SessionSecretaireController sessionController;
    private SecretaireService secretaireService;
    private SecretaireController secretaireController;
    private DashboardService dashboardService;
    private DashboardController dashboardController;
    private RealTimeService realTimeService;

    // Session actuelle
    private String currentSessionId;
    private ma.oralCare.entities.users.Secretaire currentSecretaire;
    private String realtimeSubscriptionId;
    private String currentUserName;
    private String currentUserRole;

    // Factory pour la gestion des dépendances
    private final SecretaireModuleFactory factory;

    public SecretaireDashboard() {
        super("OralCare - Espace Secrétariat", "", "Secrétaire", NAV_ITEMS);
        
        // Initialiser la factory
        this.factory = SecretaireModuleFactory.getInstance();
        factory.initialize();
        
        initializeServices();
        initializeControllers();
        setupUI();
    }

    private void initializeServices() {
        try {
            // Récupérer les services depuis la factory
            this.secretaireService = factory.getSecretaireService();
            this.sessionService = factory.getSessionService();
            this.realTimeService = factory.getRealTimeService();
            
            // Le dashboardService sera initialisé après l'authentification
            this.dashboardService = null;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'initialisation des services: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeControllers() {
        try {
            // Récupérer les contrôleurs depuis la factory
            this.sessionController = factory.getSessionController();
            this.secretaireController = factory.getSecretaireController();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'initialisation des contrôleurs: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupUI() {
        // Afficher l'écran de connexion
        showLoginScreen();
    }

    private void showLoginScreen() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Titre
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Connexion - Espace Secrétariat");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        loginPanel.add(titleLabel, gbc);

        // Login
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        loginPanel.add(new JLabel("Login:"), gbc);
        gbc.gridx = 1;
        JTextField loginField = new JTextField(20);
        loginPanel.add(loginField, gbc);

        // Mot de passe
        gbc.gridx = 0; gbc.gridy = 2;
        loginPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(20);
        loginPanel.add(passwordField, gbc);

        // Bouton de connexion
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton loginButton = new JButton("Se connecter");
        loginPanel.add(loginButton, gbc);

        // Action du bouton de connexion
        loginButton.addActionListener(e -> {
            String login = loginField.getText().trim();
            String password = new String(passwordField.getPassword());
            
            if (login.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            performLogin(login, password);
        });

        // Afficher le panel de connexion
        contentArea.removeAll();
        contentArea.add(loginPanel, "LOGIN");
        cardLayout.show(contentArea, "LOGIN");
    }

    private void performLogin(String login, String password) {
        try {
            ma.oralCare.service.modules.session.dto.LoginRequest loginRequest = 
                new ma.oralCare.service.modules.session.dto.LoginRequest(login, password);
            
            ma.oralCare.service.modules.session.dto.LoginResult result = sessionController.login(loginRequest);
            
            if (result.isSuccess()) {
                this.currentSessionId = result.getSessionId();
                this.currentSecretaire = result.getSecretaire();
                
                // Mettre à jour les informations du dashboard
                updateUserInfo();
                
                // Initialiser le dashboard principal
                initializeMainDashboard();
                
                // S'abonner aux événements en temps réel
                subscribeToRealTimeEvents();
                
                JOptionPane.showMessageDialog(this, "Connexion réussie!", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur lors de la connexion: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateUserInfo() {
        if (currentSecretaire != null) {
            this.currentUserName = currentSecretaire.getNom() + " " + currentSecretaire.getPrenom();
            this.currentUserRole = "Secrétaire";
            
            // Mettre à jour le titre
            setTitle("OralCare - Espace Secrétariat - " + currentUserName);
        }
    }

    private void initializeMainDashboard() {
        contentArea.removeAll();
        
        // Ajouter les modules principaux avec les vrais panels
        contentArea.add(createMainDashboardView(), "Dashboard");
        contentArea.add(new ma.oralCare.mvc.ui.panels.rdv.RendezVousPanel(this), "Rendez-vous");
        contentArea.add(new ma.oralCare.mvc.ui.panels.patient.PatientsPanel(this), "Patients");
        contentArea.add(new ma.oralCare.mvc.ui.panels.facture.FacturesPanel(this), "Factures");
        contentArea.add(new ConsultationsPanel(this), "Consultations");
        contentArea.add(new CertificatsPanel(this), "Certificats");
        contentArea.add(new AgendaMedecinPanel(this), "Agenda Médecin");

        // Ajouter les placeholders pour les modules non implémentés
        for (String item : NAV_ITEMS) {
            if (!isPageImplemented(item)) {
                contentArea.add(createPlaceholderPage(item), item);
            }
        }

        // Afficher le dashboard par défaut
        cardLayout.show(contentArea, "Dashboard");
    }

    private boolean isPageImplemented(String item) {
        return item.equals("Dashboard") ||
                item.equals("Rendez-vous") ||
                item.equals("Patients") ||
                item.equals("Agenda Médecin") ||
                item.equals("Factures") ||
                item.equals("Consultations") ||
                item.equals("Certificats");
    }

    // Getters pour les contrôleurs
    public SessionSecretaireController getSessionController() {
        return sessionController;
    }

    public SecretaireController getSecretaireController() {
        return secretaireController;
    }

    public ma.oralCare.mvc.controllers.RDV.api.RDVController getRdvController() {
        return factory.getRdvController();
    }

    public ma.oralCare.mvc.controllers.patient.api.PatientController getPatientController() {
        return factory.getPatientController();
    }

    public ma.oralCare.mvc.controllers.facture.api.FactureController getFactureController() {
        return factory.getFactureController();
    }

    public String getCurrentSessionId() {
        return currentSessionId;
    }

    public ma.oralCare.entities.users.Secretaire getCurrentSecretaire() {
        return currentSecretaire;
    }

    private static class AgendaMedecinPanel extends JPanel {
        public AgendaMedecinPanel(SecretaireDashboard dashboard) {
            add(new JLabel("Agenda du Médecin - Données en temps réel"));
        }
    }

    private static class FacturesPanel extends JPanel {
        public FacturesPanel(SecretaireDashboard dashboard) {
            add(new JLabel("Gestion des Factures - Données en temps réel"));
        }
    }

    private static class ConsultationsPanel extends JPanel {
        public ConsultationsPanel(SecretaireDashboard dashboard) {
            add(new JLabel("Gestion des Consultations - Données en temps réel"));
        }
    }

    private static class CertificatsPanel extends JPanel {
        public CertificatsPanel(SecretaireDashboard dashboard) {
            add(new JLabel("Gestion des Certificats - Données en temps réel"));
        }
    }

    /**
     * S'abonne aux événements en temps réel
     */
    private void subscribeToRealTimeEvents() {
        try {
            if (realTimeService != null && currentSecretaire != null) {
                SubscriptionRequest request = new SubscriptionRequest();
                request.setSecretaireId(currentSecretaire.getIdEntite());
                request.setEventTypes(Arrays.asList(
                    RealTimeEvent.TYPE_NEW_RDV,
                    RealTimeEvent.TYPE_RDV_MODIFIED,
                    RealTimeEvent.TYPE_RDV_CANCELLED,
                    RealTimeEvent.TYPE_NEW_PATIENT,
                    RealTimeEvent.TYPE_PATIENT_UPDATED,
                    RealTimeEvent.TYPE_NEW_FACTURE,
                    RealTimeEvent.TYPE_FACTURE_PAID
                ));
                
                Consumer<String> eventHandler = this::handleRealTimeEvent;
                realtimeSubscriptionId = realTimeService.subscribeToEvents(request);
                
                System.out.println("Abonné aux événements en temps réel avec ID: " + realtimeSubscriptionId);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'abonnement aux événements en temps réel: " + e.getMessage());
            // Ne pas bloquer l'application si l'abonnement échoue
        }
    }

    /**
     * Gère les événements en temps réel reçus
     */
    private void handleRealTimeEvent(String eventData) {
        // Traiter les événements en temps réel sur l'EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Parser et traiter l'événement
                System.out.println("Événement en temps réel reçu: " + eventData);
                
                // Mettre à jour les composants UI selon le type d'événement
                // TODO: Implémenter la logique de mise à jour selon le type d'événement
                
            } catch (Exception e) {
                System.err.println("Erreur lors du traitement de l'événement en temps réel: " + e.getMessage());
            }
        });
    }
}