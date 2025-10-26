package ma.oralCare.entities;

/**
 * Enumération représentant le statut de promotion d'un patient
 */
public enum EnPromo {
    YES("En promotion"),
    NO("Pas en promotion");
    
    private final String libelle;
    
    EnPromo(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
