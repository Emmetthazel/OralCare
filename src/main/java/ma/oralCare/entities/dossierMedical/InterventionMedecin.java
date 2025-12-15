package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

import java.math.BigDecimal;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class InterventionMedecin extends BaseEntity {

    private BigDecimal prixDePatient;

    private Integer numDent;

    private Consultation consultation;

    private Acte acte;

    @Override
    public String toString() {
        return """
        InterventionMedecin {
          id = %d,
          prixDePatient = %s,
          numDent = %d,
          acte = %s,
          consultation = %s,
        }
        """.formatted(
                getIdEntite(),
                prixDePatient,
                numDent,
                acte != null ? acte.getLibelle() : "null",
                consultation != null ? consultation.getDossierMedicale().getPatient() : "null"
        );
    }
}
