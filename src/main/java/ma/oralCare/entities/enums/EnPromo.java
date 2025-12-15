package ma.oralCare.entities.enums;

import lombok.Getter;

/**
 * Enumeration representant le statut de promotion d un patient
 */
@Getter

public enum EnPromo {
    YES("En promotion"),
    NO("Pas en promotion");
    
    private final String libelle;
    
    EnPromo(String libelle) {
        this.libelle = libelle;
    }

}

