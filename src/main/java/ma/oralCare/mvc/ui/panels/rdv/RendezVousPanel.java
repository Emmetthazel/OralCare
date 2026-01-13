package ma.oralCare.mvc.ui.panels.rdv;

import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.mvc.ui.components.JDatePicker;
import ma.oralCare.service.modules.RDV.dto.RDVCreateRequest;
import ma.oralCare.service.modules.RDV.dto.RDVUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Panel complet pour la gestion des rendez-vous par les secrétaires
 * Fonctionnalités: CRUD complet, validation, temps réel
 */
public class RendezVousPanel extends JPanel {

    private final SecretaireDashboard dashboard;
    
    // Composants UI
    private JTable rdvTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton cancelButton;
    private JButton confirmButton;
    private JButton refreshButton;
    private JTextField searchField;
    private JComboBox<String> statutFilter;
    private ma.oralCare.mvc.ui.components.JDatePicker debutDatePicker;
    private ma.oralCare.mvc.ui.components.JDatePicker finDatePicker;
    
    // Services et contrôleurs
    private ma.oralCare.mvc.controllers.RDV.api.RDVController rdvController;
    
    // Données
    private List<RDV> currentRDVs;
    private RDV selectedRDV;
    
    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public RendezVousPanel(SecretaireDashboard dashboard) {
        this.dashboard = dashboard;
        this.rdvController = dashboard.getRdvController();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }

    /**
     * Initialise tous les composants UI
     */
    private void initializeComponents() {
        // Table des rendez-vous
        String[] columns = {"ID", "Patient", "Médecin", "Date", "Heure", "Motif", "Statut"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-éditable directement
            }
        };
        rdvTable = new JTable(tableModel);
        rdvTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rdvTable.getTableHeader().setReorderingAllowed(false);
        
        // Boutons d'action
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        cancelButton = new JButton("Annuler");
        confirmButton = new JButton("Confirmer");
        refreshButton = new JButton("Rafraîchir");
        
        // Configuration des boutons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        cancelButton.setEnabled(false);
        confirmButton.setEnabled(false);
        
        // Champs de recherche et filtre
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par patient, médecin ou motif");
        
        statutFilter = new JComboBox<>(new String[]{"Tous", "PLANIFIE", "CONFIRME", "ANNULE", "EN_COURS", "TERMINE"});
        
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
        JScrollPane scrollPane = new JScrollPane(rdvTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
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
        
        filterPanel.add(debutDatePicker);
        filterPanel.add(new JLabel("au"));
        filterPanel.add(finDatePicker);
        
        filterPanel.add(refreshButton);
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Statistiques du jour"));
        
        JLabel totalLabel = new JLabel("Total: 0");
        JLabel confirmesLabel = new JLabel("Confirmés: 0");
        JLabel annulesLabel = new JLabel("Annulés: 0");
        
        panel.add(totalLabel);
        panel.add(confirmesLabel);
        panel.add(annulesLabel);
        
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
        panel.add(cancelButton);
        panel.add(confirmButton);
        
        return panel;
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Sélection dans le tableau
        rdvTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Boutons d'action
        addButton.addActionListener(this::handleAddRDV);
        editButton.addActionListener(this::handleEditRDV);
        deleteButton.addActionListener(this::handleDeleteRDV);
        cancelButton.addActionListener(this::handleCancelRDV);
        confirmButton.addActionListener(this::handleConfirmRDV);
        refreshButton.addActionListener(this::handleRefresh);

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
        refreshTableData();
    }

