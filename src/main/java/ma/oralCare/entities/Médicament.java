package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un médicament dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Médicament extends BaseEntity {
    
    
    private Long idMct;
    
    private String nom;
    
    private String laboratoire;
    
    private String type;
    
    private FormeMedicament forme;
    
    private Boolean remboursable;
    
    private Double prixUnitaire;
    
    private String Description;

    /**
     * Liste des prescriptions associées à ce médicament
     */
    private List<Prescription> prescriptions;
    
}
