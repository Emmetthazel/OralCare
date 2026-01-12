package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.ColorPalette;
import ma.oralCare.mvc.ui.palette.FontsPalette;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class StatsPanel extends JPanel {

    private StatCard cardTotalPatients;
    private StatCard cardTodayVisits;
    private StatCard cardTotalAppointments;

    public StatsPanel() {
        setLayout(new GridLayout(1, 3, 25, 0));
        setOpaque(false);
        // Hauteur standard pour les 3 cases de statistiques
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        cardTotalPatients = new StatCard("Total Patients", "0", "Inscrits", ColorPalette.PRIMARY);
        cardTodayVisits = new StatCard("Visites du Jour", "0", "Aujourd'hui", ColorPalette.ACTION_GREEN);
        cardTotalAppointments = new StatCard("Rendez-vous", "0", "Total prévus", ColorPalette.ACTION_YELLOW);

        add(cardTotalPatients);
        add(cardTodayVisits);
        add(cardTotalAppointments);
    }

    public void updateValues(String patients, String visits, String appts) {
        cardTotalPatients.setValue(patients);
        cardTodayVisits.setValue(visits);
        cardTotalAppointments.setValue(appts);

        this.revalidate();
        this.repaint();
    }

    // --- Classe interne StatCard centralisée ---
    private static class StatCard extends JPanel {
        private JLabel valueLabel;

        public StatCard(String title, String value, String sub, Color accent) {
            setLayout(new BorderLayout(10, 10));
            setBackground(ColorPalette.CARD_BG);
            setBorder(new EmptyBorder(25, 25, 25, 25));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(FontsPalette.LABEL);
            titleLabel.setForeground(ColorPalette.SECONDARY_TEXT);

            valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
            valueLabel.setForeground(accent);

            JLabel subLabel = new JLabel(sub);
            subLabel.setFont(FontsPalette.LABEL);
            subLabel.setForeground(ColorPalette.SECONDARY_TEXT);

            add(titleLabel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
            add(subLabel, BorderLayout.SOUTH);
        }

        public void setValue(String newValue) {
            valueLabel.setText(newValue);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
            g2.setColor(ColorPalette.CARD_BORDER);
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
            g2.dispose();
        }
    }
}