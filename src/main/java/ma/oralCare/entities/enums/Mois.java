package ma.oralCare.entities.enums;

import lombok.Getter;

/**
 * Enumeration representant les mois de l annee
 */
@Getter

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

}

