package ma.oralCare.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entite representant une secretaire dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Secretaire extends Staff {

    private String numCNSS;

    private Double commission;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Secretaire)) return false;
        Secretaire that = (Secretaire) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Secretaire {
          id = %d,
          nom = '%s',
          numCNSS = '%s',
          commission = %s
        }
        """.formatted(
                getId(),
                getNom(),
                numCNSS,
                commission
        );
    }
}
