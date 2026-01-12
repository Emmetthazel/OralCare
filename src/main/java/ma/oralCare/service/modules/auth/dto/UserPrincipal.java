package ma.oralCare.service.modules.auth.dto;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

public class UserPrincipal {

    private Long id;
    private String login;
    private String nom;
    private String prenom;
    private List<String> roles;       // liste de noms de rôles
    private Set<String> privileges;   // set de privilèges, peut rester vide si pas besoin

    // Constructeur minimal
    public UserPrincipal(Long id, String login) {
        this.id = id;
        this.login = login;
        this.roles = new ArrayList<>();       // initialisation vide
        this.privileges = new HashSet<>();
        this.nom = nom;       // ✅ Initialisation
        this.prenom = prenom;// initialisation vide
    }

    // Constructeur avec rôles
    public UserPrincipal(Long id, String login, List<String> roles) {
        this.id = id;
        this.login = login;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.privileges = new HashSet<>();
    }

    // Constructeur complet avec rôles et privilèges
    public UserPrincipal(Long id, String login, List<String> roles, Set<String> privileges) {
        this.id = id;
        this.login = login;
        this.roles = roles != null ? roles : new ArrayList<>();
        this.privileges = privileges != null ? privileges : new HashSet<>();
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public List<String> getRoles() {
        return roles;
    }

    public Set<String> getPrivileges() {
        return privileges;
    }

    // --- Setters ---
    public void setId(Long id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public void setPrivileges(Set<String> privileges) {
        this.privileges = privileges != null ? privileges : new HashSet<>();
    }

    @Override
    public String toString() {
        return "UserPrincipal{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", roles=" + roles +
                ", privileges=" + privileges +
                '}';
    }
}
