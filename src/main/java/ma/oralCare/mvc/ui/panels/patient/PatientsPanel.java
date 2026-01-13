package ma.oralCare.mvc.ui.panels.patient;

import ma.oralCare.entities.patient.Patient;
import ma.oralCare.mvc.ui.dashboard.SecretaireDashboard;
import ma.oralCare.mvc.ui.components.JDatePicker;
import ma.oralCare.mvc.ui.dialogs.PatientDetailsDialog;
import ma.oralCare.service.modules.patient.dto.PatientCreateRequest;
import ma.oralCare.service.modules.patient.dto.PatientUpdateRequest;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Panel complet pour la gestion des patients par les secrétaires
 * Fonctionnalités: CRUD complet, validation, temps réel
 */
public class PatientsPanel extends JPanel {

    private final SecretaireDashboard dashboard;
    
    // Composants UI
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private ma.oralCare.mvc.ui.components.JDatePicker debutDatePicker;
    private ma.oralCare.mvc.ui.components.JDatePicker finDatePicker;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    private JButton exportButton;
    private JButton importButton;
    private JTextField searchField;
    private JComboBox<String> assuranceFilter;
    private JComboBox<String> sexeFilter;
    
    // Services et contrôleurs
    private ma.oralCare.mvc.controllers.patient.api.PatientController patientController;
    
    // Données
    private List<Patient> currentPatients;
    private Patient selectedPatient;
    
    // Formatters
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PatientsPanel(SecretaireDashboard dashboard) {
        this.dashboard = dashboard;
        this.patientController = dashboard.getPatientController();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
    }

    /**
     * Initialise tous les composants UI
     */
    private void initializeComponents() {
        // Table des patients
        String[] columns = {"ID", "Nom", "Prénom", "Date Naissance", "Sexe", "Téléphone", "Email", "Assurance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-éditable directement
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setReorderingAllowed(false);
        
        // Boutons d'action
        addButton = new JButton("Ajouter");
        editButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        viewDetailsButton = new JButton("Détails");
        exportButton = new JButton("Exporter");
        importButton = new JButton("Importer");
        
        // Configuration des boutons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        viewDetailsButton.setEnabled(false);
        
        // Champs de recherche et filtres
        searchField = new JTextField(20);
        searchField.setToolTipText("Rechercher par nom, prénom, CIN ou téléphone");
        
        assuranceFilter = new JComboBox<>(new String[]{"Toutes", "CNSS", "AMO", "RAMED", "Prive", "Autre"});
        sexeFilter = new JComboBox<>(new String[]{"Tous", "MASCULIN", "FEMININ"});
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
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setPreferredSize(new Dimension(900, 400));
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
        
        // Date pickers
        debutDatePicker = new ma.oralCare.mvc.ui.components.JDatePicker(LocalDate.now().minusDays(30));
        finDatePicker = new ma.oralCare.mvc.ui.components.JDatePicker(LocalDate.now());
        filterPanel.add(debutDatePicker);
        filterPanel.add(finDatePicker);
        
        filterPanel.add(new JLabel("Assurance:"));
        filterPanel.add(assuranceFilter);
        
        filterPanel.add(new JLabel("Sexe:"));
        filterPanel.add(sexeFilter);
        
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
        panel.setBorder(BorderFactory.createTitledBorder("Statistiques"));
        
        JLabel totalLabel = new JLabel("Total: 0");
        JLabel hommesLabel = new JLabel("Hommes: 0");
        JLabel femmesLabel = new JLabel("Femmes: 0");
        JLabel cnssLabel = new JLabel("CNSS: 0");
        
        panel.add(totalLabel);
        panel.add(hommesLabel);
        panel.add(femmesLabel);
        panel.add(cnssLabel);
        
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
        panel.add(viewDetailsButton);
        panel.add(exportButton);
        panel.add(importButton);
        
        return panel;
    }

    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Sélection dans le tableau
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Boutons d'action
        addButton.addActionListener(this::handleAddPatient);
        editButton.addActionListener(this::handleEditPatient);
        deleteButton.addActionListener(this::handleDeletePatient);
        viewDetailsButton.addActionListener(this::handleViewDetails);
        exportButton.addActionListener(this::handleExport);
        importButton.addActionListener(this::handleImport);

        // Recherche et filtres
        searchField.addActionListener(this::handleSearch);
        assuranceFilter.addActionListener(e -> handleFilter());
        sexeFilter.addActionListener(e -> handleFilter());
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
            currentPatients = patientController.getAllPatients();
            
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
        String selectedAssurance = (String) assuranceFilter.getSelectedItem();
        String selectedSexe = (String) sexeFilter.getSelectedItem();
        
        if (searchText.isEmpty() && "Toutes".equals(selectedAssurance) && "Tous".equals(selectedSexe)) {
            return; // Pas de filtre
        }
        
        currentPatients = currentPatients.stream()
            .filter(patient -> {
                // Filtre de recherche
                boolean matchesSearch = searchText.isEmpty() || 
                    patient.getNom().toLowerCase().contains(searchText) ||
                    patient.getPrenom().toLowerCase().contains(searchText) ||
                    (patient.getTelephone() != null && patient.getTelephone().toLowerCase().contains(searchText));
                
                // Filtre d'assurance
                boolean matchesAssurance = "Toutes".equals(selectedAssurance) || 
                    (patient.getAssurance() != null && selectedAssurance.equals(patient.getAssurance().name()));
                
                // Filtre de sexe
                boolean matchesSexe = "Tous".equals(selectedSexe) || 
                    (patient.getSexe() != null && selectedSexe.equals(patient.getSexe().name()));
                
                return matchesSearch && matchesAssurance && matchesSexe;
            })
            .toList();
    }

