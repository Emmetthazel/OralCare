package ma.oralCare.repository.modules.agenda.dto;

import ma.oralCare.entities.agenda.RDV;

public class RDVWithPatient extends RDV {
    private String patientNom;
    private String patientPrenom;

    // getters & setters
    public String getPatientNom() { return patientNom; }
    public void setPatientNom(String patientNom) { this.patientNom = patientNom; }
    public String getPatientPrenom() { return patientPrenom; }
    public void setPatientPrenom(String patientPrenom) { this.patientPrenom = patientPrenom; }
}
