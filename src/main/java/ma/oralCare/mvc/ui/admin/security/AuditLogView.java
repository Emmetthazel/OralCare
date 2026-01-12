package ma.oralCare.mvc.ui.admin.security;

import ma.oralCare.mvc.controllers.admin.api.AdminDashboardController; // Import nécessaire
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AuditLogView extends JPanel {
    private final JTextArea logArea;
    private final AdminDashboardController controller;

    public AuditLogView(AdminDashboardController controller) { // Ajout du contrôleur au constructeur
        this.controller = controller;
        setLayout(new BorderLayout(0, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("📊 Journaux d'Audit & Logs");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filters.setOpaque(false);
        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.addActionListener(e -> refreshLogs()); // Action manuelle
        filters.add(btnRefresh);
        add(filters, BorderLayout.SOUTH);

        refreshLogs(); // Premier chargement
    }

    // ✅ La méthode que MainFrame appelle
    public void refreshLogs() {
        // Ici, vous devriez normalement appeler votre contrôleur pour avoir les logs réels
        // Exemple simulant l'affichage :
        logArea.append("[UPDATE] Logs rafraîchis à " + java.time.LocalTime.now() + "\n");
    }
}