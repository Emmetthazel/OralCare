package ma.oralCare.entities.enums;


import lombok.Getter;

@Getter

public enum RoleLibelle {
    ADMIN("Administrateur"),
    DOCTOR("Médecin"),
    SECRETARY("Secrétaire"),
    RECEPTIONIST("Réceptionniste");
    
    private final String libelle;
    
    RoleLibelle(String libelle) {
        this.libelle = libelle;
    }

}

