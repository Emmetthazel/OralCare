package ma.oralCare.repository.modules.caisse.api;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface de dépôt (Repository) pour l'entité Facture.
 * Gère les opérations de CRUD et les recherches spécifiques aux transactions financières.
 */
public interface FactureRepository extends CrudRepository<Facture, Long> {


    List<Facture> findBySituationFinanciereId(Long situationFinanciereId);


    List<Facture> findByConsultationId(Long consultationId);


    List<Facture> findByStatut(StatutFacture statut);


    List<Facture> findByDateFactureBetween(LocalDateTime startDate, LocalDateTime endDate);

    Facture enregistrerPaiement(Long factureId, Double montantPaye);


    void annulerFacture(Long factureId);


    void updateTotaux(Long factureId, Double nouveauTotalFacture, Double nouveauReste);
}