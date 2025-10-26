package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant un membre du personnel dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Staff extends Utilisateur {
    
    
    private Double salaire;
    
    private Double prime;
    
    private LocalDate dateRecrutement;
    
    private Integer soldeCongé;
    
    /**
     * Cabinet médical où travaille le membre du personnel
     */
    private CabinetMédicale cabinetMédicale;
    
}
