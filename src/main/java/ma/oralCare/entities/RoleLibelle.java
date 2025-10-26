package ma.oralCare.entities;

/**
 * Enumération représentant le libellé d'un rôle
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
