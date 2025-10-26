package ma.oralCare.entities;

/**
 * Enumération représentant le statut d'une facture
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
