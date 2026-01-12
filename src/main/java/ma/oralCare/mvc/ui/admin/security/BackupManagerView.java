package ma.oralCare.mvc.ui.admin.security;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BackupManagerView extends JPanel {
    public BackupManagerView() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("🛡️ Gestion des Sauvegardes");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;

        // Actions basées sur le diagramme
        JButton btnManual = createStyledButton("🚀 Lancer une sauvegarde immédiate", new Color(46, 204, 113));
        JButton btnAuto = createStyledButton("📅 Planifier sauvegarde automatique", new Color(52, 152, 219));
        JButton btnRestore = createStyledButton("⚠️ Restaurer depuis un fichier", new Color(231, 76, 60));

        content.add(btnManual, gbc);
        content.add(btnAuto, gbc);
        content.add(btnRestore, gbc);

        add(content, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(400, 50));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }
}