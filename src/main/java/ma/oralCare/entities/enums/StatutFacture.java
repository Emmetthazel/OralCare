package ma.oralCare.entities.enums;

/**
 * Enumeration representant le statut d une facture
 */
public enum StatutFacture {
    PAID("Payée"),
    PENDING("En attente"),
    OVERDUE("En retard");
    
    private final String libelle;
    
    StatutFacture(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

