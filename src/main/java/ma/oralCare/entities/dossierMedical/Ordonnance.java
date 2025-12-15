package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Ordonnance extends BaseEntity {

    private LocalDate date;

    private DossierMedicale dossierMedicale;

    private List<Prescription> prescriptions;

    private Consultation consultation;

    @Override
    public String toString() {
        return """
    Ordonnance {
      id = %d,
      date = %s,
      prescriptionsCount = %d
    }
    """.formatted(
                getIdEntite(),
                date,
                prescriptions == null ? 0 : prescriptions.size()
        );
    }

}
