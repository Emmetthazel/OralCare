package ma.oralCare.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.dossier.DossierMedicale;

import java.time.LocalDate;

/**
 * Entite representant un certificat medical dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificat extends BaseEntity {

    private Long id;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private int duree;

    private String noteMedecin;

    /**
     * Dossier medical associe au certificat
     */
    private DossierMedicale dossierMedicale;

    /**
     * Consultation associee au certificat
     */
    private Consultation consultation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Certificat)) return false;
        Certificat that = (Certificat) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Certificat {
          id = %d,
          dateDebut = %s,
          dateFin = %s,
          duree = %d,
          noteMedecin = '%s'
        }
        """.formatted(
                id,
                dateDebut,
                dateFin,
                duree,
                noteMedecin
        );
    }
}
