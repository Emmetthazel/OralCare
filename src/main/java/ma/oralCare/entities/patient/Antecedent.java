package ma.oralCare.entities.patient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.enums.CategorieAntecedent;
import ma.oralCare.entities.enums.NiveauDeRisque;

import java.util.List;

/**
 * Entite representant un antecedent medical d un patient
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Antecedent {

    private Long id;

    private String nom;

    private CategorieAntecedent categorie;

    private NiveauDeRisque niveauRisque;

    private List<Patient> patients;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Antecedent)) return false;
        Antecedent that = (Antecedent) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Antecedent {
          id = %d,
          nom = '%s',
          categorie = %s,
          niveauRisque = %s,
          patientsCount = %d
        }
        """.formatted(
                id,
                nom,
                categorie,
                niveauRisque,
                patients == null ? 0 : patients.size()
        );
    }
}

