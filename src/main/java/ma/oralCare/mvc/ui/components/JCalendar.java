package ma.oralCare.mvc.ui.components;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * Composant JCalendar pour la sélection de dates
 */
public class JCalendar extends JPanel {
    
    private LocalDate selectedDate;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private int currentMonth;
    private int currentYear;
    
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    
    public JCalendar() {
        this(LocalDate.now());
    }
    
    public JCalendar(LocalDate initialDate) {
        this.selectedDate = initialDate;
        this.currentMonth = initialDate.getMonthValue();
        this.currentYear = initialDate.getYear();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        updateCalendar();
    }
    
    /**
     * Initialise tous les composants UI
     */
    private void initializeComponents() {
        monthLabel = new JLabel();
        monthLabel.setFont(new Font("Arial", Font.BOLD, 14));
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        daysPanel = new JPanel(new GridLayout(7, 7, 2, 2));
        
        // Boutons de navigation
        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        
        prevButton.addActionListener(e -> previousMonth());
        nextButton.addActionListener(e -> nextMonth());
        
        // Panel de navigation
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.add(prevButton, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(nextButton, BorderLayout.EAST);
        
        setLayout(new BorderLayout());
        add(navPanel, BorderLayout.NORTH);
        add(daysPanel, BorderLayout.CENTER);
    }
    
    /**
     * Configure la disposition des composants
     */
    private void setupLayout() {
        // La disposition est gérée dans initializeComponents()
    }
    
    /**
     * Configure les gestionnaires d'événements
     */
    private void setupEventHandlers() {
        // Les gestionnaires sont configurés dans initializeComponents()
    }
    
    /**
     * Met à jour l'affichage du calendrier
     */
    private void updateCalendar() {
        daysPanel.removeAll();
        
        // En-têtes des jours de la semaine
        String[] dayHeaders = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
        for (String header : dayHeaders) {
            JLabel headerLabel = new JLabel(header, SwingConstants.CENTER);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
            headerLabel.setForeground(Color.BLUE);
            daysPanel.add(headerLabel);
        }
        
        // Calculer le premier jour du mois
        LocalDate firstDay = LocalDate.of(currentYear, currentMonth, 1);
        int firstDayOfWeek = firstDay.getDayOfWeek().getValue() % 7; // Ajuster pour commencer par Dimanche
        
        // Ajouter les jours vides avant le premier jour
        for (int i = 0; i < firstDayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }
        
        // Ajouter les jours du mois
        int daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = LocalDate.of(currentYear, currentMonth, day);
            JButton dayButton = new JButton(String.valueOf(day));
            
            // Mettre en évidence le jour sélectionné
            if (currentDate.equals(selectedDate)) {
                dayButton.setBackground(Color.BLUE);
                dayButton.setForeground(Color.WHITE);
            }
            
            // Mettre en évidence aujourd'hui
            if (currentDate.equals(LocalDate.now())) {
                dayButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
            
            dayButton.addActionListener(e -> {
                selectedDate = currentDate;
                updateCalendar();
            });
            
            daysPanel.add(dayButton);
        }
        
        // Mettre à jour le label du mois
        monthLabel.setText(firstDay.format(MONTH_FORMATTER));
        
        revalidate();
        repaint();
    }
    
    /**
     * Navigue vers le mois précédent
     */
    private void previousMonth() {
        if (currentMonth == 1) {
            currentMonth = 12;
            currentYear--;
        } else {
            currentMonth--;
        }
        updateCalendar();
    }
    
    /**
     * Navigue vers le mois suivant
     */
    private void nextMonth() {
        if (currentMonth == 12) {
            currentMonth = 1;
            currentYear++;
        } else {
            currentMonth++;
        }
        updateCalendar();
    }
    
    /**
     * Retourne la date sélectionnée
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }
    
    /**
     * Définit la date sélectionnée
     */
    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        this.currentMonth = date.getMonthValue();
        this.currentYear = date.getYear();
        updateCalendar();
    }
    
    /**
     * Convertit LocalDate en Date
     */
    public Date getDate() {
        return Date.from(selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Définit la date à partir d'un objet Date
     */
    public void setDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        setSelectedDate(localDate);
    }
}
