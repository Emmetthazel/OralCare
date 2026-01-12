package ma.oralCare.mvc.ui1;

import javax.swing.*;
import java.awt.*;

public class ViewPanel extends JPanel {
    public ViewPanel(String title, Color bgColor) {
        setLayout(new BorderLayout());

        JPanel content = new JPanel();
        content.setBackground(bgColor);
        content.setLayout(new GridBagLayout());

        JLabel label = new JLabel(title);
        label.setFont(new Font("Serif", Font.ITALIC, 35));
        content.add(label);

        // JScrollPane indispensable pour la réactivité du contenu
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }
}