package ma.oralCare.entities.enums;


import lombok.Getter;

@Getter

public enum NotificationType {
    ALERT("Alerte"),
    INFO("Information"),
    WARNING("Avertissement"),
    SUCCESS("Succès");
    
    private final String libelle;
    
    NotificationType(String libelle) {
        this.libelle = libelle;
    }

}

