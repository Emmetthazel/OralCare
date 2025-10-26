package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * Entité représentant une intervention médicale dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InterventionMédecin extends BaseEntity {
    
    
    private Long idIM;
    
    private Double prixDePatient;
    
    private Integer numDent;
    
    /**
     * Consultation associée à cette intervention
     */
    private Consultation consultation;
    
    /**
     * Acte associé à cette intervention
     */
    private Acte acte;
    
}
