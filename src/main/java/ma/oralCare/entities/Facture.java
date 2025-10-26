package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant une facture dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Facture extends BaseEntity {
    
    
    private Long idFature;
    
    private Double totaleFacture;
    
    private Double totalePayé;
    
    private Double Reste;
    
    private StatutFacture statut;
    
    private LocalDateTime dateFacture;
    
    /**
     * Situation financière associée à la facture
     */
    private SituationFinancière situationFinancière;

    /**
     * Consultation associée à la facture
     */
    private Consultation consultation;
    
}
