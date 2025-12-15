package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter

public enum Assurance {
    CNOPS("CNOPS"),
    CNSS("CNSS"),
    RAMED("RAMED"),
    NONE("Aucune assurance");
    
    private final String libelle;
    
    Assurance(String libelle) {
        this.libelle = libelle;
    }

}

