package ma.oralCare.service.modules.facture.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour l'enregistrement d'un paiement
 */
public class PaiementRequest {
    private BigDecimal montant;
    private String modePaiement;
    private LocalDate datePaiement;
    private String reference;
    private String notes;

    public PaiementRequest() {
        this.datePaiement = LocalDate.now();
    }

    // Getters et Setters
    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PaiementRequest{" +
                "montant=" + montant +
                ", modePaiement='" + modePaiement + '\'' +
                ", datePaiement=" + datePaiement +
                ", reference='" + reference + '\'' +
                '}';
    }
}
