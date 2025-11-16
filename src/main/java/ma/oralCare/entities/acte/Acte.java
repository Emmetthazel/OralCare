package ma.oralCare.entities.acte;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.consultation.InterventionMedecin;

import java.util.List;

/**
 * Entite representant un acte medical dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Acte extends BaseEntity {

    private Long id;

    private String libelle;

    private String categorie;

    private Double prixDeBase;

    /**
     * Liste des interventions medicales associees a cet acte
     */
    private List<InterventionMedecin> interventionsMedecin;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Acte)) return false;
        Acte that = (Acte) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

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
                id,
                libelle,
                categorie,
                prixDeBase,
                interventionsMedecin == null ? 0 : interventionsMedecin.size()
        );
    }
}
