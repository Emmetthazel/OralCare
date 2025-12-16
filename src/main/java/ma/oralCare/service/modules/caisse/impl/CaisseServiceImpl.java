package ma.oralCare.service.modules.caisse.impl;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.dossierMedical.SituationFinanciere;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;
import ma.oralCare.repository.modules.caisse.impl.FactureRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.api.SituationFinanciereRepository;
import ma.oralCare.repository.modules.dossierMedical.impl.SituationFinanciereRepositoryImpl;
import ma.oralCare.service.modules.caisse.api.CaisseService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service "Caisse" qui orchestre les opérations entre Factures et SituationFinanciere.
 */
public class CaisseServiceImpl implements CaisseService {

    private final FactureRepository factureRepository;
    private final SituationFinanciereRepository situationFinanciereRepository;

    public CaisseServiceImpl() {
        this(new FactureRepositoryImpl(), new SituationFinanciereRepositoryImpl());
    }

    public CaisseServiceImpl(FactureRepository factureRepository,
                             SituationFinanciereRepository situationFinanciereRepository) {
        this.factureRepository = Objects.requireNonNull(factureRepository);
        this.situationFinanciereRepository = Objects.requireNonNull(situationFinanciereRepository);
    }

    // -------------------------------------------------------------------------
    // Factures
    // -------------------------------------------------------------------------

    public Facture enregistrerFacture(Facture facture) {
        Objects.requireNonNull(facture, "facture ne doit pas être null");
        factureRepository.create(facture);
        return facture;
    }

    public List<Facture> getFacturesBetween(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null) return List.of();
        return factureRepository.findByDateFactureBetween(debut, fin);
    }

    public Optional<Facture> getFactureById(Long id) {
        if (id == null) return Optional.empty();
        return factureRepository.findById(id);
    }

    public Facture enregistrerPaiement(Long factureId, Double montantPaye) {
        Objects.requireNonNull(factureId, "factureId ne doit pas être null");
        return factureRepository.enregistrerPaiement(factureId, montantPaye);
    }

    // -------------------------------------------------------------------------
    // Situation Financière
    // -------------------------------------------------------------------------

    public Optional<SituationFinanciere> getSituationFinanciereById(Long id) {
        if (id == null) return Optional.empty();
        return situationFinanciereRepository.findById(id);
    }

    public SituationFinanciere sauverSituationFinanciere(SituationFinanciere situation) {
        Objects.requireNonNull(situation, "situation ne doit pas être null");
        if (situation.getIdEntite() == null) {
            situationFinanciereRepository.create(situation);
        } else {
            situationFinanciereRepository.update(situation);
        }
        return situation;
    }
}


