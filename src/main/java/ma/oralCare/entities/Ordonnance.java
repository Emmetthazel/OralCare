package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant une ordonnance dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Ordonnance extends BaseEntity {
    
    
    private Long idOrd;
    
    private LocalDate date;
    
    
    /**
     * Dossier médical associé à l'ordonnance
     */
    private DossierMédicale dossierMédicale;
    
    /**
     * Liste des prescriptions de l'ordonnance
     */
    private List<Prescription> prescriptions;

    /**
     * Consultation associée à l'ordonnance
     */
    private Consultation consultation;
    
}
