package ma.oralCare.service.modules.facture.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour la mise à jour d'une facture
 */
public class FactureUpdateRequest {
    private String numero;
    private LocalDate dateFacture;
    private BigDecimal totaleFacture;
    private String statut;
    private String notes;

    // Getters et Setters
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
        return "FactureUpdateRequest{" +
                "numero='" + numero + '\'' +
                ", dateFacture=" + dateFacture +
                ", totaleFacture=" + totaleFacture +
                ", statut='" + statut + '\'' +
                '}';
    }
}
