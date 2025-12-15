package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Prescription extends BaseEntity {

    private int quantite;

    private String frequence;

    private int dureeEnJours;

    private Ordonnance ordonnance;

    private Medicament medicament;


    @Override
    public String toString() {
        return """
        Prescription {
          id = %d,
          quantite = %d,
          frequence = '%s',
          dureeEnJours = %d,
          ordonnance = '%s',
          medicament = %s
        }
        """.formatted(
                getIdEntite(),
                quantite,
                frequence,
                dureeEnJours,
                ordonnance != null ? ordonnance.toString() : "null",
                medicament != null ? medicament.getNom() : "null"
        );
    }
}
