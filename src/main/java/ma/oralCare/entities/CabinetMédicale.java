package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un cabinet médical dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CabinetMédicale extends BaseEntity {
    
    
    private Long idUser;
    
    private String nom;
    
    private String email;
    
    private String logo;
    
    private Adresse adresse;
    
    private String cin;
    
    private String tél1;
    
    private String tél2;
    
    private String siteWeb;
    
    private String instagram;
    
    private String facebook;
    
    private String description;
    

    /**
     * Liste des charges du cabinet médical
     */
    private List<Charges> charges;
    
    /**
     * Liste des revenus du cabinet médical
     */
    private List<Revenues> revenues;
    
    /**
     * Liste des statistiques du cabinet médical
     */
    private List<Statistiques> statistiques;
    
    /**
     * Liste du personnel du cabinet médical
     */
    private List<Staff> staff;
    
}
