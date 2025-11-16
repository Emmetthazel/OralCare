package ma.oralCare.entities.enums;

/**
 * Enumeration representant le sexe d une personne
 */
public enum Sexe {
    MALE("Masculin"),
    FEMALE("Féminin"),
    OTHER("Autre");
    
    private final String libelle;
    
    Sexe(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

