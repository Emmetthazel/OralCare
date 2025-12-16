package ma.oralCare.service.modules.cabinet.api;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import java.util.Optional;

public interface ParametrageService {

    /**
     * Charge les informations actuelles du cabinet pour la page de paramétrage.
     */
    Optional<CabinetMedicale> chargerParametrage(Long cabinetId);

    /**
     * Met à jour les informations du cabinet.
     * Inclut la validation que le cabinet existe déjà.
     */
    CabinetMedicale mettreAJourParametrage(CabinetMedicale cabinet);
}