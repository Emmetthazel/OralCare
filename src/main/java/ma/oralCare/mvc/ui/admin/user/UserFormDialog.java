package ma.oralCare.mvc.ui.admin.user;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UserFormDialog extends JDialog {

    private JTextField txtNom, txtPrenom, txtLogin, txtEmail, txtCIN;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRole;
    private boolean succeeded = false;

    public UserFormDialog(Frame parent) {
        super(parent, "Ajouter un Utilisateur", true); // true = Modal

        setLayout(new BorderLayout());
        setSize(400, 500);
        setLocationRelativeTo(parent);

        // --- PANNEAU DE FORMULAIRE ---
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 15));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Nom :"));
        txtNom = new JTextField();
        formPanel.add(txtNom);

        formPanel.add(new JLabel("Prénom :"));
        txtPrenom = new JTextField();
        formPanel.add(txtPrenom);

        formPanel.add(new JLabel("CIN :"));
        txtCIN = new JTextField();
        formPanel.add(txtCIN);

        formPanel.add(new JLabel("Login :"));
        txtLogin = new JTextField();
        formPanel.add(txtLogin);

        formPanel.add(new JLabel("Mot de passe :"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        formPanel.add(new JLabel("Rôle :"));
        comboRole = new JComboBox<>(new String[]{"Médecin", "Secrétaire", "Admin"});
        formPanel.add(comboRole);

        add(formPanel, BorderLayout.CENTER);

        // --- PANNEAU DE BOUTONS ---
        JPanel bp = new JPanel();
        JButton btnSave = new JButton("Enregistrer");
        JButton btnCancel = new JButton("Annuler");

        btnSave.addActionListener(e -> {
            // Ici, vous ajouterez plus tard la logique pour sauvegarder en BDD
            if(validateForm()) {
                succeeded = true;
                dispose();
            }
        });

        btnCancel.addActionListener(e -> dispose());

        bp.add(btnSave);
        bp.add(btnCancel);
        add(bp, BorderLayout.SOUTH);
    }

    private boolean validateForm() {
        if (txtNom.getText().isEmpty() || txtLogin.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir les champs obligatoires", "Erreur", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}