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
import javax.swing.event.*;
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
* Vue d'administration : Gestion du personnel par Cabinet.
* Design moderne inspiré de React avec gradients, ombres et animations.
* Version fonctionnelle avec intégration complète du contrôleur.
*/
public class UserListView extends JPanel {
    
    // --- CONTRÔLEUR ET DONNÉES RÉELLES ---
    private final UserManagementController controller;
    private final CardLayout cardLayout;
    private final JPanel cards;
    private JPanel mainContent;
    private JTextField searchField;
    private String selectedEmail = null;
    private String editingEmail = null;
    private final Map<String, JPanel> detailPanels = new HashMap<>();
    private final Map<String, Boolean> expandedCabinets = new HashMap<>();
    private java.util.Timer searchTimer;
    private boolean isLoading = false;
    
    // --- PALETTE DE COULEURS MODERNE INSPIRÉE REACT ---
    private static final Color PRIMARY_COLOR = new Color(26, 54, 93);
    private static final Color PRIMARY_DARK = new Color(44, 82, 130);
    private static final Color PRIMARY_PURPLE = new Color(128, 90, 213);
    private static final Color PRIMARY_BLUE = new Color(102, 126, 234);
    private static final Color SUCCESS_GREEN = new Color(72, 187, 120);
    private static final Color DANGER_RED = new Color(229, 62, 62);
    private static final Color WARNING_ORANGE = new Color(237, 137, 54);
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color BG_GRAY = new Color(237, 242, 247);
    private static final Color BORDER_LIGHT = new Color(226, 232, 240);
    private static final Color BORDER_GRAY = new Color(203, 213, 224);
    private static final Color TEXT_PRIMARY = new Color(45, 55, 72);
    private static final Color TEXT_SECONDARY = new Color(113, 128, 150);
    private static final Color WHITE = Color.WHITE;
    
    // --- POLICES MODERNES ---
    private static final Font HEADER_FONT = new Font("Georgia", Font.PLAIN, 40);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Georgia", Font.PLAIN, 28);
    private static final Font SECTION_FONT = new Font("Segoe UI", Font.ITALIC, 18);
    private static final Font USER_NAME_FONT = new Font("Segoe UI", Font.BOLD, 17);
    private static final Font EMAIL_FONT = new Font("Consolas", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    
    // --- CONSTANTES ---
    private static final int BORDER_RADIUS_SMALL = 8;
    private static final int BORDER_RADIUS_MEDIUM = 12;
    private static final int BORDER_RADIUS_LARGE = 16;
    private static final int SEARCH_DEBOUNCE_MS = 300;
    
    // --- CONSTRUCTEUR ---
    public UserListView(UserManagementController controller) {
        this.controller = controller;
        this.cardLayout = new CardLayout();
        this.cards = new JPanel(cardLayout);
        
        // Configuration de l'interface moderne
        setLayout(new BorderLayout());
        setBackground(BG_LIGHT);
        setBorder(new EmptyBorder(40, 48, 40, 48));
        
        // Création du panneau principal
        JPanel listView = new JPanel(new BorderLayout());
        listView.setBackground(BG_LIGHT);
        listView.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Contenu principal
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(BG_LIGHT);
        contentWrapper.setBorder(new EmptyBorder(40, 0, 0, 0));
        
        // Barre de recherche et actions modernes
        contentWrapper.add(createModernSearchPanel(), BorderLayout.NORTH);
        
        // Contenu des cabinets avec scroll moderne
        mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(BG_LIGHT);
        JScrollPane scrollPane = createModernScrollPane(mainContent);
        contentWrapper.add(scrollPane, BorderLayout.CENTER);
        
        // Footer avec actions globales
        contentWrapper.add(createFooterPanel(), BorderLayout.SOUTH);
        
        listView.add(contentWrapper, BorderLayout.CENTER);
        cards.add(listView, "LIST");
        add(cards, BorderLayout.CENTER);
        
        // Charger les données réelles
        renderHierarchy();
    }

    // --- MÉTHODES PRINCIPALES ---

    /**
    * Rend la hiérarchie avec les données réelles du contrôleur
    */
    public void renderHierarchy() {
        mainContent.removeAll();
        detailPanels.clear();
        expandedCabinets.clear();
        
        // Utiliser les données réelles du contrôleur
        Map<String, List<UserStaffDTO>> data = controller.loadHierarchy(searchField != null ? searchField.getText().trim() : "");
        
        if (data.isEmpty()) {
            mainContent.add(createNoResultsPanel());
        } else {
            data.forEach((cabinetName, staffList) -> {
                // Créer la carte du cabinet
                CabinetCardPanel card = new CabinetCardPanel();
                card.setLayout(new BorderLayout());
                card.setBorder(new EmptyBorder(0, 0, 16, 0));
                card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 800));
                
                // Header du cabinet
                CabinetHeaderPanel header = new CabinetHeaderPanel(cabinetName, staffList.size());
                card.add(header, BorderLayout.NORTH);
                
                // Contenu (médecins + secrétaires)
                JPanel contentPanel = new JPanel();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                contentPanel.setBackground(Color.WHITE);
                contentPanel.setBorder(new EmptyBorder(16, 32, 24, 32));
                
                // Section Médecins
                List<UserStaffDTO> medecins = staffList.stream()
                    .filter(u -> "MÉDECIN".equals(u.getRole()) && u.getEmail() != null)
                    .collect(Collectors.toList());
                if (!medecins.isEmpty()) {
                    RoleSectionPanel medecinSection = new RoleSectionPanel("MÉDECINS", cabinetName);
                    for (UserStaffDTO user : medecins) {
                        if (editingEmail != null && editingEmail.equals(user.getEmail())) {
                            JPanel editPanel = createInlineEditPanel(user);
                            editPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
                            medecinSection.add(editPanel);
                        } else {
                            UserRowPanel userRow = new UserRowPanel(user);
                            userRow.setBorder(new EmptyBorder(12, 0, 12, 0));
                            medecinSection.add(userRow);
                        }
                    }
                    contentPanel.add(medecinSection);
                }
                
                // Section Secrétaires
                List<UserStaffDTO> secretaires = staffList.stream()
                    .filter(u -> "SECRÉTAIRE".equals(u.getRole()) && u.getEmail() != null)
                    .collect(Collectors.toList());
                if (!secretaires.isEmpty()) {
                    RoleSectionPanel secretaireSection = new RoleSectionPanel("SECRÉTAIRES", cabinetName);
                    for (UserStaffDTO user : secretaires) {
                        if (editingEmail != null && editingEmail.equals(user.getEmail())) {
                            JPanel editPanel = createInlineEditPanel(user);
                            editPanel.setBorder(new EmptyBorder(12, 0, 12, 0));
                            secretaireSection.add(editPanel);
                        } else {
                            UserRowPanel userRow = new UserRowPanel(user);
                            userRow.setBorder(new EmptyBorder(12, 0, 12, 0));
                            secretaireSection.add(userRow);
                        }
                    }
                    contentPanel.add(secretaireSection);
                }
                
                card.add(contentPanel, BorderLayout.CENTER);
                
                // Initialiser l'état d'expansion (premier cabinet ouvert)
                boolean isFirstCabinet = expandedCabinets.isEmpty();
                expandedCabinets.put(cabinetName, isFirstCabinet);
                contentPanel.setVisible(isFirstCabinet);
                
                mainContent.add(card);
            });
        }
        
