package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.mvc.controllers.RDV.api.RDVController;
import ma.oralCare.mvc.controllers.RDV.impl.RDVControllerImpl;
import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.mvc.controllers.dashboard.impl.DashboardControllerImpl;
import ma.oralCare.mvc.ui1.FooterPanel;
import ma.oralCare.mvc.ui1.HeaderPanel;
import ma.oralCare.service.modules.RDV.api.RDVService;
import ma.oralCare.service.modules.auth.dto.UserPrincipal;
import ma.oralCare.service.modules.dashboard.api.DashboardService;
import ma.oralCare.service.modules.dashboard.impl.DashboardServiceImpl;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final UserPrincipal currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private HeaderPanel header;
    private FooterPanel footer;
    private SideBarPanel sideBar;

    private DashboardPanel dashboardPanel;
    private DashboardController dashboardController;

    private RDVPanel rdvPanel;
    private RDVController rdvController;

    private ConsultationPanel consultationPanel;
    private PatientListView patientListView;
    private MedicalRecordDetailView medicalRecordDetailView;
    private PrescriptionView prescriptionView;
    private TreatmentView treatmentView;
    private CertificateView certificateView;
    private FinancialSituationView financialSituationView;

    // --- Getters ---
    public ConsultationPanel getConsultationPanel() { return this.consultationPanel; }
    public MedicalRecordDetailView getMedicalRecordDetailView() { return this.medicalRecordDetailView; }
    public UserPrincipal getCurrentUser() { return currentUser; }
    public TreatmentView getTreatmentView() { return this.treatmentView; }

    public MainFrame(UserPrincipal principal) {
        this.currentUser = principal;

        String userName = (principal != null) ? principal.getLogin() : "Utilisateur";
        String roleLabel = (principal != null && !principal.getRoles().isEmpty())
                ? principal.getRoles().get(0) : "Médecin";

        initializeFrame(roleLabel);
        setupCommonComponents(userName, roleLabel);
        setupModules();
        setupMainLayout();

        showView("Dashboard");
    }

    private void initializeFrame(String role) {
        setTitle("ORAL CARE - Espace " + role);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1350, 850); // Ajusté pour le confort visuel des tables
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void setupCommonComponents(String userName, String role) {
        this.sideBar = new SideBarPanel(this);
        add(sideBar, BorderLayout.WEST);

        this.header = new HeaderPanel(userName, role);
        add(header, BorderLayout.NORTH);

        this.footer = new FooterPanel();
        add(footer, BorderLayout.SOUTH);
    }

    private void setupModules() {
        // Module Dashboard
        DashboardService dashboardService = new DashboardServiceImpl();
        this.dashboardPanel = new DashboardPanel();
        String login = (currentUser != null) ? currentUser.getLogin() : "Utilisateur";
        this.dashboardController = new DashboardControllerImpl(dashboardPanel, dashboardService, login);

        // Module Rendez-vous
        this.rdvPanel = new RDVPanel();
        Long medecinId = (currentUser != null) ? currentUser.getId() : null;
        this.rdvPanel.setController(rdvController);

        // ✅ Initialisation des nouvelles vues
        this.consultationPanel = new ConsultationPanel();
        this.patientListView = new PatientListView();
        this.medicalRecordDetailView = new MedicalRecordDetailView();
        this.treatmentView = new TreatmentView();
        this.prescriptionView = new PrescriptionView();
        this.certificateView = new CertificateView();
        this.financialSituationView = new FinancialSituationView();
    }

    private void setupMainLayout() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(Color.WHITE);

        // Ajout des vues au CardLayout
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(rdvPanel, "Mes RDV");
        mainPanel.add(consultationPanel, "Consultation");
        mainPanel.add(patientListView, "Patients");
        mainPanel.add(medicalRecordDetailView, "Dossiers Médicaux");

        // Placeholders pour les modules restants
        mainPanel.add(treatmentView, "Actes et Soins");
        mainPanel.add(prescriptionView, "Ordonnances");
        mainPanel.add(certificateView, "Certificats Médicaux");
        mainPanel.add(financialSituationView, "Situations Financières");

        add(mainPanel, BorderLayout.CENTER);
    }

    public void showView(String viewName) {
        if (viewName == null || viewName.isEmpty()) return;

        // 1. NORMALISATION : On convertit le nom reçu en clé CardLayout exacte
        String internalKey = viewName;

        // Gestion des alias pour être robuste aux variations de texte de la Sidebar
        if (viewName.equalsIgnoreCase("Consultations") || viewName.equalsIgnoreCase("Consultation")) {
            internalKey = "Consultation";
        } else if (viewName.contains("Actes") || viewName.contains("Soins")) {
            internalKey = "Actes et Soins";
        } else if (viewName.equalsIgnoreCase("Dossiers") || viewName.contains("Dossier Médical")) {
            internalKey = "Dossiers Médicaux";
        }

        try {
            // 2. NAVIGATION : Changement physique du panel
            cardLayout.show(mainPanel, internalKey);

            // 3. MISE À JOUR DU HEADER : Affiche le titre du module actuel
            if (header != null) {
                header.setModuleTitle(viewName);
            }

            // 4. LOGIQUE MÉTIER : Actions spécifiques lors de l'entrée dans un module
            System.out.println("[NAV] Activation du module : " + internalKey);

            switch (internalKey) {
                case "Dashboard":
                    if (dashboardController != null) {
                        dashboardController.refreshData(); // Rafraîchit les stats
                    }
                    break;

                case "Mes RDV":
                    if (rdvController != null) {
                        rdvController.refreshView(); // Recharge la liste des RDV
                    }
                    break;

                case "Patients":
                    // Optionnel : On peut ici déclencher un rafraîchissement de la liste SQL
                    break;

                case "Actes et Soins":
                    // Logique pour s'assurer qu'un patient est bien sélectionné avant d'afficher
                    System.out.println("[SOINS] Chargement de l'interface d'interventions.");
                    break;

                case "Dossiers Médicaux":
                    // Logique d'audit ou de log si nécessaire
                    break;
                case "Ordonnances":
                    System.out.println("[NAV] Module Ordonnances activé.");
                    // Ici vous pourrez rafraîchir les données du patient en cours
                    break;
                case "Certificats Médicaux":
                    System.out.println("[NAV] Accès au module Certificats.");
                    break;
                case "Situations Financières":
                    System.out.println("[FINANCE] Chargement de l'état financier du patient.");
                    break;
                default:
                    // Pour les placeholders (Ordonnances, Certificats, etc.)
                    break;
            }

        } catch (IllegalArgumentException e) {
            // Sécurité : Si la clé n'existe pas, on revient au Dashboard par défaut
            System.err.println("[ERREUR] Clé CardLayout introuvable : " + internalKey);
            cardLayout.show(mainPanel, "Dashboard");
            if (header != null) header.setModuleTitle("Dashboard");
        }
    }
    /**
     * ✅ Méthode corrigée pour correspondre aux nouveaux paramètres
     * issus de la base de données (nom, prenom, cin, assurance, sexe, etc.)
     */
    public void openMedicalRecord(String nom, String prenom, String cin, String assurance,
                                  String sexe, String dateC, double totalA, double totalP, double credit) {

        // 1. Charger les données riches dans la vue détail
        this.medicalRecordDetailView.loadPatientData(nom, prenom, cin, assurance, sexe, dateC, totalA, totalP, credit);

        // 2. Naviguer vers le dossier
        showView("Dossiers Médicaux");
    }

    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(127, 140, 141));
        panel.add(label);
        return panel;
    }
}