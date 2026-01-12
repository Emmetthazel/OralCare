package ma.oralCare.service.modules.agenda.dto;

public class RDVDisplayModel {
    private String heure;
    private String patientNom;
    private String patientPrenom;
    private String typeSoin;
    private String statut;

    public RDVDisplayModel(String heure, String patientNom, String patientPrenom, String typeSoin, String statut) {
        this.heure = heure;
        this.patientNom = patientNom;
        this.patientPrenom = patientPrenom;
        this.typeSoin = typeSoin;
        this.statut = statut;
    }

    // Getters
    public String getHeure() { return heure; }
    public String getPatientNom() { return patientNom; }
    public String getPatientPrenom() { return patientPrenom; }
    public String getTypeSoin() { return typeSoin; }
    public String getStatut() { return statut; }
}