package ma.oralCare.entities.cabinet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.StatistiqueCategorie;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Statistiques extends BaseEntity {

    private String nom;

    private StatistiqueCategorie categorie;

    private BigDecimal chiffre;

    private LocalDate dateCalcul;

    private CabinetMedicale cabinetMedicale;


    @Override
    public String toString() {
        return """
        Statistiques {
          id = %d,
          nom = '%s',
          categorie = %s,
          chiffre = %s,
          dateCalcul = %s
        }
        """.formatted(
                getIdEntite(),
                nom,
                categorie,
                chiffre,
                dateCalcul
        );
    }
}
