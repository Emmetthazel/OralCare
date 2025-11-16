package ma.oralCare.entities.medicament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.consultation.Ordonnance;

/**
 * Entite representant une prescription medicale dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription extends BaseEntity {

    private Long id;

    private int quantite;

    private String frequence;

    private int dureeEnJours;

    /**
     * Ordonnance associee a cette prescription
     */
    private Ordonnance ordonnance;

    /**
     * Medicament prescrit
     */
    private Medicament medicament;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prescription)) return false;
        Prescription that = (Prescription) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Prescription {
          id = %d,
          quantite = %d,
          frequence = '%s',
          dureeEnJours = %d,
          medicament = %s
        }
        """.formatted(
                id,
                quantite,
                frequence,
                dureeEnJours,
                medicament != null ? medicament.getNom() : null
        );
    }
}
