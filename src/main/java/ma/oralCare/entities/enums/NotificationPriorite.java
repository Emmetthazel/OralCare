package ma.oralCare.entities.enums;


import lombok.Getter;

@Getter
public enum NotificationPriorite {
    HIGH("Haute"),
    MEDIUM("Moyenne"),
    LOW("Basse");
    
    private final String libelle;
    
    NotificationPriorite(String libelle) {
        this.libelle = libelle;
    }

}

