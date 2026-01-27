package ma.oralCare.mvc.ui.mainframe;

import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController;
import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.controllers.admin.api.SystemReferentielController;
import ma.oralCare.mvc.controllers.dashboard.api.DashboardController;
import ma.oralCare.mvc.controllers.patient.api.PatientController;
import ma.oralCare.mvc.controllers.RDV.api.RDVController;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

// Importer les nouveaux renderers
import ma.oralCare.mvc.ui.mainframe.FixedSizeButtonRenderer;
import ma.oralCare.mvc.ui.mainframe.StatusDotRenderer;

/**
 * AdminDashboard refactorisé avec pattern MVC flexible
 * Package: ma.oralCare.mvc.ui.mainframe
 * Conserve le style et l'UI de l'original
 */
public class AdminDashboard extends JPanel {

    // Couleurs basées sur le design Flat 2.0 (identique à l'original)
    private final Color BG_COLOR = Color.decode("#F8FAFC");
    private final Color TEXT_DARK = Color.decode("#1E293B");
    private final Color SLATE_400 = Color.decode("#94A3B8");
    private final Color BORDER_COLOR = Color.decode("#E2E8F0");

    // Dégradé exact du bouton Action (identique à l'original)
    private final Color CYAN_DEGRADE = Color.decode("#06B6D4");
    private final Color BLUE_DEGRADE = Color.decode("#3B82F6");

    // Composants principaux pour la navigation MVC
    private NavigationController navigationController;
    private Map<String, JPanel> viewPanels;
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    // Mapping des contrôleurs (pattern MapsTo)
    private Map<String, NavigationController> controllerMap;
    
    // Références aux contrôleurs (injectées)
    private AdminDashboardController adminDashboardController;
    private UserManagementController userManagementController;
    private SystemReferentielController systemReferentielController;
    private DashboardController dashboardController;
    private PatientController patientController;
    private RDVController rdvController;

    public AdminDashboard() {
        setupTheme();
        initializeControllers();
        setupUI();
        setupControllerMapping();
    }

    /**
     * Injection des dépendances des contrôleurs
     */
    public void setControllers(AdminDashboardController adminDashboardController,
                              UserManagementController userManagementController,
                              SystemReferentielController systemReferentielController,
                              DashboardController dashboardController,
                              PatientController patientController,
                              RDVController rdvController) {
        this.adminDashboardController = adminDashboardController;
        this.userManagementController = userManagementController;
        this.systemReferentielController = systemReferentielController;
        this.dashboardController = dashboardController;
        this.patientController = patientController;
        this.rdvController = rdvController;
    }

    private void initializeControllers() {
        controllerMap = new HashMap<>();
    }

    private void setupControllerMapping() {
        // Pattern MapsTo(Controller) - mapping flexible des vues aux contrôleurs
        controllerMap.put("DASHBOARD", new NavigationController() {
            @Override
            public void executeNavigation(String viewId, JPanel targetPanel) {
                if (adminDashboardController != null) {
                    showDashboardView(targetPanel);
                }
            }
            @Override
            public String getControllerName() { return "AdminDashboardController"; }
        });

        controllerMap.put("PATIENTS", new NavigationController() {
            @Override
            public void executeNavigation(String viewId, JPanel targetPanel) {
                if (patientController != null) {
                    showPatientsView(targetPanel);
                }
            }
            @Override
            public String getControllerName() { return "PatientController"; }
        });

        controllerMap.put("RDV", new NavigationController() {
            @Override
            public void executeNavigation(String viewId, JPanel targetPanel) {
                if (rdvController != null) {
                    showRDVView(targetPanel);
                }
            }
            @Override
            public String getControllerName() { return "RDVController"; }
        });

        controllerMap.put("USERS", new NavigationController() {
            @Override
            public void executeNavigation(String viewId, JPanel targetPanel) {
                if (userManagementController != null) {
                    showUsersView(targetPanel);
                }
            }
            @Override
            public String getControllerName() { return "UserManagementController"; }
        });

        controllerMap.put("SETTINGS", new NavigationController() {
            @Override
            public void executeNavigation(String viewId, JPanel targetPanel) {
                if (systemReferentielController != null) {
                    showSettingsView(targetPanel);
                }
            }
            @Override
            public String getControllerName() { return "SystemReferentielController"; }
        });
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        // Création du panneau de contenu principal avec le style original
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(BG_COLOR);

        // Initialisation des vues
        initializeViews();

        add(mainContentPanel, BorderLayout.CENTER);
    }

    private void initializeViews() {
        // Création des panneaux pour chaque vue
        mainContentPanel.add(createOriginalDashboardView(), "DASHBOARD");
        mainContentPanel.add(createPatientsPanel(), "PATIENTS");
        mainContentPanel.add(createRDVPanel(), "RDV");
        mainContentPanel.add(createUsersPanel(), "USERS");
        mainContentPanel.add(createSettingsPanel(), "SETTINGS");
    }

    /**
     * ActionListener implémentant le pattern de navigation flexible
     */
    private class NavigationActionListener implements ActionListener {
        private final String viewId;

