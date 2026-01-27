package ma.oralCare.mvc.dto.admin;

public class UserStaffDTO {
    private String nomComplet;
    private String email;
    private String statut;
    private String role;       // MÉDECIN ou SECRÉTAIRE
    private String cabinetNom;
    private String tel;
    private String cin;
    private String dateNaissance;
    private String sexe;
    private String adresse;
    private String specialite; // Pour les médecins
    private String numCnss;    // Pour les secrétaires
    private String password;

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
    public String getTel() { return tel; }
    public String getCin() { return cin; }
    public String getDateNaissance() { return dateNaissance; }
    public String getSexe() { return sexe; }
    public String getAdresse() { return adresse; }
    public String getSpecialite() { return specialite; }
    public String getNumCnss() { return numCnss; }
    public String getPassword() { return password; }
    
    // Setters
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public void setEmail(String email) { this.email = email; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setRole(String role) { this.role = role; }
    public void setCabinetNom(String cabinetNom) { this.cabinetNom = cabinetNom; }
    public void setTel(String tel) { this.tel = tel; }
    public void setCin(String cin) { this.cin = cin; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public void setSexe(String sexe) { this.sexe = sexe; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public void setNumCnss(String numCnss) { this.numCnss = numCnss; }
    public void setPassword(String password) { this.password = password; }
}