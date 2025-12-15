package ma.oralCare.entities.enums;


public enum CategorieAntecedent {
    ALLERGIE("ALLERGIE"),
    MALADIE_CHRONIQUE("MALADIE_CHRONIQUE"),
    CONTRE_INDICATION("CONTRE_INDICATION"),
    TRAITEMENT_EN_COURS("TRAITEMENT_EN_COURS"),
    ANTECEDENT_CHIRURGICAL("ANTECEDENT_CHIRURGICAL"),
    ANTECEDENT_INFECTIEUX("ANTECEDENT_INFECTIEUX"),
    ANTECEDENT_DENTAIRE("ANTECEDENT_DENTAIRE"),
    HABITUDE_DE_VIE("HABITUDE_DE_VIE"),
    AUTRE("AUTRE"),;

    private final String libelle;

    CategorieAntecedent(String libelle) {
        this.libelle = libelle;
    }

}

