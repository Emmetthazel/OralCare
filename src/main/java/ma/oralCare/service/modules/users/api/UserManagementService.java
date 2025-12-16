package ma.oralCare.service.modules.users.api;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.service.modules.users.dto.*;

import java.util.List;

public interface UserManagementService {

    // ----- Création comptes -----

    UserAccountDto createAdmin(CreateAdminRequest request);

    UserAccountDto createMedecin(CreateMedecinRequest request);

    UserAccountDto createSecretaire(CreateSecretaireRequest request);


    // ----- Consultation & recherche -----

    UserAccountDto getUserById(Long id);

    List<UserAccountDto> getAllUsers();

    List<UserAccountDto> searchUsersByKeyword(String keyword); // nom, email, login


    // ----- Mise à jour profil -----

    UserAccountDto updateUserProfile(UpdateUserProfileRequest request);


    // ----- Gestion des rôles -----

    void assignRoleToUser(Long utilisateurId, RoleLibelle roleType);

    void removeRoleFromUser(Long utilisateurId, RoleLibelle roleType);
}
