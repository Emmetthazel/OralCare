package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant une consultation dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Consultation extends BaseEntity {
    
    
    private Long idConsultation;
    
    private LocalDate Date;
    
    private StatutConsultation statut;
    
    private String observationMedecin;
    
    /**
     * Dossier médical associé à la consultation
     */
    private DossierMédicale dossierMédicale;
    
    /**
     * Liste des interventions du médecin lors de cette consultation
     */
    private List<InterventionMédecin> interventionsMédecin;

    /**
     * Liste des factures associées à la consultation
     */
    private List<Facture> factures;

    /**
     * Liste des ordonnances associées à la consultation
     */
    private List<Ordonnance> ordonnances;
    
    /**
     * Certificat associé à la consultation
     */
    private Certificat certificat;

    /**
     * Liste des rendez-vous associés à la consultation
     */
    private List<RDV> rendezVous;
    
}
