package ma.oralCare.entities.enums;

import lombok.Getter;

/**
 * Enumeration representant le niveau de risque d un antecedent medical
 */
@Getter

public enum NiveauDeRisque {
    LOW("Faible"),
    MEDIUM("Moyen"),
    HIGH("Élevé");

    private final String libelle;

    NiveauDeRisque(String libelle) {
        this.libelle = libelle;
    }

}

