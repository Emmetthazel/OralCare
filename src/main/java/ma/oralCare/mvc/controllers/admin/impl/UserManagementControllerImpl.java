package ma.oralCare.mvc.controllers.admin.impl;

import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.controllers.admin.api.UserManagementController;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.service.modules.admin.api.UserManagementService;
import ma.oralCare.service.modules.cabinet.api.CabinetManagementService;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur gérant les interactions entre la vue UserListView et les services métier.
 * Centralise la logique de dialogue utilisateur et la délégation aux services.
 */
public class UserManagementControllerImpl implements UserManagementController {

    private final UserManagementService userService;
    private final CabinetManagementService cabinetService;

    /**
     * Constructeur unique pour l'injection des dépendances.
     */
    public UserManagementControllerImpl(UserManagementService userService, CabinetManagementService cabinetService) {
        this.userService = userService;
        this.cabinetService = cabinetService;
    }

    // =========================================================================
    //                            GESTION DES CABINETS
    // =========================================================================

    @Override
    public void addNewCabinet(Map<String, String> data) throws Exception {
        // 1. Construction de l'objet Adresse (Value Object / Embedded)
        Adresse adresse = Adresse.builder()
                .numero(data.get("numero"))
                .rue(data.get("rue"))
                .codePostal(data.get("codePostal"))
                .ville(data.get("ville"))
                .pays(data.get("pays"))
                .complement(data.get("complement"))
                .build();

        // 2. Construction du Cabinet avec SuperBuilder (hérite de BaseEntity)
        CabinetMedicale cabinet = CabinetMedicale.builder()
                .nom(data.get("nom"))
                .email(data.get("email"))
                .tel1(data.get("tel1"))
                .adresse(adresse)
                .description(data.get("description"))
                .build();

        // 3. Appel du service de gestion des cabinets
        cabinetService.createCabinet(cabinet);
    }

    // =========================================================================
    //                            GESTION DU STAFF
    // =========================================================================

    @Override
    public Map<String, List<UserStaffDTO>> loadHierarchy(String search) {
        return userService.getStaffHierarchy(search);
    }

    @Override
    public String addNewUser(String cabinetName, String role, Map<String, String> data) {
        try {
            return userService.createStaffMember(cabinetName, role, data);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création : " + e.getMessage());
        }
    }

    @Override
    public void updateUser(String email, String nom, String prenom, String tel, String cin, String ville) {
        userService.updateUserBasicInfo(email, nom, prenom, tel, cin, ville);
    }

    @Override
    public void deleteUser(String email) {
        if (email == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Souhaitez-vous vraiment supprimer l'utilisateur : \n" + email + " ?",
                "⚠️ Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                userService.deleteUser(email);
                JOptionPane.showMessageDialog(null, "Utilisateur supprimé avec succès.");
            } catch (Exception e) {
                showError("Erreur de suppression", e.getMessage());
            }
        }
    }

    // =========================================================================
    //                            SÉCURITÉ & DÉTAILS
    // =========================================================================

    @Override
    public Utilisateur getUserDetails(String email) {
        return userService.getUserFullDetails(email);
    }

    @Override
    public String generateAndSaveNewPassword(String email) {
        return userService.generateAndSaveNewPassword(email);
    }

    @Override
    public String resetPassword(String email) {
        if (email == null || email.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Sélectionnez un utilisateur.", "Sélection requise", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            Utilisateur user = userService.getUserFullDetails(email);
            String oldPwd = user.getMotDePass();

            int confirm = JOptionPane.showConfirmDialog(null,
                    "Réinitialiser le mot de passe pour " + email + " ?",
                    "🔐 Sécurité", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String newPwd = userService.generateAndSaveNewPassword(email);
                showPasswordResetSuccess(oldPwd, newPwd);
                return newPwd;
            }
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
        return null;
    }

    // =========================================================================
    //                            MÉTHODES PRIVÉES (UI)
    // =========================================================================

    private void showPasswordResetSuccess(String oldPwd, String newPwd) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        JTextField newField = new JTextField(newPwd);
        newField.setEditable(false);
        newField.setFont(new Font("Monospaced", Font.BOLD, 16));
        newField.setForeground(new Color(39, 174, 96));

        panel.add(new JLabel("Ancien : " + oldPwd));
        panel.add(new JLabel("Nouveau mot de passe généré :"));
        panel.add(newField);

        JOptionPane.showMessageDialog(null, panel, "Réinitialisation réussie", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}