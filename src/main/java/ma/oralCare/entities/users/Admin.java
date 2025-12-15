package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(force = true)
@EqualsAndHashCode(callSuper = true)

public class Admin extends Utilisateur {

    

    @Override
    public String toString() {
        return """
    Staff {
      id = %s,
      nom = %s,
      email = %s,
      cin = %s,
      tel = %s,
      sexe = %s,
      login = %s,
      motDePass = %s,
      dateNaissance = %s,
      lastLoginDate = %s
    }
    """.formatted(
                getIdEntite(),
                getNom() != null ? getNom() : "null",
                getEmail() != null ? getEmail() : "null",
                getCin() != null ? getCin() : "null",
                getTel() != null ? getTel() : "null",
                getSexe() != null ? getSexe() : "null",
                getLogin() != null ? getLogin() : "null",
                getMotDePass() != null ? getMotDePass() : "null",
                getDateNaissance() != null ? getDateNaissance() : "null",
                getLastLoginDate() != null ? getLastLoginDate() : "null"
                );
    }
}
