package ma.oralCare.entities.patient;

import lombok.*;

import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.Sexe;
import ma.oralCare.entities.enums.Assurance;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Patient extends BaseEntity {

    private String nom;

    private String prenom;

    private LocalDate dateDeNaissance;

    private Sexe sexe;

    private String adresse;

    private String email;

    private String telephone;

    private Assurance assurance;

    private List<Antecedent> antecedents = new ArrayList<>();

    public int calculerAge() {
        if (dateDeNaissance == null) return 0;
        return LocalDate.now().getYear() - dateDeNaissance.getYear();
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getIdEntite() +
                ", nom='" + nom + '\'' +
                ", dateDeNaissance=" + dateDeNaissance +
                ", sexe=" + sexe +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", assurance=" + assurance +
                ", dateCreation=" + getDateCreation() +
                ", dateDerniereModification=" + getDateDerniereModification() +
                ", creePar='" + getCreePar() + '\'' +
                ", modifiePar='" + getModifiePar() + '\'' +
                ", antecedentsCount="+ (antecedents == null ? "null" : antecedents.size()) +
                '}';
    }


}
