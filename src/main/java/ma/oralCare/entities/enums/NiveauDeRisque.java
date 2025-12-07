package ma.oralCare.entities.enums;

/**
 * Enumeration representant le niveau de risque d un antecedent medical
 */
public enum NiveauDeRisque {
    LOW("Faible"),
    MEDIUM("Moyen"),
    HIGH("Élevé");

    private final String libelle;

    NiveauDeRisque(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}