    /**
     * Rafraîchit les données du tableau
     */
    private void refreshTableData() {
        try {
            LocalDate debut = debutDatePicker.getDate();
            LocalDate fin = finDatePicker.getDate();
            currentRDVs = rdvController.getRDVsByPeriode(debut, fin);
            
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
        
        currentRDVs = currentRDVs.stream()
            .filter(rdv -> {
                // Filtre de recherche
                boolean matchesSearch = searchText.isEmpty() || 
                    rdv.getPatientNom().toLowerCase().contains(searchText) ||
                    rdv.getMedecinNom().toLowerCase().contains(searchText) ||
                    rdv.getMotif().toLowerCase().contains(searchText);
                
                // Filtre de statut
                boolean matchesStatut = "Tous".equals(selectedStatut) || 
                    selectedStatut.equals(rdv.getStatut());
                
                return matchesSearch && matchesStatut;
            })
            .toList();
    }

    /**
     * Met à jour le tableau avec les données actuelles
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (RDV rdv : currentRDVs) {
            Object[] row = {
                rdv.getIdEntite(),
                rdv.getPatientNom(),
                rdv.getMedecinNom(),
                rdv.getDate().format(DATE_FORMATTER),
                rdv.getHeure().format(TIME_FORMATTER),
                rdv.getMotif(),
                rdv.getStatut()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void updateStatistics() {
        long total = currentRDVs.size();
        long confirmes = currentRDVs.stream().filter(r -> "CONFIRME".equals(r.getStatut())).count();
        long annules = currentRDVs.stream().filter(r -> "ANNULE".equals(r.getStatut())).count();
        
        // Mettre à jour les labels de statistiques
        JPanel statsPanel = (JPanel) ((JPanel) getComponent(0)).getComponent(1);
        if (statsPanel.getComponentCount() >= 3) {
            ((JLabel) statsPanel.getComponent(0)).setText("Total: " + total);
            ((JLabel) statsPanel.getComponent(1)).setText("Confirmés: " + confirmes);
            ((JLabel) statsPanel.getComponent(2)).setText("Annulés: " + annules);
        }
    }

    /**
     * Gère la sélection dans le tableau
     */
    private void handleTableSelection() {
        int selectedRow = rdvTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentRDVs.size()) {
            selectedRDV = currentRDVs.get(selectedRow);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            cancelButton.setEnabled(true);
            confirmButton.setEnabled(true);
        } else {
            selectedRDV = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            cancelButton.setEnabled(false);
            confirmButton.setEnabled(false);
        }
    }

    /**
     * Gère l'ajout d'un rendez-vous
     */
    private void handleAddRDV(ActionEvent e) {
        RDVDialog dialog = new RDVDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                       "Ajouter un rendez-vous", null);
        
        if (dialog.showDialog()) {
            try {
                RDVCreateRequest request = dialog.getRDVCreateRequest();
                RDV newRDV = rdvController.createRDV(request);
                
                JOptionPane.showMessageDialog(this, 
                    "Rendez-vous créé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshTableData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la création: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère la modification d'un rendez-vous
     */
    private void handleEditRDV(ActionEvent e) {
        if (selectedRDV == null) return;
        
        RDVDialog dialog = new RDVDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                       "Modifier un rendez-vous", selectedRDV);
        
        if (dialog.showDialog()) {
            try {
                RDVUpdateRequest request = dialog.getRDVUpdateRequest();
                RDV updatedRDV = rdvController.updateRDV(selectedRDV.getIdEntite(), request);
                
                JOptionPane.showMessageDialog(this, 
                    "Rendez-vous modifié avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshTableData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la modification: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère la suppression d'un rendez-vous
     */
    private void handleDeleteRDV(ActionEvent e) {
        if (selectedRDV == null) return;
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce rendez-vous?", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                rdvController.deleteRDV(selectedRDV.getIdEntite());
                
                JOptionPane.showMessageDialog(this, 
                    "Rendez-vous supprimé avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshTableData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la suppression: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Gère l'annulation d'un rendez-vous
     */
    private void handleCancelRDV(ActionEvent e) {
        if (selectedRDV == null) return;
        
        try {
            rdvController.cancelRDV(selectedRDV.getIdEntite());
            
            JOptionPane.showMessageDialog(this, 
                "Rendez-vous annulé avec succès!", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            refreshTableData();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'annulation: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère la confirmation d'un rendez-vous
     */
    private void handleConfirmRDV(ActionEvent e) {
        if (selectedRDV == null) return;
        
        try {
            rdvController.confirmRDV(selectedRDV.getIdEntite());
            
            JOptionPane.showMessageDialog(this, 
                "Rendez-vous confirmé avec succès!", 
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            refreshTableData();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de la confirmation: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère le rafraîchissement manuel
     */
    private void handleRefresh(ActionEvent e) {
        refreshTableData();
    }

    /**
     * Gère la recherche
     */
    private void handleSearch(ActionEvent e) {
        refreshTableData();
    }

    /**
     * Gère les filtres
     */
    private void handleFilter() {
        refreshTableData();
    }

    /**
     * Méthode publique pour rafraîchir les données (appelée par le dashboard)
     */
    public void refreshData() {
        refreshTableData();
    }
}
