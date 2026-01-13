package ma.oralCare.mvc.ui1.medecin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CertificateView extends JPanel {

    private JTable tableCertificats;
    private DefaultTableModel certModel;
    private JTextArea txtNoteMedecin;
    private JLabel lblDetails;

    private final Color ACCENT_COLOR = new Color(41, 128, 185);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public CertificateView() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 246, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
    }

    private void initComponents() {
        // --- ENTÊTE (Infos Patient issues de la consultation en cours) ---
        JPanel headerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        headerPanel.setOpaque(false);
        headerPanel.add(new JLabel("<html>👤 Patient : <b>-</b></html>"));
        headerPanel.add(new JLabel("<html>📅 Consultation : <b>-</b></html>"));
        headerPanel.add(new JLabel("<html>Statut : <span style='color:green;'>-</span></html>"));
        add(headerPanel, BorderLayout.NORTH);

        // --- CENTRE : LISTE ET ACTIONS ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);

        // Table des Certificats
        JPanel listPanel = createStyledPanel("Liste des Certificats");
        String[] columns = {"N°", "Date Début", "Date Fin", "Médecin", "Type", "Action"};
        certModel = new DefaultTableModel(columns, 0);
        tableCertificats = new JTable(certModel);
        tableCertificats.setRowHeight(30);
        listPanel.add(new JScrollPane(tableCertificats), BorderLayout.CENTER);

        // Barre d'outils
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setOpaque(false);
        JButton btnAdd = new JButton("+ Créer Certificat");
        JButton btnEdit = new JButton("Modifier");
        JButton btnDelete = new JButton("Supprimer");

        stylePrimaryButton(btnAdd);
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);
        listPanel.add(toolbar, BorderLayout.SOUTH);

        // --- BAS : DÉTAILS DU CERTIFICAT SÉLECTIONNÉ ---
        JPanel detailsContent = new JPanel(new BorderLayout(10, 10));
        detailsContent.setOpaque(false);
        detailsContent.setPreferredSize(new Dimension(0, 250));

        JPanel infoPanel = createStyledPanel("📝 Détails Certificat");
        lblDetails = new JLabel("<html>Sélectionnez un certificat pour voir les détails...</html>");
        lblDetails.setVerticalAlignment(SwingConstants.TOP);
        lblDetails.setBorder(new EmptyBorder(10,10,10,10));

        txtNoteMedecin = new JTextArea();
        txtNoteMedecin.setEditable(false);
        txtNoteMedecin.setLineWrap(true);
        txtNoteMedecin.setBackground(new Color(250, 250, 250));

        JScrollPane scrollNote = new JScrollPane(txtNoteMedecin);
        scrollNote.setBorder(BorderFactory.createTitledBorder("Note du Médecin"));

        infoPanel.add(lblDetails, BorderLayout.WEST);
        infoPanel.add(scrollNote, BorderLayout.CENTER);

        JButton btnPrint = new JButton("🖨 Imprimer Certificat");
        styleAccentButton(btnPrint);
        infoPanel.add(btnPrint, BorderLayout.SOUTH);

        mainContent.add(listPanel, BorderLayout.CENTER);
        mainContent.add(infoPanel, BorderLayout.SOUTH);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                title, TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), ACCENT_COLOR));
        return p;
    }

    private void stylePrimaryButton(JButton b) {
        b.setBackground(ACCENT_COLOR);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void styleAccentButton(JButton b) {
        b.setBackground(new Color(46, 204, 113));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}