package ma.oralCare.entities.enums;

/**
 * Enumeration representant le statut d une consultation
 */
public enum StatutConsultation {
    SCHEDULED("Programmée"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée");
    
    private final String libelle;
    
    StatutConsultation(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

