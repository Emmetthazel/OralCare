package ma.oralCare.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.medicament.Prescription;

import java.time.LocalDate;
import java.util.List;

/**
 * Entite representant une ordonnance dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance extends BaseEntity {

    private Long id;

    private LocalDate date;

    /**
     * Dossier medical associe a l ordonnance
     */
    private DossierMedicale dossierMedicale;

    /**
     * Liste des prescriptions de l ordonnance
     */
    private List<Prescription> prescriptions;

    /**
     * Consultation associee a l ordonnance
     */
    private Consultation consultation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ordonnance)) return false;
        Ordonnance that = (Ordonnance) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Ordonnance {
          id = %d,
          date = %s,
          prescriptionsCount = %d
        }
        """.formatted(
                id,
                date,
                prescriptions == null ? 0 : prescriptions.size()
        );
    }
}
