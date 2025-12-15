package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter

public enum NotificationTitre {
    APPOINTMENT_REMINDER("Rappel de rendez-vous"),
    PAYMENT_DUE("Paiement en retard"),
    NEW_MESSAGE("Nouveau message"),
    SYSTEM_UPDATE("Mise à jour système"),
    EMERGENCY("Urgence");
    
    private final String libelle;
    
    NotificationTitre(String libelle) {
        this.libelle = libelle;
    }

}

