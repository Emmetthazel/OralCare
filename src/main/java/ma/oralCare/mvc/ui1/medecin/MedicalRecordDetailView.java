package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MedicalRecordDetailView extends JPanel {

    private JLabel lblPatientTitle;
    private JLabel lblDetailsHTML;
    private final Color TEXT_COLOR = new Color(44, 62, 80);
    private final Color ACCENT_COLOR = new Color(41, 128, 185);


    public MedicalRecordDetailView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- ENTÊTE DU DOSSIER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        lblPatientTitle = new JLabel("📁 Dossier Médical : (Aucun patient sélectionné)");
        lblPatientTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPatientTitle.setForeground(ACCENT_COLOR);
        headerPanel.add(lblPatientTitle, BorderLayout.WEST);

        JButton btnBack = new JButton("⬅ Retour à la liste");
        styleSecondaryButton(btnBack);
        headerPanel.add(btnBack, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- CORPS DU DOSSIER (GRILLE) ---
        JPanel mainContent = new JPanel(new GridLayout(1, 2, 20, 0));
        mainContent.setOpaque(false);

        // Panneau Gauche : Infos & Antécédents
        JPanel leftPanel = createStyledPanel("Résumé du Patient");
        lblDetailsHTML = new JLabel("<html><body style='padding:10px;'>Sélectionnez un patient dans la liste...</body></html>");
        lblDetailsHTML.setVerticalAlignment(SwingConstants.TOP);
        leftPanel.add(new JScrollPane(lblDetailsHTML), BorderLayout.CENTER);

        // Panneau Droite : Historique & Actions rapides
        JPanel rightPanel = createStyledPanel("Actions et Suivi");
        setupRightPanel(rightPanel);

        mainContent.add(leftPanel);
        mainContent.add(rightPanel);
        add(mainContent, BorderLayout.CENTER);
    }

    public void loadPatientData(String nom, String prenom, String cin, String assurance, String sexe,
                                String dateCreation, double totalActes, double totalPaye, double credit) {

        lblPatientTitle.setText("📁 Dossier Médical : " + nom.toUpperCase() + " " + prenom);

        String creditColor = (credit > 0) ? "#e74c3c" : "#27ae60";

        // Utilisation de styles CSS inline pour contrôler l'espacement et empêcher les retours à la ligne
        String htmlText = "<html>" +
                "<body style='font-family: Segoe UI; font-size: 11pt; color: #2c3e50; padding: 15px;'>" +

                "<h2>📋 Identification du Patient</h2>" +
                "<table border='0' cellspacing='5'>" +
                "<tr><td width='100'><b>CIN :</b></td><td width='250'>" + cin + "</td></tr>" +
                "<tr><td><b>Sexe :</b></td><td>" + sexe + "</td></tr>" +
                "<tr><td><b>Assurance :</b></td><td><span style='color:#2980b9;'><b>" + assurance + "</b></span></td></tr>" +
                "<tr><td><b>Ouvert le :</b></td><td>" + dateCreation + "</td></tr>" +
                "</table>" +

                "<div style='margin: 15px 0; border-top: 1px solid #eeeeee;'></div>" +

                "<h2>⚠️ Antécédents Médicaux</h2>" +
                "<ul style='margin-left: 15px;'>" +
                "<li><b>Diabète</b> <small>(Risque: HIGH)</small></li>" +
                "<li><b>Allergie Pénicilline</b> <small>(Risque: MEDIUM)</small></li>" +
                "</ul>" +

                "<div style='margin: 15px 0; border-top: 1px solid #eeeeee;'></div>" +

                "<h2>💰 Situation Financière</h2>" +
                "<table border='0' cellspacing='5'>" +
                "<tr><td width='120'><b>Total des actes :</b></td><td width='200'>" + String.format("%.2f", totalActes) + " DH</td></tr>" +
                "<tr><td><b>Total payé :</b></td><td><span style='color:#27ae60;'>" + String.format("%.2f", totalPaye) + " DH</span></td></tr>" +
                "<tr><td><b>Crédit restant :</b></td><td><span style='color:" + creditColor + ";'><b>" + String.format("%.2f", credit) + " DH</b></span></td></tr>" +
                "</table>" +

                "</body></html>";

        lblDetailsHTML.setText(htmlText);
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));
        return p;
    }

    private void setupRightPanel(JPanel p) {
        JPanel btnGrid = new JPanel(new GridLayout(4, 1, 10, 10));
        btnGrid.setOpaque(false);
        btnGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnNewConsult = new JButton("➕ Nouvelle Consultation");
        JButton btnOrdo = new JButton("📄 Historique Ordonnances");
        JButton btnRadio = new JButton("🖼️ Imagerie / Radios");
        JButton btnFin = new JButton("💰 Détails Paiements");

        stylePrimaryButton(btnNewConsult);
        styleSecondaryButton(btnOrdo);
        styleSecondaryButton(btnRadio);
        styleSecondaryButton(btnFin);

        btnGrid.add(btnNewConsult);
        btnGrid.add(btnOrdo);
        btnGrid.add(btnRadio);
        btnGrid.add(btnFin);

        p.add(btnGrid, BorderLayout.NORTH);
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(41, 128, 185));
        b.setForeground(TEXT_COLOR);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setForeground(TEXT_COLOR);
        b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}