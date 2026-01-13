package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.mvc.controllers.RDV.api.RDVController;
import ma.oralCare.mvc.controllers.RDV.impl.RDVControllerImpl;
import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.mvc.controllers.dashboard.impl.DashboardControllerImpl;
import ma.oralCare.mvc.controllers.consultation.api.ConsultationController;
import ma.oralCare.mvc.controllers.consultation.impl.ConsultationControllerImpl;
import ma.oralCare.mvc.controllers.dossier.api.DossierMedicaleController;
import ma.oralCare.mvc.controllers.dossier.impl.DossierMedicaleControllerImpl;
import ma.oralCare.mvc.controllers.ordonnance.api.OrdonnanceController;
import ma.oralCare.mvc.controllers.ordonnance.impl.OrdonnanceControllerImpl;
import ma.oralCare.mvc.controllers.certificat.api.CertificatController;
import ma.oralCare.mvc.controllers.certificat.impl.CertificatControllerImpl;
import ma.oralCare.mvc.controllers.acte.api.ActeController;
import ma.oralCare.mvc.controllers.acte.impl.ActeControllerImpl;
import ma.oralCare.mvc.controllers.intervention.api.InterventionMedecinController;
import ma.oralCare.mvc.controllers.intervention.impl.InterventionMedecinControllerImpl;
import ma.oralCare.mvc.controllers.situation.api.SituationFinanciereController;
import ma.oralCare.mvc.controllers.situation.impl.SituationFinanciereControllerImpl;
import ma.oralCare.mvc.controllers.patient.api.PatientController;
import ma.oralCare.mvc.controllers.patient.impl.PatientControllerImpl;
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
    private ConsultationController consultationController;

    private PatientListView patientListView;
    private PatientController patientController;

    private MedicalRecordDetailView medicalRecordDetailView;
    private DossierMedicaleController dossierMedicaleController;

    private PrescriptionView prescriptionView;
    private OrdonnanceController ordonnanceController;

    private TreatmentView treatmentView;
    private ActeController acteController;
    private InterventionMedecinController interventionMedecinController;

    private CertificateView certificateView;
    private CertificatController certificatController;

    private FinancialSituationView financialSituationView;
    private SituationFinanciereController situationFinanciereController;

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
        // TODO: Créer RDVService et initialiser RDVController
        // this.rdvController = new RDVControllerImpl(rdvService, rdvPanel, medecinId);
        // this.rdvPanel.setController(rdvController);

        // ✅ Module Consultation
        this.consultationPanel = new ConsultationPanel();
        this.consultationController = new ConsultationControllerImpl(consultationPanel, this);

        // ✅ Module Patients
        this.patientListView = new PatientListView();
        this.patientController = new PatientControllerImpl(patientListView);

        // ✅ Module Dossiers Médicaux
        this.medicalRecordDetailView = new MedicalRecordDetailView();
        this.dossierMedicaleController = new DossierMedicaleControllerImpl(medicalRecordDetailView);

        // ✅ Module Ordonnances
        this.prescriptionView = new PrescriptionView();
        this.ordonnanceController = new OrdonnanceControllerImpl(prescriptionView);

        // ✅ Module Actes et Soins
        this.treatmentView = new TreatmentView();
        this.acteController = new ActeControllerImpl(treatmentView);
        this.interventionMedecinController = new InterventionMedecinControllerImpl(treatmentView);

        // ✅ Module Certificats
        this.certificateView = new CertificateView();
        this.certificatController = new CertificatControllerImpl(certificateView);

        // ✅ Module Situations Financières
        this.financialSituationView = new FinancialSituationView();
        this.situationFinanciereController = new SituationFinanciereControllerImpl(financialSituationView);
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

                case "Consultation":
                    if (consultationController != null) {
                        // Le controller gère déjà ses propres événements
                        System.out.println("[NAV] Module Consultation activé.");
                    }
                    break;

                case "Patients":
                    if (patientController != null) {
                        patientController.refreshView();
                    }
                    break;

                case "Actes et Soins":
                    if (acteController != null) {
                        acteController.refreshView();
                    }
                    System.out.println("[SOINS] Chargement de l'interface d'interventions.");
                    break;

                case "Dossiers Médicaux":
                    // Le controller gère ses propres événements
                    break;

                case "Ordonnances":
                    System.out.println("[NAV] Module Ordonnances activé.");
                    break;

                case "Certificats Médicaux":
                    System.out.println("[NAV] Accès au module Certificats.");
                    break;

                case "Situations Financières":
                    if (situationFinanciereController != null) {
                        // TODO: Passer l'ID du dossier si nécessaire
                        // situationFinanciereController.refreshView(dossierId);
                    }
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