        mainContent.revalidate();
        mainContent.repaint();
    }

    // --- MÉTHODES UTILITAIRES ---

    /**
    * Crée le panneau de recherche moderne
    */
    private JPanel createModernSearchPanel() {
        RoundedShadowPanel panel = new RoundedShadowPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(28, 40, 28, 40));
        
        // Panneau de recherche à gauche
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        searchPanel.setOpaque(false);
        
        // Icône de recherche
        searchPanel.add(createSearchIcon());
        
        JLabel searchLabel = new JLabel("Recherche :");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchPanel.add(searchLabel);
        
        searchField = createModernTextField(40);
        searchField.setText("");
        searchField.putClientProperty("JTextField.placeholderText", "Rechercher par nom ou email...");
        
        // Ajouter le listener pour la recherche avec debounce
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                scheduleSearch();
            }
        });
        
        searchPanel.add(searchField);
        
        // Panneau d'actions à droite
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        ModernGradientButton btnAddCabinet = new ModernGradientButton(
            "+ Nouveau Cabinet",
            PRIMARY_BLUE,
            PRIMARY_PURPLE,
            true
        );
        btnAddCabinet.setFont(BUTTON_FONT);
        btnAddCabinet.setBorder(new EmptyBorder(14, 28, 14, 28));
        btnAddCabinet.addActionListener(e -> navigateToNewCabinet());
        
        actionPanel.add(btnAddCabinet);
        
        panel.add(searchPanel, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout.EAST);
        return panel;
    }

    /**
    * Crée une icône de recherche moderne
    */
    private JLabel createSearchIcon() {
        JLabel icon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(113, 128, 150));
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int size = Math.min(getWidth(), getHeight()) - 4;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Cercle de la loupe
                g2d.drawOval(centerX - size/3, centerY - size/3, size*2/3, size*2/3);
                
                // Manche de la loupe
                g2d.drawLine(centerX + size/4, centerY + size/4, 
                            centerX + size/2 - 2, centerY + size/2 - 2);
                
                g2d.dispose();
            }
        };
        icon.setPreferredSize(new Dimension(20, 20));
        icon.setOpaque(false);
        return icon;
    }

    /**
    * Crée un champ de texte moderne avec effets avancés
    */
    private JTextField createModernTextField(int columns) {
        JTextField field = new JTextField(columns) {
            private boolean isHovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean hasFocus = this.isFocusOwner();
                
                // Fond avec effet de focus
                if (hasFocus) {
                    // Ombre de focus
                    g2d.setColor(new Color(41, 98, 255, 30));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    
                    // Fond de focus
                    GradientPaint focusGradient = new GradientPaint(
                        0, 0, new Color(248, 250, 255),
                        0, getHeight(), new Color(240, 245, 255)
                    );
                    g2d.setPaint(focusGradient);
                } else if (isHovered) {
                    // Fond au survol
                    g2d.setColor(new Color(250, 251, 252));
                } else {
                    // Fond normal
                    g2d.setColor(WHITE);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Bordure animée
                if (hasFocus) {
                    g2d.setColor(new Color(41, 98, 255));
                    g2d.setStroke(new BasicStroke(2.5f));
                } else if (isHovered) {
                    g2d.setColor(new Color(150, 150, 150));
                    g2d.setStroke(new BasicStroke(1.5f));
                } else {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                
                // Effet de brillance subtile
                if (hasFocus) {
                    GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 30),
                        0, getHeight() / 3, new Color(255, 255, 255, 0)
                    );
                    g2d.setPaint(shine);
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3 - 2, 6, 6);
                }
                
                g2d.dispose();
            }
        };
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(33, 37, 41)); // Texte noir foncé
        field.setCaretColor(new Color(41, 98, 255)); // Curseur bleu
        field.setBorder(new EmptyBorder(14, 18, 14, 18));
        field.setOpaque(false);
        field.setBackground(WHITE);
        
        // Effets de focus et hover
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                field.repaint();
            }
        });
        
        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                field.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                field.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                field.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                field.repaint();
            }
        });
        
        return field;
    }

    /**
    * Crée une zone de texte moderne avec effets avancés et visibilité garantie
    */
    private JTextArea createModernTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                boolean hasFocus = this.isFocusOwner();
                
                // Fond avec effet de focus
                if (hasFocus) {
                    // Ombre de focus
                    g2d.setColor(new Color(41, 98, 255, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    
                    // Fond de focus
                    GradientPaint focusGradient = new GradientPaint(
                        0, 0, new Color(248, 250, 255),
                        0, getHeight(), new Color(240, 245, 255)
                    );
                    g2d.setPaint(focusGradient);
                } else {
                    // Fond normal
                    g2d.setColor(WHITE);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Bordure animée
                if (hasFocus) {
                    g2d.setColor(new Color(41, 98, 255));
                    g2d.setStroke(new BasicStroke(2f));
                } else {
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                
                g2d.dispose();
            }
        };
        
        // Configuration avec visibilité garantie
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setForeground(new Color(33, 37, 41)); // Texte noir foncé
        textArea.setCaretColor(new Color(41, 98, 255)); // Curseur bleu
        textArea.setBorder(new EmptyBorder(14, 18, 14, 18));
        textArea.setOpaque(false);
        textArea.setBackground(WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        // Effets de focus
        textArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textArea.repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                textArea.repaint();
            }
        });
        
        return textArea;
    }

    /**
    * Crée un scroll pane moderne
    */
    private JScrollPane createModernScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBackground(WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getVerticalScrollBar().setBlockIncrement(64);
        return scrollPane;
    }

    /**
    * Crée le footer avec les actions globales
    */
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        footer.setBackground(BG_LIGHT);
        footer.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton btnReset = new ModernGradientButton("Réinitialiser MDP", WARNING_ORANGE, WARNING_ORANGE, true);
        btnReset.setBorder(new EmptyBorder(12, 24, 12, 24));
        btnReset.addActionListener(e -> {
            if (selectedEmail != null) {
                String newPwd = controller.resetPassword(selectedEmail);
                if (newPwd != null) {
                    showNotification("Mot de passe réinitialisé pour " + selectedEmail, WARNING_ORANGE);
                    renderHierarchy();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur.", 
                                            "Information", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        footer.add(btnReset);
        return footer;
    }

    /**
    * Crée un panneau pour afficher quand aucun résultat n'est trouvé
    */
    private JPanel createNoResultsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(60, 20, 60, 20)
        ));
        JLabel noResultsLabel = new JLabel("Aucun résultat trouvé");
        noResultsLabel.setFont(LABEL_FONT);
        noResultsLabel.setForeground(TEXT_SECONDARY);
        panel.add(noResultsLabel);
        return panel;
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
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel label = new JLabel(message);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.WHITE);
        
        panel.add(label, BorderLayout.CENTER);
        dialog.add(panel);
        
        // Auto-fermeture après 3 secondes
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dispose();
            }
        }, 3000);
        
        dialog.setVisible(true);
    }

    /**
    * Navigation vers nouveau cabinet
    */
    private void navigateToNewCabinet() {
        showCreateCabinetDialog();
    }

    /**
    * Navigation vers nouvel utilisateur
    */
    private void navigateToNewUser(String cabinetName, String roleType) {
        showCreateUserDialog(cabinetName, roleType);
    }

    /**
    * Affiche le dialogue de création d'un nouveau cabinet
    * Utilise les champs complets du CabinetFormView existant avec style moderne et animations
    */
    private void showCreateCabinetDialog() {
        // Créer un dialogue moderne avec style personnalisé et animation d'ouverture
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "🏢 Nouveau Cabinet Médical", true);
        dialog.setSize(950, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        
        // Rendre le dialog non-décoré pour permettre l'opacité
        dialog.setUndecorated(true);
        
        // Animation d'ouverture
        dialog.setOpacity(0f);
        
        // Panel principal avec style moderne
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec dégradé moderne
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(245, 248, 252),
                    0, getHeight(), new Color(235, 240, 245)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        mainPanel.setBackground(new Color(245, 248, 252));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header moderne
        JPanel headerPanel = createModernDialogHeader("🏢 Création d'un Nouveau Cabinet Médical", 
                                                   "Remplissez les informations pour créer un nouveau cabinet", dialog);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel de formulaire avec style amélioré
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec ombre moderne
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.dispose();
            }
        };
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new EmptyBorder(35, 45, 35, 45)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Champs du CabinetFormView ---
        JTextField txtNom = createModernTextField(30);
        JTextField txtEmail = createModernTextField(30);
        JTextField txtTel1 = createModernTextField(30);
        JTextField txtTel2 = createModernTextField(30);
        JTextField txtCin = createModernTextField(30);
        JTextField txtLogo = createModernTextField(30);
        JTextField txtNum = createModernTextField(20);
        JTextField txtRue = createModernTextField(30);
        JTextField txtVille = createModernTextField(30);
        JTextField txtCP = createModernTextField(20);
        JTextField txtPays = createModernTextField(30);
        txtPays.setText("Maroc");
        JTextField txtComp = createModernTextField(30);
        JTextField txtSite = createModernTextField(30);
        JTextField txtInsta = createModernTextField(30);
        JTextField txtFb = createModernTextField(30);
        JTextArea txtDesc = createModernTextArea(5, 30);
        
        int r = 0;
        
        // --- Section Informations Générales ---
        addFormSection(formPanel, gbc, r++, "📋 Informations Générales", 2);
        addModernFormRow(formPanel, gbc, r++, "Nom du Cabinet *:", txtNom, true);
        addModernFormRow(formPanel, gbc, r++, "Email Professionnel *:", txtEmail, true);
        addModernFormRow(formPanel, gbc, r++, "Téléphone 1 *:", txtTel1, true);
        addModernFormRow(formPanel, gbc, r++, "Téléphone 2:", txtTel2, false);
        addModernFormRow(formPanel, gbc, r++, "Identifiant (CIN/IF):", txtCin, false);
        addModernFormRow(formPanel, gbc, r++, "Logo (URL):", txtLogo, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Section Localisation & Adresse ---
        addFormSection(formPanel, gbc, r++, "📍 Localisation & Adresse", 2);
        addModernFormRow(formPanel, gbc, r++, "Numéro:", txtNum, false);
        addModernFormRow(formPanel, gbc, r++, "Rue *:", txtRue, true);
        addModernFormRow(formPanel, gbc, r++, "Ville *:", txtVille, true);
        addModernFormRow(formPanel, gbc, r++, "Code Postal *:", txtCP, true);
        addModernFormRow(formPanel, gbc, r++, "Pays *:", txtPays, true);
        addModernFormRow(formPanel, gbc, r++, "Complément:", txtComp, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Section Présence Digitale ---
        addFormSection(formPanel, gbc, r++, "🌐 Présence Digitale", 2);
        addModernFormRow(formPanel, gbc, r++, "Site Web:", txtSite, false);
        addModernFormRow(formPanel, gbc, r++, "Instagram:", txtInsta, false);
        addModernFormRow(formPanel, gbc, r++, "Facebook:", txtFb, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Description ---
        addFormSection(formPanel, gbc, r++, "📝 Description du Cabinet", 2);
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        formPanel.add(createModernScrollPane(txtDesc), gbc);
        r++;
        
        // Panel de scroll pour le formulaire
        JScrollPane scrollPane = createModernScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer avec boutons modernes
        JPanel footerPanel = createModernDialogFooter(
            () -> {
                if (txtNom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                    showNotification("Le nom du cabinet et l'email sont obligatoires.", DANGER_RED);
                    return;
                }
                
                try {
                    Map<String, String> data = new HashMap<>();
                    // --- Informations Générales ---
                    data.put("nom", txtNom.getText().trim());
                    data.put("email", txtEmail.getText().trim());
                    data.put("tel1", txtTel1.getText().trim());
                    data.put("tel2", txtTel2.getText().trim());
                    data.put("cin", txtCin.getText().trim());
                    data.put("logo", txtLogo.getText().trim());
                    
                    // --- Localisation & Adresse ---
                    data.put("numero", txtNum.getText().trim());
                    data.put("rue", txtRue.getText().trim());
                    data.put("ville", txtVille.getText().trim());
                    data.put("codePostal", txtCP.getText().trim());
                    data.put("pays", txtPays.getText().trim());
                    data.put("complement", txtComp.getText().trim());
                    
                    // --- Présence Digitale ---
                    data.put("siteWeb", txtSite.getText().trim());
                    data.put("instagram", txtInsta.getText().trim());
                    data.put("facebook", txtFb.getText().trim());
                    data.put("description", txtDesc.getText().trim());
                    
                    controller.addNewCabinet(data);
                    showNotification("Cabinet '" + txtNom.getText().trim() + "' créé avec succès", SUCCESS_GREEN);
                    renderHierarchy();
                    dialog.dispose();
                } catch (Exception ex) {
                    showNotification("Erreur : " + ex.getMessage(), DANGER_RED);
                }
            },
            () -> {
                // Animation de fermeture
                javax.swing.Timer fadeOutTimer = new javax.swing.Timer(50, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        float opacity = dialog.getOpacity();
                        if (opacity > 0.1f) {
                            dialog.setOpacity(Math.max(0f, opacity - 0.1f));
                        } else {
                            dialog.dispose();
                            ((javax.swing.Timer) evt.getSource()).stop();
                        }
                    }
                });
                fadeOutTimer.start();
            },
            "💾 Créer le Cabinet"
        );
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        
        // Animation d'ouverture
        javax.swing.Timer fadeInTimer = new javax.swing.Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                float opacity = dialog.getOpacity();
                if (opacity < 0.9f) {
                    dialog.setOpacity(Math.min(1f, opacity + 0.1f));
                } else {
                    dialog.setOpacity(1f);
                    ((javax.swing.Timer) evt.getSource()).stop();
                }
            }
        });
        fadeInTimer.start();
        
        dialog.setVisible(true);
    }

    /**
    * Affiche le dialogue de création d'un nouvel utilisateur
    * Version améliorée avec style moderne et tous les champs nécessaires
    */
    private void showCreateUserDialog(String cabinetName, String roleType) {
        // Créer un dialogue moderne avec style personnalisé
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                                   "👤 Création d'un Nouvel Utilisateur", true);
        dialog.setSize(850, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        
        // Rendre le dialog non-décoré pour permettre l'opacité
        dialog.setUndecorated(true);
        
        // Animation d'ouverture
        dialog.setOpacity(0f);
        
        // Panel principal avec style moderne
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec dégradé moderne
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(245, 248, 252),
                    0, getHeight(), new Color(235, 240, 245)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        mainPanel.setBackground(new Color(245, 248, 252));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header moderne
        JPanel headerPanel = createModernDialogHeader("👤 Création d'un Nouvel Utilisateur", 
                                                   "Complétez les informations pour créer un compte " + roleType, dialog);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel de formulaire avec style amélioré
        JPanel formPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond blanc avec ombre moderne
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.setColor(WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.dispose();
            }
        };
        formPanel.setBackground(WHITE);
        formPanel.setBorder(new CompoundBorder(
            new EmptyBorder(10, 10, 10, 10),
            new EmptyBorder(35, 45, 35, 45)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- Champs du formulaire utilisateur ---
        JTextField txtNom = createModernTextField(25);
        JTextField txtPrenom = createModernTextField(25);
        JTextField txtEmail = createModernTextField(25);
        JTextField txtCin = createModernTextField(25);
        JTextField txtTel = createModernTextField(25);
        JTextField txtPays = createModernTextField(25);
        txtPays.setText("Maroc");
        JTextField txtVille = createModernTextField(25);
        JTextField txtSpec = createModernTextField(25);
        
        // Champ de mot de passe généré
        JTextField txtPassword = createModernTextField(25);
        txtPassword.setEditable(false);
        txtPassword.setBackground(new Color(240, 255, 240));
        txtPassword.setForeground(new Color(0, 100, 0));
        
        // Génération du mot de passe
        String generatedPassword = generatePassword();
        txtPassword.setText(generatedPassword);
        
        // Champ de login auto-généré
        JTextField txtLogin = createModernTextField(25);
        txtLogin.setEditable(false);
        txtLogin.setBackground(new Color(240, 248, 255));
        
        // Synchronisation du login
        txtNom.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateLogin(); }
            @Override public void removeUpdate(DocumentEvent e) { updateLogin(); }
            @Override public void changedUpdate(DocumentEvent e) { updateLogin(); }
            
            private void updateLogin() {
                String nom = txtNom.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
                String prenom = txtPrenom.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
                if (!nom.isEmpty() && !prenom.isEmpty()) {
                    txtLogin.setText(prenom + "." + nom);
                } else if (!nom.isEmpty()) {
                    txtLogin.setText(nom);
                } else {
                    txtLogin.setText("");
                }
            }
        });
        
        txtPrenom.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateLogin(); }
            @Override public void removeUpdate(DocumentEvent e) { updateLogin(); }
            @Override public void changedUpdate(DocumentEvent e) { updateLogin(); }
            
            private void updateLogin() {
                String nom = txtNom.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
                String prenom = txtPrenom.getText().trim().toLowerCase().replaceAll("[^a-z]", "");
                if (!nom.isEmpty() && !prenom.isEmpty()) {
                    txtLogin.setText(prenom + "." + nom);
                } else if (!nom.isEmpty()) {
                    txtLogin.setText(nom);
                } else {
                    txtLogin.setText("");
                }
            }
        });
        
        // ComboBox pour le sexe
        JComboBox<String> comboSexe = new JComboBox<>(new String[]{"Homme", "Femme"});
        comboSexe.setFont(INPUT_FONT);
        comboSexe.setBackground(WHITE);
        comboSexe.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_LIGHT, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // DatePicker pour la date de naissance
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Aujourd'hui");
        p.put("text.month", "Mois");
        p.put("text.year", "Année");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        
        int r = 0;
        
        // --- Section Informations Personnelles ---
        addFormSection(formPanel, gbc, r++, "👤 Informations Personnelles", 2);
        addModernFormRow(formPanel, gbc, r++, "Nom *:", txtNom, true);
        addModernFormRow(formPanel, gbc, r++, "Prénom *:", txtPrenom, true);
        addModernFormRow(formPanel, gbc, r++, "Email Professionnel *:", txtEmail, true);
        addModernFormRow(formPanel, gbc, r++, "CIN:", txtCin, false);
        addModernFormRow(formPanel, gbc, r++, "Téléphone:", txtTel, false);
        addModernFormRow(formPanel, gbc, r++, "Sexe:", comboSexe, false);
        addModernFormRow(formPanel, gbc, r++, "Date de Naissance:", datePicker, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Section Compte ---
        addFormSection(formPanel, gbc, r++, "🔐 Informations du Compte", 2);
        addModernFormRow(formPanel, gbc, r++, "Login (auto-généré):", txtLogin, false);
        addModernFormRow(formPanel, gbc, r++, "Mot de Passe (généré):", txtPassword, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Section Localisation ---
        addFormSection(formPanel, gbc, r++, "📍 Localisation", 2);
        addModernFormRow(formPanel, gbc, r++, "Pays:", txtPays, false);
        addModernFormRow(formPanel, gbc, r++, "Ville:", txtVille, false);
        
        gbc.gridx = 0; gbc.gridy = r; gbc.gridwidth = 2;
        formPanel.add(Box.createVerticalStrut(25), gbc);
        r++;
        
        // --- Section Professionnelle ---
        addFormSection(formPanel, gbc, r++, "💼 Informations Professionnelles", 2);
        String specLabel = roleType.contains("MÉD") ? "Spécialité:" : "Numéro CNSS:";
        addModernFormRow(formPanel, gbc, r++, specLabel, txtSpec, false);
        
        // Panel de scroll pour le formulaire
        JScrollPane scrollPane = createModernScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Footer avec boutons modernes
        JPanel footerPanel = createModernDialogFooter(
            () -> {
                if (txtNom.getText().trim().isEmpty() || txtPrenom.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()) {
                    showNotification("Nom, Prénom et Email sont obligatoires.", DANGER_RED);
                    return;
                }
                
                try {
                    Map<String, String> data = new HashMap<>();
                    // --- Informations Personnelles ---
                    data.put("nom", txtNom.getText().trim());
                    data.put("prenom", txtPrenom.getText().trim());
                    data.put("email", txtEmail.getText().trim());
                    data.put("cin", txtCin.getText().trim());
                    data.put("tel", txtTel.getText().trim());
                    data.put("sexe", (String) comboSexe.getSelectedItem());
                    data.put("password", generatedPassword);
                    
                    // --- Localisation ---
                    data.put("pays", txtPays.getText().trim());
                    data.put("ville", txtVille.getText().trim());
                    
                    // --- Professionnel ---
                    data.put(roleType.contains("MÉD") ? "specialite" : "numCnss", txtSpec.getText().trim());

                    // --- Date de naissance ---
                    if (model.getValue() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        data.put("dateNaissance", sdf.format(model.getValue()));
                    }

                    controller.addNewUser(cabinetName, roleType, data);
                    showNotification("Compte créé avec succès !", SUCCESS_GREEN);
                    renderHierarchy();
                    dialog.dispose();
                } catch (Exception ex) {
                    showNotification("Erreur : " + ex.getMessage(), DANGER_RED);
                }
            },
            () -> {
                // Animation de fermeture
                javax.swing.Timer fadeOutTimer = new javax.swing.Timer(50, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        float opacity = dialog.getOpacity();
                        if (opacity > 0.1f) {
                            dialog.setOpacity(Math.max(0f, opacity - 0.1f));
                        } else {
                            dialog.dispose();
                            ((javax.swing.Timer) evt.getSource()).stop();
                        }
                    }
                });
                fadeOutTimer.start();
            },
            "💾 Créer l'Utilisateur"
        );
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        dialog.add(mainPanel);
        
        // Animation d'ouverture
        javax.swing.Timer fadeInTimer = new javax.swing.Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                float opacity = dialog.getOpacity();
                if (opacity < 0.9f) {
                    dialog.setOpacity(Math.min(1f, opacity + 0.1f));
                } else {
                    dialog.setOpacity(1f);
                    ((javax.swing.Timer) evt.getSource()).stop();
                }
            }
        });
        fadeInTimer.start();
        
        dialog.setVisible(true);
    }
    
    /**
    * Génère un mot de passe aléatoire sécurisé
    */
    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        
        // Générer un mot de passe de 12 caractères
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
    * Crée un header moderne pour les dialogs avec effets avancés
    */
    private JPanel createModernDialogHeader(String title, String subtitle, JDialog dialog) {
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé moderne pour le header avec plus de profondeur
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 98, 255),
                    getWidth() * 0.7f, getHeight() * 0.3f, new Color(0, 72, 186)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(0, 3, getWidth(), getHeight() - 3, 20, 20);
                
                // Ligne de séparation subtile
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.drawLine(20, getHeight() - 1, getWidth() - 20, getHeight() - 1);
                
                g2d.dispose();
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        
        // Panel gauche avec icône et texte
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        leftPanel.setOpaque(false);
        
        // Icône moderne avec animation
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Cercle moderne avec dégradé
                GradientPaint iconGradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    getWidth(), getHeight(), new Color(255, 255, 255, 100)
                );
                g2d.setPaint(iconGradient);
                g2d.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
                
                // Bordure de l'icône
                g2d.setColor(new Color(255, 255, 255, 180));
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
                
                g2d.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(WHITE);
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        leftPanel.add(iconLabel);
        leftPanel.add(textPanel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        // Bouton de fermeture moderne
        JButton closeButton = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    // Fond rouge avec dégradé au hover
                    GradientPaint hoverGradient = new GradientPaint(
                        0, 0, new Color(255, 100, 100, 200),
                        getWidth(), getHeight(), new Color(255, 50, 50, 200)
                    );
                    g2d.setPaint(hoverGradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                
                g2d.setColor(WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("✕")) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString("✕", x, y);
                
                g2d.dispose();
            }
        };
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(false);
        closeButton.setForeground(WHITE);
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            // Animation de fermeture
            javax.swing.Timer fadeOutTimer = new javax.swing.Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    float opacity = dialog.getOpacity();
                    if (opacity > 0.1f) {
                        dialog.setOpacity(Math.max(0f, opacity - 0.05f));
                    } else {
                        dialog.dispose();
                        ((javax.swing.Timer) evt.getSource()).stop();
                    }
                }
            });
            fadeOutTimer.start();
        });
        
        headerPanel.add(closeButton, BorderLayout.EAST);
        return headerPanel;
    }
    
    /**
    * Crée un footer moderne pour les dialogs avec effets avancés
    */
    private JPanel createModernDialogFooter(Runnable onSave, Runnable onCancel, String saveButtonText) {
        JPanel footerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec dégradé subtil
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(248, 250, 252),
                    0, getHeight(), new Color(240, 244, 248)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Ombre en haut du footer
                GradientPaint shadowGradient = new GradientPaint(
                    0, 0, new Color(0, 0, 0, 25),
                    0, 15, new Color(0, 0, 0, 0)
                );
                g2d.setPaint(shadowGradient);
                g2d.fillRect(0, 0, getWidth(), 15);
                
                // Ligne de séparation
                g2d.setColor(new Color(200, 200, 200, 100));
                g2d.drawLine(20, 1, getWidth() - 20, 1);
                
                g2d.dispose();
            }
        };
        footerPanel.setBackground(new Color(248, 250, 252));
        footerPanel.setBorder(new EmptyBorder(30, 50, 35, 50));
        
        // Panel pour les boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = createEnhancedButton("Annuler", new Color(220, 53, 69), false);
        cancelButton.addActionListener(e -> {
            // Animation de fermeture
            JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(cancelButton);
            if (dialog != null) {
                javax.swing.Timer fadeOutTimer = new javax.swing.Timer(20, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        float opacity = dialog.getOpacity();
                        if (opacity > 0.1f) {
                            dialog.setOpacity(Math.max(0f, opacity - 0.05f));
                        } else {
                            dialog.dispose();
                            ((javax.swing.Timer) evt.getSource()).stop();
                        }
                    }
                });
                fadeOutTimer.start();
            }
            onCancel.run();
        });
        
        JButton saveButton = createEnhancedButton(saveButtonText, new Color(40, 167, 69), true);
        saveButton.addActionListener(e -> {
            // Animation de sauvegarde
            saveButton.setText("⏳ Enregistrement...");
            saveButton.setEnabled(false);
            
            javax.swing.Timer timer = new javax.swing.Timer(300, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    onSave.run();
                    ((javax.swing.Timer) evt.getSource()).stop();
                }
            });
            timer.start();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    /**
    * Crée un bouton amélioré avec effets hover et animations modernes
    */
    private JButton createEnhancedButton(String text, Color color, boolean isPrimary) {
        JButton button = new JButton(text) {
            private float animationProgress = 0f;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean isHovered = this.getModel().isRollover();
                boolean isPressed = this.getModel().isPressed();
                
                // Fond avec animation
                if (isPrimary) {
                    // Ombre profonde
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                    g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 14, 14);
                    
                    // Dégradé animé
                    GradientPaint gradient;
                    if (isPressed) {
                        gradient = new GradientPaint(
                            0, 0, adjustColor(color, -40),
                            getWidth(), getHeight(), adjustColor(color, -60)
                        );
                    } else if (isHovered) {
                        gradient = new GradientPaint(
                            0, 0, adjustColor(color, 20),
                            getWidth(), getHeight(), adjustColor(color, 0)
                        );
                    } else {
                        gradient = new GradientPaint(
                            0, 0, color,
                            getWidth(), getHeight(), adjustColor(color, -20)
                        );
                    }
                    g2d.setPaint(gradient);
                } else {
                    // Fond blanc pour bouton secondaire
                    if (isPressed) {
                        g2d.setColor(new Color(240, 240, 240));
                    } else if (isHovered) {
                        g2d.setColor(WHITE);
                    } else {
                        g2d.setColor(new Color(248, 250, 252));
                    }
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                
                // Bordure animée
                if (!isPrimary) {
                    if (isHovered) {
                        g2d.setColor(color);
                        g2d.setStroke(new BasicStroke(2.5f));
                    } else {
                        g2d.setColor(adjustColor(color, -50));
                        g2d.setStroke(new BasicStroke(1.5f));
                    }
                } else {
                    g2d.setColor(adjustColor(color, -60));
                    g2d.setStroke(new BasicStroke(1.5f));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 14, 14);
                
                // Effet de brillance
                if (isHovered && isPrimary) {
                    GradientPaint shine = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 60),
                        0, getHeight() / 3, new Color(255, 255, 255, 0)
                    );
                    g2d.setPaint(shine);
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() / 3 - 2, 12, 12);
                }
                
                g2d.dispose();
            }
            
            private Color adjustColor(Color color, int amount) {
                int r = Math.max(0, Math.min(255, color.getRed() + amount));
                int g = Math.max(0, Math.min(255, color.getGreen() + amount));
                int b = Math.max(0, Math.min(255, color.getBlue() + amount));
                return new Color(r, g, b);
            }
            
            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                if (!enabled) {
                    setForeground(Color.GRAY);
                } else {
                    setForeground(isPrimary ? Color.WHITE : color);
                }
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(isPrimary ? Color.WHITE : color);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(16, 36, 16, 36));
        
        return button;
    }
    
    /**
    * Ajoute une section de formulaire moderne avec effets avancés et visibilité garantie
    */
    private void addFormSection(JPanel panel, GridBagConstraints gbc, int row, String title, int colspan) {
        JPanel sectionPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond subtile de section
                GradientPaint bgGradient = new GradientPaint(
                    0, 0, new Color(248, 250, 252),
                    0, getHeight(), new Color(240, 244, 248)
                );
                g2d.setPaint(bgGradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(0, 2, getWidth(), getHeight() - 2, 12, 12);
                
                g2d.dispose();
            }
        };
        sectionPanel.setOpaque(false);
        sectionPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        sectionLabel.setForeground(new Color(41, 98, 255));
        sectionLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        
        // Icône décorative
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(41, 98, 255));
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int size = Math.min(getWidth(), getHeight()) - 6;
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Dessiner une petite icône décorative
                g2d.drawOval(centerX - size/4, centerY - size/4, size/2, size/2);
                g2d.drawLine(centerX + size/8, centerY - size/4, centerX + size/8, centerY + size/4);
                
                g2d.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(28, 28));
        iconLabel.setOpaque(false);
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(sectionLabel);
        
        sectionPanel.add(leftPanel, BorderLayout.WEST);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = colspan;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 0, 10, 0);
        panel.add(sectionPanel, gbc);
        
        // Ligne de séparation améliorée
        JSeparator separator = new JSeparator() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dégradé pour la ligne
                GradientPaint lineGradient = new GradientPaint(
                    0, 0, new Color(200, 200, 200, 100),
                    getWidth(), 0, new Color(200, 200, 200, 0)
                );
                g2d.setPaint(lineGradient);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(10, getHeight() / 2, getWidth() - 10, getHeight() / 2);
                
                g2d.dispose();
            }
        };
        
        gbc.gridy = row + 1;
        gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(separator, gbc);
    }
    
    /**
    * Ajoute une ligne de formulaire moderne avec effets avancés et visibilité garantie
    */
    private void addModernFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field, boolean required) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        gbc.insets = new Insets(10, 0, 10, 20);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        
        JLabel labelComponent = new JLabel(label) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond subtile pour le label
                if (required) {
                    GradientPaint bgGradient = new GradientPaint(
                        0, 0, new Color(255, 240, 240),
                        0, getHeight(), new Color(255, 250, 250)
                    );
                    g2d.setPaint(bgGradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    
                    // Bordure pour champ obligatoire
                    g2d.setColor(new Color(220, 50, 50, 100));
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                }
                
                g2d.dispose();
            }
        };
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(required ? new Color(220, 50, 50) : new Color(33, 37, 41));
        labelComponent.setBorder(new EmptyBorder(8, 8, 8, 8));
        
        // Icône pour champ obligatoire
        if (required) {
            JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            labelPanel.setOpaque(false);
            
            JLabel asterisk = new JLabel("*") {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(new Color(220, 50, 50));
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    g2d.drawString("*", 0, getHeight() - 2);
                    
                    g2d.dispose();
                }
            };
            asterisk.setPreferredSize(new Dimension(12, 16));
            asterisk.setOpaque(false);
            
            labelPanel.add(labelComponent);
            labelPanel.add(asterisk);
            panel.add(labelPanel, gbc);
        } else {
            panel.add(labelComponent, gbc);
        }
        
        gbc.gridx = 1; gbc.weightx = 0.7;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        // Wrapper pour le champ avec effet de conteneur
        JPanel fieldWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile du conteneur
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                g2d.dispose();
            }
        };
        fieldWrapper.setOpaque(false);
        fieldWrapper.setBorder(new EmptyBorder(2, 2, 2, 2));
        fieldWrapper.add(field, BorderLayout.CENTER);
        
        panel.add(fieldWrapper, gbc);
    }

    /**
    * Formatter pour le DatePicker
    */
    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private String datePattern = "dd-MM-yyyy";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws java.text.ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                java.util.Calendar cal = (java.util.Calendar) value;
                return dateFormatter.format(cal.getTime());
            }
            return "";
        }
    }

    /**
    * Crée le panneau d'édition inline avec tous les champs de l'utilisateur
    */
    private JPanel createInlineEditPanel(UserStaffDTO user) {
        // Récupérer les détails complets de l'utilisateur
        Utilisateur u = controller.getUserDetails(user.getEmail());
        String password = controller.getUserPassword(user.getEmail());
        
        JPanel editPanel = new JPanel(new BorderLayout());
        editPanel.setBackground(new Color(248, 249, 250));
        editPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));
        
        // Formulaire d'édition
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 249, 250));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Ajouter tous les champs de l'utilisateur
        int row = 0;
        
        // Login
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("🔑 Login:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField loginField = new JTextField(u.getLogin(), 20);
        loginField.setEditable(false);
        loginField.setBackground(Color.WHITE);
        formPanel.add(loginField, gbc);
        row++;
        
        // Mot de passe
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("🔐 Mot de passe:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField passwordField = new JTextField(password, 20);
        passwordField.setEditable(false);
        passwordField.setBackground(new Color(255, 255, 200));
        if (password.startsWith("[HASHÉ")) {
            passwordField.setForeground(Color.RED);
        } else {
            passwordField.setForeground(new Color(0, 128, 0));
        }
        formPanel.add(passwordField, gbc);
        row++;
        
        // Nom
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("👤 Nom:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField nomField = new JTextField(u.getNom(), 20);
        nomField.setBackground(Color.WHITE);
        formPanel.add(nomField, gbc);
        row++;
        
        // Prénom
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("👤 Prénom:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField prenomField = new JTextField(u.getPrenom(), 20);
        prenomField.setBackground(Color.WHITE);
        formPanel.add(prenomField, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("📧 Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField emailField = new JTextField(u.getEmail(), 20);
        emailField.setEditable(false);
        emailField.setBackground(Color.WHITE);
        formPanel.add(emailField, gbc);
        row++;
        
        // Téléphone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("📱 Téléphone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField telField = new JTextField(u.getTel(), 20);
        telField.setBackground(Color.WHITE);
        formPanel.add(telField, gbc);
        row++;
        
        // CIN
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("🆔 CIN:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField cinField = new JTextField(u.getCin(), 20);
        cinField.setBackground(Color.WHITE);
        formPanel.add(cinField, gbc);
        row++;
        
        // Date de naissance
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("🎂 Date naissance:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField dateNaissanceField = new JTextField(
            u.getDateNaissance() != null ? u.getDateNaissance().toString() : "N/A", 20);
        dateNaissanceField.setEditable(false);
        dateNaissanceField.setBackground(Color.WHITE);
        formPanel.add(dateNaissanceField, gbc);
        row++;
        
        // Sexe
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("⚧ Sexe:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField sexeField = new JTextField(u.getSexe() != null ? u.getSexe().getLibelle() : "N/A", 20);
        sexeField.setBackground(Color.WHITE);
        formPanel.add(sexeField, gbc);
        row++;
        
        // Adresse
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("🏠 Adresse:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        String adresse = u.getAdresse() != null ? u.getAdresse().getAdresseComplete() : "N/A";
        JTextField adresseField = new JTextField(adresse, 20);
        adresseField.setEditable(false);
        adresseField.setBackground(Color.WHITE);
        formPanel.add(adresseField, gbc);
        row++;
        
        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton saveButton = new ModernGradientButton("💾 Sauver", SUCCESS_GREEN, SUCCESS_GREEN, true);
        saveButton.addActionListener(e -> {
            String ville = u.getAdresse() != null ? u.getAdresse().getVille() : "N/A";
            controller.updateUser(user.getEmail(), nomField.getText(), prenomField.getText(), 
                                 telField.getText(), cinField.getText(), ville);
            editingEmail = null;
            renderHierarchy();
            showNotification("Utilisateur mis à jour avec succès", SUCCESS_GREEN);
        });
        
        JButton cancelButton = new ModernGradientButton("❌ Fermer", DANGER_RED, DANGER_RED, true);
        cancelButton.addActionListener(e -> {
            editingEmail = null;
            renderHierarchy();
        });
        
        JButton resetPasswordButton = new ModernGradientButton("🔄 Réinitialiser MDP", WARNING_ORANGE, WARNING_ORANGE, true);
        resetPasswordButton.addActionListener(e -> {
            String newPwd = controller.resetPassword(user.getEmail());
            if (newPwd != null) {
                passwordField.setText(newPwd);
                passwordField.setForeground(new Color(0, 128, 0));
                showNotification("Nouveau mot de passe : " + newPwd, WARNING_ORANGE);
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(resetPasswordButton);
        
        editPanel.add(formPanel, BorderLayout.CENTER);
        editPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return editPanel;
    }

    /**
    * Planifie le rendu de la hiérarchie après un délai (debounce)
    */
    private void scheduleSearch() {
        if (searchTimer != null) {
            searchTimer.cancel();
        }
        
        searchTimer = new java.util.Timer();
        searchTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    renderHierarchy();
                });
            }
        }, SEARCH_DEBOUNCE_MS);
    }

    // --- CLASSES INTERNES ---

    /**
    * Panel avec ombre douce et bordures arrondies
    */
    private class RoundedShadowPanel extends JPanel {
        private int radius = 16;
        
        @Override
        protected void paintComponent(Graphics g){
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ombre douce
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            
            // Fond blanc
            g2d.setColor(Color.WHITE);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Bordure subtile
            g2d.setColor(new Color(0, 0, 0, 15));
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            
            g2d.dispose();
        }
    }

    /**
    * Bouton moderne avec dégradé
    */
    private class ModernGradientButton extends JButton {
        private Color color1;
        private Color color2;
        private boolean filled;
        
        public ModernGradientButton(String text, Color color1, Color color2, boolean filled) {
            super(text);
            this.color1 = color1;
            this.color2 = color2;
            this.filled = filled;
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(filled ? Color.WHITE : color1);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(12, 24, 12, 24));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (filled) {
                // Ombre
                g2d.setColor(new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), 100));
                g2d.fillRoundRect(2, 6, getWidth() - 4, getHeight() - 8, 12, 12);
                
                // Dégradé
                GradientPaint gradient = new GradientPaint(
                    0, 0, color1,
                    getWidth(), getHeight(), color2
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            } else {
                // Fond blanc
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Bordure colorée
                g2d.setColor(color1);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            }
            
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    /**
    * Carte de cabinet avec ombre et bordure arrondie
    */
    class CabinetCardPanel extends JPanel {
        public CabinetCardPanel() {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 16, 0));
        }
    }

    /**
    * Header du cabinet avec nom et nombre de membres
    */
    class CabinetHeaderPanel extends JPanel {
        public CabinetHeaderPanel(String cabinetName, int memberCount) {
            setLayout(new BorderLayout());
            setBackground(new Color(248, 250, 252));
            setBorder(new EmptyBorder(20, 32, 20, 32));
            
            // Panel gauche avec icône et infos
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
            leftPanel.setOpaque(false);
            
            // Icône de bâtiment
            JLabel buildingIcon = new JLabel("🏢");
            buildingIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            leftPanel.add(buildingIcon);
            
            // Panel texte
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(cabinetName);
            nameLabel.setFont(TITLE_FONT);
            nameLabel.setForeground(TEXT_PRIMARY);
            
            JLabel countLabel = new JLabel(memberCount + " membre" + (memberCount > 1 ? "s" : ""));
            countLabel.setFont(SUBTITLE_FONT);
            countLabel.setForeground(TEXT_SECONDARY);
            
            textPanel.add(nameLabel);
            textPanel.add(countLabel);
            leftPanel.add(textPanel);
            
            // Icône chevron
            JLabel chevronIcon = new JLabel("▼");
            chevronIcon.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            chevronIcon.setForeground(TEXT_SECONDARY);
            
            add(leftPanel, BorderLayout.CENTER);
            add(chevronIcon, BorderLayout.EAST);
            
            // Gestion du clic pour expand/collapse
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Logique d'expansion/collapse
                    JPanel parent = (JPanel) getParent();
                    Component content = parent.getComponent(1);
                    content.setVisible(!content.isVisible());
                }
            });
        }
    }

    /**
    * Section de rôle (Médecins/Secrétaires)
    */
    class RoleSectionPanel extends JPanel {
        public RoleSectionPanel(String roleName, String cabinetName) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(16, 0, 16, 0));
            
            // Header de section
            JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            headerPanel.setOpaque(false);
            
            JLabel titleLabel = new JLabel(roleName);
            titleLabel.setFont(SECTION_FONT);
            titleLabel.setForeground(TEXT_SECONDARY);
            titleLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
            
            headerPanel.add(titleLabel);
            
            // Bouton d'ajout
            JButton addButton = new ModernGradientButton("+ Ajouter", PRIMARY_BLUE, PRIMARY_BLUE, false);
            addButton.setBorder(new EmptyBorder(6, 16, 6, 16));
            addButton.addActionListener(e -> {
                // Logique pour ajouter un utilisateur
                navigateToNewUser(cabinetName, roleName);
            });
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.add(addButton);
            
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            topPanel.add(headerPanel, BorderLayout.WEST);
            topPanel.add(buttonPanel, BorderLayout.EAST);
            
            add(topPanel);
        }
    }

    /**
    * Ligne d'utilisateur avec avatar et actions
    */
    class UserRowPanel extends JPanel {
        private final UserStaffDTO user;
        private boolean isSelected = false;
        private boolean isHovered = false;
        
        public UserRowPanel(UserStaffDTO user) {
            this.user = user;
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(12, 16, 12, 16));
            
            // Panel gauche avec avatar et infos
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
            leftPanel.setOpaque(false);
            
            // Avatar
            JLabel avatarLabel = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Fond dégradé
                    GradientPaint gradient = new GradientPaint(
                        0, 0, PRIMARY_BLUE,
                        getWidth(), getHeight(), PRIMARY_PURPLE
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
                    
                    // Initiales
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    String initials = getInitials(user.getNomComplet());
                    FontMetrics fm = g2d.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(initials)) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2;
                    g2d.drawString(initials, x, y);
                    
                    g2d.dispose();
                }
            };
            avatarLabel.setPreferredSize(new Dimension(48, 48));
            leftPanel.add(avatarLabel);
            
            // Infos utilisateur
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setOpaque(false);
            
            JLabel nameLabel = new JLabel(user.getNomComplet());
            nameLabel.setFont(USER_NAME_FONT);
            nameLabel.setForeground(TEXT_PRIMARY);
            
            JLabel emailLabel = new JLabel(user.getEmail());
            emailLabel.setFont(EMAIL_FONT);
            emailLabel.setForeground(TEXT_SECONDARY);
            
            infoPanel.add(nameLabel);
            infoPanel.add(emailLabel);
            leftPanel.add(infoPanel);
            
            // Panel d'actions
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            actionPanel.setOpaque(false);
            
            JButton editButton = new JButton("✏️");
            editButton.setPreferredSize(new Dimension(32, 32));
            editButton.setFocusPainted(false);
            editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            editButton.setToolTipText("Modifier");
            editButton.addActionListener(e -> handleEdit());
            
            JButton deleteButton = new JButton("🗑️");
            deleteButton.setPreferredSize(new Dimension(32, 32));
            deleteButton.setFocusPainted(false);
            deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            deleteButton.setToolTipText("Supprimer");
            deleteButton.addActionListener(e -> handleDelete());
            
            actionPanel.add(editButton);
            actionPanel.add(deleteButton);
            
            add(leftPanel, BorderLayout.WEST);
            add(actionPanel, BorderLayout.EAST);
            
            // Gestion des clics
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) {
                        setSelected(true);
                        selectedEmail = user.getEmail();
                        resetRowsBackground();
                        setBackground(new Color(232, 244, 253));
                    } else if (e.getClickCount() == 2) {
                        handleEdit();
                    }
                }
                
                @Override
                public void mouseEntered(MouseEvent e) {
                    setHovered(true);
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    setHovered(false);
                }
            });
        }
        
        private void handleEdit() {
            editingEmail = user.getEmail();
            renderHierarchy();
        }
        
        private void handleDelete() {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer cet utilisateur ?\n" + user.getNomComplet(),
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                controller.deleteUser(user.getEmail());
                showNotification("Utilisateur supprimé avec succès", DANGER_RED);
                renderHierarchy();
            }
        }
        
        private String getInitials(String fullName) {
            String[] parts = fullName.split("\\s+");
            if (parts.length >= 2) {
                return (parts[0].charAt(0) + "" + parts[1].charAt(0)).toUpperCase();
            }
            return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
        }
        
        public void setSelected(boolean selected) {
            this.isSelected = selected;
            repaint();
        }
        
        public void setHovered(boolean hovered) {
            this.isHovered = hovered;
            repaint();
        }
        
        private void resetRowsBackground() {
            // Réinitialiser les fonds de toutes les lignes
            Container parent = getParent();
            if (parent != null) {
                for (Component comp : parent.getComponents()) {
                    if (comp instanceof UserRowPanel) {
                        comp.setBackground(Color.WHITE);
                    }
                }
            }
        }
    }
}
