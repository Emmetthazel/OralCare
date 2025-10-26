package ma.oralCare.entities;

/**
 * Enumération représentant le statut d'une situation financière
 */
public enum StatutSituationFinanciere {
    ACTIVE("Active"),
    CLOSED("Fermée"),
    ARCHIVED("Archivée");
    
    private final String libelle;
    
    StatutSituationFinanciere(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
