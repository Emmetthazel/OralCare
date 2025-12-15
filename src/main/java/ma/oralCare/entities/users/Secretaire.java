package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Secretaire extends Staff {

    private String numCNSS;

    private BigDecimal commission;


    @Override
    public String toString() {
        return """
    Secretaire {
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
      salaire = %s,
      prime = %s,
      dateRecrutement = %s,
      soldeConge = %s,
      cabinetId = %s,
      numCNSS = %s,
      commission = %s
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
                getLastLoginDate() != null ? getLastLoginDate() : "null",
                getSalaire() != null ? getSalaire() : "null",
                getPrime() != null ? getPrime() : "null",
                getDateRecrutement() != null ? getDateRecrutement() : "null",
                getSoldeConge() != null ? getSoldeConge() : "null",
                getCabinetMedicale() != null ? getCabinetMedicale().getIdEntite() : "null",
                numCNSS != null ? numCNSS : "null",
                commission != null ? commission : "null"
        );
    }

}
