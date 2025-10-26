package ma.oralCare.entities;

/**
 * Enumération représentant le niveau de risque d'un antécédent médical
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
