package ma.oralCare.entities.enums;

import lombok.Getter;


@Getter

public enum Jour {
    MONDAY("Lundi"),
    TUESDAY("Mardi"),
    WEDNESDAY("Mercredi"),
    THURSDAY("Jeudi"),
    FRIDAY("Vendredi"),
    SATURDAY("Samedi"),
    SUNDAY("Dimanche");
    
    private final String libelle;
    
    Jour(String libelle) {
        this.libelle = libelle;
    }

}

