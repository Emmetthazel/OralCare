package ma.oralCare.entities.enums;

import lombok.Getter;

@Getter

public enum FormeMedicament {
    TABLET("Comprimé"),
    CAPSULE("Gélule"),
    SYRUP("Sirop"),
    INJECTION("Injection"),
    CREAM("Crème"),
    OINTMENT("Pommade"),
    DROPS("Gouttes");
    
    private final String libelle;
    
    FormeMedicament(String libelle) {
        this.libelle = libelle;
    }

}

