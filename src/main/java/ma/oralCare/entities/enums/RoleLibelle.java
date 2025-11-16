package ma.oralCare.entities.enums;

/**
 * Enumeration representant le libelle d un role
 */
public enum RoleLibelle {
    ADMIN("Administrateur"),
    DOCTOR("Médecin"),
    SECRETARY("Secrétaire"),
    RECEPTIONIST("Réceptionniste");
    
    private final String libelle;
    
    RoleLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getLibelle() {
        return libelle;
    }
}

