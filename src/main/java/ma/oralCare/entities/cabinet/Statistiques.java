package ma.oralCare.entities.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.common.BaseEntity;
import ma.oralCare.entities.enums.StatistiqueCategorie;

import java.time.LocalDate;

/**
 * Entite representant les statistiques dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statistiques extends BaseEntity {

    private Long id;

    private String nom;

    private StatistiqueCategorie categorie;

    private Double chiffre;

    private LocalDate dateCalcul;

    /**
     * Cabinet medical associe a cette statistique
     */
    private CabinetMedicale cabinetMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Statistiques)) return false;
        Statistiques that = (Statistiques) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Statistiques {
          id = %d,
          nom = '%s',
          categorie = %s,
          chiffre = %s,
          dateCalcul = %s
        }
        """.formatted(
                id,
                nom,
                categorie,
                chiffre,
                dateCalcul
        );
    }
}
