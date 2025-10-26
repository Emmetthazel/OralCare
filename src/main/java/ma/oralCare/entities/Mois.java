package ma.oralCare.entities;

/**
 * Enumération représentant les mois de l'année
 */
public enum Mois {
    JANUARY("Janvier"),
    FEBRUARY("Février"),
    MARCH("Mars"),
    APRIL("Avril"),
    MAY("Mai"),
    JUNE("Juin"),
    JULY("Juillet"),
    AUGUST("Août"),
    SEPTEMBER("Septembre"),
    OCTOBER("Octobre"),
    NOVEMBER("Novembre"),
    DECEMBER("Décembre");
    
    private final String libelle;
    
    Mois(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}
