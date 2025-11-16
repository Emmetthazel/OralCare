package ma.oralCare.entities.facture;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.EnPromo;
import ma.oralCare.entities.enums.StatutSituationFinanciere;

import java.util.List;

/**
 * Entite representant la situation financiere d un patient dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituationFinanciere extends BaseEntity {

    private Long id;

    private Double totaleDesActes;

    private Double totalePaye;

    private Double credit;

    private StatutSituationFinanciere statut;

    private EnPromo enPromo;

    /**
     * Dossier medical associe a cette situation financiere
     */
    private DossierMedicale dossierMedicale;

    /**
     * Liste des factures associees a cette situation financiere
     */
    private List<Facture> factures;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SituationFinanciere)) return false;
        SituationFinanciere that = (SituationFinanciere) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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
          facturesCount = %d
        }
        """.formatted(
                id,
                totaleDesActes,
                totalePaye,
                credit,
                statut,
                enPromo,
                factures == null ? 0 : factures.size()
        );
    }
}
