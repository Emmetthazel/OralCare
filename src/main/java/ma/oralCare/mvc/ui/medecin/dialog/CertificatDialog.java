package ma.oralCare.mvc.ui.medecin.dialog;

import javax.swing.*;
import java.awt.*;

public class CertificatDialog extends JDialog {
    private JSpinner spinDuree = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
    private JTextArea txtNote = new JTextArea(5, 25);
    private boolean validated = false;

    public CertificatDialog(Frame parent) {
        super(parent, "Générer un Certificat Médical", true);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(0, 1, 5, 5));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        form.add(new JLabel("Durée de l'arrêt (jours) :"));
        form.add(spinDuree);
        form.add(new JLabel("Note / Justification :"));
        form.add(new JScrollPane(txtNote));

        JButton btnPrint = new JButton("Valider et Imprimer");
        btnPrint.setBackground(new Color(46, 204, 113));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.addActionListener(e -> { validated = true; setVisible(false); });

        add(form, BorderLayout.CENTER);
        add(btnPrint, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isValidated() { return validated; }
}