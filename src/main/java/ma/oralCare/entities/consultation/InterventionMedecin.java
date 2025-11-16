package ma.oralCare.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.acte.Acte;
import ma.oralCare.entities.common.BaseEntity;

/**
 * Entite representant une intervention medicale dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionMedecin extends BaseEntity {

    private Long id;

    private Double prixDePatient;

    private Integer numDent;

    /**
     * Consultation associee a cette intervention
     */
    private Consultation consultation;

    /**
     * Acte associe a cette intervention
     */
    private Acte acte;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InterventionMedecin)) return false;
        InterventionMedecin that = (InterventionMedecin) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        InterventionMedecin {
          id = %d,
          prixDePatient = %s,
          numDent = %d,
          acte = %s
        }
        """.formatted(
                id,
                prixDePatient,
                numDent,
                acte != null ? acte.getLibelle() : null
        );
    }
}
