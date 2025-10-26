package ma.oralCare.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe de base pour toutes les entités du système OralCare
 * Contient les attributs communs à toutes les entités
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    
    
    private Long idEntité;
    
    private LocalDate dateCréation;
    
    private LocalDateTime dateDernièreModification;
    
    private String modifiéPar;
    
    private String crééPar;
    
}
