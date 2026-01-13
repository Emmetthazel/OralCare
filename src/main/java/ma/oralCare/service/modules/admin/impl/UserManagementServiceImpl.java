package ma.oralCare.service.modules.admin.impl;

import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.users.Medecin;
import ma.oralCare.entities.users.Secretaire;
import ma.oralCare.entities.users.Staff;
import ma.oralCare.entities.users.Utilisateur;
import ma.oralCare.mvc.dto.admin.UserStaffDTO;
import ma.oralCare.repository.modules.users.api.UtilisateurRepository;
import ma.oralCare.service.modules.admin.api.UserManagementService;
import org.mindrot.jbcrypt.BCrypt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserManagementServiceImpl implements UserManagementService {

    private final UtilisateurRepository userRepository;

    public UserManagementServiceImpl(UtilisateurRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Map<String, List<UserStaffDTO>> getStaffHierarchy(String search) {
        // 1. Récupérer TOUS les noms de cabinets existants en base
        List<String> allCabinetNames = userRepository.findAllCabinetNames();

        // 2. Récupérer le personnel existant (filtré par recherche si nécessaire)
        List<UserStaffDTO> allStaff = userRepository.findAllStaffWithCabinetDetails(search);

        // 3. Initialiser la Map avec tous les cabinets (pour garantir l'affichage des vides)
        Map<String, List<UserStaffDTO>> hierarchy = new LinkedHashMap<>();
        for (String name : allCabinetNames) {
            hierarchy.put(name, new ArrayList<>());
        }

        // 4. Remplir avec le personnel trouvé
        for (UserStaffDTO staff : allStaff) {
            if (hierarchy.containsKey(staff.getCabinetNom())) {
                hierarchy.get(staff.getCabinetNom()).add(staff);
            }
        }

        return hierarchy;
    }

    @Override
    public void deleteUser(String email) {
        // Le service fait le pont entre l'email (UI) et l'ID (DB)
        userRepository.findByEmail(email).ifPresent(user -> {
            userRepository.deleteById(user.getIdEntite());
        });
    }
    @Override
    public Utilisateur getUserFullDetails(String email) {
        Utilisateur u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // On peut injecter le statut calculé ici si besoin,
        // ou laisser la vue appeler une méthode utilitaire.
        return u;
    }

    private String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String specials = "@#$!%*?&";
        String allChars = upper + lower + digits + specials;
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder();
        sb.append(upper.charAt(random.nextInt(upper.length())));
        sb.append(lower.charAt(random.nextInt(lower.length())));
        sb.append(digits.charAt(random.nextInt(digits.length())));
        sb.append(specials.charAt(random.nextInt(specials.length())));
        for (int i = 4; i < 10; i++) sb.append(allChars.charAt(random.nextInt(allChars.length())));
        return sb.toString();
    }

    @Override
    public String generateAndSaveNewPassword(String email) {
        // 1. Génération du mot de passe en clair (Strong Password)
        String finalPwd = generateStrongPassword();

        // 2. Hachage du mot de passe avant sauvegarde
        String hashedPwd = BCrypt.hashpw(finalPwd, BCrypt.gensalt());

        // 3. Persistance du mot de passe haché
        userRepository.updatePassword(email, hashedPwd);

        return finalPwd; // On retourne le mot de passe en clair pour l'affichage UI
    }

    private String calculateStatut(Utilisateur u) {
        if (u.getLastLoginDate() == null) {
            return "JAMAIS CONNECTÉ";
        }

        LocalDate uneSemaineAvant = LocalDate.now().minusDays(7);
        return u.getLastLoginDate().isAfter(uneSemaineAvant) ? "ACTIF" : "INACTIF";
    }


    // Dans UserManagementServiceImpl.java
    @Override
    public void updateUserBasicInfo(String email, String nom, String prenom, String tel, String cin, String ville) {
        // Utilisation de l'Optional retourné par votre repository
        Utilisateur u = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        u.setNom(nom);
        u.setPrenom(prenom);
        u.setTel(tel);
        u.setCin(cin);

        if (u.getAdresse() == null) {
            u.setAdresse(new Adresse());
        }
        u.getAdresse().setVille(ville);

        userRepository.update(u);
    }

    @Override
    public String createStaffMember(String cabinetName, String role, Map<String, String> data) {
        // 1. Déterminer le type d'objet et initialiser l'objet spécifique
        Staff user;
        String roleToAssign;
        String roleUpper = role.toUpperCase();

        if (roleUpper.contains("MÉDECIN") || roleUpper.contains("DOCTOR")) {
            user = new Medecin();
            ((Medecin) user).setSpecialite(data.get("specialite"));
            roleToAssign = "MEDECIN"; // Libellé exact attendu dans votre table 'role'
        } else {
            user = new Secretaire();
            ((Secretaire) user).setNumCNSS(data.get("numCnss"));
            roleToAssign = "SECRETARY";
        }

        // 2. Génération automatique du login : p.nom
        String prenom = data.get("prenom").trim().toLowerCase();
        String nom = data.get("nom").trim().toLowerCase();
        String generatedLogin = prenom.substring(0, 1) + "." + nom;

        // 3. Remplissage des données de base
        user.setNom(data.get("nom").trim());
        user.setPrenom(data.get("prenom").trim());
        user.setEmail(data.get("email").trim());
        user.setLogin(generatedLogin);
        user.setCin(data.get("cin").trim());
        user.setTel(data.get("tel").trim());

        if (data.get("sexe") != null) {
            user.setSexe(Sexe.valueOf(data.get("sexe").toUpperCase()));
        }

        // 4. Hachage sécurisé du mot de passe
        String clearPassword = data.get("password");
        if (clearPassword != null && !clearPassword.isEmpty()) {
            String hashedPwd = BCrypt.hashpw(clearPassword, BCrypt.gensalt());
            user.setMotDePass(hashedPwd);
        }

        // 5. Adresse et Date de Naissance
        Adresse adresse = new Adresse();
        adresse.setVille(data.get("ville"));
        adresse.setPays(data.get("pays"));
        user.setAdresse(adresse);

        if (data.get("dateNaissance") != null && !data.get("dateNaissance").isEmpty()) {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
                user.setDateNaissance(java.time.LocalDate.parse(data.get("dateNaissance"), formatter));
            } catch (Exception e) {
                System.err.println("Format date invalide : " + e.getMessage());
            }
        }

        // 6. Données administratives et Cabinet
        user.setSalaire(new BigDecimal("0.00"));
        user.setDateRecrutement(LocalDate.now());

        Long cabinetId = userRepository.findCabinetIdByName(cabinetName);
        if (cabinetId != null) {
            ma.oralCare.entities.cabinet.CabinetMedicale cab = new ma.oralCare.entities.cabinet.CabinetMedicale();
            cab.setIdEntite(cabinetId);
            user.setCabinetMedicale(cab);
        }

        // 7. Sauvegarde de l'utilisateur (Génère l'ID en base)
        userRepository.save(user);

        // 8. ✅ AJOUT CRUCIAL : Liaison avec le rôle en base de données
        // Ceci remplit la table 'utilisateur_role' pour permettre la connexion
        Long roleId = userRepository.findRoleIdByName(roleToAssign);
        if (roleId != null && user.getIdEntite() != null) {
            userRepository.addRoleToUtilisateur(user.getIdEntite(), roleId);
            System.out.println("[SERVICE] Rôle " + roleToAssign + " lié à l'utilisateur " + generatedLogin);
        } else {
            System.err.println("[SERVICE] Erreur : Impossible de lier le rôle " + roleToAssign);
        }

        return clearPassword;
    }

    @Override
    public Utilisateur findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        return userRepository.findByEmail(email.trim()).orElse(null);
    }
}