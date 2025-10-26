package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entité représentant les statistiques dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Statistiques extends BaseEntity {
    
    
    private Long id;
    
    private String nom;
    
    private StatistiqueCategorie catégorie;
    
    private Double chiffre;
    
    private LocalDate dateCalcul;
    

    /**
     * Cabinet médical associé à cette statistique
     */
    private CabinetMédicale cabinetMédicale;
    
}
