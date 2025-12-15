package ma.oralCare.entities.cabinet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Charges extends BaseEntity {

    private String titre;

    private String description;

    private BigDecimal montant;

    private LocalDateTime date;

    private CabinetMedicale cabinetMedicale;


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
                getIdEntite(),
                titre,
                description,
                montant,
                date,
                cabinetMedicale != null ? cabinetMedicale.getNom() : null
        );
    }
}
