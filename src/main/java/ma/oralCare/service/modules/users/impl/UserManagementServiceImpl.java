package ma.oralCare.service.modules.users.impl;

import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.enums.RoleLibelle;
import ma.oralCare.entities.users.Admin;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.repository.modules.users.api.*;
import ma.oralCare.repository.modules.users.impl.*;
import ma.oralCare.service.modules.auth.api.PasswordEncoder;
import ma.oralCare.service.modules.auth.impl.PasswordEncoderImpl;
import ma.oralCare.service.modules.users.api.UserManagementService;
import ma.oralCare.service.modules.users.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository utilisateurRepository;
    private final AdminRepository adminRepository;
    private final MedecinRepository medecinRepository;
    private final SecretaireRepository secretaireRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // 🔑 Ajout indispensable
    /**
     * ✅ CONSTRUCTEUR UNIQUE ET SIMPLIFIÉ
     * On instancie les repositories sans argument car ils sont désormais autonomes.
     */
    public UserManagementServiceImpl() {
        this.utilisateurRepository = new UtilisateurRepositoryImpl();
        this.adminRepository = new AdminRepositoryImpl();
        this.medecinRepository = new MedecinRepositoryImpl();
        this.secretaireRepository = new SecretaireRepositoryImpl();
        this.roleRepository = new RoleRepositoryImpl();
        this.passwordEncoder = new PasswordEncoderImpl(); // Initialisé ici
    }

    public UserManagementServiceImpl(UtilisateurRepository utilisateurRepository,
                                     AdminRepository adminRepository,
                                     MedecinRepository medecinRepository,
                                     SecretaireRepository secretaireRepository,
                                     RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = Objects.requireNonNull(utilisateurRepository);
        this.adminRepository = Objects.requireNonNull(adminRepository);
        this.medecinRepository = Objects.requireNonNull(medecinRepository);
        this.secretaireRepository = Objects.requireNonNull(secretaireRepository);
        this.roleRepository = Objects.requireNonNull(roleRepository);
        this.passwordEncoder = Objects.requireNonNull(passwordEncoder); // Initialisé ici aussi
    }


    @Override
    public UserAccountDto createAdmin(CreateAdminRequest request) {
        Objects.requireNonNull(request);

        // 🔑 1. On hache le mot de passe reçu en clair
        String hashedPwd = passwordEncoder.encode(request.motDePasse());

        Adresse adresse = Adresse.builder()
                .rue(request.adresse() != null ? request.adresse() : "")
                .ville("Ville non spécifiée")
                .codePostal("00000")
                .pays("Maroc")
                .build();

        Admin admin = Admin.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .cin(request.cin())
                .tel(request.tel())
                .sexe(request.sexe())
                .login(request.login())
                .motDePass(hashedPwd) // ✅ Utilisation de la variable hachée
                .dateNaissance(request.dateNaissance())
                .adresse(adresse)
                .creePar(1L)
                .build();

        adminRepository.create(admin);
        assignRoleToUser(admin.getIdEntite(), RoleLibelle.ADMIN);
        return toDto(admin);
    }

    @Override
    public UserAccountDto createMedecin(CreateMedecinRequest request) {
        Objects.requireNonNull(request);
        String hashedPwd = passwordEncoder.encode(request.motDePasse());

        Medecin medecin = Medecin.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .login(request.login())
                .motDePass(hashedPwd)
                .build();
        medecinRepository.create(medecin);
        assignRoleToUser(medecin.getIdEntite(), RoleLibelle.DOCTOR);
        return toDto(medecin);
    }

    @Override
    public UserAccountDto createSecretaire(CreateSecretaireRequest request) {
        Objects.requireNonNull(request);
        String hashedPwd = passwordEncoder.encode(request.motDePasse());
        Secretaire secretaire = Secretaire.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .login(request.login())
                .motDePass(hashedPwd) // 2. Utilisation ic
                .build();
        secretaireRepository.create(secretaire);
        assignRoleToUser(secretaire.getIdEntite(), RoleLibelle.SECRETARY);
        return toDto(secretaire);
    }

    @Override
    public UserAccountDto getUserById(Long id) {
        return utilisateurRepository.findById(id).map(this::toDto).orElse(null);
    }

    @Override
    public List<UserAccountDto> getAllUsers() {
        List<UserAccountDto> out = new ArrayList<>();
        utilisateurRepository.findAll().forEach(u -> out.add(toDto(u)));
        return out;
    }

    @Override
    public List<UserAccountDto> searchUsersByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) return List.of();
        List<UserAccountDto> out = new ArrayList<>();
        utilisateurRepository.findAllByNomContaining(keyword).forEach(u -> out.add(toDto(u)));
        return out;
    }

    @Override
    public UserAccountDto updateUserProfile(UpdateUserProfileRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findById(request.id())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));

        utilisateur.setNom(request.nom());
        utilisateur.setEmail(request.email());
        utilisateur.setTel(request.tel());
        utilisateurRepository.update(utilisateur);
        return toDto(utilisateur);
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, RoleLibelle roleType) {
        roleRepository.findByLibelle(roleType.name()).ifPresent(role ->
                utilisateurRepository.addRoleToUtilisateur(utilisateurId, role.getIdEntite())
        );
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, RoleLibelle roleType) {
        roleRepository.findByLibelle(roleType.name()).ifPresent(role ->
                utilisateurRepository.removeRoleFromUtilisateur(utilisateurId, role.getIdEntite())
        );
    }

    private UserAccountDto toDto(Utilisateur u) {
        return new UserAccountDto(
                u.getIdEntite(),
                u.getNom(),
                u.getEmail(),
                u.getLogin(),
                u.getSexe(),
                u.getDateNaissance(),
                Set.of(),
                Set.of()
        );
    }
}