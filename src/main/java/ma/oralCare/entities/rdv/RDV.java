package ma.oralCare.entities.rdv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.StatutRDV;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entite representant un rendez-vous dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDV extends BaseEntity {

    private Long id;

    private LocalDate date;

    private LocalTime heure;

    private String motif;

    private StatutRDV statut;

    private String noteMedecin;

    /**
     * Consultation associee au rendez-vous
     */
    private Consultation consultation;

    /**
     * Dossier medical associe au rendez-vous
     */
    private DossierMedicale dossierMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        RDV {
          id = %d,
          date = %s,
          heure = %s,
          motif = '%s',
          statut = %s
        }
        """.formatted(
                id,
                date,
                heure,
                motif,
                statut
        );
    }
}
