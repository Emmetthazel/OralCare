package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entité représentant une prescription médicale dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Prescription extends BaseEntity {
    
    
    private Long idPr;
    
    private int quantité;
    
    private String fréquence;
    
    private int duréeEnJours;
    

    /**
     * Ordonnance associée à cette prescription
     */
    private Ordonnance ordonnance;
    
    /**
     * Médicament prescrit
     */
    private Médicament médicament;
    
}
