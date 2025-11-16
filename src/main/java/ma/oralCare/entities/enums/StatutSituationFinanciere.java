package ma.oralCare.entities.enums;

/**
 * Enumeration representant le statut d une situation financiere
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

