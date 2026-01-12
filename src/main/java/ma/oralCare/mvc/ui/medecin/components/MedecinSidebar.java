package ma.oralCare.mvc.ui.medecin.components;

import ma.oralCare.mvc.ui.MainFrame;
import javax.swing.*;
import java.awt.*;

public class MedecinSidebar extends JPanel {
    private final Color BG_COLOR = new Color(44, 62, 80);
    private final Color HOVER_COLOR = new Color(52, 73, 94);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);

    public MedecinSidebar(MainFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(250, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Profil Médecin Rapide
        JLabel lblRole = new JLabel("ESPACE PRATICIEN");
        lblRole.setForeground(ACCENT_COLOR);
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblRole);
        add(Box.createVerticalStrut(30));

        // Boutons de navigation
        addMenuBtn("🏠 Dashboard", "DASHBOARD", frame);
        addMenuBtn("📅 Mon Agenda", "AGENDA", frame);
        addMenuBtn("🦷 Consultation", "CONSULTATION", frame);
        addMenuBtn("📂 Patients", "HISTORIQUE", frame);

        add(Box.createVerticalGlue());

        JButton btnLogout = new JButton("🚪 Quitter");
        btnLogout.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(btnLogout);
    }

    private void addMenuBtn(String text, String pageId, MainFrame frame) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setBackground(BG_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.addActionListener(e -> frame.changePage(pageId));
        add(btn);
        add(Box.createVerticalStrut(10));
    }
}