package ma.oralCare.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.Jour;
import ma.oralCare.entities.enums.Mois;
import ma.oralCare.entities.users.Medecin;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class AgendaMensuel extends BaseEntity {

    private Mois mois;

    private int annee;

    private List<Jour> joursNonDisponible;

    private Medecin medecin;


    @Override
    public String toString() {
        return """
        AgendaMensuel {
          id = %d,
          mois = %s,
          annee = %d,
          joursNonDisponibleCount = %d,
          medecin = %s
        }
        """.formatted(
                getIdEntite(),
                mois,
                annee,
                joursNonDisponible == null ? 0 : joursNonDisponible.size(),
                medecin != null ? medecin.getNom() : "null"
        );
    }
}
