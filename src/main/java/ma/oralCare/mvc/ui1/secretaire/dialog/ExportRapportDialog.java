package ma.oralCare.mvc.ui1.secretaire.dialog;

import ma.oralCare.mvc.ui1.MainFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Dialogue pour exporter des rapports (statistiques de caisse, situations financières, etc.)
 */
public class ExportRapportDialog extends JDialog {

    private final MainFrame mainFrame;
    private final String reportType;
    private JComboBox<String> cbFormat;
    private JComboBox<String> cbPeriode;
    private JTextField dateDebut, dateFin;
    private JCheckBox chkInclureDetails;

    public ExportRapportDialog(MainFrame mainFrame, String reportType) {
        super(mainFrame, "Exporter Rapport", true);
        this.mainFrame = mainFrame;
        this.reportType = reportType;

        setSize(500, 400);
        setLocationRelativeTo(mainFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Format d'export
        cbFormat = new JComboBox<>(new String[]{"PDF", "Excel (XLSX)", "CSV"});

        // Période
        cbPeriode = new JComboBox<>(new String[]{
                "Aujourd'hui",
                "Cette semaine",
                "Ce mois",
                "Ce trimestre",
                "Cette année",
                "Période personnalisée"
        });
        cbPeriode.addActionListener(e -> {
            boolean custom = cbPeriode.getSelectedItem().equals("Période personnalisée");
            dateDebut.setEnabled(custom);
            dateFin.setEnabled(custom);
        });

        // Dates (désactivées par défaut)
        dateDebut = new JTextField(10);
        dateDebut.setEnabled(false);
        dateDebut.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        dateFin = new JTextField(10);
        dateFin.setEnabled(false);
        dateFin.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        chkInclureDetails = new JCheckBox("Inclure les détails", true);
    }

    private void setupLayout() {
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPanel);

        // Titre
        JLabel lblTitle = new JLabel("📄 Exporter Rapport");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        contentPanel.add(lblTitle, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Options d'export"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Format
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Format:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbFormat, gbc);

        // Période
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Période:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbPeriode, gbc);

        // Date début
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date début:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateDebut, gbc);

        // Date fin
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Date fin:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateFin, gbc);

        // Options
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(chkInclureDetails, gbc);

        contentPanel.add(formPanel, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnExport = new JButton("📥 Exporter");
        btnExport.setBackground(new Color(39, 174, 96));
        btnExport.setForeground(Color.WHITE);
        btnExport.addActionListener(e -> handleExport());

        JButton btnCancel = new JButton("Annuler");
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnExport);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleExport() {
        String format = cbFormat.getSelectedItem().toString();
        String periode = cbPeriode.getSelectedItem().toString();

        // Sélection du répertoire de sauvegarde
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer le rapport");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            String fileName = generateFileName(format, periode);

            // Simulation de l'export (à remplacer par la vraie logique)
            JOptionPane.showMessageDialog(this,
                    String.format("Rapport exporté avec succès !\n\nFormat: %s\nPériode: %s\nFichier: %s",
                            format, periode, new File(directory, fileName).getAbsolutePath()),
                    "Export réussi",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
        }
    }

    private String generateFileName(String format, String periode) {
        String extension = format.equals("PDF") ? ".pdf" : format.equals("Excel (XLSX)") ? ".xlsx" : ".csv";
        String type = reportType != null ? reportType : "Rapport";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s_%s%s", type, periode.replace(" ", "_"), date, extension);
    }
}
