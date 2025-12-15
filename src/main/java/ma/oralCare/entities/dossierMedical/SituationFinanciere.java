package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.EnPromo;
import ma.oralCare.entities.enums.StatutSituationFinanciere;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class SituationFinanciere extends BaseEntity {

    private BigDecimal totaleDesActes;

    private BigDecimal totalePaye;

    private BigDecimal credit;

    private StatutSituationFinanciere statut;

    private EnPromo enPromo;

    private DossierMedicale dossierMedicale;

    private List<Facture> factures;

    @Override
    public String toString() {
        return """
        SituationFinanciere {
          id = %d,
          totaleDesActes = %s,
          totalePaye = %s,
          credit = %s,
          statut = %s,
          enPromo = %s,
          dossierMedicale = %s,
          facturesCount = %d
        }
        """.formatted(
                getIdEntite(),
                totaleDesActes,
                totalePaye,
                credit,
                statut,
                enPromo,
                dossierMedicale != null ? dossierMedicale.getPatient() : "null",
                factures == null ? 0 : factures.size()
        );
    }
}
