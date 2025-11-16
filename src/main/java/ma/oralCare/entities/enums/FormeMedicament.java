package ma.oralCare.entities.enums;

/**
 * Enumeration representant la forme pharmaceutique d un Médicament
 */
public enum FormeMedicament {
    TABLET("Comprimé"),
    CAPSULE("Gélule"),
    SYRUP("Sirop"),
    INJECTION("Injection"),
    CREAM("Crème"),
    OINTMENT("Pommade"),
    DROPS("Gouttes");
    
    private final String libelle;
    
    FormeMedicament(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

