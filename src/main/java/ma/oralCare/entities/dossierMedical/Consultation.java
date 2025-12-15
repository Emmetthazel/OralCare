package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.dossierMedical.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entite representant une consultation dans le systeme OralCare
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Consultation extends BaseEntity {

    private LocalDate date;

    private StatutConsultation statut;

    private String observationMedecin;

    private DossierMedicale dossierMedicale;

    private List<InterventionMedecin> interventionsMedecin;

    private List<Facture> factures;

    private List<Ordonnance> ordonnances;

    private Certificat certificat;

    private List<RDV> rendezVous;


    @Override
    public String toString() {
        return """
        Consultation {
          id = %d,
          date = %s,
          statut = %s,
          observationMedecin = '%s',
          dossierMedicale = %s,
          interventionsMedecinCount = %d,
          facturesCount = %d,
          ordonnancesCount = %d,
          certificat = '%s',
          rendezVousCount = %d
        }
        """.formatted(
                getIdEntite(),
                date,
                statut,
                observationMedecin,
                dossierMedicale != null ? dossierMedicale.getPatient() : "null",
                interventionsMedecin != null ? interventionsMedecin.size() : 0,
                ordonnances != null ? ordonnances.size() : 0,
                certificat != null ? "Yes (ID: \" + certificat.getIdEntite() + \")" : "null",
                factures == null ? 0 : factures.size(),
                ordonnances == null ? 0 : ordonnances.size(),
                rendezVous == null ? 0 : rendezVous.size()
        );
    }
}
