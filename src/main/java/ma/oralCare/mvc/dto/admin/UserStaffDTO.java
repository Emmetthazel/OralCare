package ma.oralCare.mvc.dto.admin;

public class UserStaffDTO {
    private String nomComplet;
    private String email;
    private String statut;
    private String role;       // MÉDECIN ou SECRÉTAIRE
    private String cabinetNom;

    public UserStaffDTO(String nom, String prenom, String email, String statut, String role, String cabinetNom) {
        this.nomComplet = nom + " " + prenom;
        this.email = email;
        this.statut = statut;
        this.role = role;
        this.cabinetNom = cabinetNom;
    }

    // Getters
    public String getNomComplet() { return nomComplet; }
    public String getEmail() { return email; }
    public String getStatut() { return statut; }
    public String getRole() { return role; }
    public String getCabinetNom() { return cabinetNom; }
}