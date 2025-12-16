package ma.oralCare.service.modules.caisse.api;

import ma.oralCare.entities.cabinet.Charges; // Supposons l'existence d'une entité Charge
import java.time.LocalDate;
import java.util.List;

public interface ChargesService {

    // Gestion CRUD de l'entité Charge
    Charges enregistrerCharge(Charges charge);
    Charges updateCharge(Charges charge);

    // Analyse des charges
    List<Charges> getChargesBetween(LocalDate start, LocalDate end);

    /**
     * Calcule le total des charges engagées pour une période donnée.
     */
    Double calculerChargesTotales(LocalDate start, LocalDate end);
}