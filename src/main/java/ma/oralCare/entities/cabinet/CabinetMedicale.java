package ma.oralCare.entities.cabinet;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.Adresse;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.users.Staff;

import java.util.List;

/**
 * Entite representant un cabinet medical dans le systeme OralCare
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class CabinetMedicale extends BaseEntity {

    private String nom;

    private String email;

    private String logo;

    private Adresse adresse;

    private String cin;

    private String tel1;

    private String tel2;

    private String siteWeb;

    private String instagram;

    private String facebook;

    private String description;

    private List<Charges> charges;

    private List<Revenues> revenues;

    private List<Statistiques> statistiques;

    private List<Staff> staff;

    @Override
    public String toString() {
        return """
        CabinetMedicale {
          id = %d,
          nom = '%s',
          email = '%s',
          logo = '%s',
          adresse = '%s',
          cin = '%s',
          tel1 = '%s',
          tel2 = '%s',
          siteWeb = '%s',
          instagram = '%s',
          facebook = '%s',
          description = '%s',
          chargesCount = %d,
          revenuesCount = %d,
          statistiquesCount = %d,
          staffCount = %d
        }
        """.formatted(
                getIdEntite(),
                nom,
                email,
                logo,
                adresse,
                cin,
                tel1,
                tel2,
                siteWeb,
                instagram,
                facebook,
                description,
                charges == null ? 0 : charges.size(),
                revenues == null ? 0 : revenues.size(),
                statistiques == null ? 0 : statistiques.size(),
                staff == null ? 0 : staff.size()
        );
    }
}
