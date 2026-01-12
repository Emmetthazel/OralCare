package ma.oralCare.mvc.ui.medecin.dialog;

import javax.swing.*;
import java.awt.*;

public class RadiographieDialog extends JDialog {
    public RadiographieDialog(Frame parent) {
        super(parent, "Radiographies du Patient", true);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Simulation d'une zone d'affichage d'image (Table imagerie/radio)
        JPanel pnlImage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.drawString("Aperçu de la Radio (X-Ray View)", 150, 180);
            }
        };

        JButton btnImport = new JButton("Importer une nouvelle radio");
        add(new JLabel("Historique des clichés : 12/01/2026 - Panoramique"), BorderLayout.NORTH);
        add(pnlImage, BorderLayout.CENTER);
        add(btnImport, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);
    }
}