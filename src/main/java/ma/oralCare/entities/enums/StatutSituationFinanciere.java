package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter

public enum StatutSituationFinanciere {
    ACTIVE("Active"),
    ARCHIVED("Archivée"),
    CLOSED("Fermée");

    private final String libelle;

    StatutSituationFinanciere(String libelle) {
        this.libelle = libelle;
    }

}

