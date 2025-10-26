package ma.oralCare.entities;

/**
 * Enumération représentant la priorité d'une notification
 */
public enum NotificationPriorite {
    HIGH("Haute"),
    MEDIUM("Moyenne"),
    LOW("Basse");
    
    private final String libelle;
    
    NotificationPriorite(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
