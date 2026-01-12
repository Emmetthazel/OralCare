package ma.oralCare.mvc.ui1.medecin;

import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.service.modules.dashboard.dto.DashboardDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * DashboardPanel final et dynamique.
 * Connecté au DashboardController via la méthode updateUIWithData.
 */
public class DashboardPanel extends JPanel {

    // ✅ Références pour la mise à jour dynamique
    private JLabel lblWelcome; // Ajouté pour afficher le nom du Dr.
    private JLabel lblRdvCount;
    private JLabel lblPatientCount;
    private JLabel lblRevenueCount;
    private DefaultTableModel tableModel;
    private JLabel lblDate;
    private JLabel lblTime;

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        initUI();
        updateDateTime();
    }


    /**
     * Met à jour dynamiquement l'interface pour le médecin.
     * Affiche systématiquement "Bonjour, Dr. [Nom]"
     */
    public void updateUIWithData(DashboardDTO data) {
        // --- 1. AFFICHAGE DU NOM DU DOCTEUR ---
        String nomAafficher = "";

        if (data.getMedecinNom() != null && !data.getMedecinNom().isEmpty()) {
            nomAafficher = data.getMedecinNom();
        }
        else if (data.getAdminName() != null && !data.getAdminName().isEmpty()) {
            // Si on est en mode admin mais qu'on veut l'affichage Dr.
            nomAafficher = data.getAdminName();
        }

        // Mise à jour du label avec le format imposé : Bonjour, Dr. Nom
        if (!nomAafficher.isEmpty()) {
            lblWelcome.setText("Bonjour, Dr. " + nomAafficher);
        } else {
            lblWelcome.setText("Bonjour, Dr.");
        }

        // --- 2. MISE À JOUR DES STATISTIQUES ---
        lblRdvCount.setText(String.valueOf(data.getRdvToday()));
        lblPatientCount.setText(String.valueOf(data.getTotalPatients()));
        lblRevenueCount.setText(String.format("%.2f DH", data.getRevenuesToday()));

        // --- 3. MISE À JOUR DU TABLEAU DES RDV ---
        tableModel.setRowCount(0);
        if (data.getProchainsRDV() != null && !data.getProchainsRDV().isEmpty()) {
            for (RDV rdv : data.getProchainsRDV()) {
                tableModel.addRow(new Object[]{
                        rdv.getHeure() != null ? rdv.getHeure().toString() : "--:--",
                        rdv.getPatientNomComplet(),
                        rdv.getMotif()
                });
            }
        } else {
            tableModel.addRow(new Object[]{"-", "Aucun rendez-vous aujourd'hui", "-"});
        }

        // --- 4. RAFRAÎCHISSEMENT ---
        updateDateTime();
        repaint();
    }

    private void initUI() {
        // --- 1. HEADER SECTION ---
        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setOpaque(false);

        JPanel welcomeBox = new JPanel(new GridLayout(2, 1));
        welcomeBox.setOpaque(false);

        // ✅ Initialisation de la référence lblWelcome
        lblWelcome = new JLabel("Bonjour, Dr. ");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JLabel lblSub = new JLabel("Voici l'état de votre cabinet pour aujourd'hui");
        lblSub.setForeground(Color.GRAY);
        welcomeBox.add(lblWelcome);
        welcomeBox.add(lblSub);

        JPanel dateTimeBox = new JPanel(new GridLayout(2, 1, 0, 5));
        dateTimeBox.setOpaque(false);
        lblDate = new JLabel("", SwingConstants.RIGHT);
        lblDate.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTime = new JLabel("", SwingConstants.RIGHT);
        lblTime.setFont(new Font("Monospaced", Font.BOLD, 14));
        dateTimeBox.add(lblDate);
        dateTimeBox.add(lblTime);

        headerWrapper.add(welcomeBox, BorderLayout.WEST);
        headerWrapper.add(dateTimeBox, BorderLayout.EAST);

        // --- 2. STATS CARDS SECTION ---
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setBorder(new EmptyBorder(20, 0, 20, 0));

        lblRdvCount = new JLabel("0");
        lblPatientCount = new JLabel("0");
        lblRevenueCount = new JLabel("0,00 DH");

        statsRow.add(createStatCard("RDV DU JOUR", lblRdvCount, new Color(52, 152, 219)));
        statsRow.add(createStatCard("PATIENTS TOTAUX", lblPatientCount, new Color(46, 204, 113)));
        statsRow.add(createStatCard("REVENUS J.", lblRevenueCount, new Color(155, 89, 182)));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(headerWrapper, BorderLayout.NORTH);
        northPanel.add(statsRow, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // --- 3. MAIN CONTENT ---
        JPanel rdvBox = createStyledPanel("PROCHAINS RENDEZ-VOUS");
        String[] columns = {"Heure", "Patient", "Motif"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable tableRDV = new JTable(tableModel);
        tableRDV.setRowHeight(35);
        tableRDV.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        rdvBox.add(new JScrollPane(tableRDV), BorderLayout.CENTER);

        add(rdvBox, BorderLayout.CENTER);
    }

    // ... (méthodes createStatCard, createStyledPanel et updateDateTime restent identiques)

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        p.add(lblTitle, BorderLayout.NORTH);
        return p;
    }

    private void updateDateTime() {
        lblDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
        lblTime.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
    }
}