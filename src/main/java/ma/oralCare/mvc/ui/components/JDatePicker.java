package ma.oralCare.mvc.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Composant JDatePicker pour la sélection de dates
 */
public class JDatePicker extends JPanel {
    
    private LocalDate selectedDate;
    private JTextField dateField;
    private JButton calendarButton;
    private JDialog calendarDialog;
    private JCalendar calendar;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    public JDatePicker() {
        this(LocalDate.now());
    }
    
    public JDatePicker(LocalDate initialDate) {
        this.selectedDate = initialDate;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateDisplay();
    }
    
    /**
     * Initialise tous les composants UI
     */
    private void initializeComponents() {
        dateField = new JTextField(15);
        dateField.setEditable(false);
        dateField.setHorizontalAlignment(SwingConstants.CENTER);
        
        calendarButton = new JButton("📅");
        calendarButton.setPreferredSize(new Dimension(30, 25));
        
        // Créer le dialogue calendrier
        calendarDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sélectionner une date", true);
        calendar = new JCalendar(selectedDate);
        
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annuler");
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        calendarDialog.setLayout(new BorderLayout());
        calendarDialog.add(calendar, BorderLayout.CENTER);
        calendarDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        okButton.addActionListener(e -> {
            selectedDate = calendar.getSelectedDate();
            updateDisplay();
            calendarDialog.setVisible(false);
        });
        
        cancelButton.addActionListener(e -> {
            calendarDialog.setVisible(false);
        });
        
        calendarDialog.pack();
        calendarDialog.setLocationRelativeTo(this);
    }
    
    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));
        
        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        calendarButton.addActionListener(e -> {
            calendar.setSelectedDate(selectedDate);
            calendarDialog.setVisible(true);
        });
        
        dateField.addActionListener(e -> {
            calendarDialog.setVisible(true);
        });
    }
    
    /**
     * Met à jour l'affichage du champ texte
     */
    private void updateDisplay() {
        dateField.setText(selectedDate.format(DATE_FORMATTER));
    }
    
    /**
     * Retourne la date sélectionnée
     */
    public LocalDate getDate() {
        return selectedDate;
    }
    
    /**
     * Définit la date sélectionnée
     */
    public void setDate(LocalDate date) {
        this.selectedDate = date;
        updateDisplay();
    }
    
    /**
     * Ajoute un listener pour les changements de date
     */
    public void addChangeListener(ActionListener listener) {
        // Créer un listener personnalisé pour les changements de date
        dateField.addActionListener(e -> listener.actionPerformed(e));
    }
    
    /**
     * Retourne le formatteur de date
     */
    public static DateTimeFormatter getDateFormatter() {
        return DATE_FORMATTER;
    }
}
