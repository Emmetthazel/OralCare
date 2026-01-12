package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import ma.oralCare.service.modules.agenda.api.RDVDisplayModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Classe de base corrigée : Le bouton d'action a été supprimé.
 */
public abstract class BaseDashboard extends JFrame implements DashboardView {
    protected JPanel contentArea;
    protected CardLayout cardLayout;
    protected StatsPanel statsPanel = new StatsPanel();
    protected TodayAppointmentsPanel todayAppointmentsPanel = new TodayAppointmentsPanel();

    public BaseDashboard(String title, String userName, String userRole, String[] navItems) {
        setTitle(title);
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 1. Container Principal
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(ColorPalette.BACKGROUND);

        // 2. Sidebar
        SidebarPanel sidebar = new SidebarPanel(userName, userRole, navItems, item -> cardLayout.show(contentArea, item));
        mainContainer.add(sidebar, BorderLayout.WEST);

        // 3. Zone de contenu principale
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(ColorPalette.BACKGROUND);
        rightContainer.setBorder(new EmptyBorder(30, 30, 30, 30));

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setOpaque(false);

        rightContainer.add(contentArea, BorderLayout.CENTER);
        mainContainer.add(rightContainer, BorderLayout.CENTER);

        add(mainContainer);
    }

    /**
     * Vue par défaut du tableau de bord (Statistiques + Tableau des RDV)
     * MODIFICATION : Suppression du bouton d'action.
     */
    protected JPanel createMainDashboardView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout(0, 25));
        view.setOpaque(false);

        // --- EN-TÊTE DU MODULE (Titre seul) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);

        topPanel.add(titleLabel, BorderLayout.WEST);

        // --- ZONE CENTRALE (Stats + Tableau) ---
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        centerContent.add(statsPanel);
        centerContent.add(Box.createRigidArea(new Dimension(0, 30)));
        centerContent.add(todayAppointmentsPanel);

        view.add(topPanel, BorderLayout.NORTH);
        view.add(centerContent, BorderLayout.CENTER);

        return view;
    }

    @Override
    public void updateStatCards(String patients, String visits, String appts) {
        if (statsPanel != null) {
            statsPanel.updateValues(patients, visits, appts);
        }
    }

    @Override
    public void updateAppointments(List<RDVDisplayModel> rdvs) {
        if (todayAppointmentsPanel != null) {
            todayAppointmentsPanel.updateAppointments(rdvs);
        }
    }

    protected JPanel createPlaceholderPage(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        JLabel label = new JLabel("Module en cours de développement : " + title);
        label.setFont(FontsPalette.TITLE);
        label.setForeground(ColorPalette.SECONDARY_TEXT);
        panel.add(label);
        return panel;
    }
}