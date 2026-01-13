package ma.oralCare.service.modules.admin.api;

import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import java.util.List;
import java.util.Map;

public interface UserManagementService {
    // Cette méthode DOIT être présente ici
    Map<String, List<UserStaffDTO>> getStaffHierarchy(String search);
    // Si vous voyez 'createAdmin' ici et que vous n'en avez pas besoin, supprimez-le.
    // Sinon, vous devrez l'implémenter dans le ServiceImpl.
    void deleteUser(String email);
    Utilisateur getUserFullDetails(String email);
    // Dans UserManagementService.java
    String generateAndSaveNewPassword(String email);
    void updateUserBasicInfo(String email, String nom, String prenom, String tel, String cin, String ville);
    String createStaffMember(String cabinetName, String role, Map<String, String> data);
    // Ajout de la méthode findByEmail
    Utilisateur findByEmail(String email);
}