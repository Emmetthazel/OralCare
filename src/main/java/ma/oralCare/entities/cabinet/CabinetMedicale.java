package ma.oralCare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.Adresse;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.staff.Staff;

import java.util.List;

/**
 * Entite representant un cabinet medical dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CabinetMedicale extends BaseEntity {

    private Long id;

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

    /**
     * Liste des charges du cabinet medical
     */
    private List<Charges> charges;

    /**
     * Liste des revenus du cabinet medical
     */
    private List<Revenues> revenues;

    /**
     * Liste des statistiques du cabinet medical
     */
    private List<Statistiques> statistiques;

    /**
     * Liste du personnel du cabinet medical
     */
    private List<Staff> staff;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CabinetMedicale)) return false;
        CabinetMedicale that = (CabinetMedicale) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        CabinetMedicale {
          id = %d,
          nom = '%s',
          email = '%s',
          tel1 = '%s',
          tel2 = '%s',
          chargesCount = %d,
          revenuesCount = %d,
          staffCount = %d
        }
        """.formatted(
                id,
                nom,
                email,
                tel1,
                tel2,
                charges == null ? 0 : charges.size(),
                revenues == null ? 0 : revenues.size(),
                staff == null ? 0 : staff.size()
        );
    }
}
