package ma.oralCare.mvc.ui1;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FooterPanel : Barre d'état réactive située au bas de l'application.
 * Fournit des informations système, l'heure en temps réel et des notifications de statut.
 */
public class FooterPanel extends JPanel {

    private JLabel lblStatus;
    private JLabel lblDateTime;
    private JLabel lblVersion;

    // Couleurs et styles
    private final Color BG_COLOR = new Color(240, 240, 240);
    private final Color TEXT_COLOR = new Color(80, 80, 80);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color ERROR_COLOR = new Color(192, 57, 43);

    public FooterPanel() {
        // Configuration du conteneur
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 35));
        setBackground(BG_COLOR);
        setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        // --- SECTION GAUCHE : Statut et Version ---
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        leftPanel.setOpaque(false);

        lblStatus = new JLabel("● Système prêt");
        lblStatus.setForeground(SUCCESS_COLOR);
        lblStatus.setFont(new Font("SansSerif", Font.BOLD, 11));

        lblVersion = new JLabel("| v1.2.0-stable");
        lblVersion.setForeground(Color.GRAY);
        lblVersion.setFont(new Font("SansSerif", Font.PLAIN, 10));

        leftPanel.add(lblStatus);
        leftPanel.add(lblVersion);

        // --- SECTION DROITE : Horloge ---
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 8));
        rightPanel.setOpaque(false);

        lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblDateTime.setForeground(TEXT_COLOR);
        updateClock(); // Initialisation immédiate
        rightPanel.add(lblDateTime);

        // Assemblage final
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);

        // Timer Swing pour mettre à jour l'horloge chaque seconde
        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();
    }

    /**
     * Met à jour le message de statut dynamiquement.
     * @param message Le texte à afficher
     * @param isError Si vrai, le texte sera rouge, sinon vert.
     */
    public void setStatus(String message, boolean isError) {
        lblStatus.setText("● " + message);
        lblStatus.setForeground(isError ? ERROR_COLOR : SUCCESS_COLOR);

        if (!isError && !message.equals("Système prêt")) {
            Timer resetTimer = new Timer(5000, e -> setStatus("Système prêt", false));
            resetTimer.setRepeats(false);
            resetTimer.start();
        }
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd MMMM yyyy - HH:mm:ss");
        lblDateTime.setText(sdf.format(new Date()));
    }
}