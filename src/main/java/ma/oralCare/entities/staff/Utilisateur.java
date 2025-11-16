package ma.oralCare.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.Adresse;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.notification.Notification;
import ma.oralCare.entities.notification.Role;

import java.time.LocalDate;
import java.util.List;

/**
 * Entite representant un utilisateur dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Utilisateur extends BaseEntity {

    private Long id;

    private String nom;

    private String email;

    private Adresse adresse;

    private String cin;

    private String tel;

    private Sexe sexe;

    private String login;

    private String motDePass;

    private LocalDate lastLoginDate;

    private LocalDate dateNaissance;

    /**
     * Liste des roles de l utilisateur
     */
    private List<Role> roles;

    /**
     * Liste des notifications de l utilisateur
     */
    private List<Notification> notifications;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Utilisateur {
          id = %d,
          nom = '%s',
          email = '%s',
          cin = '%s',
          tel = '%s',
          sexe = %s,
          login = '%s',
          rolesCount = %d,
          notificationsCount = %d
        }
        """.formatted(
                id,
                nom,
                email,
                cin,
                tel,
                sexe,
                login,
                roles == null ? 0 : roles.size(),
                notifications == null ? 0 : notifications.size()
        );
    }
}
