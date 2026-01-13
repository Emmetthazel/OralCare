package ma.oralCare.mvc.ui1;

import ma.oralCare.mvc.ui1.medecin.ConsultationPanel;
import ma.oralCare.mvc.ui1.medecin.DashboardMedecinPanel;
import ma.oralCare.mvc.ui1.medecin.DossierMedicalMedecinPanel;
import ma.oralCare.mvc.ui1.secretaire.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * MainFrame : Fenêtre principale corrigée pour les rôles Secrétaire et Médecin.
 * Implémente Navigatable pour une compatibilité totale avec MenuBarPanel.
 */
public class MainFrame extends JFrame implements Navigatable {

    private CardLayout cardLayout;
    private JPanel centerPanel;
    private SideBarPanel sideBar;
    private HeaderPanel headerPanel;
    private MenuBarPanel menuBar;
    private FooterPanel footerPanel;

    private String currentUserRole;
    private String currentUserName;

    public MainFrame(String role, String userName) {
        this.currentUserRole = role;
        this.currentUserName = userName;

        // --- CONFIGURATION DE LA FENÊTRE ---
        setTitle("OralCare - Gestion de Cabinet Dentaire");
        setMinimumSize(new Dimension(1300, 850));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Initialisation du gestionnaire de navigation
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);

        // 2. Initialisation des composants de structure
        headerPanel = new HeaderPanel(currentUserName, currentUserRole);
        sideBar = new SideBarPanel(this, currentUserRole);
        // ✅ Correction : 'this' est maintenant valide car on implémente Navigatable
        menuBar = new MenuBarPanel(this, currentUserRole, currentUserName);
        footerPanel = new FooterPanel();

        // 3. Mise en page globale (Optimisation du Nord)
        setLayout(new BorderLayout());

        // Empilement MenuBar + Header
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(menuBar, BorderLayout.NORTH);
        northPanel.add(headerPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(sideBar, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);

        // 4. Chargement des modules
        setupViews();

        // 5. Gestion du responsive
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                if (menuBar != null) menuBar.adaptToWidth(width);

                if (width < 1400) {
                    sideBar.setPreferredSize(new Dimension(200, 0));
                } else {
                    sideBar.setPreferredSize(new Dimension(250, 0));
                }
                sideBar.revalidate();
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    /**
     * ✅ Méthode requise par l'interface Navigatable
     */
    @Override
    public void showView(String viewID) {
        cardLayout.show(centerPanel, viewID);

        // Mise à jour visuelle des composants de structure
        if (headerPanel != null) {
            headerPanel.setModuleTitle(viewID.replace("_", " "));
        }
        if (footerPanel != null) {
            footerPanel.setStatus("Module " + viewID + " chargé.", false);
        }

        refreshCurrentView();
    }

    private void setupViews() {
        // --- 1. DASHBOARDS ---
        if (isMedecin()) {
            centerPanel.add(new DashboardMedecinPanel(this), "DASHBOARD");
        } else {
            centerPanel.add(new DashboardSecretairePanel(this), "DASHBOARD");
        }

        // --- 2. MODULES COMMUNS ---
        centerPanel.add(new PatientManagementPanel(this), "PATIENTS");
        centerPanel.add(new VisualAgendaPanel(this), "VISUAL_AGENDA");
        centerPanel.add(new AgendaManagementPanel(this), "RDV");
        centerPanel.add(new CaisseFacturationPanel(this), "CAISSE");

        // --- 3. GESTION DES DOSSIERS (DIFFÉRENCIÉE) ---
        if (isMedecin()) {
            centerPanel.add(new DossierMedicalMedecinPanel(this), "DOSSIERS");
            centerPanel.add(new ConsultationPanel(), "CONSULTATIONS");
            centerPanel.add(new JPanel(), "ACTES");
        } else {
            centerPanel.add(new DossierMedicalSecretairePanel(this), "DOSSIERS");
            // Modules spécifiques secrétaire
            centerPanel.add(new SituationFinanciereSecretairePanel(this), "SITUATION_FINANCIERE");
            centerPanel.add(new FileAttentePanel(this), "FILE_ATTENTE");
            centerPanel.add(new NotificationsPanel(this), "NOTIFICATIONS");
        }

        // --- 4. PROFILE (Appelé par MenuBarPanel) ---
        centerPanel.add(new JPanel(), "PROFILE");

        showView("DASHBOARD");
    }

    public void refreshCurrentView() {
        Component currentComp = getVisibleCard();
        if (currentComp == null) return;

        // Routage vers les méthodes de rafraîchissement
        if (currentComp instanceof DashboardSecretairePanel) {
            ((DashboardSecretairePanel) currentComp).refreshData();
        }
        else if (currentComp instanceof DashboardMedecinPanel) {
            ((DashboardMedecinPanel) currentComp).refreshData();
        }
        else if (currentComp instanceof PatientManagementPanel) {
            ((PatientManagementPanel) currentComp).refreshTable();
        }
        // ... (ajoutez les autres panels au besoin)
    }

    /**
     * Permet d'accéder au SideBarPanel pour mettre en évidence les boutons
     */
    public SideBarPanel getSideBarPanel() {
        return sideBar;
    }
    
    /**
     * Permet d'accéder directement au DossierMedicalPanel actuel
     */
    public JPanel getDossierMedicalPanel() {
        try {
            // Le centerPanel est dans BorderLayout.CENTER
            Component centerComponent = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent instanceof JPanel) {
                JPanel centerPanel = (JPanel) centerComponent;
                
                // Chercher le DossierMedicalPanel ou DossierMedicalSecretairePanel dans le centerPanel
                for (Component comp : centerPanel.getComponents()) {
                    if (comp instanceof DossierMedicalPanel || comp instanceof DossierMedicalSecretairePanel) {
                        return (JPanel) comp;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'accès au DossierMedicalPanel: " + e.getMessage());
        }
        return null;
    }

    private Component getVisibleCard() {
        for (Component comp : centerPanel.getComponents()) {
            if (comp.isVisible()) return comp;
        }
        return null;
    }

    private boolean isMedecin() {
        String role = currentUserRole.toUpperCase();
        return role.contains("MEDECIN") || role.contains("DOCTOR");
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            // Test Secrétaire
            MainFrame app = new MainFrame("SECRETAIRE", "Mme. Fatima");
            app.setVisible(true);
        });
    }
}