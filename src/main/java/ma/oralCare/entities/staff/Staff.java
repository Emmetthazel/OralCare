package ma.oralCare.entities.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.oralCare.entities.cabinet.CabinetMedicale;

import java.time.LocalDate;

/**
 * Entite representant un membre du personnel dans le systeme OralCare
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff extends Utilisateur {

    private Double salaire;

    private Double prime;

    private LocalDate dateRecrutement;

    private Integer soldeConge;

    /**
     * Cabinet medical ou travaille le membre du personnel
     */
    private CabinetMedicale cabinetMedicale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff that = (Staff) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
        Staff {
          id = %d,
          nom = '%s',
          salaire = %s,
          prime = %s,
          dateRecrutement = %s,
          soldeConge = %d
        }
        """.formatted(
                getId(),
                getNom(),
                salaire,
                prime,
                dateRecrutement,
                soldeConge
        );
    }
}
