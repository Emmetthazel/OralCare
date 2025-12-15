package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.patient.Patient;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.users.Medecin;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class DossierMedicale extends BaseEntity {

    private Patient patient;

    private SituationFinanciere situationFinanciere;

    private List<Consultation> consultations;

    private List<Ordonnance> ordonnances;

    private List<Certificat> certificats;

    private List<RDV> rendezVous;

    private Medecin medecin;


    @Override
    public String toString() {
        return "DossierMedicale{" +
                "id=" + getIdEntite() +
                ", patient=" + (patient != null ? patient.getNom() : "null") +
                ", consultations=" + (consultations != null ? consultations.size() : 0) +
                ", ordonnances=" + (ordonnances != null ? ordonnances.size() : 0) +
                ", certificats=" + (certificats != null ? certificats.size() : 0) +
                ", situationFinanciere=" + (situationFinanciere != null ? situationFinanciere.getIdEntite() : "null") +
                ", rendezVous=" + (rendezVous != null ? rendezVous.size() : 0) +
                ", medecin=" + (medecin != null ? medecin.getNom() : "null") +
                '}';
    }

}
