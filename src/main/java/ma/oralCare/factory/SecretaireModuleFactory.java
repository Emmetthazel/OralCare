package ma.oralCare.factory;

import ma.oralCare.mvc.controllers.RDV.impl.RDVControllerImpl;
import ma.oralCare.mvc.controllers.facture.impl.FactureControllerImpl;
import ma.oralCare.mvc.controllers.patient.impl.PatientControllerImpl;
import ma.oralCare.mvc.controllers.session.api.SessionSecretaireController;
import ma.oralCare.mvc.controllers.session.impl.SessionSecretaireControllerImpl;
import ma.oralCare.mvc.controllers.users.api.SecretaireController;
import ma.oralCare.mvc.controllers.users.impl.SecretaireControllerImpl;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;
import ma.oralCare.repository.modules.caisse.impl.FactureRepositoryImpl;
import ma.oralCare.repository.modules.users.api.SecretaireRepository;
import ma.oralCare.repository.modules.users.impl.SecretaireRepositoryImpl;
import ma.oralCare.service.modules.RDV.api.RDVService;
import ma.oralCare.service.modules.RDV.impl.RDVServiceImpl;
import ma.oralCare.service.modules.facture.api.FactureService;
import ma.oralCare.service.modules.facture.impl.FactureServiceImpl;
import ma.oralCare.service.modules.realtime.api.RealTimeService;
import ma.oralCare.service.modules.realtime.impl.RealTimeServiceImpl;
import ma.oralCare.service.modules.session.api.SessionSecretaireService;
import ma.oralCare.service.modules.session.impl.SessionSecretaireServiceImpl;
import ma.oralCare.service.modules.users.api.SecretaireService;
import ma.oralCare.service.modules.users.impl.SecretaireServiceImpl;
import ma.oralCare.repository.modules.agenda.api.RDVRepository; // Import ajouté

/**
 * Factory pour la création et l'injection des dépendances du module secrétaire
 */
public class SecretaireModuleFactory {

    private static SecretaireModuleFactory instance;
    private boolean initialized = false;

    // Services singleton
    private SecretaireRepository secretaireRepository;
    private SecretaireService secretaireService;
    private SessionSecretaireService sessionService;
    private RealTimeService realTimeService;
    private ma.oralCare.service.modules.RDV.api.RDVService rdvService;
    private ma.oralCare.service.modules.patient.api.PatientService patientService;
    private ma.oralCare.service.modules.facture.api.FactureService factureService;
    private ma.oralCare.service.modules.consultation.api.ConsultationService consultationService;
    private ma.oralCare.service.modules.certificat.api.CertificatService certificatService;

    // Contrôleurs
    private SecretaireController secretaireController;
    private SessionSecretaireController sessionController;
    private ma.oralCare.mvc.controllers.RDV.api.RDVController rdvController;
    private ma.oralCare.mvc.controllers.patient.api.PatientController patientController;
    private ma.oralCare.mvc.controllers.facture.api.FactureController factureController;
    private ma.oralCare.mvc.controllers.consultation.api.ConsultationController consultationController;
    private ma.oralCare.mvc.controllers.certificat.api.CertificatController certificatController;

    private SecretaireModuleFactory() {
    }

    public static synchronized SecretaireModuleFactory getInstance() {
        if (instance == null) {
            instance = new SecretaireModuleFactory();
        }
        return instance;
    }

    /**
     * Initialise tous les services et contrôleurs
     */
    public synchronized void initialize() {
        if (!initialized) {
            try {
                initializeRepositories();
                initializeServices();
                initializeControllers();
                startBackgroundServices();
                initialized = true;
                System.out.println("[FACTORY] Module secrétaire initialisé avec succès");
            } catch (Exception e) {
                System.err.println("[FACTORY] Erreur lors de l'initialisation: " + e.getMessage());
                throw new RuntimeException("Échec de l'initialisation du module secrétaire", e);
            }
        }
    }

    /**
     * Initialise les repositories
     */
    private void initializeRepositories() {
        this.secretaireRepository = new SecretaireRepositoryImpl();
        System.out.println("[FACTORY] Repository secrétaire initialisé");
    }

    /**
     * Initialise les services
     */
    private void initializeServices() {
        this.secretaireService = new SecretaireServiceImpl(secretaireRepository);
        this.realTimeService = new RealTimeServiceImpl();
        this.sessionService = new SessionSecretaireServiceImpl(secretaireService);

        // Initialisation correcte des dépendances pour RDV
        this.rdvService = new RDVServiceImpl();

        // Initialisation correcte de PatientService
        ma.oralCare.repository.modules.patient.api.PatientRepository patientRepo = new ma.oralCare.repository.modules.patient.impl.PatientRepositoryImpl();
        this.patientService = new ma.oralCare.service.modules.patient.impl.PatientServiceImpl(patientRepo);

        // Initialisation correcte de FactureService
        FactureRepository factureRepo = new FactureRepositoryImpl();
        this.factureService = new ma.oralCare.service.modules.facture.impl.FactureServiceImpl(factureRepo, realTimeService);

        // Placeholders pour les services futurs
        this.consultationService = null;
        this.certificatService = null;

        System.out.println("[FACTORY] Services initialisés");
    }

