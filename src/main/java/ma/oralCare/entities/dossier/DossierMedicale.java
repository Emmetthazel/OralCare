package ma.oralCare.entities.dossier;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.consultation.Consultation;
import ma.oralCare.entities.consultation.Ordonnance;
import ma.oralCare.entities.consultation.Certificat;
import ma.oralCare.entities.facture.SituationFinanciere;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.rdv.RDV;
import ma.oralCare.entities.staff.Medecin;

import java.time.LocalDate;
import java.util.List;

/**
 * Entite representant un dossier medical dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedicale extends BaseEntity {

    private Long id;

    private LocalDate dateDeCreation;

    /**
     * Patient associe au dossier medical
     */
    private Patient patient;

    /**
     * Liste des consultations du dossier medical
     */
    private List<Consultation> consultations;

    /**
     * Liste des ordonnances du dossier medical
     */
    private List<Ordonnance> ordonnances;

    /**
     * Liste des certificats du dossier medical
     */
    private List<Certificat> certificats;

    /**
     * Situation financiere du dossier medical
     */
    private SituationFinanciere situationFinanciere;

    /**
     * Liste des rendez-vous du dossier medical
     */
    private List<RDV> rendezVous;

    /**
     * Medecin associe au dossier medical
     */
    private Medecin medecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DossierMedicale)) return false;
        DossierMedicale that = (DossierMedicale) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        DossierMedicale {
          id = %d,
          dateDeCreation = %s,
          consultationsCount = %d,
          ordonnancesCount = %d
        }
        """.formatted(
                id,
                dateDeCreation,
                consultations == null ? 0 : consultations.size(),
                ordonnances == null ? 0 : ordonnances.size()
        );
    }
}
