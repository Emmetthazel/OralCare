package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un acte médical dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Acte extends BaseEntity {
    

    private Long idActe;
    
    private String libellé;
    
    private String catégorie;
    
    private Double prixDeBase;
    

    /**
     * Liste des interventions médicales associées à cet acte
     */
    private List<InterventionMédecin> interventionsMédecin;
    
}
