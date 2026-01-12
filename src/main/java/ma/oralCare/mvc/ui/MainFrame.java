package ma.oralCare.mvc.ui;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.mvc.ui.admin.user.CabinetFormView;
import ma.oralCare.repository.modules.cabinet.api.*;
import ma.oralCare.repository.modules.cabinet.impl.*;
import ma.oralCare.repository.modules.system.api.*;
import ma.oralCare.repository.modules.system.impl.*;
import ma.oralCare.repository.modules.users.api.*;
import ma.oralCare.repository.modules.users.impl.*;
import ma.oralCare.repository.modules.dossierMedical.api.*;
import ma.oralCare.repository.modules.dossierMedical.impl.*;
import ma.oralCare.repository.modules.patient.api.*;
import ma.oralCare.repository.modules.patient.impl.*;
import ma.oralCare.service.modules.admin.api.*;
import ma.oralCare.service.modules.admin.impl.*;
import ma.oralCare.service.modules.cabinet.api.*; // ✅ Ajouté
import ma.oralCare.service.modules.cabinet.impl.*; // ✅ Ajouté
import ma.oralCare.mvc.controllers.admin.api.*;
import ma.oralCare.mvc.controllers.admin.impl.*;

import ma.oralCare.mvc.ui.admin.AdminDashboard;
import ma.oralCare.mvc.ui.admin.components.AdminSidebar;
import ma.oralCare.mvc.ui.admin.user.UserListView;
import ma.oralCare.mvc.ui.admin.roles.RoleManagerView;
import ma.oralCare.mvc.ui.admin.referentiel.ReferentielView;
import ma.oralCare.mvc.ui.admin.security.BackupManagerView;
import ma.oralCare.mvc.ui.admin.security.AuditLogView;

import ma.oralCare.mvc.ui1.FooterPanel;
import ma.oralCare.mvc.ui1.HeaderPanel;
import ma.oralCare.mvc.ui1.MenuBarPanel;
import ma.oralCare.mvc.ui1.Navigatable;

import ma.oralCare.service.modules.auth.dto.UserPrincipal;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame implements Navigatable {

    private final JPanel mainContent;
    private final CardLayout cardLayout;
    private final AdminSidebar sidebar;

    private final HeaderPanel header;
    private final FooterPanel footer;
    private final MenuBarPanel menuBar;

    public MainFrame(UserPrincipal principal) {
        // --- 1. INITIALISATION DES REPOSITORIES ---
        LogRepository logRepo = new LogRepositoryImpl();
        SystemRepository systemRepo = new SystemRepositoryImpl();
        CabinetMedicaleRepository cabinetRepo = new CabinetMedicaleRepositoryImpl();
        UtilisateurRepository userRepo = new UtilisateurRepositoryImpl();
        MedicamentRepository medicRepo = new MedicamentRepositoryImpl();
        AntecedentRepository anteRepo = new AntecedentRepositoryImpl();
        ActeRepository acteRepo = new ActeRepositoryImpl();

        // --- 2. INITIALISATION DES SERVICES ---
        UserManagementService userService = new UserManagementServiceImpl(userRepo);

        // ✅ CORRECTION : Initialisation du service de gestion des cabinets
        CabinetManagementService cabinetService = new CabinetManagementServiceImpl(cabinetRepo);

        SystemReferentielService refService = new SystemReferentielServiceImpl(medicRepo, anteRepo, acteRepo);

        String currentAdminLogin = principal.getLogin();
        String currentRole = "ADMINISTRATEUR";

        // --- 3. INITIALISATION DES CONTROLLERS ---

        // ✅ CORRECTION : Le contrôleur reçoit maintenant userService ET cabinetService
        UserManagementController userCtrl = new UserManagementControllerImpl(userService, cabinetService);

        AdminDashboardController dashboardCtrl = new AdminDashboardControllerImpl(
                cabinetRepo,
                logRepo,
                systemRepo,
                userRepo,
                currentAdminLogin
        );

        SystemReferentielController refCtrl = new SystemReferentielControllerImpl(refService);

        // --- 4. CONFIGURATION DE LA FENÊTRE ---
        setTitle("OralCare System - Administration [" + currentAdminLogin + "]");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 5. COMPOSANTS DE STRUCTURE ---
        header = new HeaderPanel(currentAdminLogin, currentRole);
        footer = new FooterPanel();
        menuBar = new MenuBarPanel(this, currentRole, currentAdminLogin);
        sidebar = new AdminSidebar(this);

        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.add(menuBar, BorderLayout.NORTH);
        northWrapper.add(header, BorderLayout.SOUTH);

        add(northWrapper, BorderLayout.NORTH);
        add(sidebar, BorderLayout.WEST);
        add(footer, BorderLayout.SOUTH);

        // --- 6. CONTENEUR CENTRAL (CARDLAYOUT) ---
        cardLayout = new CardLayout();
        mainContent = new JPanel(cardLayout);

        // Enregistrement des vues
        mainContent.add(new AdminDashboard(currentAdminLogin, dashboardCtrl), "DASHBOARD");
        mainContent.add(new UserListView(userCtrl), "USERS");
        mainContent.add(new CabinetFormView(userCtrl, this), "FORM_CABINET");
        mainContent.add(new RoleManagerView(), "ROLES");
        mainContent.add(new ReferentielView(refCtrl), "REF_DATA");
        mainContent.add(new BackupManagerView(), "SECURITY");
        mainContent.add(new AuditLogView(dashboardCtrl), "LOGS");

        mainContent.add(new JPanel(), "PROFILE");

        add(mainContent, BorderLayout.CENTER);

        showView("DASHBOARD");
    }

    @Override
    public void showView(String viewKey) {
        changePage(viewKey);
    }

    public void changePage(String pageId) {
        cardLayout.show(mainContent, pageId);

        if (header != null) header.setModuleTitle(formatTitle(pageId));
        if (footer != null) footer.setStatus("Module " + formatTitle(pageId) + " actif", false);

        Component currentCard = getVisibleCard();

        if (currentCard instanceof AdminDashboard) {
            ((AdminDashboard) currentCard).refreshData();
        } else if (currentCard instanceof UserListView) {
            ((UserListView) currentCard).renderHierarchy();
        } else if (currentCard instanceof AuditLogView) {
            ((AuditLogView) currentCard).refreshLogs();
        }

        mainContent.revalidate();
        mainContent.repaint();
    }

    private String formatTitle(String id) {
        if ("LOGS".equals(id)) return "LOGS & AUDIT";
        if ("FORM_CABINET".equals(id)) return "CRÉATION CABINET"; // ✅ Titre propre
        return id.replace("_", " ");
    }

    private Component getVisibleCard() {
        for (Component comp : mainContent.getComponents()) {
            if (comp.isVisible()) return comp;
        }
        return null;
    }
}