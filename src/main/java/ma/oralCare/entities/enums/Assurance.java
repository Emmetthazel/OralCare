package ma.oralCare.entities.enums;

/**
 * Enumeration representant le type d assurance d un patient
 */
public enum Assurance {
    CNOPS("CNOPS"),
    CNSS("CNSS"),
    RAMED("RAMED"),
    NONE("Aucune assurance");
    
    private final String libelle;
    
    Assurance(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

