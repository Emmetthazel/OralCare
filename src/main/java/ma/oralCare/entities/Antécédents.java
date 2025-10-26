package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant les antécédents médicaux d'un patient
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Antécédents extends BaseEntity {
    
    
    private Long id_Antécédent;
    
    private String nom;
    
    private String catégorie;
    
    private NiveauDeRisque niveauDeRisque;
    
    /**
     * Liste des patients associés à cet antécédent
     */
    private List<Patient> patients;
}