    /**
     * Initialise les contrôleurs
     */
    private void initializeControllers() {
        this.secretaireController = new SecretaireControllerImpl(secretaireService);
        this.sessionController = new SessionSecretaireControllerImpl(sessionService);

        // Initialisation correcte du contrôleur Facture
        this.factureController = new ma.oralCare.mvc.controllers.facture.impl.FactureControllerImpl(factureService);

        // Controllers nécessitant une vue UI (Initialisation différée)
        this.rdvController = null;
        this.patientController = null;

        this.consultationController = null;
        this.certificatController = null;

        System.out.println("[FACTORY] Contrôleurs initialisés");
    }

    /**
     * Démarre les services de fond
     */
    private void startBackgroundServices() {
        if (realTimeService != null) {
            realTimeService.startRealTimeService();
        }

        if (sessionService instanceof SessionSecretaireServiceImpl) {
            ((SessionSecretaireServiceImpl) sessionService).startSessionCleanupTask();
        }

        System.out.println("[FACTORY] Services de fond démarrés");
    }

    /**
     * Arrête proprement tous les services
     */
    public synchronized void shutdown() {
        if (initialized) {
            try {
                if (realTimeService != null) {
                    realTimeService.stopRealTimeService();
                }
                initialized = false;
                System.out.println("[FACTORY] Module secrétaire arrêté");
            } catch (Exception e) {
                System.err.println("[FACTORY] Erreur lors de l'arrêt: " + e.getMessage());
            }
        }
    }

    // Getters pour les services

    public SecretaireRepository getSecretaireRepository() {
        checkInitialized();
        return secretaireRepository;
    }

    public SecretaireService getSecretaireService() {
        checkInitialized();
        return secretaireService;
    }

    public SessionSecretaireService getSessionService() {
        checkInitialized();
        return sessionService;
    }

    public RealTimeService getRealTimeService() {
        checkInitialized();
        return realTimeService;
    }

    // Getters pour les contrôleurs

    public SecretaireController getSecretaireController() {
        checkInitialized();
        return secretaireController;
    }

    public SessionSecretaireController getSessionController() {
        checkInitialized();
        return sessionController;
    }

    public ma.oralCare.mvc.controllers.RDV.api.RDVController getRdvController() {
        checkInitialized();
        if (rdvController == null) {
            throw new UnsupportedOperationException("RDV controller requires UI components - create when view is available");
        }
        return rdvController;
    }

    public ma.oralCare.mvc.controllers.patient.api.PatientController getPatientController() {
        checkInitialized();
        if (patientController == null) {
            throw new UnsupportedOperationException("Patient controller requires UI components - create when view is available");
        }
        return patientController;
    }

    public ma.oralCare.mvc.controllers.facture.api.FactureController getFactureController() {
        checkInitialized();
        return factureController;
    }

    public ma.oralCare.mvc.controllers.consultation.api.ConsultationController getConsultationController() {
        checkInitialized();
        if (consultationController == null) {
            throw new UnsupportedOperationException("Consultation controller not yet implemented");
        }
        return consultationController;
    }

    public ma.oralCare.mvc.controllers.certificat.api.CertificatController getCertificatController() {
        checkInitialized();
        if (certificatController == null) {
            throw new UnsupportedOperationException("Certificat controller not yet implemented");
        }
        return certificatController;
    }

    public ma.oralCare.service.modules.RDV.api.RDVService getRdvService() {
        checkInitialized();
        return rdvService;
    }

    public ma.oralCare.service.modules.patient.api.PatientService getPatientService() {
        checkInitialized();
        return patientService;
    }

    public ma.oralCare.service.modules.facture.api.FactureService getFactureService() {
        checkInitialized();
        return factureService;
    }

    public ma.oralCare.service.modules.consultation.api.ConsultationService getConsultationService() {
        checkInitialized();
        if (consultationService == null) {
            throw new UnsupportedOperationException("Consultation service not yet implemented");
        }
        return consultationService;
    }

    public ma.oralCare.service.modules.certificat.api.CertificatService getCertificatService() {
        checkInitialized();
        if (certificatService == null) {
            throw new UnsupportedOperationException("Certificat service not yet implemented");
        }
        return certificatService;
    }

    private void checkInitialized() {
        if (!initialized) {
            throw new IllegalStateException("La factory n'est pas initialisée. Appelez initialize() d'abord.");
        }
    }

    public ma.oralCare.mvc.ui.dashboard.SecretaireDashboard createSecretaireDashboard() {
        checkInitialized();
        return new ma.oralCare.mvc.ui.dashboard.SecretaireDashboard();
    }

    public synchronized void reset() {
        shutdown();
        instance = null;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public String getSystemStatus() {
        if (!initialized) {
            return "Non initialisé";
        }

        StringBuilder status = new StringBuilder();
        status.append("=== État du Module Secrétaire ===\n");
        status.append("Initialisé: ").append(initialized).append("\n");

        if (sessionService != null) {
            status.append("Sessions actives: ").append(
                    ((SessionSecretaireServiceImpl) sessionService).getAllActiveSessions().size()).append("\n");
        }

        if (realTimeService != null) {
            status.append("Service temps réel: ").append("Actif").append("\n");
        }

        return status.toString();
    }
}