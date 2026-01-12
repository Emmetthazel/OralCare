package ma.oralCare.mvc.ui.medecin.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SchemaDentairePanel extends JPanel {
    public SchemaDentairePanel(ActionListener dentListener) {
        setLayout(new GridLayout(2, 1, 0, 10));
        setBackground(Color.WHITE);

        // Mâchoire Supérieure (18-11, 21-28)
        JPanel top = new JPanel(new FlowLayout());
        top.setOpaque(false);
        for(int i=18; i>=11; i--) top.add(createTooth(i, dentListener));
        for(int i=21; i<=28; i++) top.add(createTooth(i, dentListener));

        // Mâchoire Inférieure (48-41, 31-38)
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.setOpaque(false);
        for(int i=48; i>=41; i--) bottom.add(createTooth(i, dentListener));
        for(int i=31; i<=38; i++) bottom.add(createTooth(i, dentListener));

        add(top);
        add(bottom);
    }

    private JButton createTooth(int num, ActionListener listener) {
        JButton b = new JButton(String.valueOf(num));
        b.setPreferredSize(new Dimension(40, 50));
        b.setFont(new Font("Arial", Font.PLAIN, 10));
        b.setActionCommand(String.valueOf(num));
        b.addActionListener(listener);
        return b;
    }
}