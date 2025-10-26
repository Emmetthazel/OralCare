package ma.oralCare.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entité représentant un médecin dans le système OralCare
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Médecin extends Staff {
    
    
    private String spécialité;
    
    private AgendaMensuel agendaMensuel;
    

    /**
     * Liste des dossiers médicale associé au médecin;
     */
    private List<DossierMédicale> DossierMédicaux;
    
}
