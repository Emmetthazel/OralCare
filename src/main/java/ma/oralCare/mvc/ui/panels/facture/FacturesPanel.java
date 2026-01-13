package ma.oralCare.mvc.ui.panels.facture;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.mvc.ui.components.JDatePicker;
import ma.oralCare.mvc.ui.dialogs.FactureDialog;
import ma.oralCare.mvc.ui.dialogs.FactureDetailsDialog;
import ma.oralCare.mvc.ui.dialogs.PaiementDialog;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Panel complet pour la gestion des factures par les secrétaires
 * Fonctionnalités: CRUD complet, paiements, export, temps réel
 */
public class FacturesPanel extends JPanel {

    private final SecretaireDashboard dashboard;
    
    // Composants UI
    private JTable factureTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton paiementButton;
    private JButton pdfButton;
    private JButton exportButton;
    private JTextField searchField;
    private JComboBox<String> statutFilter;
    private ma.oralCare.mvc.ui.components.JDatePicker debutDatePicker;
    private ma.oralCare.mvc.ui.components.JDatePicker finDatePicker;
    
    // Services et contrôleurs
    private ma.oralCare.mvc.controllers.facture.api.FactureController factureController;
    
    // Données
    private List<Facture> currentFactures;
    private Facture selectedFacture;
    
    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public FacturesPanel(SecretaireDashboard dashboard) {
        this.dashboard = dashboard;
        this.factureController = dashboard.getFactureController();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }

    /**
     * Initialise tous les composants UI
     */
    private void initializeComponents() {
        // Table des factures
        String[] columns = {"Numéro", "Patient", "Date", "Total", "Payé", "Reste", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-éditable directement
            }
        };
        factureTable = new JTable(tableModel);
        factureTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        factureTable.getTableHeader().setReorderingAllowed(false);
        
        // Boutons d'action
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        viewButton = new JButton("Détails");
        paiementButton = new JButton("Paiement");
        pdfButton = new JButton("PDF");
        exportButton = new JButton("Exporter");
        
        // Configuration des boutons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewButton.setEnabled(false);
        paiementButton.setEnabled(false);
        pdfButton.setEnabled(false);
        
        // Champs de recherche et filtres
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par numéro ou patient");
        
        statutFilter = new JComboBox<>(new String[]{"Tous", "EN_ATTENTE", "VALIDEE", "PAYEE", "PARTIELLEMENT_PAYEE", "ANNULEE"});
        
