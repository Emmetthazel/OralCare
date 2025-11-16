package ma.oralCare.entities.enums;

/**
 * Enumeration representant le type d une notification
 */
public enum NotificationType {
    ALERT("Alerte"),
    INFO("Information"),
    WARNING("Avertissement"),
    SUCCESS("Succès");
    
    private final String libelle;
    
    NotificationType(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

