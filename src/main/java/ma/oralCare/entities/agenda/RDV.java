package ma.oralCare.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.StatutRDV;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entite representant un rendez-vous dans le systeme OralCare
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class RDV extends BaseEntity {

    private Long id;

    private LocalDate date;

    private LocalTime heure;

    private String motif;

    private StatutRDV statut;

    private String noteMedecin;

    private Consultation consultation;

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
