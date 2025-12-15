package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter
public enum StatutConsultation {
    SCHEDULED("Programmée"),
    COMPLETED("Terminée"),
    CANCELLED("Annulée");

    private final String libelle;

    StatutConsultation(String libelle) {
        this.libelle = libelle;
    }

}

