package ma.oralCare.entities.dossierMedical;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.oralCare.entities.base.BaseEntity;
import ma.oralCare.entities.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class Facture extends BaseEntity {

    private BigDecimal totaleFacture;

    private BigDecimal totalePaye;

    private BigDecimal reste;

    private StatutFacture statut;

    private LocalDateTime dateFacture;

    private SituationFinanciere situationFinanciere;

    private Consultation consultation;

    @Override
    public String toString() {
        return """
                Facture {
                  id = %d,
                  totaleFacture = %s,
                  totalePaye = %s,
                  reste = %s,
                  statut = %s,
                  dateFacture = %s,
                  situationFinanciere = %s,
                  consultation = %s
                }
                """.formatted(
                getIdEntite(),
                totaleFacture,
                totalePaye,
                reste,
                statut,
                dateFacture,
                situationFinanciere == null ? situationFinanciere.getStatut() : "null",
                consultation == null ? consultation.getStatut() : "null"
        );
    }
}