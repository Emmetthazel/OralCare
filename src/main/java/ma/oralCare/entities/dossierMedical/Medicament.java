package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.FormeMedicament;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Medicament extends BaseEntity {

    private String nom;

    private String laboratoire;

    private String type;

    private FormeMedicament forme;

    private Boolean remboursable;

    private BigDecimal prixUnitaire;

    private String description;

    private List<Prescription> prescriptions;

    @Override
    public String toString() {
        return """
        Medicament {
          id = %d,
          nom = '%s',
          laboratoire = '%s',
          type = '%s',
          forme = %s,
          remboursable = %s,
          prixUnitaire = %s,
          description = '%s',
          prescriptionsCount = %d
        }
        """.formatted(
                getIdEntite(),
                nom,
                laboratoire,
                type,
                forme,
                remboursable,
                prixUnitaire,
                description,
                prescriptions == null ? 0 : prescriptions.size()
        );
    }
}
