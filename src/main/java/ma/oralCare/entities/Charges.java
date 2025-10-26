package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une charge dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Charges extends BaseEntity {
    
    
    private String titre;
    
    private String description;
    
    private Double montant;
    
    private LocalDateTime date;
    

    /**
     * Cabinet médical associé à cette charge
     */
    private CabinetMédicale cabinetMédicale;
    
}
