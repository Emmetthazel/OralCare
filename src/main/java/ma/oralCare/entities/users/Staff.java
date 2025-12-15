package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.cabinet.CabinetMedicale;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Staff extends Utilisateur {

    private BigDecimal salaire;

    private BigDecimal prime;

    private LocalDate dateRecrutement;

    private Integer soldeConge;

    private CabinetMedicale cabinetMedicale;

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
      lastLoginDate = %s,
      salaire = %s,
      prime = %s,
      dateRecrutement = %s,
      soldeConge = %s,
      cabinetId = %s
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
                salaire != null ? salaire : "null",
                prime != null ? prime : "null",
                dateRecrutement != null ? dateRecrutement : "null",
                soldeConge != null ? soldeConge : "null",
                cabinetMedicale != null ? cabinetMedicale.getIdEntite() : "null"
        );
    }
}
