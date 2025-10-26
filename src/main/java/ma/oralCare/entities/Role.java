package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un rôle dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {
    
    
    private Long idRole;
    
    private RoleLibelle libellé;
    
    private List<String> privilèges;
    

    /**
     * Liste des utilisateurs ayant ce rôle
     */
    private List<Utilisateur> utilisateurs;
    
}
