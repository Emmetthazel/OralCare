package ma.oralCare.mvc.ui.dashboard;

import ma.oralCare.mvc.ui.palette.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Page de gestion d'agenda médecin (Figure 11).
 * Le bouton d'action est désormais placé sous le titre.
 */
public class AgendaMedecinPanel extends JPanel {

    public AgendaMedecinPanel() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(new EmptyBorder(0, 0, 20, 0));

        // --- EN-TÊTE (Modifié pour alignement vertical) ---
        JPanel headerPanel = createHeader();

        // --- FILTRES ET TABLEAU ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setOpaque(false);

        // Zone des filtres (Demain, Aujourd'hui, Hier)
        JPanel filterPanel = createFilterPanel();

        // Tableau des rendez-vous
        String[] columns = {"Nom", "Prénom", "Date Rendez-vous", "Heure Rendez-vous", "Acte"};
        Object[][] data = {
                {"Allami", "Youness", "11-10-2024", "9:00 AM", "Dent de sagesse"}
        };

        CustomTable table = new CustomTable(columns, data);
        table.setShowVerticalLines(true);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(ColorPalette.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createHeader() {
        // Changement vers BoxLayout pour empiler verticalement
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Agenda Médecin");
        titleLabel.setFont(FontsPalette.TITLE);
        titleLabel.setForeground(ColorPalette.TEXT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnAjouter = ButtonPalette.primary("+ Ajouter patient");
        btnAjouter.setMaximumSize(new Dimension(180, 40));
        btnAjouter.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemblage avec espacement de 15px
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(btnAjouter);

        return panel;
    }

    private JPanel createFilterPanel() {
        // Conserve le subtitle "les rendez-vous" et les boutons de filtre
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        JLabel subtitle = new JLabel("les rendez-vous");
        subtitle.setFont(FontsPalette.BUTTON);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnGroup.setOpaque(false);
        btnGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnGroup.add(createFilterButton("Demain"));
        btnGroup.add(createFilterButton("Aujourd'hui"));
        btnGroup.add(createFilterButton("Hier"));

        container.add(subtitle);
        container.add(Box.createVerticalStrut(10));
        container.add(btnGroup);

        return container;
    }

    private JButton createFilterButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FontsPalette.LABEL);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new LineBorder(Color.BLACK, 1));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(100, 30));
        return btn;
    }
}