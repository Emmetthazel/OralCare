package ma.oralCare.service.modules.RDV.dto;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO pour la mise à jour d'un rendez-vous
 */
public class RDVUpdateRequest {
    private Long patientId;
    private Long medecinId;
    private LocalDate date;
    private LocalTime heureDebut;
    private LocalTime heureFin;
    private String motif;
    private String notes;
    private String statut;

    // Getters et Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(LocalTime heureDebut) {
        this.heureDebut = heureDebut;
    }

    public LocalTime getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(LocalTime heureFin) {
        this.heureFin = heureFin;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "RDVUpdateRequest{" +
                "patientId=" + patientId +
                ", medecinId=" + medecinId +
                ", date=" + date +
                ", heureDebut=" + heureDebut +
                ", heureFin=" + heureFin +
                ", motif='" + motif + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
