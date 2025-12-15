package ma.oralCare.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.dossierMedical.DossierMedicale;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Medecin extends Staff {

    private String specialite;

    private AgendaMensuel agendaMensuel;

    private List<DossierMedicale> dossierMedicaux;

    @Override
    public String toString() {
        return """
    Medecin {
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
      specialite = %s,
      agendaMensuelId = %s,
      dossierMedicauxCount = %d
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
                specialite != null ? specialite : "null",
                agendaMensuel != null ? agendaMensuel.getIdEntite() : "null",
                dossierMedicaux != null ? dossierMedicaux.size() : 0
        );
    }

}
