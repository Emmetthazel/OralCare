package ma.oralCare.mvc.ui.secretaire;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(new Color(245, 246, 250));

        // --- TITRE ---
        JLabel title = new JLabel("Tableau de Bord - Statistiques du Cabinet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // --- GRILLE DE CARTES (Statistiques) ---
        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 20));
        statsGrid.setOpaque(false);

        // Cartes basées sur vos tables SQL (Patient, RDV, Facture, SituationFinanciere)
        statsGrid.add(createStatCard("Patients Total", "128", new Color(52, 152, 219)));
        statsGrid.add(createStatCard("RDV Aujourd'hui", "12", new Color(155, 89, 182)));
        statsGrid.add(createStatCard("Recettes (DH)", "4,500", new Color(46, 204, 113)));
        statsGrid.add(createStatCard("Crédits Clients", "1,200", new Color(231, 76, 60)));

        // --- ZONE CENTRALE (Aperçu rapide) ---
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerPanel.setOpaque(false);

        // Liste simplifiée des prochains RDV
        JPanel pnlNextRDV = new JPanel(new BorderLayout());
        pnlNextRDV.setBorder(BorderFactory.createTitledBorder("Prochains Rendez-vous"));
        String[] sampleRDV = {"10:00 - Ahmed Benani", "11:30 - Sara Idrissi", "15:00 - Karim Tazi"};
        JList<String> listRDV = new JList<>(sampleRDV);
        pnlNextRDV.add(new JScrollPane(listRDV));

        // Alertes (ex: Factures en retard / OVERDUE selon SQL)
        JPanel pnlAlerts = new JPanel(new BorderLayout());
        pnlAlerts.setBorder(BorderFactory.createTitledBorder("Alertes Paiements (OVERDUE)"));
        String[] sampleAlerts = {"Facture #F-102 : Retard de 5 jours", "Facture #F-105 : Crédit élevé"};
        JList<String> listAlerts = new JList<>(sampleAlerts);
        listAlerts.setForeground(Color.RED);
        pnlAlerts.add(new JScrollPane(listAlerts));

        centerPanel.add(pnlNextRDV);
        centerPanel.add(pnlAlerts);

        JPanel mainContent = new JPanel(new BorderLayout(0, 30));
        mainContent.setOpaque(false);
        mainContent.add(statsGrid, BorderLayout.NORTH);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createMatteBorder(0, 0, 5, 0, color));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblValue.setForeground(color);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(20));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(20));

        return card;
    }
}