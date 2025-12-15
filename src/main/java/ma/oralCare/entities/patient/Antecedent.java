package ma.oralCare.entities.patient;

import lombok.*;
import java.util.ArrayList;

import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;

import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Antecedent extends BaseEntity {

    private String nom;

    private CategorieAntecedent categorie;

    private NiveauDeRisque niveauDeRisque;

    private List<Patient> patients = new ArrayList<>();

    @Override
    public String toString() {
        return "Antecedent{" +
                "id=" + getIdEntite() +
                ", nom='" + nom + '\'' +
                ", categorie='" + categorie + '\'' +
                ", niveauDeRisque=" + niveauDeRisque +
                '}';
    }

}

