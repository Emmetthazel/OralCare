package ma.oralCare.mvc.ui.medecin.components;

import javax.swing.*;
import java.awt.*;

public class RadiologiePanel extends JPanel {

    public RadiologiePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Radiographies & Imagerie"));
        setBackground(Color.BLACK); // Style "Négatoscope"

        // Simulation d'une zone d'affichage d'image
        JLabel lblImage = new JLabel(new ImageIcon());
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setText("Sélectionnez un cliché dans l'historique");
        lblImage.setForeground(Color.GRAY);

        // Liste des clichés sur le côté
        DefaultListModel<String> model = new DefaultListModel<>();
        model.addElement("Radio Panoramique - 12/01/2026");
        model.addElement("Rétro-alvéolaire Dent 14 - 05/01/2026");
        JList<String> listRadios = new JList<>(model);
        listRadios.setPreferredSize(new Dimension(200, 0));

        add(new JScrollPane(listRadios), BorderLayout.WEST);
        add(lblImage, BorderLayout.CENTER);
    }
}