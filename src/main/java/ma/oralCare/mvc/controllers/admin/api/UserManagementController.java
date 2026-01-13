package ma.oralCare.mvc.controllers.admin.api;

import ma.oralCare.entities.dossierMedical.Acte;
import ma.oralCare.entities.dossierMedical.Medicament;
import ma.oralCare.entities.patient.Antecedent;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import java.util.List;
import java.util.Map;

public interface UserManagementController {
    Map<String, List<UserStaffDTO>> loadHierarchy(String search);
    Utilisateur getUserDetails(String email);
    void deleteUser(String email);
    String generateAndSaveNewPassword(String email);
    // ACTION : Ouvre la popup de confirmation et affiche le nouveau MDP
    void updateUser(String email, String nom, String prenom, String tel, String cin, String ville);
    String addNewUser(String cabinetName, String role, Map<String, String> data) throws Exception;
    String resetPassword(String email);
    void addNewCabinet(Map<String, String> data) throws Exception;
    // Nouvelle méthode pour obtenir le mot de passe en clair
    String getUserPassword(String email);
}