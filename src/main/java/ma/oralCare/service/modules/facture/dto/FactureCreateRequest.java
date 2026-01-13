package ma.oralCare.service.modules.facture.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour la création d'une facture
 */
public class FactureCreateRequest {
    private Long patientId;
    private String numero;
    private LocalDate dateFacture;
    private BigDecimal totaleFacture;
    private String statut;
    private String notes;

    public FactureCreateRequest() {
        this.dateFacture = LocalDate.now();
        this.statut = "EN_ATTENTE";
    }

    // Getters et Setters
    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDate getDateFacture() {
        return dateFacture;
    }

    public void setDateFacture(LocalDate dateFacture) {
        this.dateFacture = dateFacture;
    }

    public BigDecimal getTotaleFacture() {
        return totaleFacture;
    }

    public void setTotaleFacture(BigDecimal totaleFacture) {
        this.totaleFacture = totaleFacture;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "FactureCreateRequest{" +
                "patientId=" + patientId +
                ", numero='" + numero + '\'' +
                ", dateFacture=" + dateFacture +
                ", totaleFacture=" + totaleFacture +
                ", statut='" + statut + '\'' +
                '}';
    }
}
