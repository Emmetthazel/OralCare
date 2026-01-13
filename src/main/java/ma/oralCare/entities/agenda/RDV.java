package ma.oralCare.entities.agenda;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.dossierMedical.DossierMedicale;
import ma.oralCare.entities.enums.StatutRDV;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entite representant un rendez-vous dans le systeme OralCare
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor

public class RDV extends BaseEntity {

    private LocalDate date;

    private LocalTime heure;

    private String motif;

    private StatutRDV statut;

    private String noteMedecin;

    private Consultation consultation;

    private DossierMedicale dossierMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RDV)) return false;
        RDV that = (RDV) o;
        return getIdEntite() != null && getIdEntite().equals(that.getIdEntite());
    }

    @Override
    public int hashCode() {
        return getIdEntite() != null ? getIdEntite().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
                RDV {
                  id = %d,
                  date = %s,
                  heure = %s,
                  motif = '%s',
                  statut = %s
                }
                """.formatted(
                getIdEntite(),
                date,
                heure,
                motif,
                statut
        );
    }
    // Dans ma.oralCare.entities.agenda.RDV.java

    public String getPatientNomComplet() {
        if (this.dossierMedicale != null && this.dossierMedicale.getPatient() != null) {
            String nom = this.dossierMedicale.getPatient().getNom();
            String prenom = this.dossierMedicale.getPatient().getPrenom();
            return nom.toUpperCase() + " " + prenom;
        }
        return "Patient Inconnu";
    }

    public String getPatientNom() {
        if (this.dossierMedicale != null && this.dossierMedicale.getPatient() != null) {
            return this.dossierMedicale.getPatient().getNom();
        }
        return "Patient Inconnu";
    }

    public String getMedecinNom() {
        if (this.dossierMedicale != null && this.dossierMedicale.getMedecin() != null) {
            return this.dossierMedicale.getMedecin().getNom();
        }
        return "Médecin Inconnu";
    }
}
