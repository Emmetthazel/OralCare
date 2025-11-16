package ma.oralCare.entities.facture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.enums.StatutFacture;

import java.time.LocalDateTime;

/**
 * Entite representant une facture dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture extends BaseEntity {

    private Long id;

    private Double totaleFacture;

    private Double totalePaye;

    private Double reste;

    private StatutFacture statut;

    private LocalDateTime dateFacture;

    /**
     * Situation financiere associee a la facture
     */
    private SituationFinanciere situationFinanciere;

    /**
     * Consultation associee a la facture
     */
    private Consultation consultation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facture)) return false;
        Facture that = (Facture) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Facture {
          id = %d,
          totaleFacture = %s,
          totalePaye = %s,
          reste = %s,
          statut = %s,
          dateFacture = %s
        }
        """.formatted(
                id,
                totaleFacture,
                totalePaye,
                reste,
                statut,
                dateFacture
        );
    }
}
