package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité représentant un rendez-vous dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RDV extends BaseEntity {
    
    
    private Long idRDV;
    
    private LocalDate Date;
    
    private LocalTime heure;
    
    private String motif;
    
    private StatutRDV statut;
    
    private String noteMedecin;
    

    /**
     * Consultation associé au rendez-vous
     */
    private Consultation consultation;
    
    /**
     * Dossier médical associé au rendez-vous
     */
    private DossierMédicale dossierMédicale;
    
}
