package ma.oralCare.entities.enums;


import lombok.Getter;

@Getter

public enum Sexe {
    MALE("Masculin"),
    FEMALE("Féminin"),
    OTHER("Autre");
    
    private final String libelle;
    
    Sexe(String libelle) {
        this.libelle = libelle;
    }

}

