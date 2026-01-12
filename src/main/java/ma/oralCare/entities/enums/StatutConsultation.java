package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter
public enum StatutConsultation {
    SCHEDULED("Programmée"),
    IN_PROGRESS("En cours"), // ✅ À ajouter pour gérer la séance active
    COMPLETED("Terminée"),
    CANCELLED("Annulée");

    private final String libelle;

    StatutConsultation(String libelle) {
        this.libelle = libelle;
    }

}

