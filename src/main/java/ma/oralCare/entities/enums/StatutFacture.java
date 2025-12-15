package ma.oralCare.entities.enums;


public enum StatutFacture {
    PAID("Payée"),
    PENDING("En attente"),
    OVERDUE("En retard");

    private final String libelle;
    
    StatutFacture(String libelle) {
        this.libelle = libelle;
    }

}

