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

    // ✅ Nettoyage des constructeurs complexes.
    // Le repository gère maintenant ses propres connexions via SessionFactory.
    public ParametrageServiceImpl() {
        this.cabinetRepository = new CabinetMedicaleRepositoryImpl();
    }

    // Gardé uniquement pour les tests unitaires si nécessaire
    public ParametrageServiceImpl(CabinetMedicaleRepository cabinetRepository) {
        this.cabinetRepository = Objects.requireNonNull(cabinetRepository);
    }

    @Override
    public Optional<CabinetMedicale> chargerParametrage(Long cabinetId) {
        if (cabinetId == null) return Optional.empty();
        // Le repository ouvrira et fermera sa connexion en interne
        return cabinetRepository.findById(cabinetId);
    }

    @Override
    public CabinetMedicale mettreAJourParametrage(CabinetMedicale cabinet) {
        Objects.requireNonNull(cabinet, "cabinet ne doit pas être null");
        if (cabinet.getIdEntite() == null) {
            throw new IllegalArgumentException("Le cabinet doit déjà exister pour être paramétré (idEntite non null)");
        }

        // Mise à jour via le repository autonome
        cabinetRepository.update(cabinet);
        return cabinet;
    }
}