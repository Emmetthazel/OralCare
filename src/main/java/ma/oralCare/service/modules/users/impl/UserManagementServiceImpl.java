package ma.oralCare.service.modules.users.impl;

import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.users.Admin;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.users.api.*;
import ma.oralCare.repository.modules.users.impl.*;
import ma.oralCare.service.modules.users.api.UserManagementService;
import ma.oralCare.service.modules.users.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Service de gestion des comptes utilisateurs (Admin, Médecin, Secrétaire).
 *
 * ATTENTION : ici, on effectue un simple mapping DTO <-> entités,
 * sans logique de sécurité avancée, en restant cohérent avec la couche repository.
 */
public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final MedecinRepository medecinRepository;
    private final SecretaireRepository secretaireRepository;
    private final RoleRepository roleRepository;

    public UserManagementServiceImpl() {
        this(
                new UtilisateurRepositoryImpl(),
                new AdminRepositoryImpl(),
                new MedecinRepositoryImpl(),
                new SecretaireRepositoryImpl(),
                new RoleRepositoryImpl()
        );
    }

    public UserManagementServiceImpl(UtilisateurRepository utilisateurRepository,
                                     AdminRepository adminRepository,
                                     MedecinRepository medecinRepository,
                                     SecretaireRepository secretaireRepository,
                                     RoleRepository roleRepository) {
        this.utilisateurRepository = Objects.requireNonNull(utilisateurRepository);
        this.adminRepository = Objects.requireNonNull(adminRepository);
        this.medecinRepository = Objects.requireNonNull(medecinRepository);
        this.secretaireRepository = Objects.requireNonNull(secretaireRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
    }

    // -------------------------------------------------------------------------
    // Création comptes
    // -------------------------------------------------------------------------

    @Override
    public UserAccountDto createAdmin(CreateAdminRequest request) {
        Objects.requireNonNull(request);
        Admin admin = Admin.builder()
                .nom(request.nom())
                .email(request.email())
                .login(request.login())
                .motDePass(request.motDePasse())
                .build();
        adminRepository.create(admin);
        return toDto(admin);
    }

    @Override
    public UserAccountDto createMedecin(CreateMedecinRequest request) {
        Objects.requireNonNull(request);
        Medecin medecin = Medecin.builder()
                .nom(request.nom())
                .email(request.email())
                .login(request.login())
                .motDePass(request.motDePasse())
                .build();
        medecinRepository.create(medecin);
        return toDto(medecin);
    }

    @Override
    public UserAccountDto createSecretaire(CreateSecretaireRequest request) {
        Objects.requireNonNull(request);
        Secretaire secretaire = Secretaire.builder()
                .nom(request.nom())
                .email(request.email())
                .login(request.login())
                .motDePass(request.motDePasse())
                .build();
        secretaireRepository.create(secretaire);
        return toDto(secretaire);
    }

    // -------------------------------------------------------------------------
    // Consultation & recherche
    // -------------------------------------------------------------------------

    @Override
    public UserAccountDto getUserById(Long id) {
        if (id == null) return null;
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        return opt.map(this::toDto).orElse(null);
    }

    @Override
    public List<UserAccountDto> getAllUsers() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        List<UserAccountDto> out = new ArrayList<>();
        for (Utilisateur u : utilisateurs) {
            out.add(toDto(u));
        }
        return out;
    }

    @Override
    public List<UserAccountDto> searchUsersByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllByNomContaining(keyword);
        List<UserAccountDto> out = new ArrayList<>();
        for (Utilisateur u : utilisateurs) {
            out.add(toDto(u));
        }
        return out;
    }

    // -------------------------------------------------------------------------
    // Mise à jour profil
    // -------------------------------------------------------------------------

    @Override
    public UserAccountDto updateUserProfile(UpdateUserProfileRequest request) {
        Objects.requireNonNull(request);
        Utilisateur utilisateur = utilisateurRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable pour id=" + request.id()));

        utilisateur.setNom(request.nom());
        utilisateur.setEmail(request.email());
        utilisateur.setTel(request.tel());
        utilisateur.setSexe(request.sexe());
        utilisateur.setDateNaissance(request.dateNaissance());

        utilisateurRepository.update(utilisateur);
        return toDto(utilisateur);
    }

    // -------------------------------------------------------------------------
    // Gestion des rôles
    // -------------------------------------------------------------------------

    @Override
    public void assignRoleToUser(Long utilisateurId, RoleLibelle roleType) {
        Objects.requireNonNull(utilisateurId);
        Objects.requireNonNull(roleType);

        roleRepository.findByLibelle(roleType.name()).ifPresent(role ->
                utilisateurRepository.addRoleToUtilisateur(utilisateurId, role.getIdEntite())
        );
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, RoleLibelle roleType) {
        Objects.requireNonNull(utilisateurId);
        Objects.requireNonNull(roleType);

        roleRepository.findByLibelle(roleType.name()).ifPresent(role ->
                utilisateurRepository.removeRoleFromUtilisateur(utilisateurId, role.getIdEntite())
        );
    }

    // -------------------------------------------------------------------------
    // Mapping utilitaire
    // -------------------------------------------------------------------------

    private UserAccountDto toDto(Utilisateur u) {
        return new UserAccountDto(
                u.getIdEntite(),
                u.getNom(),
                u.getEmail(),
                u.getLogin(),
                u.getSexe(),
                u.getDateNaissance(),
                // Conversion minimale : on ne charge pas tous les rôles / privilèges ici
                Set.of(),
                Set.of()
        );
    }
}


