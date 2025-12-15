package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Certificat extends BaseEntity {

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private int duree;

    private String noteMedecin;

    private Consultation consultation;


    @Override
    public String toString() {
        return """
    Certificat {
      id = %d,
      dateDebut = %s,
      dateFin = %s,
      duree = %d,
      noteMedecin = %s,
      dossierMedicaleId = %s,
      consultationId = %s
    }
    """.formatted(
                getIdEntite(),
                dateDebut != null ? dateDebut : "null",
                dateFin != null ? dateFin : "null",
                duree,
                noteMedecin != null ? noteMedecin : "null",
                consultation != null ? consultation.getIdEntite() : "null"
        );
    }

}
