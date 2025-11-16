package ma.oralCare.entities.consultation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.dossier.DossierMedicale;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.facture.Facture;
import ma.oralCare.entities.rdv.RDV;

import java.time.LocalDate;
import java.util.List;

/**
 * Entite representant une consultation dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation extends BaseEntity {

    private Long id;

    private LocalDate date;

    private StatutConsultation statut;

    private String observationMedecin;

    /**
     * Dossier medical associe a la consultation
     */
    private DossierMedicale dossierMedicale;

    /**
     * Liste des interventions du medecin lors de cette consultation
     */
    private List<InterventionMedecin> interventionsMedecin;

    /**
     * Liste des factures associees a la consultation
     */
    private List<Facture> factures;

    /**
     * Liste des ordonnances associees a la consultation
     */
    private List<Ordonnance> ordonnances;

    /**
     * Certificat associe a la consultation
     */
    private Certificat certificat;

    /**
     * Liste des rendez-vous associes a la consultation
     */
    private List<RDV> rendezVous;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Consultation)) return false;
        Consultation that = (Consultation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Consultation {
          id = %d,
          date = %s,
          statut = %s,
          observationMedecin = '%s',
          facturesCount = %d,
          ordonnancesCount = %d
        }
        """.formatted(
                id,
                date,
                statut,
                observationMedecin,
                factures == null ? 0 : factures.size(),
                ordonnances == null ? 0 : ordonnances.size()
        );
    }
}
