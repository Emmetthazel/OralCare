package ma.oralCare.entities.enums;

/**
 * Enumeration representant le statut d un rendez-vous
 */
public enum StatutRDV {
    CONFIRMED("Confirmé"),
    PENDING("En attente"),
    CANCELLED("Annulé"),
    COMPLETED("Terminé");
    
    private final String libelle;
    
    StatutRDV(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

