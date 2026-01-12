package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter
public enum StatutRDV {
    CONFIRMED("Confirmé"),
    PENDING("En attente"),
    IN_PROGRESS("En cours"),
    CANCELLED("Annulé"),
    COMPLETED("Terminé");
    
    private final String libelle;
    
    StatutRDV(String libelle) {
        this.libelle = libelle;
    }

}

