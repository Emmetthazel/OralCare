package ma.oralCare.service.modules.caisse.impl;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;
import ma.oralCare.repository.modules.caisse.impl.FactureRepositoryImpl;
import ma.oralCare.service.modules.caisse.api.FactureService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service dédié aux opérations métier sur les factures.
 */
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;

    public FactureServiceImpl() {
        this(new FactureRepositoryImpl());
    }

    public FactureServiceImpl(FactureRepository factureRepository) {
        this.factureRepository = Objects.requireNonNull(factureRepository);
    }

    public Facture createFacture(Facture facture) {
        Objects.requireNonNull(facture, "facture ne doit pas être null");
        factureRepository.create(facture);
        return facture;
    }

    public Optional<Facture> getFactureById(Long id) {
        if (id == null) return Optional.empty();
        return factureRepository.findById(id);
    }

    public List<Facture> getFacturesBySituationFinanciere(Long situationId) {
        if (situationId == null) return List.of();
        return factureRepository.findBySituationFinanciereId(situationId);
    }

    public List<Facture> getFacturesByConsultation(Long consultationId) {
        if (consultationId == null) return List.of();
        return factureRepository.findByConsultationId(consultationId);
    }

    public List<Facture> getFacturesByStatut(StatutFacture statut) {
        if (statut == null) return List.of();
        return factureRepository.findByStatut(statut);
    }

    public List<Facture> getFacturesBetween(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return List.of();
        return factureRepository.findByDateFactureBetween(start, end);
    }

    public Facture enregistrerPaiement(Long factureId, Double montantPaye) {
        return factureRepository.enregistrerPaiement(factureId, montantPaye);
    }

    public void annulerFacture(Long factureId) {
        if (factureId == null) return;
        factureRepository.annulerFacture(factureId);
    }

    public void updateTotaux(Long factureId, Double total, Double reste) {
        if (factureId == null) return;
        factureRepository.updateTotaux(factureId, total, reste);
    }
}


