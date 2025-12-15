package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.Sexe;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Utilisateur extends BaseEntity {

    private String nom;

    private String prenom;

    private String email;

    private Adresse adresse;

    private String cin;

    private String tel;

    private Sexe sexe;

    private String login;

    private String motDePass;

    private LocalDate lastLoginDate;

    private LocalDate dateNaissance;

    private List<Role> roles;


    private List<Notification> notifications;


    @Override
    public String toString() {
        return """
    Utilisateur {
      id = %s,
      nom = %s,
      email = %s,
      cin = %s,
      tel = %s,
      sexe = %s,
      login = %s,
      motDePass = %s,
      dateNaissance = %s,
      lastLoginDate = %s,
      adresse = %s,
      rolesCount = %d,
      notificationsCount = %d
     }
    """.formatted(
                getIdEntite(),
                nom != null ? nom : "null",
                email != null ? email : "null",
                cin != null ? cin : "null",
                tel != null ? tel : "null",
                sexe != null ? sexe : "null",
                login != null ? login : "null",
                motDePass != null ? motDePass : "null",
                dateNaissance != null ? dateNaissance : "null",
                lastLoginDate != null ? lastLoginDate : "null",
                adresse != null ? adresse.toString() : "null",
                roles != null ? roles.size() : 0,
                notifications != null ? notifications.size() : 0
        );
    }

}
