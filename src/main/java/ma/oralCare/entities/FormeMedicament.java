package ma.oralCare.entities;

/**
 * Enumération représentant la forme pharmaceutique d'un médicament
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