        public NavigationActionListener(String viewId) {
            this.viewId = viewId;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Utilisation directe du CardLayout pour la navigation
            CardLayout cl = (CardLayout) mainContentPanel.getLayout();
            cl.show(mainContentPanel, viewId);
        }
    }

    // Méthodes de création des vues (déléguées aux contrôleurs)
    private void showDashboardView(JPanel targetPanel) {
        targetPanel.add(createOriginalDashboardView(), BorderLayout.CENTER);
    }

    private void showPatientsView(JPanel targetPanel) {
        targetPanel.add(createPatientsPanel(), BorderLayout.CENTER);
    }

    private void showRDVView(JPanel targetPanel) {
        targetPanel.add(createRDVPanel(), BorderLayout.CENTER);
    }

    private void showUsersView(JPanel targetPanel) {
        targetPanel.add(createUsersPanel(), BorderLayout.CENTER);
    }

    private void showSettingsView(JPanel targetPanel) {
        targetPanel.add(createSettingsPanel(), BorderLayout.CENTER);
    }

    // Vue Dashboard avec le style original COMPLET
    private JPanel createOriginalDashboardView() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);
        
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(BG_COLOR);
        container.setBorder(new EmptyBorder(30, 50, 30, 50));

        // 1. Barre de recherche (style original)
        container.add(createSearchBar());
        container.add(Box.createVerticalStrut(35));

        // 2. Statistiques (style original)
        container.add(createSectionTitle("📈", "Statistiques Globales", Color.decode("#06B6D4")));
        container.add(Box.createVerticalStrut(20));
        container.add(createStatsGrid());

        // 3. Dernières Inscriptions (style original)
        container.add(Box.createVerticalStrut(40));
        container.add(createSectionTitle("📂", "Dernières Inscriptions (Cabinets)", Color.decode("#8B5CF6")));
        container.add(Box.createVerticalStrut(20));
        container.add(createTableCard());

        // 4. Activité Récente (style original)
        container.add(Box.createVerticalStrut(40));
        container.add(createSectionTitle("🕒", "Activité Récente du Système", Color.decode("#22C55E")));
        container.add(Box.createVerticalStrut(20));
        container.add(createActivityCard());

        JScrollPane scrollMain = new JScrollPane(container);
        scrollMain.setBorder(null);
        scrollMain.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollMain, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createTableCard() {
        // Conteneur principal avec border-radius de 15px AVEC border comme la partie activité
        RoundedPanel card = new RoundedPanel(15, Color.WHITE); // Utilise le RoundedPanel avec border automatique
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 25, 20, 25)); // Padding intérieur

        String[] columns = {"NOM DU CABINET", "DATE CRÉATION", "STATUT", "ACTION"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Uniquement la colonne ACTION est éditable
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        // Données d'exemple
        model.addRow(new Object[]{"Cabinet Ibn Sina", "20/01/2026", "En ligne", "Voir Détails"});
        model.addRow(new Object[]{"Clinique Dr. Amrani", "18/01/2026", "Hors ligne", "Voir Détails"});
        model.addRow(new Object[]{"Centre Dentaire Al Mansour", "15/01/2026", "En ligne", "Voir Détails"});
        model.addRow(new Object[]{"Cabinet Sourire Plus", "12/01/2026", "Hors ligne", "Voir Détails"});

        JTable table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        // Style SANS BORDURES
        table.setRowHeight(56);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(Color.decode("#F0F9FF"));
        table.setSelectionForeground(Color.decode("#0369A1"));
        table.setBorder(null); // PAS de bordure

        // Header SANS BORDURE
        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setForeground(Color.decode("#6B7280"));
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 48));
        header.setBorder(null); // PAS de bordure
        
        // Centrer les colonnes sauf la première
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) c;
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("Segoe UI", Font.BOLD, 11));
                label.setForeground(Color.decode("#6B7280"));
                if (column == 0) {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return label;
            }
        };
        
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Rendu des colonnes avec style moderne
        table.getColumn("STATUT").setCellRenderer(new StatusDotRenderer());
        table.getColumn("ACTION").setCellRenderer(new FixedSizeButtonRenderer());
        table.getColumn("ACTION").setCellEditor(new FixedSizeButtonEditor());

        // Style des colonnes de texte
        DefaultTableCellRenderer textRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                JLabel label = (JLabel) c;
                
                if (column == 0) { // NOM DU CABINET
                    label.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    label.setForeground(Color.decode("#111827"));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                } else if (column == 1) { // DATE CRÉATION
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    label.setForeground(Color.decode("#6B7280"));
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setVerticalAlignment(SwingConstants.CENTER);
                    label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                }
                
                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#FAFAFA"));
                }
                
                return label;
            }
        };
        
        table.getColumn("NOM DU CABINET").setCellRenderer(textRenderer);
        table.getColumn("DATE CRÉATION").setCellRenderer(textRenderer);

        // Ajout direct du tableau dans le RoundedPanel avec border
        card.add(table, BorderLayout.CENTER);
        card.setPreferredSize(new Dimension(0, 280));
        return card;
    }

    // Méthodes du style original (copiées de votre AdminDashboard original)
    private void setupTheme() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Component.focusWidth", 0);
            UIManager.put("Button.arc", 12);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private JPanel createSearchBar() {
        RoundedPanel p = new RoundedPanel(14, Color.WHITE);
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(12, 25, 12, 25));
        p.setMaximumSize(new Dimension(2000, 60));
        JTextField input = new JTextField("Recherche rapide...");
        input.setBorder(null);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        input.setForeground(SLATE_400);
        JLabel searchIcon = new JLabel("🔍   ");
        searchIcon.setForeground(SLATE_400);
        p.add(searchIcon, BorderLayout.WEST);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStatsGrid() {
        JPanel statsGrid = new JPanel(new GridLayout(1, 3, 25, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(new StatCard("🏢", "Cabinets Actifs", "12", "+2 ce mois", Color.decode("#3B82F6"), Color.decode("#06B6D4")));
        statsGrid.add(new StatCard("👥", "Total Utilisateurs", "45", "+8 cette semaine", Color.decode("#8B5CF6"), Color.decode("#D946EF")));
        statsGrid.add(new StatCard("🔔", "Alertes Système", "02", "Nécessite attention", Color.decode("#F97316"), Color.decode("#EF4444")));
        return statsGrid;
    }

    private JPanel createActivityCard() {
        RoundedPanel card = new RoundedPanel(16, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.add(createActivityRow("🏢", "10:15", "Nouveau cabinet \"Santé Pro\" ajouté", Color.decode("#22C55E")));
        card.add(Box.createVerticalStrut(20));
        card.add(createActivityRow("📉", "09:00", "Sauvegarde automatique réussie (Cloud)", Color.decode("#3B82F6")));
        card.add(Box.createVerticalStrut(20));
        card.add(createActivityRow("🚀", "Hier", "Mise à jour v2.1 déployée avec succès", Color.decode("#10B981")));
        return card;
    }

    private JPanel createActivityRow(String icon, String time, String text, Color iconBg) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        p.setOpaque(false);
        JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(iconBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconLbl.setForeground(Color.WHITE);
        iconLbl.setPreferredSize(new Dimension(42, 42));
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        JLabel content = new JLabel("<html><font color='#94A3B8'>["+time+"]</font> <font color='#475569'>&nbsp;"+text+"</font></html>");
        content.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        p.add(iconLbl); p.add(content);
        return p;
    }

    private JPanel createSectionTitle(String emoji, String text, Color accent) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        p.setOpaque(false);
        JLabel icon = new JLabel(emoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(TEXT_DARK);
        p.add(icon); p.add(label);
        return p;
    }

    class StatCard extends JPanel {
        public StatCard(String icon, String title, String val, String trend, Color c1, Color c2) {
            setLayout(new BorderLayout(20, 0));
            setOpaque(false);
            setBorder(new EmptyBorder(25, 25, 25, 25));
            JLabel iconLbl = new JLabel(icon, SwingConstants.CENTER) {
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            iconLbl.setPreferredSize(new Dimension(55, 55));
            iconLbl.setForeground(Color.WHITE);
            iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            JPanel r = new JPanel(); r.setLayout(new BoxLayout(r, BoxLayout.Y_AXIS)); r.setOpaque(false);
            JLabel t = new JLabel(title); t.setForeground(SLATE_400); t.setFont(new Font("Segoe UI", Font.BOLD, 12));
            JLabel v = new JLabel(val); v.setFont(new Font("Segoe UI", Font.BOLD, 30)); v.setForeground(TEXT_DARK);
            JLabel tr = new JLabel(trend); tr.setForeground(SLATE_400); tr.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            r.add(t); r.add(v); r.add(tr);
            add(iconLbl, BorderLayout.WEST); add(r, BorderLayout.CENTER);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 18, 18);
            g2.dispose();
        }
    }

    class RoundedPanel extends JPanel {
        private int r; Color c;
        public RoundedPanel(int r, Color c) { this.r=r; this.c=c; setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, r, r);
            g2.dispose();
        }
    }

    // Panneaux de vue simplifiés (à remplacer par les vraies implémentations)
    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel("Gestion des Patients", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JLabel("Interface Patients en cours de développement...", SwingConstants.CENTER), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createRDVPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel("Gestion des Rendez-vous", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JLabel("Interface Rendez-vous en cours de développement...", SwingConstants.CENTER), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel("Gestion des Utilisateurs", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JLabel("Interface Utilisateurs en cours de développement...", SwingConstants.CENTER), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_COLOR);
        
        JLabel titleLabel = new JLabel("Paramètres", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_DARK);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JLabel("Interface Paramètres en cours de développement...", SwingConstants.CENTER), BorderLayout.CENTER);
        
        return panel;
    }

    // Méthodes de navigation MVC
    public void setNavigationController(NavigationController controller) {
        this.navigationController = controller;
    }

    public void navigateToView(String viewId) {
        CardLayout cl = (CardLayout) mainContentPanel.getLayout();
        cl.show(mainContentPanel, viewId);
    }

    public void refreshData() {
        revalidate();
        repaint();
    }
}
