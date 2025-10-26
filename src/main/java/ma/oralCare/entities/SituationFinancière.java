package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant la situation financière d'un patient dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SituationFinancière extends BaseEntity {
    
    
    private Long idSF;
    
    private Double totaleDesActes;
    
    private Double totalePayé;
    
    private Double crédit;
    
    private StatutSituationFinanciere statut;
    
    private EnPromo enPromo;
    
    /**
     * Dossier médical associé à cette situation financière
     */
    private DossierMédicale dossierMédicale;

    /**
     * Liste des factures associées à cette situation financière
     */
    private List<Facture> factures;

}
