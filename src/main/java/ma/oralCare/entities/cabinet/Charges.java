package ma.oralCare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entite representant une charge dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Charges extends BaseEntity {

    private Long id;

    private String titre;

    private String description;

    private Double montant;

    private LocalDateTime date;

    /**
     * Cabinet medical associe a cette charge
     */
    private CabinetMedicale cabinetMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Charges)) return false;
        Charges that = (Charges) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Charges {
          id = %d,
          titre = '%s',
          description = '%s',
          montant = %s,
          date = %s
        }
        """.formatted(
                id,
                titre,
                description,
                montant,
                date
        );
    }
}
