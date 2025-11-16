package ma.oralCare.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.agenda.AgendaMensuel;
import ma.oralCare.entities.dossier.DossierMedicale;

import java.util.List;

/**
 * Entite representant un medecin dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medecin extends Staff {

    private String specialite;

    private AgendaMensuel agendaMensuel;

    /**
     * Liste des dossiers medicale associe au medecin
     */
    private List<DossierMedicale> dossierMedicaux;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Medecin)) return false;
        Medecin that = (Medecin) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Medecin {
          id = %d,
          nom = '%s',
          specialite = '%s',
          dossierMedicauxCount = %d
        }
        """.formatted(
                getId(),
                getNom(),
                specialite,
                dossierMedicaux == null ? 0 : dossierMedicaux.size()
        );
    }
}
