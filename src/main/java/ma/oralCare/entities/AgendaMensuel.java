package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant l'agenda mensuel d'un médecin dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AgendaMensuel extends BaseEntity {
    
    
    private Mois mois;
    
    private List<Jour> joursNonDisponible;
    

    /**
     * Médecin associé à cet agenda
     */
    private Médecin médecin;

}
