package ma.oralCare.entities.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe de base pour toutes les entités du systeme OralCare
 * Contient les attributs communs à toutes les entités
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    
    
    private Long idEntite;
    
    private LocalDate dateCréation;
    
    private LocalDateTime dateDerniereModification;
    
    private String modifiePar;
    
    private String creePar;
    
}

