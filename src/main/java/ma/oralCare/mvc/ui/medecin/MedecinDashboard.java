package ma.oralCare.mvc.ui.medecin;

import javax.swing.*;
import java.awt.*;

public class MedecinDashboard extends JPanel {

    public MedecinDashboard() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // --- EN-TÊTE ---
        JPanel header = new JPanel(new GridLayout(1, 2));
        header.setOpaque(false);
        JLabel title = new JLabel("Bonjour, Dr. Alami");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        JLabel date = new JLabel("Mardi 06 Janvier 2026", SwingConstants.RIGHT);
        header.add(title);
        header.add(date);
        add(header, BorderLayout.NORTH);

        // --- GRILLE DE STATS (Cartes) ---
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setOpaque(false);
        statsGrid.add(createStatCard("RDV du jour", "12", new Color(41, 128, 185)));
        statsGrid.add(createStatCard("Urgences", "2", new Color(231, 76, 60)));
        statsGrid.add(createStatCard("Consultations", "8", new Color(46, 204, 113)));
        statsGrid.add(createStatCard("Nouveaux Patients", "3", new Color(155, 89, 182)));

        // --- ZONE CENTRALE : AGENDA RAPIDE & NOTES ---
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setOpaque(false);

        // Table des prochains patients
        JPanel pnlNext = new JPanel(new BorderLayout());
        pnlNext.setBorder(BorderFactory.createTitledBorder("Prochains Rendez-vous"));
        String[] cols = {"Heure", "Patient", "Acte prévu"};
        Object[][] data = {{"14:30", "Meryem Tazi", "Détartrage"}, {"15:15", "Karim Sabri", "Implant"}};
        pnlNext.add(new JScrollPane(new JTable(data, cols)));

        centerPanel.add(statsGrid, BorderLayout.NORTH);
        centerPanel.add(pnlNext, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 100));
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, color));

        JLabel lblTitle = new JLabel("  " + title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblVal.setHorizontalAlignment(SwingConstants.CENTER);
        lblVal.setForeground(new Color(44, 62, 80));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblVal, BorderLayout.CENTER);
        return card;
    }
}