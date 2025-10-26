package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant un certificat médical dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Certificat extends BaseEntity {
    
    
    private Long idCertif;
    
    private LocalDate dateDebut;
    
    private LocalDate dateFin;
    
    private int durée;
    
    private String noteMedecin;
    
    
    /**
     * Dossier médical associé au certificat
     */
    private DossierMédicale dossierMédicale;
    
    /**
     * Consultation associée au certificat
     */
    private Consultation consultation;

}
