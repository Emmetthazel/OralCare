package ma.oralCare.service.modules.patient.dto;

import java.time.LocalDate;

/**
 * DTO pour la création d'un patient
 */
public class PatientCreateRequest {
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String sexe;
    private String telephone;
    private String email;
    private String cin;
    private String numero;
    private String rue;
    private String codePostal;
    private String ville;
    private String pays;
    private String complement;
    private String assurance;

    public PatientCreateRequest() {}

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNumeroSecuriteSociale() {
        return numero;
    }

    public String getAdresse() {
        return rue + " " + codePostal + " " + ville + " " + pays;
    }

    public void setAdresse(String adresse) {
        String[] parts = adresse.split(",");
        if (parts.length >= 4) {
            this.rue = parts[0].trim();
            this.codePostal = parts[1].trim();
            this.ville = parts[2].trim();
            this.pays = parts[3].trim();
        }
    }

    public String getRue() {
        return rue;
    }

    public void setRue(String rue) {
        this.rue = rue;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getAssurance() {
        return assurance;
    }

    public void setAssurance(String assurance) {
        this.assurance = assurance;
    }

    @Override
    public String toString() {
        return "PatientCreateRequest{" +
                "nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", sexe='" + sexe + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", cin='" + cin + '\'' +
                ", assurance='" + assurance + '\'' +
                '}';
    }
}
