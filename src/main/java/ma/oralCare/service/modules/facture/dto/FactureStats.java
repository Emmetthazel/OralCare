package ma.oralCare.service.modules.facture.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO pour les statistiques de facturation
 */
public class FactureStats {
    private long totalFactures;
    private long facturesPayees;
    private long facturesImpayees;
    private long facturesPartiellementPayees;
    private BigDecimal totalMontant;
    private BigDecimal totalPaye;
    private BigDecimal totalCreances;
    private BigDecimal moyenneFacture;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;

    public FactureStats() {}

    // Getters et Setters
    public long getTotalFactures() {
        return totalFactures;
    }

    public void setTotalFactures(long totalFactures) {
        this.totalFactures = totalFactures;
    }

    public long getFacturesPayees() {
        return facturesPayees;
    }

    public void setFacturesPayees(long facturesPayees) {
        this.facturesPayees = facturesPayees;
    }

    public long getFacturesImpayees() {
        return facturesImpayees;
    }

    public void setFacturesImpayees(long facturesImpayees) {
        this.facturesImpayees = facturesImpayees;
    }

    public long getFacturesPartiellementPayees() {
        return facturesPartiellementPayees;
    }

    public void setFacturesPartiellementPayees(long facturesPartiellementPayees) {
        this.facturesPartiellementPayees = facturesPartiellementPayees;
    }

    public BigDecimal getTotalMontant() {
        return totalMontant;
    }

    public void setTotalMontant(BigDecimal totalMontant) {
        this.totalMontant = totalMontant;
    }

    public BigDecimal getTotalPaye() {
        return totalPaye;
    }

    public void setTotalPaye(BigDecimal totalPaye) {
        this.totalPaye = totalPaye;
    }

    public BigDecimal getTotalCreances() {
        return totalCreances;
    }

    public void setTotalCreances(BigDecimal totalCreances) {
        this.totalCreances = totalCreances;
    }

    public BigDecimal getMoyenneFacture() {
        return moyenneFacture;
    }

    public void setMoyenneFacture(BigDecimal moyenneFacture) {
        this.moyenneFacture = moyenneFacture;
    }

    public LocalDate getPeriodeDebut() {
        return periodeDebut;
    }

    public void setPeriodeDebut(LocalDate periodeDebut) {
        this.periodeDebut = periodeDebut;
    }

    public LocalDate getPeriodeFin() {
        return periodeFin;
    }

    public void setPeriodeFin(LocalDate periodeFin) {
        this.periodeFin = periodeFin;
    }

    @Override
    public String toString() {
        return "FactureStats{" +
                "totalFactures=" + totalFactures +
                ", facturesPayees=" + facturesPayees +
                ", facturesImpayees=" + facturesImpayees +
                ", totalMontant=" + totalMontant +
                ", totalPaye=" + totalPaye +
                ", totalCreances=" + totalCreances +
                '}';
    }
}
