package ma.oralCare.entities.enums;

/**
 * Enumeration representant la priorite d une notification
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

