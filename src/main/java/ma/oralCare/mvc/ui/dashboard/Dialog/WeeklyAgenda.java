package ma.oralCare.mvc.ui.dashboard.Dialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeeklyAgenda extends JFrame {
    private JTable agendaTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> weekSelector;
    private JComboBox<String> yearSelector;
    private LocalDate currentWeekStart;
    private List<Appointment> appointments;

    // Constantes pour les colonnes
    private static final String[] COLUMNS = {"Heure", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    private static final String[] TIME_SLOTS = {
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00",
            "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00"
    };

    public WeeklyAgenda() {
        appointments = new ArrayList<>();
        currentWeekStart = getStartOfWeek(LocalDate.now());

        initializeUI();
        loadWeekData();
        setupListeners();
    }

    private void initializeUI() {
        setTitle("Agenda Hebdomadaire - Style Google Agenda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel supérieur avec les contrôles
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Boutons de navigation simples
        JButton prevButton = new JButton(" < ");
        JButton nextButton = new JButton(" > ");
        JButton todayButton = new JButton("Aujourd'hui");

        // Sélecteur d'année
        yearSelector = new JComboBox<>(getYearOptions());
        yearSelector.setSelectedItem(String.valueOf(LocalDate.now().getYear()));

        // Sélecteur de semaine
        weekSelector = new JComboBox<>(getWeekOptions());
        weekSelector.setSelectedIndex(getCurrentWeekNumber() - 1);

        // Bouton d'ajout de rendez-vous
        JButton addButton = new JButton("+");
        addButton.setBackground(new Color(66, 133, 244));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));

        // Personnalisation des boutons de navigation
        prevButton.setPreferredSize(new Dimension(40, 25));
        nextButton.setPreferredSize(new Dimension(40, 25));
        todayButton.setPreferredSize(new Dimension(100, 25));

        // Ajout des composants dans l'ordre désiré
        topPanel.add(prevButton);
        topPanel.add(nextButton);
        topPanel.add(todayButton);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(new JLabel("Année:"));
        topPanel.add(yearSelector);
        topPanel.add(new JLabel("Semaine:"));
        topPanel.add(weekSelector);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(addButton);

        add(topPanel, BorderLayout.NORTH);

        // Tableau de l'agenda
        tableModel = new DefaultTableModel(COLUMNS, TIME_SLOTS.length) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        agendaTable = new JTable(tableModel);
        agendaTable.setRowHeight(60);
        agendaTable.getTableHeader().setReorderingAllowed(false);

        // Définir la largeur de la première colonne (heures)
        agendaTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        agendaTable.getColumnModel().getColumn(0).setMinWidth(80);
        agendaTable.getColumnModel().getColumn(0).setMaxWidth(80);

        // Remplir les heures dans la première colonne
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            tableModel.setValueAt(TIME_SLOTS[i], i, 0);
        }

        JScrollPane scrollPane = new JScrollPane(agendaTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder(getWeekRangeText()));
        add(scrollPane, BorderLayout.CENTER);

        // Panel d'information
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel("Double-cliquez sur une cellule pour ajouter/modifier un rendez-vous"));
        add(infoPanel, BorderLayout.SOUTH);

        // Ajouter des listeners aux boutons
        prevButton.addActionListener(e -> navigateWeek(-1));
        nextButton.addActionListener(e -> navigateWeek(1));
        todayButton.addActionListener(e -> goToToday());
        addButton.addActionListener(e -> showAddAppointmentDialog());

        // Double-clic sur une cellule pour éditer
        agendaTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = agendaTable.rowAtPoint(evt.getPoint());
                    int col = agendaTable.columnAtPoint(evt.getPoint());
                    if (col > 0) {
                        editAppointment(row, col);
                    }
                }
            }
        });

        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void setupListeners() {
        weekSelector.addActionListener(e -> {
            if (weekSelector.getSelectedIndex() >= 0) {
                int selectedWeek = weekSelector.getSelectedIndex() + 1;
                int selectedYear = Integer.parseInt((String) yearSelector.getSelectedItem());
                loadSpecificWeek(selectedYear, selectedWeek);
            }
        });

        yearSelector.addActionListener(e -> {
            if (yearSelector.getSelectedItem() != null) {
                int selectedYear = Integer.parseInt((String) yearSelector.getSelectedItem());
                weekSelector.setModel(new DefaultComboBoxModel<>(getWeekOptionsForYear(selectedYear)));
                weekSelector.setSelectedIndex(0);
            }
        });
    }

    private void loadWeekData() {
        // Vider toutes les cellules (sauf la colonne des heures)
        for (int row = 0; row < TIME_SLOTS.length; row++) {
            for (int col = 1; col < COLUMNS.length; col++) {
                tableModel.setValueAt("", row, col);
            }
        }

        // Afficher les rendez-vous pour la semaine courante
        LocalDate weekDay = currentWeekStart;
        for (int col = 1; col < COLUMNS.length; col++) {
            LocalDate currentDate = weekDay;
            List<Appointment> dayAppointments = getAppointmentsForDate(currentDate);

            for (Appointment app : dayAppointments) {
                int timeIndex = getTimeSlotIndex(app.getTime());
                if (timeIndex >= 0 && timeIndex < TIME_SLOTS.length) {
                    String currentValue = (String) tableModel.getValueAt(timeIndex, col);
                    String newValue = currentValue.isEmpty() ? app.getDescription()
                            : currentValue + "\n" + app.getDescription();
                    tableModel.setValueAt(newValue, timeIndex, col);

                    // Colorier la cellule si elle contient un rendez-vous
                    agendaTable.getColumnModel().getColumn(col).setCellRenderer(new AppointmentRenderer());
                }
            }
            weekDay = weekDay.plusDays(1);
        }

        // Mettre à jour le titre
        ((javax.swing.border.TitledBorder) ((JScrollPane) getContentPane().getComponent(1)).getBorder())
                .setTitle(getWeekRangeText());
    }

    private void loadSpecificWeek(int year, int weekNumber) {
        LocalDate date = LocalDate.of(year, 1, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), weekNumber)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        currentWeekStart = date;
        loadWeekData();
    }

    private void navigateWeek(int weeks) {
        currentWeekStart = currentWeekStart.plusWeeks(weeks);
        updateSelectors();
        loadWeekData();
    }

    private void goToToday() {
        currentWeekStart = getStartOfWeek(LocalDate.now());
        updateSelectors();
        loadWeekData();
    }

    private void updateSelectors() {
        int year = currentWeekStart.getYear();
        int weekNumber = currentWeekStart.get(WeekFields.of(Locale.getDefault()).weekOfYear());

        yearSelector.setSelectedItem(String.valueOf(year));

        // Vérifier si la semaine existe dans la liste actuelle
        boolean weekFound = false;
        for (int i = 0; i < weekSelector.getItemCount(); i++) {
            if (weekSelector.getItemAt(i).equals("Semaine " + weekNumber)) {
                weekSelector.setSelectedIndex(i);
                weekFound = true;
                break;
            }
        }

        if (!weekFound) {
            weekSelector.setModel(new DefaultComboBoxModel<>(getWeekOptionsForYear(year)));
            weekSelector.setSelectedItem("Semaine " + weekNumber);
        }
    }

    private void showAddAppointmentDialog() {
        JDialog dialog = new JDialog(this, "Ajouter un rendez-vous", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Date
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> dateCombo = new JComboBox<>(getWeekDays());
        dialog.add(dateCombo, gbc);

        // Heure
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Heure:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> timeCombo = new JComboBox<>(TIME_SLOTS);
        dialog.add(timeCombo, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        dialog.add(scrollPane, gbc);

        // Boutons
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Enregistrer");
        JButton cancelButton = new JButton("Annuler");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, gbc);

        // Actions
        saveButton.addActionListener(e -> {
            int dayIndex = dateCombo.getSelectedIndex();
            String time = (String) timeCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();

            if (!description.isEmpty()) {
                LocalDate appointmentDate = currentWeekStart.plusDays(dayIndex);
                appointments.add(new Appointment(appointmentDate, time, description));
                loadWeekData();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Veuillez saisir une description");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editAppointment(int row, int col) {
        String currentText = (String) tableModel.getValueAt(row, col);
        if (currentText == null) currentText = "";

        String newText = JOptionPane.showInputDialog(this,
                "Modifier le rendez-vous:", currentText);

        if (newText != null && !newText.equals(currentText)) {
            // Mettre à jour l'interface
            tableModel.setValueAt(newText, row, col);

            // Mettre à jour les données
            LocalDate date = currentWeekStart.plusDays(col - 1);
            String time = TIME_SLOTS[row];

            // Supprimer les anciens rendez-vous pour cette cellule
            appointments.removeIf(app ->
                    app.getDate().equals(date) && app.getTime().equals(time));

            // Ajouter le nouveau rendez-vous si non vide
            if (!newText.trim().isEmpty()) {
                appointments.add(new Appointment(date, time, newText));
            }
        }
    }

    // Méthodes utilitaires
    private LocalDate getStartOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    private String getWeekRangeText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        return String.format("Semaine du %s au %s",
                currentWeekStart.format(formatter),
                weekEnd.format(formatter));
    }

    private String[] getWeekDays() {
        String[] days = new String[7];
        LocalDate date = currentWeekStart;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd/MM");

        for (int i = 0; i < 7; i++) {
            days[i] = date.format(formatter);
            date = date.plusDays(1);
        }
        return days;
    }

    private String[] getYearOptions() {
        List<String> years = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            years.add(String.valueOf(i));
        }
        return years.toArray(new String[0]);
    }

    private String[] getWeekOptions() {
        return getWeekOptionsForYear(LocalDate.now().getYear());
    }

    private String[] getWeekOptionsForYear(int year) {
        List<String> weeks = new ArrayList<>();
        LocalDate date = LocalDate.of(year, 1, 1);
        int maxWeeks = date.isLeapYear() ? 53 : 52;

        for (int i = 1; i <= maxWeeks; i++) {
            weeks.add("Semaine " + i);
        }
        return weeks.toArray(new String[0]);
    }

    private int getCurrentWeekNumber() {
        return LocalDate.now().get(WeekFields.of(Locale.getDefault()).weekOfYear());
    }

    private int getTimeSlotIndex(String time) {
        for (int i = 0; i < TIME_SLOTS.length; i++) {
            if (TIME_SLOTS[i].equals(time)) {
                return i;
            }
        }
        return -1;
    }

    private List<Appointment> getAppointmentsForDate(LocalDate date) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment app : appointments) {
            if (app.getDate().equals(date)) {
                result.add(app);
            }
        }
        return result;
    }

    // Renderer pour colorier les cellules avec rendez-vous
    private class AppointmentRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value != null && !value.toString().trim().isEmpty()) {
                c.setBackground(new Color(220, 240, 255)); // Bleu clair pour les rendez-vous
                c.setForeground(Color.BLACK);
            } else {
                c.setBackground(Color.WHITE);
                c.setForeground(Color.BLACK);
            }

            if (isSelected) {
                c.setBackground(new Color(180, 220, 255)); // Bleu plus foncé pour la sélection
            }

            return c;
        }
    }

    // Classe pour représenter un rendez-vous
    private static class Appointment {
        private LocalDate date;
        private String time;
        private String description;

        public Appointment(LocalDate date, String time, String description) {
            this.date = date;
            this.time = time;
            this.description = description;
        }

        public LocalDate getDate() { return date; }
        public String getTime() { return time; }
        public String getDescription() { return description; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            WeeklyAgenda agenda = new WeeklyAgenda();
            agenda.setVisible(true);
        });
    }
}