    /**
     * Met à jour le tableau avec les données actuelles
     */
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Patient patient : currentPatients) {
            Object[] row = {
                patient.getIdEntite(),
                patient.getNom(),
                patient.getPrenom(),
                patient.getDateDeNaissance() != null ? patient.getDateDeNaissance().format(DATE_FORMATTER) : "",
                patient.getSexe(),
                patient.getTelephone(),
                patient.getEmail(),
                patient.getAssurance()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Met à jour les statistiques
     */
    private void updateStatistics() {
        long total = currentPatients.size();
        long hommes = currentPatients.stream().filter(p -> "MASCULIN".equals(p.getSexe())).count();
        long femmes = currentPatients.stream().filter(p -> "FEMININ".equals(p.getSexe())).count();
        long cnss = currentPatients.stream().filter(p -> "CNSS".equals(p.getAssurance() != null ? p.getAssurance().name() : "")).count();
        
        // Mettre à jour les labels de statistiques
        JPanel statsPanel = (JPanel) ((JPanel) getComponent(0)).getComponent(1);
        if (statsPanel.getComponentCount() >= 4) {
            ((JLabel) statsPanel.getComponent(0)).setText("Total: " + total);
            ((JLabel) statsPanel.getComponent(1)).setText("Hommes: " + hommes);
            ((JLabel) statsPanel.getComponent(2)).setText("Femmes: " + femmes);
            ((JLabel) statsPanel.getComponent(3)).setText("CNSS: " + cnss);
        }
    }

    /**
     * Gère la sélection dans le tableau
     */
    private void handleTableSelection() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < currentPatients.size()) {
            selectedPatient = currentPatients.get(selectedRow);
            editButton.setEnabled(true);
            deleteButton.setEnabled(true);
            viewDetailsButton.setEnabled(true);
        } else {
            selectedPatient = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            viewDetailsButton.setEnabled(false);
        }
    }

    /**
     * Gère l'ajout d'un patient
     */
    private void handleAddPatient(ActionEvent e) {
        PatientDialog dialog = new PatientDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                               "Ajouter un patient", null);
        
        if (dialog.showDialog()) {
            try {
                PatientCreateRequest request = dialog.getPatientCreateRequest();
                Patient newPatient = patientController.createPatient(request);
                
                JOptionPane.showMessageDialog(this, 
                    "Patient créé avec succès!", 
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
     * Gère la modification d'un patient
     */
    private void handleEditPatient(ActionEvent e) {
        if (selectedPatient == null) return;
        
        PatientDialog dialog = new PatientDialog((JFrame) SwingUtilities.getWindowAncestor(this), 
                                               "Modifier un patient", selectedPatient);
        
        if (dialog.showDialog()) {
            try {
                PatientUpdateRequest request = dialog.getPatientUpdateRequest();
                Patient updatedPatient = patientController.updatePatient(selectedPatient.getIdEntite(), request);
                
                JOptionPane.showMessageDialog(this, 
                    "Patient modifié avec succès!", 
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
     * Gère la suppression d'un patient
     */
    private void handleDeletePatient(ActionEvent e) {
        if (selectedPatient == null) return;
        
        int result = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce patient?\nCette action est irréversible.", 
            "Confirmation", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                patientController.deletePatient(selectedPatient.getIdEntite());
                
                JOptionPane.showMessageDialog(this, 
                    "Patient supprimé avec succès!", 
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
     * Gère l'affichage des détails d'un patient
     */
    private void handleViewDetails(ActionEvent e) {
        if (selectedPatient == null) return;
        
        PatientDetailsDialog dialog = new PatientDetailsDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), selectedPatient);
        dialog.setVisible(true);
    }

    /**
     * Gère l'exportation des patients
     */
    private void handleExport(ActionEvent e) {
        try {
            String csvContent = patientController.exportPatientsToCSV(currentPatients);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Exporter les patients");
            fileChooser.setSelectedFile(new java.io.File("patients.csv"));
            
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                java.io.File fileToSave = fileChooser.getSelectedFile();
                java.nio.file.Files.write(fileToSave.toPath(), csvContent.getBytes());
                
                JOptionPane.showMessageDialog(this, 
                    "Patients exportés avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Erreur lors de l'exportation: " + ex.getMessage(), 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Gère l'importation des patients
     */
    private void handleImport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Importer des patients");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Fichiers CSV", "csv"));
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File fileToOpen = fileChooser.getSelectedFile();
                String csvContent = new String(java.nio.file.Files.readAllBytes(fileToOpen.toPath()));
                
                List<Patient> importedPatients = patientController.importPatientsFromCSV(csvContent);
                
                JOptionPane.showMessageDialog(this, 
                    importedPatients.size() + " patients importés avec succès!", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                
                refreshData();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de l'importation: " + ex.getMessage(), 
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
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
