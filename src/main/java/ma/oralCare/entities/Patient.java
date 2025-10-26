package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entité représentant un patient dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Patient extends BaseEntity {
    
    
    private Long idPatient;
    
    private String nom;
    
    private LocalDate dateDeNaissance;
    
    private Sexe sexe;
    
    private String adresse;
    
    private String téléphone;
    
    private Assurance assurance;
    
    
    /**
     * Dossier médical associé au patient
     */
    private DossierMédicale dossierMédicale;
    
    /**
     * Liste des antécédents médicaux du patient
     */
    private List<Antécédents> antécédents;
    
    
}
