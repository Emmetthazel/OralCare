package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Acte extends BaseEntity {

    private String libelle;
    private String categorie;
    private BigDecimal prixDeBase;
    private List<InterventionMedecin> interventionsMedecin;


    @Override
    public String toString() {
        return """
        Acte {
          id = %d,
          libelle = '%s',
          categorie = '%s',
          prixDeBase = %s,
          interventionsMedecinCount = %d
        }
        """.formatted(
                getIdEntite(),
                libelle,
                categorie,
                prixDeBase,
                interventionsMedecin == null ? 0 : interventionsMedecin.size()
        );
    }
}
