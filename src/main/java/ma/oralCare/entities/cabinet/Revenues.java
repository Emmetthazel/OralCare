package ma.oralCare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entite representant un revenu dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Revenues extends BaseEntity {

    private Long id;

    private String titre;

    private String description;

    private Double montant;

    private LocalDateTime date;

    /**
     * Cabinet medical associe a ce revenu
     */
    private CabinetMedicale cabinetMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Revenues)) return false;
        Revenues that = (Revenues) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Revenues {
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
