package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant un revenu dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Revenues extends BaseEntity {
    
    
    private String titre;
    
    private String description;
    
    private Double montant;
    
    private LocalDateTime date;
    

    /**
     * Cabinet médical associé à ce revenu
     */
    private CabinetMédicale cabinetMédicale;
    
}
