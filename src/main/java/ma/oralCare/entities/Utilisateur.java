package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant un utilisateur dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Utilisateur extends BaseEntity {
    
    
    private Long idUser;
    
    private String nom;
    
    private String email;
    
    private Adresse adresse;
    
    private String cin;
    
    private String tél;
    
    private Sexe sexe;
    
    private String login;
    
    private String motDePass;
    
    private LocalDate lastLoginDate;
    
    private LocalDate dateNaissance;
    

    /**
     * Liste des rôles de l'utilisateur
     */
    private List<Role> roles;
    
    /**
     * Liste des notifications de l'utilisateur
     */
    private List<Notification> notifications;
    
}