        // Date pickers
        debutDatePicker = new ma.oralCare.mvc.ui.components.JDatePicker(LocalDate.now().minusDays(30));
        finDatePicker = new ma.oralCare.mvc.ui.components.JDatePicker(LocalDate.now());
    }

    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel supérieur - Barre d'outils
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Panel central - Tableau
        JScrollPane scrollPane = new JScrollPane(factureTable);
        scrollPane.setPreferredSize(new Dimension(1000, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Panel inférieur - Boutons d'action
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Crée le panel supérieur avec les contrôles
     */
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de recherche et filtres
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Recherche:"));
        filterPanel.add(searchField);
        
        filterPanel.add(new JLabel("Statut:"));
        filterPanel.add(statutFilter);
        
        filterPanel.add(new JLabel("Période:"));
        filterPanel.add(debutDatePicker);
        filterPanel.add(new JLabel("au"));
        filterPanel.add(finDatePicker);
        
        filterPanel.add(new JButton("Rafraîchir") {{ addActionListener(e -> refreshData()); }});
        
        panel.add(filterPanel, BorderLayout.CENTER);
        
        // Panel de statistiques
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.EAST);
        
        return panel;
    }

    /**
     * Crée le panel de statistiques
     */
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Statistiques de la période"));
        
        JLabel totalLabel = new JLabel("Total: 0");
        JLabel payeesLabel = new JLabel("Payées: 0");
        JLabel creancesLabel = new JLabel("Créances: 0 DH");
        JLabel montantLabel = new JLabel("Montant: 0 DH");
        
        panel.add(totalLabel);
        panel.add(payeesLabel);
        panel.add(creancesLabel);
        panel.add(montantLabel);
        
        return panel;
    }

    /**
     * Crée le panel inférieur avec les boutons d'action
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(viewButton);
        panel.add(paiementButton);
        panel.add(pdfButton);
        panel.add(exportButton);
        
        return panel;
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Sélection dans le tableau
        factureTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Boutons d'action
        addButton.addActionListener(this::handleAddFacture);
        editButton.addActionListener(this::handleEditFacture);
        deleteButton.addActionListener(this::handleDeleteFacture);
        viewButton.addActionListener(this::handleViewDetails);
        paiementButton.addActionListener(this::handlePaiement);
        pdfButton.addActionListener(this::handleExportPDF);
        exportButton.addActionListener(this::handleExportCSV);

        // Recherche et filtres
        searchField.addActionListener(this::handleSearch);
        statutFilter.addActionListener(e -> handleFilter());
        debutDatePicker.addChangeListener(e -> handleFilter());
        finDatePicker.addChangeListener(e -> handleFilter());
    }

    /**
     * Charge les données initiales
     */
    private void loadInitialData() {
        refreshData();
    }

    /**
     * Rafraîchit les données du tableau
     */
    public void refreshData() {
        try {
            LocalDate debut = debutDatePicker.getDate();
            LocalDate fin = finDatePicker.getDate();
            currentFactures = factureController.getFacturesByPeriode(debut, fin);
            
            // Appliquer les filtres
            applyFilters();
            
            // Mettre à jour le tableau
            updateTable();
            
            // Mettre à jour les statistiques
            updateStatistics();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors du chargement des données: " + e.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Applique les filtres de recherche et statut
     */
    private void applyFilters() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedStatut = (String) statutFilter.getSelectedItem();
        
        if (searchText.isEmpty() && "Tous".equals(selectedStatut)) {
            return; // Pas de filtre
        }
        
        currentFactures = currentFactures.stream()
            .filter(facture -> {
                // Filtre de recherche
                boolean matchesSearch = searchText.isEmpty() || 
                    (facture.getNumero() != null && facture.getNumero().toLowerCase().contains(searchText)) ||
                    (facture.getPatientNom() != null && facture.getPatientNom().toLowerCase().contains(searchText));
                
                // Filtre de statut
                boolean matchesStatut = "Tous".equals(selectedStatut) || 
                    selectedStatut.equals(facture.getStatut());
                
                return matchesSearch && matchesStatut;
            })
            .toList();
    }

    /**
     * Met à jour le tableau avec les données actuelles
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Facture facture : currentFactures) {
            BigDecimal reste = facture.getTotaleFacture().subtract(facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO);
            
            Object[] row = {
                facture.getNumero(),
                facture.getPatientNom(),
                facture.getDateFacture().format(DATE_FORMATTER),
                facture.getTotaleFacture() + " DH",
                (facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO) + " DH",
                reste + " DH",
                facture.getStatut()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void updateStatistics() {
        try {
            LocalDate debut = debutDatePicker.getDate();
            LocalDate fin = finDatePicker.getDate();
            
            ma.oralCare.service.modules.facture.dto.FactureStats stats = factureController.getFactureStats(debut, fin);
            
            // Mettre à jour les labels de statistiques
            JPanel statsPanel = (JPanel) ((JPanel) getComponent(0)).getComponent(1);
            if (statsPanel.getComponentCount() >= 4) {
                ((JLabel) statsPanel.getComponent(0)).setText("Total: " + stats.getTotalFactures());
                ((JLabel) statsPanel.getComponent(1)).setText("Payées: " + stats.getFacturesPayees());
                ((JLabel) statsPanel.getComponent(2)).setText("Créances: " + stats.getTotalCreances() + " DH");
                ((JLabel) statsPanel.getComponent(3)).setText("Montant: " + stats.getTotalMontant() + " DH");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour des statistiques: " + e.getMessage());
        }
    }

    /**
     * Gère la sélection dans le tableau
     */
    private void handleTableSelection() {
        int selectedRow = factureTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentFactures.size()) {
            selectedFacture = currentFactures.get(selectedRow);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            viewButton.setEnabled(true);
            paiementButton.setEnabled(true);
            pdfButton.setEnabled(true);
        } else {
            selectedFacture = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            viewButton.setEnabled(false);
            paiementButton.setEnabled(false);
            pdfButton.setEnabled(false);
        }
    }

    /**
     * Gère l'ajout d'une facture
     */
    private void handleAddFacture(ActionEvent e) {
        FactureDialog dialog = new FactureDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                "Ajouter une facture", null, factureController);
        
        if (dialog.showDialog()) {
            try {
                FactureCreateRequest request = dialog.getFactureCreateRequest();
                Facture newFacture = factureController.createFacture(request);
                
                JOptionPane.showMessageDialog(this, 
                    "Facture créée avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la création: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère la modification d'une facture
     */
    private void handleEditFacture(ActionEvent e) {
        if (selectedFacture == null) return;
        
        FactureDialog dialog = new FactureDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                                "Modifier une facture", selectedFacture, factureController);
        
        if (dialog.showDialog()) {
            try {
                FactureUpdateRequest request = dialog.getFactureUpdateRequest();
                Facture updatedFacture = factureController.updateFacture(selectedFacture.getIdEntite(), request);
                
                JOptionPane.showMessageDialog(this, 
                    "Facture modifiée avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la modification: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère la suppression d'une facture
     */
    private void handleDeleteFacture(ActionEvent e) {
        if (selectedFacture == null) return;
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cette facture?\nCette action est irréversible.", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                factureController.deleteFacture(selectedFacture.getIdEntite());
                
                JOptionPane.showMessageDialog(this, 
                    "Facture supprimée avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère l'affichage des détails d'une facture
     */
    private void handleViewDetails(ActionEvent e) {
        if (selectedFacture == null) return;
        
        FactureDetailsDialog dialog = new FactureDetailsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), selectedFacture);
        dialog.setVisible(true);
    }

    /**
     * Gère l'enregistrement d'un paiement
     */
    private void handlePaiement(ActionEvent e) {
        if (selectedFacture == null) return;
        
        PaiementDialog dialog = new PaiementDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), selectedFacture);
        
        if (dialog.showDialog()) {
            try {
                PaiementRequest paiementRequest = dialog.getPaiementRequest();
                Facture updatedFacture = factureController.enregistrerPaiement(
                    selectedFacture.getIdEntite(), paiementRequest);
                
                JOptionPane.showMessageDialog(this, 
                    "Paiement enregistré avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'enregistrement du paiement: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère l'exportation PDF
     */
    private void handleExportPDF(ActionEvent e) {
        if (selectedFacture == null) return;
        
        try {
            byte[] pdfBytes = factureController.exporterFacturePDF(selectedFacture.getIdEntite());
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exporter la facture en PDF");
            fileChooser.setSelectedFile(new java.io.File("facture_" + selectedFacture.getNumero() + ".pdf"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                java.nio.file.Files.write(fileToSave.toPath(), pdfBytes);
                
                JOptionPane.showMessageDialog(this, 
                    "Facture exportée en PDF avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'exportation PDF: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère l'exportation CSV
     */
    private void handleExportCSV(ActionEvent e) {
        try {
            String csvContent = factureController.exporterFacturesCSV(currentFactures);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exporter les factures en CSV");
            fileChooser.setSelectedFile(new java.io.File("factures.csv"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                java.nio.file.Files.write(fileToSave.toPath(), csvContent.getBytes());
                
                JOptionPane.showMessageDialog(this, 
                    "Factures exportées en CSV avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'exportation CSV: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère la recherche
     */
    private void handleSearch(ActionEvent e) {
        refreshData();
    }

    /**
     * Gère les filtres
     */
    private void handleFilter() {
        refreshData();
    }
}
