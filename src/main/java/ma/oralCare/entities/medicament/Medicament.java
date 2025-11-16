package ma.oralCare.entities.medicament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.FormeMedicament;

import java.util.List;

/**
 * Entite representant un Medicament dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament extends BaseEntity {

    private Long id;

    private String nom;

    private String laboratoire;

    private String type;

    private FormeMedicament forme;

    private Boolean remboursable;

    private Double prixUnitaire;

    private String description;

    /**
     * Liste des prescriptions associees a ce Medicament
     */
    private List<Prescription> prescriptions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medicament)) return false;
        Medicament that = (Medicament) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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
          prescriptionsCount = %d
        }
        """.formatted(
                id,
                nom,
                laboratoire,
                type,
                forme,
                remboursable,
                prixUnitaire,
                prescriptions == null ? 0 : prescriptions.size()
        );
    }
}
