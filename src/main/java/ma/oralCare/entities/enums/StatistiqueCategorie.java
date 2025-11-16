package ma.oralCare.entities.enums;

/**
 * Enumeration representant la categorie d une statistique
 */
public enum StatistiqueCategorie {
    REVENUE("Revenus"),
    EXPENSE("Dépenses"),
    PATIENT_COUNT("Nombre de patients"),
    APPOINTMENT_COUNT("Nombre de rendez-vous"),
    TREATMENT_COUNT("Nombre de traitements");
    
    private final String libelle;
    
    StatistiqueCategorie(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

