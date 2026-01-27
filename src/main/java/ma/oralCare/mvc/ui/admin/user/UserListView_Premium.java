package ma.oralCare.mvc.ui.admin.user;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.mvc.ui1.Navigatable;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.font.TextAttribute;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.util.Hashtable;

/**
 * Vue d'administration premium : Gestion du personnel par Cabinet.
 * Design moderne inspiré de React avec gradients, ombres et animations.
 * Version fidèle au composant React DentalStaffManager.
 */
public class UserListView_Premium extends JPanel {

    // ========== CONSTANTES PREMIUM ==========
    
    // Couleurs premium inspirées de React
    private static final Color PRIMARY_COLOR = new Color(102, 126, 234);
    private static final Color PRIMARY_DARK = new Color(76, 95, 185);
    private static final Color SECONDARY_COLOR = new Color(118, 75, 162);
    private static final Color SUCCESS_COLOR = new Color(72, 187, 120);
    private static final Color SUCCESS_DARK = new Color(56, 142, 60);
    private static final Color DANGER_COLOR = new Color(229, 62, 62);
    private static final Color DANGER_DARK = new Color(197, 48, 48);
    private static final Color WARNING_COLOR = new Color(237, 137, 54);
    private static final Color WARNING_DARK = new Color(221, 107, 32);
    private static final Color LIGHT_BG = new Color(248, 250, 252);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HOVER_BG = new Color(237, 242, 247);
    private static final Color SELECTED_BG = new Color(232, 234, 246);
    private static final Color TEXT_PRIMARY = new Color(45, 55, 72);
    private static final Color TEXT_SECONDARY = new Color(113, 128, 150);
    private static final Color WHITE = Color.WHITE;
    private static final Color HEADER_START = new Color(26, 54, 93);
    private static final Color HEADER_END = new Color(44, 82, 130);
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 40);
    private static final Color AVATAR_MED = new Color(102, 126, 234);
    private static final Color AVATAR_SEC = new Color(240, 147, 251);
    
    // Polices premium
    private static final Font HEADER_FONT = new Font("Playfair Display", Font.PLAIN, 40);
    private static final Font SUBTITLE_FONT = new Font("Crimson Pro", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Playfair Display", Font.PLAIN, 22);
    private static final Font SECTION_FONT = new Font("Crimson Pro", Font.ITALIC, 18);
    private static final Font USER_NAME_FONT = new Font("Crimson Pro", Font.BOLD, 17);
    private static final Font EMAIL_FONT = new Font("IBM Plex Mono", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Crimson Pro", Font.PLAIN, 15);
    private static final Font LABEL_FONT = new Font("Crimson Pro", Font.BOLD, 13);
    private static final Font INPUT_FONT = new Font("Crimson Pro", Font.PLAIN, 14);
    
    // Dimensions premium
    private static final int BORDER_RADIUS_LARGE = 20;
    private static final int BORDER_RADIUS_MEDIUM = 16;
    private static final int BORDER_RADIUS_SMALL = 12;
    private static final int BORDER_RADIUS_BUTTON = 10;
    private static final int AVATAR_SIZE = 48;
    private static final int BUTTON_ICON_SIZE = 16;
    private static final int HEADER_ICON_SIZE = 42;
    private static final int CABINET_ICON_SIZE = 24;
    private static final int SEARCH_ICON_SIZE = 20;
    
    // Messages
    private static final String MSG_DELETE_CONFIRM = "Êtes-vous sûr de vouloir supprimer cet utilisateur ?";
    private static final String MSG_DELETE_SUCCESS = "Utilisateur supprimé avec succès";
    private static final String MSG_SAVE_SUCCESS = "Modifications enregistrées";
    private static final String MSG_REQUIRED_FIELDS = "Les champs Nom, Prénom et Email sont obligatoires";
    private static final String MSG_NO_RESULTS = "Aucun résultat trouvé";
    
    // Délais et animations
    private static final int SEARCH_DEBOUNCE_MS = 300;
    private static final int ANIMATION_DURATION_MS = 200;
    
    // ========== VARIABLES D'INSTANCE ==========
    
    private final Map<String, JPanel> detailPanels = new HashMap<>();
    private final Map<String, Boolean> expandedCabinets = new HashMap<>();
    private final UserManagementController controller;
    private final CardLayout cardLayout;
    private final JPanel cards;
    private JPanel mainContent;
    private JTextField searchField;
    private String selectedEmail = null;
    private String editingEmail = null;
    private java.util.Timer searchTimer;
    private boolean isLoading = false;
    private JLabel loadingLabel;
    private Map<String, String> pendingChanges = new HashMap<>();
    
    // Mock data pour le développement
    private Map<String, List<UserStaffDTO>> mockData;
    
    // État du dialogue de création
    private boolean showCreateDialog = false;
    private String createCabinet = "";
    private String createRole = "";

    public UserListView_Premium(UserManagementController controller) {
        this.controller = controller;
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        
        // Initialiser les données mock pour le développement
        initializeMockData();
        
        // Configuration de l'interface premium
        setLayout(new BorderLayout());
        setBackground(LIGHT_BG);
        setBorder(new EmptyBorder(40, 48, 40, 48));

        // Création du panneau principal
        JPanel listView = new JPanel(new BorderLayout());
        listView.setBackground(LIGHT_BG);
        listView.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Header premium avec gradient
        listView.add(createPremiumHeader(), BorderLayout.NORTH);
        
        // Contenu principal
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(LIGHT_BG);
        contentWrapper.setBorder(new EmptyBorder(40, 0, 0, 0));
        
        // Barre de recherche et actions premium
        contentWrapper.add(createPremiumSearchPanel(), BorderLayout.NORTH);
        
        // Contenu des cabinets avec scroll moderne
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(LIGHT_BG);
        
        JScrollPane scrollPane = createPremiumScrollPane(mainContent);
        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        
        listView.add(contentWrapper, BorderLayout.CENTER);

        cards.add(listView, "LIST");
        add(cards, BorderLayout.CENTER);
        
        // Charger les données initiales
        renderHierarchy();
    }
    
    /**
     * Initialise les données mock pour le développement
     */
    private void initializeMockData() {
        mockData = new HashMap<>();
        
        // Cabinet Dentaire Central
        List<UserStaffDTO> cabinet1 = new ArrayList<>();
        cabinet1.add(createMockUser("dr.hassan@dentalcare.ma", "Dr. Hassan ALAMI", "MÉDECIN", "Cabinet Dentaire Central"));
        cabinet1.add(createMockUser("fatima.benali@dentalcare.ma", "Fatima BENALI", "SECRÉTAIRE", "Cabinet Dentaire Central"));
        mockData.put("Cabinet Dentaire Central", cabinet1);
        
        // Cabinet Sourire Plus
        List<UserStaffDTO> cabinet2 = new ArrayList<>();
        cabinet2.add(createMockUser("dr.amina@sourire.ma", "Dr. Amina LAHLOU", "MÉDECIN", "Cabinet Sourire Plus"));
        mockData.put("Cabinet Sourire Plus", cabinet2);
    }
    
    private UserStaffDTO createMockUser(String email, String nomComplet, String role, String cabinetNom) {
        // Extraire nom et prénom du nom complet
        String[] parts = nomComplet.split(" ", 2);
        String nom = parts.length > 1 ? parts[1] : parts[0];
        String prenom = parts.length > 1 ? parts[0] : "";
        
        UserStaffDTO user = new UserStaffDTO(nom, prenom, email, "ACTIF", role, cabinetNom);
        
        // Ajouter des données mock complètes
        if ("MÉDECIN".equals(role)) {
            user.setTel("06" + String.format("%08d", (int)(Math.random() * 100000000)));
            user.setCin("AB" + String.format("%06d", (int)(Math.random() * 1000000)));
            user.setDateNaissance("198" + (int)(Math.random() * 10) + "-0" + (int)(Math.random() * 9 + 1) + "-" + String.format("%02d", (int)(Math.random() * 28 + 1)));
            user.setSexe("MALE");
            user.setAdresse("Rabat, Agdal");
            user.setSpecialite("Orthodontie");
            user.setPassword("dental2024");
        } else {
            user.setTel("06" + String.format("%08d", (int)(Math.random() * 100000000)));
            user.setCin("CD" + String.format("%06d", (int)(Math.random() * 1000000)));
            user.setDateNaissance("199" + (int)(Math.random() * 10) + "-0" + (int)(Math.random() * 9 + 1) + "-" + String.format("%02d", (int)(Math.random() * 28 + 1)));
            user.setSexe("FEMALE");
            user.setAdresse("Rabat, Hassan");
            user.setNumCnss(String.format("%09d", (int)(Math.random() * 1000000000)));
            user.setPassword("secure123");
        }
        
        return user;
    }

    // ========== MÉTHODES DE CRÉATION DE COMPOSANTS PREMIUM ==========
    
    /**
     * Crée le header premium avec gradient et icônes
     */
    private JPanel createPremiumHeader() {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Créer le gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, HEADER_START,
                    getWidth(), getHeight(), HEADER_END
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Ajouter l'ombre en bas
                g2d.setColor(new Color(0, 0, 0, 32));
                g2d.fillRect(0, getHeight() - 3, getWidth(), 3);
                
                g2d.dispose();
            }
        };
        
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(40, 48, 40, 48));
        
        // Contenu du header
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        
        // Section gauche avec icône et titre
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        leftSection.setOpaque(false);
        
        // Icône Building2
        JLabel buildingIcon = new JLabel("🏢") {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, HEADER_ICON_SIZE);
            }
        };
        buildingIcon.setForeground(WHITE);
        leftSection.add(buildingIcon);
        
        // Section titre
        JPanel titleSection = new JPanel(new BorderLayout(0, 8));
        titleSection.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Gestion du Personnel") {
            @Override
            public Font getFont() {
                return HEADER_FONT;
            }
        };
        titleLabel.setForeground(WHITE);
        titleLabel.setFont(HEADER_FONT);
        titleSection.add(titleLabel, BorderLayout.NORTH);
        
        JLabel subtitleLabel = new JLabel("Administration des Cabinets Dentaires") {
            @Override
            public Font getFont() {
                return SUBTITLE_FONT;
            }
        };
        subtitleLabel.setForeground(new Color(255, 255, 255, 230));
        subtitleLabel.setFont(SUBTITLE_FONT);
        titleSection.add(subtitleLabel, BorderLayout.CENTER);
        
        leftSection.add(titleSection);
        headerContent.add(leftSection, BorderLayout.WEST);
        
        headerPanel.add(headerContent, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Crée la barre de recherche premium avec bouton flottant
     */
    private JPanel createPremiumSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(WHITE);
        searchPanel.setBorder(createRoundedBorder(BORDER_RADIUS_MEDIUM, BORDER_COLOR));
        searchPanel.setPreferredSize(new Dimension(0, 80));
        
        // Padding interne
        JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.setBackground(WHITE);
        innerPanel.setBorder(new EmptyBorder(28, 32, 28, 32));
        
        // Section recherche à gauche
        JPanel searchSection = new JPanel(new BorderLayout());
        searchSection.setBackground(WHITE);
        searchSection.setMaximumSize(new Dimension(400, 60));
        
        // Panneau de recherche avec icône
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setBackground(WHITE);
        
        // Icône de recherche
        JLabel searchIcon = new JLabel("🔍") {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, SEARCH_ICON_SIZE);
            }
        };
        searchIcon.setForeground(TEXT_SECONDARY);
        searchIcon.setBorder(new EmptyBorder(0, 16, 0, 8));
        searchWrapper.add(searchIcon, BorderLayout.WEST);
        
        // Champ de recherche
        searchField = new JTextField() {
            @Override
            public Font getFont() {
                return INPUT_FONT;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setColor(TEXT_SECONDARY);
                    g2d.setFont(INPUT_FONT);
                    g2d.drawString("Rechercher par nom ou email...", 8, getHeight() / 2 + 5);
                    g2d.dispose();
                }
            }
        };
        searchField.setFont(INPUT_FONT);
        searchField.setBorder(createRoundedBorder(BORDER_RADIUS_SMALL, new Color(226, 232, 240)));
        searchField.setBackground(WHITE);
        searchField.setForeground(TEXT_PRIMARY);
        searchField.setCaretColor(PRIMARY_COLOR);
        searchField.setBorder(new EmptyBorder(14, 16, 14, 16));
        
        // Gestion du focus
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                searchField.setBorder(createRoundedBorder(BORDER_RADIUS_SMALL, PRIMARY_COLOR));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                searchField.setBorder(createRoundedBorder(BORDER_RADIUS_SMALL, BORDER_COLOR));
            }
        });
        
        // Gestion de la recherche avec debounce
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                scheduleSearch();
            }
        });
        
        searchWrapper.add(searchField, BorderLayout.CENTER);
        searchSection.add(searchWrapper, BorderLayout.CENTER);
        
        innerPanel.add(searchSection, BorderLayout.WEST);
        
        // Bouton "Nouveau Cabinet" à droite
        JButton newCabinetBtn = createPremiumButton(
            "Nouveau Cabinet", 
            "➕", 
            PRIMARY_COLOR, 
            SECONDARY_COLOR,
            new EmptyBorder(14, 28, 14, 28)
        );
        
        newCabinetBtn.addActionListener(e -> navigateToNewCabinet());
        innerPanel.add(newCabinetBtn, BorderLayout.EAST);
        
        searchPanel.add(innerPanel, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    /**
     * Crée un scroll pane premium avec design moderne
     */
    private JScrollPane createPremiumScrollPane(JPanel content) {
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBackground(LIGHT_BG);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        scrollPane.getVerticalScrollBar().setUI(new PremiumScrollBarUI());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        return scrollPane;
    }
    
    /**
     * Crée un bouton premium avec gradient et ombre
     */
    private JButton createPremiumButton(String text, String icon, Color startColor, Color endColor, EmptyBorder margin) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Créer le gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    getWidth(), getHeight(), endColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS_BUTTON, BORDER_RADIUS_BUTTON);
                
                // Ajouter l'ombre
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(0, 2, getWidth(), getHeight() - 2, BORDER_RADIUS_BUTTON, BORDER_RADIUS_BUTTON);
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
            
            @Override
            public Font getFont() {
                return BUTTON_FONT;
            }
        };
        
        button.setFont(BUTTON_FONT);
        button.setForeground(WHITE);
        button.setBorder(margin);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Animation hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(new EmptyBorder(12, 28, 16, 28));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(margin);
            }
        });
        
        return button;
    }
    
    /**
     * Crée une bordure arrondie
     */
    private Border createRoundedBorder(int radius, Color color) {
        return new Border() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
                g2d.dispose();
            }
            
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(2, 2, 2, 2);
            }
            
            @Override
            public boolean isBorderOpaque() {
                return false;
            }
        };
    }
    
    /**
     * ScrollBar UI premium
     */
    private static class PremiumScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        
        @Override
        protected void configureScrollBarColors() {
            thumbColor = new Color(203, 213, 224);
            thumbHighlightColor = new Color(226, 232, 240);
            thumbDarkShadowColor = new Color(160, 174, 192);
            thumbLightShadowColor = new Color(248, 250, 252);
            trackColor = LIGHT_BG;
            trackHighlightColor = BORDER_COLOR;
        }
        
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }
        
        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }
        
        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
        
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, 8, 8);
            
            g2.dispose();
        }
    }
    
    // --- RENDU DE LA HIERARCHIE PREMIUM ---

    public void renderHierarchy() {
        mainContent.removeAll();
        detailPanels.clear();
        expandedCabinets.clear();

        // Utiliser le controller pour charger les données réelles
        Map<String, List<UserStaffDTO>> data;
        
        try {
            // Utiliser le controller si disponible
            if (controller != null) {
                String searchTerm = searchField != null ? searchField.getText().trim() : "";
                data = controller.loadHierarchy(searchTerm);
            } else {
                // Fallback sur les données mock si controller null
                data = mockData;
                if (searchField != null && !searchField.getText().trim().isEmpty()) {
                    data = filterData(data, searchField.getText().trim());
                }
            }
        } catch (Exception e) {
            // En cas d'erreur avec le controller, utiliser les données mock
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
            data = mockData;
            if (searchField != null && !searchField.getText().trim().isEmpty()) {
                data = filterData(data, searchField.getText().trim());
            }
        }

        if (data.isEmpty()) {
            mainContent.add(createNoResultsPanel());
        } else {
            data.forEach((cabinetName, staffList) -> {
                addPremiumCabinetCard(cabinetName, staffList);
                
                // Initialiser l'état d'expansion (premier cabinet ouvert)
                if (expandedCabinets.isEmpty()) {
                    expandedCabinets.put(cabinetName, true);
                } else {
                    expandedCabinets.put(cabinetName, false);
                }
            });
        }

        mainContent.revalidate();
        mainContent.repaint();
    }
    
    /**
     * Ajoute une carte de cabinet premium avec design moderne
     */
    private void addPremiumCabinetCard(String cabinetName, List<UserStaffDTO> staffList) {
        // Carte principale du cabinet
        JPanel cabinetCard = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Ombre portée
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, BORDER_RADIUS_MEDIUM, BORDER_RADIUS_MEDIUM);
                
                g2d.dispose();
            }
        };
        cabinetCard.setBackground(WHITE);
        cabinetCard.setBorder(createRoundedBorder(BORDER_RADIUS_MEDIUM, new Color(0, 0, 0, 6)));
        cabinetCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));
        
        // Header du cabinet
        JPanel cabinetHeader = createPremiumCabinetHeader(cabinetName, staffList, cabinetCard);
        cabinetCard.add(cabinetHeader, BorderLayout.NORTH);
        
        // Contenu du cabinet (sections MÉDECINS/SECRÉTAIRES)
        JPanel cabinetContent = createPremiumCabinetContent(cabinetName, staffList);
        cabinetCard.add(cabinetContent, BorderLayout.CENTER);
        
        mainContent.add(cabinetCard);
        mainContent.add(Box.createVerticalStrut(24)); // Espacement entre les cartes
    }
    
    /**
     * Crée le header d'un cabinet premium
     */
    private JPanel createPremiumCabinetHeader(String cabinetName, List<UserStaffDTO> staffList, JPanel cabinetCard) {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Fond gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(247, 250, 252),
                    0, getHeight(), new Color(237, 242, 247)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Ligne de séparation en bas
                g2d.setColor(new Color(226, 232, 240));
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                
                g2d.dispose();
            }
        };
        
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setBorder(new EmptyBorder(24, 32, 24, 32));
        headerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Contenu du header
        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        
        // Section gauche avec icône, chevron et titre
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftSection.setOpaque(false);
        
        // Chevron d'expansion
        JLabel chevronIcon = new JLabel() {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, 24);
            }
            
            @Override
            public String getText() {
                return expandedCabinets.getOrDefault(cabinetName, false) ? "▼" : "▶";
            }
        };
        chevronIcon.setForeground(TEXT_PRIMARY);
        leftSection.add(chevronIcon);
        
        // Icône du cabinet
        JLabel cabinetIcon = new JLabel("🏢") {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, CABINET_ICON_SIZE);
            }
        };
        cabinetIcon.setForeground(PRIMARY_COLOR);
        leftSection.add(cabinetIcon);
        
        // Nom du cabinet
        JLabel cabinetLabel = new JLabel(cabinetName) {
            @Override
            public Font getFont() {
                return TITLE_FONT;
            }
        };
        cabinetLabel.setForeground(TEXT_PRIMARY);
        cabinetLabel.setFont(TITLE_FONT);
        leftSection.add(cabinetLabel);
        
        headerContent.add(leftSection, BorderLayout.WEST);
        
        // Badge du nombre de membres
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        badgePanel.setOpaque(false);
        
        JLabel memberBadge = new JLabel(staffList.size() + " membre" + (staffList.size() > 1 ? "s" : "")) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond du badge
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
            }
            
            @Override
            public Font getFont() {
                return new Font("Crimson Pro", Font.PLAIN, 13);
            }
        };
        memberBadge.setForeground(WHITE);
        memberBadge.setBorder(new EmptyBorder(6, 16, 6, 16));
        memberBadge.setOpaque(false);
        badgePanel.add(memberBadge);
        
        headerContent.add(badgePanel, BorderLayout.EAST);
        
        headerPanel.add(headerContent, BorderLayout.CENTER);
        
        // Gestion du clic pour l'expansion
        headerPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                togglePremiumCabinetExpansion(cabinetName, cabinetCard);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                headerPanel.setBackground(new Color(237, 242, 247));
                headerPanel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                headerPanel.setBackground(new Color(247, 250, 252));
                headerPanel.repaint();
            }
        });
        
        return headerPanel;
    }
    
    /**
     * Crée le contenu d'un cabinet premium
     */
    private JPanel createPremiumCabinetContent(String cabinetName, List<UserStaffDTO> staffList) {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(WHITE);
        contentPanel.setBorder(new EmptyBorder(24, 32, 24, 32));
        
        // Panel principal pour les sections
        JPanel sectionsPanel = new JPanel();
        sectionsPanel.setLayout(new BoxLayout(sectionsPanel, BoxLayout.Y_AXIS));
        sectionsPanel.setBackground(WHITE);
        
        // Section MÉDECINS
        List<UserStaffDTO> medecins = staffList.stream()
            .filter(user -> "MÉDECIN".equals(user.getRole()))
            .collect(java.util.stream.Collectors.toList());
        
        if (!medecins.isEmpty()) {
            JPanel medecinsSection = createPremiumRoleSection(cabinetName, "MÉDECINS", medecins, AVATAR_MED);
            sectionsPanel.add(medecinsSection);
            sectionsPanel.add(Box.createVerticalStrut(32));
        }
        
        // Section SECRÉTAIRES
        List<UserStaffDTO> secretaires = staffList.stream()
            .filter(user -> "SECRÉTAIRE".equals(user.getRole()))
            .collect(java.util.stream.Collectors.toList());
        
        if (!secretaires.isEmpty()) {
            JPanel secretairesSection = createPremiumRoleSection(cabinetName, "SECRÉTAIRES", secretaires, AVATAR_SEC);
            sectionsPanel.add(secretairesSection);
        }
        
        contentPanel.add(sectionsPanel, BorderLayout.CENTER);
        
        // Gérer la visibilité du contenu
        boolean isExpanded = expandedCabinets.getOrDefault(cabinetName, false);
        contentPanel.setVisible(isExpanded);
        
        return contentPanel;
    }
    
    /**
     * Bascule l'expansion d'un cabinet premium
     */
    private void togglePremiumCabinetExpansion(String cabinetName, JPanel cabinetCard) {
        boolean isExpanded = expandedCabinets.getOrDefault(cabinetName, false);
        expandedCabinets.put(cabinetName, !isExpanded);
        
        // Mettre à jour le chevron
        Component[] components = cabinetCard.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel headerPanel = (JPanel) comp;
                Component[] headerComponents = headerPanel.getComponents();
                for (Component headerComp : headerComponents) {
                    if (headerComp instanceof JPanel) {
                        JPanel headerContent = (JPanel) headerComp;
                        Component[] contentComponents = headerContent.getComponents();
                        for (Component contentComp : contentComponents) {
                            if (contentComp instanceof JPanel) {
                                JPanel leftSection = (JPanel) contentComp;
                                Component[] leftComponents = leftSection.getComponents();
                                if (leftComponents.length > 0 && leftComponents[0] instanceof JLabel) {
                                    JLabel chevronIcon = (JLabel) leftComponents[0];
                                    chevronIcon.setText(expandedCabinets.get(cabinetName) ? "▼" : "▶");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Afficher/masquer le contenu
        for (Component comp : components) {
            if (comp instanceof JPanel && comp != cabinetCard.getComponent(0)) {
                comp.setVisible(expandedCabinets.get(cabinetName));
            }
        }
        
        cabinetCard.revalidate();
        cabinetCard.repaint();
    }
    
    /**
     * Crée une section de rôle premium (MÉDECINS/SECRÉTAIRES)
     */
    private JPanel createPremiumRoleSection(String cabinetName, String roleTitle, List<UserStaffDTO> users, Color avatarColor) {
        JPanel sectionPanel = new JPanel(new BorderLayout());
        sectionPanel.setBackground(WHITE);
        
        // Header de la section
        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setBackground(WHITE);
        sectionHeader.setBorder(new EmptyBorder(0, 0, 16, 0));
        
        // Titre de la section
        JLabel titleLabel = new JLabel(roleTitle) {
            @Override
            public Font getFont() {
                return SECTION_FONT;
            }
        };
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setFont(SECTION_FONT);
        sectionHeader.add(titleLabel, BorderLayout.WEST);
        
        // Bouton "Ajouter"
        JButton addButton = createPremiumButton(
            "Ajouter",
            "👤",
            SUCCESS_COLOR,
            SUCCESS_DARK,
            new EmptyBorder(8, 16, 8, 16)
        );
        
        addButton.setFont(new Font("Crimson Pro", Font.PLAIN, 13));
        addButton.addActionListener(e -> navigateToNewUser(cabinetName, roleTitle));
        sectionHeader.add(addButton, BorderLayout.EAST);
        
        // Ligne de séparation
        JPanel separatorPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(226, 232, 240));
                g2d.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2d.dispose();
            }
        };
        separatorPanel.setPreferredSize(new Dimension(0, 18));
        separatorPanel.setBackground(WHITE);
        
        sectionPanel.add(sectionHeader, BorderLayout.NORTH);
        sectionPanel.add(separatorPanel, BorderLayout.CENTER);
        
        // Liste des utilisateurs
        JPanel usersPanel = new JPanel();
        usersPanel.setLayout(new BoxLayout(usersPanel, BoxLayout.Y_AXIS));
        usersPanel.setBackground(WHITE);
        
        for (UserStaffDTO user : users) {
            // Vérifier que l'utilisateur a un email valide
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                continue; // Ignorer les utilisateurs sans email
            }
            
            // Vérifier si cet utilisateur est en cours d'édition
            boolean isEditing = editingEmail != null && editingEmail.equals(user.getEmail());
            
            if (isEditing) {
                // Afficher le formulaire d'édition inline
                JPanel editPanel = createInlineEditPanel(user);
                usersPanel.add(editPanel);
            } else {
                // Afficher la ligne d'utilisateur normale
                JPanel userRow = createPremiumUserRow(user, avatarColor);
                usersPanel.add(userRow);
            }
            usersPanel.add(Box.createVerticalStrut(12));
        }
        
        JPanel usersWrapper = new JPanel(new BorderLayout());
        usersWrapper.setBackground(WHITE);
        usersWrapper.add(usersPanel, BorderLayout.CENTER);
        
        sectionPanel.add(usersWrapper, BorderLayout.SOUTH);
        
        return sectionPanel;
    }
    
    /**
     * Crée une ligne d'utilisateur premium avec avatar coloré
     */
    private JPanel createPremiumUserRow(UserStaffDTO user, Color avatarColor) {
        // Vérifications de sécurité
        if (user == null || user.getEmail() == null) {
            return new JPanel(); // Retourner un panneau vide si l'utilisateur est invalide
        }
        
        JPanel userRow = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Fond sélectionné ou normal
                if (user.getEmail().equals(selectedEmail)) {
                    // Fond gradient pour sélection
                    GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(232, 234, 246),
                        0, getHeight(), new Color(243, 229, 245)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else {
                    g2d.setColor(WHITE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                
                // Bordure
                g2d.setColor(user.getEmail().equals(selectedEmail) ? PRIMARY_COLOR : new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                
                g2d.dispose();
            }
        };
        
        userRow.setPreferredSize(new Dimension(0, 80));
        userRow.setBorder(new EmptyBorder(16, 24, 16, 24));
        userRow.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Contenu principal
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        
        // Section gauche avec avatar et infos
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 0));
        leftSection.setOpaque(false);
        
        // Avatar avec initiales
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond gradient de l'avatar
                GradientPaint gradient = new GradientPaint(
                    0, 0, avatarColor,
                    getWidth(), getHeight(), "MÉDECIN".equals(user.getRole()) ? SECONDARY_COLOR : avatarColor
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                g2d.dispose();
                
                // Dessiner les initiales
                Graphics2D textG2d = (Graphics2D) g.create();
                textG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                textG2d.setColor(WHITE);
                textG2d.setFont(new Font("Crimson Pro", Font.BOLD, 19));
                
                String initials = getInitials(user.getNomComplet() != null ? user.getNomComplet() : "??");
                FontMetrics fm = textG2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(initials)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;
                textG2d.drawString(initials, x, y);
                textG2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
        avatarPanel.setOpaque(false);
        leftSection.add(avatarPanel);
        
        // Infos utilisateur
        JPanel userInfoPanel = new JPanel(new BorderLayout(0, 4));
        userInfoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(user.getNomComplet() != null ? user.getNomComplet() : "Nom inconnu") {
            @Override
            public Font getFont() {
                return USER_NAME_FONT;
            }
        };
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setFont(USER_NAME_FONT);
        userInfoPanel.add(nameLabel, BorderLayout.NORTH);
        
        JLabel emailLabel = new JLabel(user.getEmail()) {
            @Override
            public Font getFont() {
                return EMAIL_FONT;
            }
        };
        emailLabel.setForeground(TEXT_SECONDARY);
        emailLabel.setFont(EMAIL_FONT);
        userInfoPanel.add(emailLabel, BorderLayout.CENTER);
        
        leftSection.add(userInfoPanel);
        mainContent.add(leftSection, BorderLayout.CENTER);
        
        // Boutons d'action
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionsPanel.setOpaque(false);
        
        // Bouton éditer
        JButton editButton = createIconButton("✏️", new Color(75, 85, 99), new Color(203, 213, 224));
        editButton.addActionListener(e -> handleEditUser(user));
        actionsPanel.add(editButton);
        
        // Bouton supprimer
        JButton deleteButton = createIconButton("🗑️", new Color(229, 62, 62), new Color(252, 165, 165));
        deleteButton.addActionListener(e -> handleDeleteUser(user));
        actionsPanel.add(deleteButton);
        
        mainContent.add(actionsPanel, BorderLayout.EAST);
        userRow.add(mainContent, BorderLayout.CENTER);
        
        // Gestion des interactions
        userRow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    handleEditUser(user);
                } else {
                    selectUser(user.getEmail());
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!user.getEmail().equals(selectedEmail)) {
                    userRow.setBackground(new Color(247, 250, 252));
                    userRow.repaint();
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!user.getEmail().equals(selectedEmail)) {
                    userRow.setBackground(WHITE);
                    userRow.repaint();
                }
            }
        });
        
        return userRow;
    }
    
    /**
     * Crée le formulaire d'édition inline exactement comme dans React
     */
    private JPanel createInlineEditPanel(UserStaffDTO user) {
        JPanel editPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Fond gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(248, 249, 250),
                    0, getHeight(), WHITE
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure principale
                g2d.setColor(PRIMARY_COLOR);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                
                g2d.dispose();
            }
        };
        
        editPanel.setBorder(new EmptyBorder(24, 32, 24, 32));
        editPanel.setPreferredSize(new Dimension(0, 400));
        
        // Grille de champs
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        fieldsPanel.setOpaque(false);
        
        // Champ Login
        JPanel loginField = createPremiumInputField("👤", "Login", user.getEmail().split("@")[0], true);
        fieldsPanel.add(loginField);
        
        // Champ Mot de passe
        JPanel passwordField = createPremiumInputField("🔒", "Mot de passe", user.getPassword(), true);
        fieldsPanel.add(passwordField);
        
        // Champ Nom
        String fullName = user.getNomComplet() != null ? user.getNomComplet() : "";
        String[] nameParts = fullName.split(" ", 2);
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        JPanel nameField = createPremiumInputField("👤", "Nom", lastName, false);
        fieldsPanel.add(nameField);
        
        // Champ Prénom
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        JPanel firstNameField = createPremiumInputField("👤", "Prénom", firstName, false);
        fieldsPanel.add(firstNameField);
        
        // Champ Email
        JPanel emailField = createPremiumInputField("📧", "Email", user.getEmail(), true);
        fieldsPanel.add(emailField);
        
        // Champ Téléphone
        JPanel phoneField = createPremiumInputField("📞", "Téléphone", user.getTel(), false);
        fieldsPanel.add(phoneField);
        
        // Champ CIN
        JPanel cinField = createPremiumInputField("👤", "CIN", user.getCin(), false);
        fieldsPanel.add(cinField);
        
        // Champ Date de naissance
        JPanel birthDateField = createPremiumInputField("📅", "Date de naissance", user.getDateNaissance(), true);
        fieldsPanel.add(birthDateField);
        
        // Champ Sexe
        JPanel genderField = createPremiumInputField("👤", "Sexe", user.getSexe(), true);
        fieldsPanel.add(genderField);
        
        // Champ Adresse
        JPanel addressField = createPremiumInputField("📍", "Adresse", user.getAdresse(), true);
        fieldsPanel.add(addressField);
        
        editPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Panneau de boutons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(new EmptyBorder(24, 0, 0, 0));
        
        // Bouton Sauvegarder
        JButton saveButton = createPremiumButton(
            "💾 Sauvegarder",
            "",
            SUCCESS_COLOR,
            SUCCESS_DARK,
            new EmptyBorder(12, 24, 12, 24)
        );
        saveButton.addActionListener(e -> handleSaveUser(user));
        buttonsPanel.add(saveButton);
        
        // Bouton Réinitialiser MDP
        JButton resetPasswordButton = createPremiumButton(
            "🔑 Réinitialiser MDP",
            "",
            WARNING_COLOR,
            WARNING_DARK,
            new EmptyBorder(12, 24, 12, 24)
        );
        resetPasswordButton.addActionListener(e -> {
            selectedEmail = user.getEmail();
            handleResetPasswordAction();
        });
        buttonsPanel.add(resetPasswordButton);
        
        // Bouton Annuler
        JButton cancelButton = createPremiumButton(
            "❌ Annuler",
            "",
            DANGER_COLOR,
            DANGER_DARK,
            new EmptyBorder(12, 24, 12, 24)
        );
        cancelButton.addActionListener(e -> cancelEditUser());
        buttonsPanel.add(cancelButton);
        
        editPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return editPanel;
    }
    
    /**
     * Crée un bouton avec icône
     */
    private JButton createIconButton(String icon, Color normalColor, Color hoverColor) {
        JButton button = new JButton(icon) {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, BUTTON_ICON_SIZE);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordure
                Color borderColor = getModel().isRollover() ? hoverColor : normalColor;
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
            }
        };
        
        button.setForeground(normalColor);
        button.setBorder(new EmptyBorder(8, 8, 8, 8));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Animation hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(hoverColor);
                button.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(normalColor);
                button.repaint();
            }
        });
        
        return button;
    }
    
    /**
     * Crée un champ de saisie premium
     */
    private JPanel createPremiumInputField(String icon, String label, String value, boolean disabled) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setOpaque(false);
        
        // Label
        JLabel labelComponent = new JLabel(label) {
            @Override
            public Font getFont() {
                return LABEL_FONT;
            }
        };
        labelComponent.setForeground(TEXT_PRIMARY);
        labelComponent.setFont(LABEL_FONT);
        labelComponent.setBorder(new EmptyBorder(0, 0, 8, 0));
        fieldPanel.add(labelComponent, BorderLayout.NORTH);
        
        // Panneau de saisie
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setOpaque(false);
        
        // Icône
        JLabel iconLabel = new JLabel(icon) {
            @Override
            public Font getFont() {
                return new Font("Segoe UI Emoji", Font.PLAIN, 18);
            }
        };
        iconLabel.setForeground(disabled ? new Color(160, 174, 192) : TEXT_SECONDARY);
        iconLabel.setBorder(new EmptyBorder(0, 12, 0, 8));
        inputPanel.add(iconLabel, BorderLayout.WEST);
        
        // Champ de texte
        JTextField textField = new JTextField(value) {
            @Override
            public Font getFont() {
                return INPUT_FONT;
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Fond
                if (disabled) {
                    g2d.setColor(new Color(247, 250, 252));
                } else if (value.startsWith("temp")) {
                    g2d.setColor(new Color(255, 251, 235)); // Highlight pour mot de passe temporaire
                } else {
                    g2d.setColor(WHITE);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Bordure
                g2d.setColor(new Color(226, 232, 240));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                
                g2d.dispose();
            }
        };
        
        textField.setFont(INPUT_FONT);
        textField.setBorder(new EmptyBorder(12, 12, 12, 12));
        textField.setOpaque(false);
        textField.setEnabled(!disabled);
        
        if (disabled) {
            textField.setForeground(new Color(160, 174, 192));
        } else if (value.startsWith("temp")) {
            textField.setForeground(new Color(4, 120, 87)); // Vert pour mot de passe temporaire
        } else {
            textField.setForeground(TEXT_PRIMARY);
        }
        
        // Gestion du focus
        if (!disabled) {
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    textField.repaint();
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    textField.repaint();
                }
            });
        }
        
        inputPanel.add(textField, BorderLayout.CENTER);
        fieldPanel.add(inputPanel, BorderLayout.CENTER);
        
        return fieldPanel;
    }
    
    /**
     * Filtre les données selon la recherche
     */
    private Map<String, List<UserStaffDTO>> filterData(Map<String, List<UserStaffDTO>> data, String query) {
        Map<String, List<UserStaffDTO>> filtered = new HashMap<>();
        String lowerQuery = query.toLowerCase();
        
        data.forEach((cabinet, staff) -> {
            List<UserStaffDTO> matchingStaff = staff.stream()
                .filter(user -> user.getNomComplet().toLowerCase().contains(lowerQuery) ||
                              user.getEmail().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
            
            if (!matchingStaff.isEmpty()) {
                filtered.put(cabinet, matchingStaff);
            }
        });
        
        return filtered;
    }
    
    /**
     * Crée un panneau pour afficher quand aucun résultat n'est trouvé
     */
    private JPanel createNoResultsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(60, 20, 60, 20)
        ));
        
        JLabel noResultsLabel = new JLabel(MSG_NO_RESULTS);
        noResultsLabel.setFont(LABEL_FONT);
        noResultsLabel.setForeground(TEXT_SECONDARY);
        
        panel.add(noResultsLabel);
        return panel;
    }
    
    /**
     * Extrait les initiales d'un nom
     */
    private String getInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "??";
        }
        
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
        } else if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        
        return "??";
    }
    
    // ========== GESTION DES ACTIONS ==========
    
    /**
     * Sélectionne un utilisateur
     */
    private void selectUser(String email) {
        selectedEmail = email;
        renderHierarchy();
    }
    
    /**
     * Gère l'édition d'un utilisateur
     */
    private void handleEditUser(UserStaffDTO user) {
        editingEmail = user.getEmail();
        renderHierarchy(); // Re-rendre pour afficher le formulaire d'édition
    }
    
    /**
     * Gère la suppression d'un utilisateur
     */
    private void handleDeleteUser(UserStaffDTO user) {
        int result = JOptionPane.showConfirmDialog(
            this,
            MSG_DELETE_CONFIRM,
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                // Utiliser le controller pour supprimer l'utilisateur
                if (controller != null) {
                    controller.deleteUser(user.getEmail());
                } else {
                    // Fallback sur les données mock
                    mockData.forEach((cabinet, staff) -> {
                        staff.removeIf(u -> u.getEmail().equals(user.getEmail()));
                    });
                }
                
                showNotification(MSG_DELETE_SUCCESS, SUCCESS_COLOR);
                renderHierarchy();
            } catch (Exception e) {
                showNotification("Erreur lors de la suppression: " + e.getMessage(), DANGER_COLOR);
            }
        }
    }
    
    /**
     * Gère la sauvegarde d'un utilisateur
     */
    private void handleSaveUser(UserStaffDTO user) {
        try {
            // Utiliser le controller pour sauvegarder l'utilisateur
            if (controller != null) {
                // Extraire nom et prénom du nom complet
                String[] nameParts = user.getNomComplet().split(" ", 2);
                String nom = nameParts.length > 1 ? nameParts[1] : "";
                String prenom = nameParts.length > 0 ? nameParts[0] : "";
                
                // Extraire la ville de l'adresse
                String ville = "Rabat"; // Valeur par défaut
                if (user.getAdresse() != null && user.getAdresse().contains(",")) {
                    ville = user.getAdresse().split(",")[1].trim();
                }
                
                controller.updateUser(
                    user.getEmail(),
                    nom,
                    prenom,
                    user.getTel(),
                    user.getCin(),
                    ville
                );
            } else {
                // Fallback sur les données mock
                mockData.forEach((cabinet, staff) -> {
                    for (int i = 0; i < staff.size(); i++) {
                        if (staff.get(i).getEmail().equals(user.getEmail())) {
                            staff.set(i, user);
                            break;
                        }
                    }
                });
            }
            
            showNotification(MSG_SAVE_SUCCESS, SUCCESS_COLOR);
            renderHierarchy();
        } catch (Exception e) {
            showNotification("Erreur lors de la sauvegarde: " + e.getMessage(), DANGER_COLOR);
        }
    }
    
    /**
     * Annule l'édition d'un utilisateur
     */
    private void cancelEditUser() {
        editingEmail = null;
        renderHierarchy(); // Re-rendre pour cacher le formulaire d'édition
    }
    
    /**
     * Gère la réinitialisation du mot de passe
     */
    private void handleResetPasswordAction() {
        if (selectedEmail == null) {
            showNotification("Veuillez d'abord sélectionner un utilisateur", WARNING_COLOR);
            return;
        }
        
        try {
            String newPassword;
            if (controller != null) {
                newPassword = controller.resetPassword(selectedEmail);
            } else {
                // Simulation pour le moment
                newPassword = "temp" + System.currentTimeMillis() % 100000;
            }
            
            if (newPassword != null) {
                JOptionPane.showMessageDialog(
                    this,
                    "Nouveau mot de passe généré : " + newPassword,
                    "Réinitialisation du mot de passe",
                    JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                showNotification("Erreur lors de la réinitialisation du mot de passe", DANGER_COLOR);
            }
        } catch (Exception e) {
            showNotification("Erreur: " + e.getMessage(), DANGER_COLOR);
        }
    }
    
    /**
     * Affiche une notification moderne
     */
    private void showNotification(String message, Color color) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Notification", false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(400, 100);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(INPUT_FONT);
        messageLabel.setForeground(WHITE);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        dialog.add(panel);
        
        // Auto-fermeture après 3 secondes
        javax.swing.Timer timer = new javax.swing.Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();
        
        dialog.setVisible(true);
    }
    
    /**
     * Planifie une recherche avec debounce
     */
    private void scheduleSearch() {
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        
        searchTimer = new java.util.Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        isLoading = true;
                        renderHierarchy();
                        
                        // Simuler un délai de chargement
                        java.util.Timer loadingTimer = new java.util.Timer();
                        loadingTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        isLoading = false;
                                        renderHierarchy();
                                    }
                                });
                            }
                        }, 500);
                    }
                });
            }
        }, SEARCH_DEBOUNCE_MS);
    }
    
    // ========== NAVIGATION ==========
    
    private void navigateToNewCabinet() {
        JOptionPane.showMessageDialog(
            this,
            "Navigation vers la création d'un nouveau cabinet",
            "Navigation",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    private void navigateToNewUser(String cabinetName, String role) {
        showCreateUserDialog(cabinetName, role);
    }
    
    /**
     * Affiche le dialogue de création d'utilisateur
     */
    private void showCreateUserDialog(String cabinetName, String role) {
        // Implémentation du dialogue de création
        JOptionPane.showMessageDialog(
            this,
            "Création d'un nouvel utilisateur\n\n" +
            "Cabinet: " + cabinetName + "\n" +
            "Rôle: " + role,
            "Création d'utilisateur",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
