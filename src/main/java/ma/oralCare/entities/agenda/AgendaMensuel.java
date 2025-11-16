package ma.oralCare.entities.agenda;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.staff.Medecin;

import java.util.List;

/**
 * Entite representant l agenda mensuel d un medecin dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgendaMensuel extends BaseEntity {

    private Long id;

    private Mois mois;

    private List<Jour> joursNonDisponible;

    /**
     * Medecin associe a cet agenda
     */
    private Medecin medecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AgendaMensuel)) return false;
        AgendaMensuel that = (AgendaMensuel) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        AgendaMensuel {
          id = %d,
          mois = %s,
          joursNonDisponibleCount = %d,
          medecin = %s
        }
        """.formatted(
                id,
                mois,
                joursNonDisponible == null ? 0 : joursNonDisponible.size(),
                medecin != null ? medecin.getNom() : null
        );
    }
}
