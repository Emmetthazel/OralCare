package ma.oralCare.service.modules.cabinet.impl;

import ma.oralCare.entities.cabinet.CabinetMedicale;
import ma.oralCare.entities.cabinet.Charges;
import ma.oralCare.entities.cabinet.Revenues;
import ma.oralCare.entities.cabinet.Statistiques;
import ma.oralCare.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.oralCare.repository.modules.cabinet.api.ChargesRepository;
import ma.oralCare.repository.modules.cabinet.api.RevenuesRepository;
import ma.oralCare.repository.modules.cabinet.api.StatistiquesRepository;
import ma.oralCare.repository.modules.cabinet.impl.CabinetMedicaleRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.impl.ChargesRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.impl.RevenuesRepositoryImpl;
import ma.oralCare.repository.modules.cabinet.impl.StatistiquesRepositoryImpl;
import ma.oralCare.service.modules.cabinet.api.CabinetMedicalService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service de haut niveau pour la gestion du cabinet (informations, charges, revenus, statistiques).
 *
 * Implémentation simple qui délègue aux repositories existants, sans framework d'injection.
 */
public class CabinetMedicalServiceImpl implements CabinetMedicalService {

    private final CabinetMedicaleRepository cabinetRepository;
    private final ChargesRepository chargesRepository;
    private final RevenuesRepository revenuesRepository;
    private final StatistiquesRepository statistiquesRepository;

    public CabinetMedicalServiceImpl() {
        this(
                new CabinetMedicaleRepositoryImpl(),
                new ChargesRepositoryImpl(),
                new RevenuesRepositoryImpl(),
                new StatistiquesRepositoryImpl()
        );
    }

    public CabinetMedicalServiceImpl(CabinetMedicaleRepository cabinetRepository,
                                     ChargesRepository chargesRepository,
                                     RevenuesRepository revenuesRepository,
                                     StatistiquesRepository statistiquesRepository) {
        this.cabinetRepository = Objects.requireNonNull(cabinetRepository);
        this.chargesRepository = Objects.requireNonNull(chargesRepository);
        this.revenuesRepository = Objects.requireNonNull(revenuesRepository);
        this.statistiquesRepository = Objects.requireNonNull(statistiquesRepository);
    }

    // -------------------------------------------------------------------------
    // Cabinet
    // -------------------------------------------------------------------------

    public Optional<CabinetMedicale> getCabinetById(Long id) {
        if (id == null) return Optional.empty();
        return cabinetRepository.findById(id);
    }

    public CabinetMedicale saveCabinet(CabinetMedicale cabinet) {
        Objects.requireNonNull(cabinet, "cabinet ne doit pas être null");
        if (cabinet.getIdEntite() == null) {
            cabinetRepository.create(cabinet);
        } else {
            cabinetRepository.update(cabinet);
        }
        return cabinet;
    }

    // -------------------------------------------------------------------------
    // Charges
    // -------------------------------------------------------------------------

    public Charges createCharge(Charges charges) {
        Objects.requireNonNull(charges, "charges ne doit pas être null");
        chargesRepository.create(charges);
        return charges;
    }

    public List<Charges> getChargesByCabinet(Long cabinetId) {
        if (cabinetId == null) return List.of();
        return chargesRepository.findByCabinetMedicaleId(cabinetId);
    }

    public List<Charges> getChargesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return List.of();
        return chargesRepository.findByDateBetween(start, end);
    }

    public Double getTotalChargesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return 0.0;
        return chargesRepository.calculateTotalChargesByDateBetween(start, end);
    }

    // -------------------------------------------------------------------------
    // Revenus & Statistiques (simplifiés)
    // -------------------------------------------------------------------------

    public Revenues createRevenu(Revenues revenu) {
        Objects.requireNonNull(revenu, "revenu ne doit pas être null");
        revenuesRepository.create(revenu);
        return revenu;
    }

    public List<Revenues> getAllRevenus() {
        return revenuesRepository.findAll();
    }

    public List<Statistiques> getAllStatistiques() {
        return statistiquesRepository.findAll();
    }
}


