package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant un dossier médical dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DossierMédicale extends BaseEntity {
    
    
    private Long idDM;
    
    private LocalDate dateDeCréation;
    

    /**
     * Patient associé au dossier médical
     */
    private Patient patient;
    
    /**
     * Liste des consultations du dossier médical
     */
    private List<Consultation> consultations;
    
    /**
     * Liste des ordonnances du dossier médical
     */
    private List<Ordonnance> ordonnances;
    
    /**
     * Liste des certificats du dossier médical
     */
    private List<Certificat> certificats;
    
    /**
    * Situation financière du dossier médical
    */
    private SituationFinancière situationFinancière;

    /**
     * Liste des rendez-vous du dossier médical
     */
    private List<RDV> rendezVous;

    /**
     * Médecin associé au dossier médical
     */
    private Médecin médecin;

}
