package ma.oralCare.service.modules.cabinet.impl;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.service.modules.cabinet.api.ParametrageService;

import java.util.Objects;
import java.util.Optional;

/**
 * Service dédié au paramétrage du cabinet (modification des infos générales).
 */
public class ParametrageServiceImpl implements ParametrageService {

    private final CabinetMedicaleRepository cabinetRepository;

    public ParametrageServiceImpl() {
        this(new CabinetMedicaleRepositoryImpl());
    }

    public ParametrageServiceImpl(CabinetMedicaleRepository cabinetRepository) {
        this.cabinetRepository = Objects.requireNonNull(cabinetRepository);
    }

    public Optional<CabinetMedicale> chargerParametrage(Long cabinetId) {
        if (cabinetId == null) return Optional.empty();
        return cabinetRepository.findById(cabinetId);
    }

    public CabinetMedicale mettreAJourParametrage(CabinetMedicale cabinet) {
        Objects.requireNonNull(cabinet, "cabinet ne doit pas être null");
        if (cabinet.getIdEntite() == null) {
            throw new IllegalArgumentException("Le cabinet doit déjà exister pour être paramétré (idEntite non null)");
        }
        cabinetRepository.update(cabinet);
        return cabinet;
    }
}


