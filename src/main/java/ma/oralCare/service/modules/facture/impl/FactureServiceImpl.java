package ma.oralCare.service.modules.facture.impl;

import ma.oralCare.entities.dossierMedical.Facture;
import ma.oralCare.entities.enums.StatutFacture;
import ma.oralCare.repository.modules.caisse.api.FactureRepository;
import ma.oralCare.service.modules.facture.api.FactureService;
import ma.oralCare.service.modules.facture.dto.FactureCreateRequest;
import ma.oralCare.service.modules.facture.dto.FactureUpdateRequest;
import ma.oralCare.service.modules.facture.dto.PaiementRequest;
import ma.oralCare.service.modules.facture.dto.FactureStats;
import ma.oralCare.service.modules.realtime.api.RealTimeService;
import ma.oralCare.service.modules.realtime.dto.RealTimeEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des factures
 */
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final RealTimeService realTimeService;

    public FactureServiceImpl(FactureRepository factureRepository, RealTimeService realTimeService) {
        this.factureRepository = factureRepository;
        this.realTimeService = realTimeService;
    }

    @Override
    public Facture createFacture(FactureCreateRequest request) {
        // Validation des données
        validateCreateRequest(request);

        try {
            // Création de la facture
            Facture facture = new Facture();
            // Note: Patient ID should come from situationFinanciere
            // facture.setNumero() - this field doesn't exist
            facture.setDateFacture(request.getDateFacture() != null ? request.getDateFacture().atStartOfDay() : LocalDateTime.now());
            facture.setTotaleFacture(request.getTotaleFacture());
            facture.setStatut(convertStringToStatutFacture(request.getStatut()));
            // facture.setNotes() - this field doesn't exist

            factureRepository.create(facture);
            
            // Retourner la facture créée (le repository ne retourne rien, donc on retourne l'objet créé)
            return facture;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la création de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture updateFacture(Long id, FactureUpdateRequest request) {
        // Validation
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }

        Optional<Facture> existingFactureOpt = factureRepository.findById(id);
        if (existingFactureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée avec l'ID: " + id);
        }

        Facture existingFacture = existingFactureOpt.get();

        // Validation de la mise à jour
        validateUpdateRequest(request);

        try {
            // Mise à jour des champs
            if (request.getDateFacture() != null) {
                existingFacture.setDateFacture(request.getDateFacture().atStartOfDay());
            }
            if (request.getTotaleFacture() != null) {
                existingFacture.setTotaleFacture(request.getTotaleFacture());
            }
            if (request.getStatut() != null) {
                existingFacture.setStatut(convertStringToStatutFacture(request.getStatut()));
            }

            factureRepository.update(existingFacture);
            Facture updatedFacture = existingFacture; // Return the updated facture

            // Publication de l'événement temps réel
            publishFactureEvent("FACTURE_UPDATED", updatedFacture);

            return updatedFacture;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFacture(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID de la facture est obligatoire");
        }

        try {
            factureRepository.deleteById(id);
            
            // Publication de l'événement temps réel
            Facture deletedFacture = new Facture();
            deletedFacture.setIdEntite(id);
            publishFactureEvent("FACTURE_DELETED", deletedFacture);

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression de la facture: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Facture> findFactureById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return factureRepository.findById(id);
    }

    @Override
    public List<Facture> findAllFactures() {
        try {
            return factureRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesByPatient(Long patientId) {
        if (patientId == null) {
            return List.of();
        }
        try {
            return factureRepository.findAll().stream()
                .filter(f -> {
                    // Get patient ID from situationFinanciere
                    if (f.getSituationFinanciere() != null && f.getSituationFinanciere().getDossierMedicale() != null && f.getSituationFinanciere().getDossierMedicale().getPatient() != null) {
                        return patientId.equals(f.getSituationFinanciere().getDossierMedicale().getPatient().getIdEntite());
                    }
                    return false;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures du patient: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesByStatut(String statut) {
        if (statut == null || statut.trim().isEmpty()) {
            return List.of();
        }
        try {
            // Convert string status to StatutFacture enum
            StatutFacture statutEnum = convertStringToStatutFacture(statut.trim().toUpperCase());
            if (statutEnum == null) {
                return List.of();
            }
            return factureRepository.findByStatut(statutEnum);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures par statut: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesByDate(LocalDate date) {
        if (date == null) {
            return List.of();
        }
        try {
            // Convert LocalDate to LocalDateTime range for the entire day
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);
            return factureRepository.findByDateFactureBetween(startOfDay, endOfDay);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures du " + date + ": " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesByPeriode(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || debut.isAfter(fin)) {
            return List.of();
        }
        try {
            // Convert LocalDate to LocalDateTime range
            LocalDateTime startDateTime = debut.atStartOfDay();
            LocalDateTime endDateTime = fin.atTime(23, 59, 59);
            return factureRepository.findByDateFactureBetween(startDateTime, endDateTime);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures de la période: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesImpayees() {
        try {
            return factureRepository.findByStatut(StatutFacture.PENDING);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures impayées: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesPayeesPartiellement() {
        try {
            // Repository doesn't have PARTIAL status, return empty list for now
            return List.of();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures payées partiellement: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Facture> findFacturesPayeesComplement() {
        try {
            return factureRepository.findByStatut(StatutFacture.PAID);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des factures payées complètement: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture enregistrerPaiement(Long factureId, PaiementRequest paiementRequest) {
        Optional<Facture> factureOpt = findFactureById(factureId);
        if (factureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée avec l'ID: " + factureId);
        }

        Facture facture = factureOpt.get();
        
        // Validation du paiement
        validatePaiementRequest(paiementRequest);
        
        // Vérifier que le montant ne dépasse pas le reste à payer
        BigDecimal total = facture.getTotaleFacture() != null ? facture.getTotaleFacture() : BigDecimal.ZERO;
        BigDecimal paye = facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO;
        BigDecimal reste = total.subtract(paye);
        
        if (paiementRequest.getMontant().compareTo(reste) > 0) {
            throw new IllegalArgumentException("Le montant du paiement ne peut pas dépasser le reste à payer: " + reste + " DH");
        }

        try {
            // Mettre à jour le montant payé
            BigDecimal nouveauTotalPaye = paye.add(paiementRequest.getMontant());
            facture.setTotalePaye(nouveauTotalPaye);
            
            // Mettre à jour le statut si nécessaire
            if (nouveauTotalPaye.compareTo(total) >= 0) {
                facture.setStatut(StatutFacture.PAID);
            } else {
                // Repository doesn't have PARTIAL status, keep as PENDING for now
                facture.setStatut(StatutFacture.PENDING);
            }

            factureRepository.update(facture);
            Facture updatedFacture = facture; // Return the updated facture
            
            // Publication de l'événement temps réel
            publishFactureEvent("FACTURE_PAID", updatedFacture);

            return updatedFacture;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du paiement: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture annulerFacture(Long id) {
        Optional<Facture> factureOpt = findFactureById(id);
        if (factureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée avec l'ID: " + id);
        }

        Facture facture = factureOpt.get();
        facture.setStatut(StatutFacture.CANCELLED);
        
        factureRepository.update(facture);
        Facture updatedFacture = facture; // Return the updated facture
        
        // Publication de l'événement temps réel
        publishFactureEvent("FACTURE_CANCELLED", updatedFacture);

        return updatedFacture;
    }

    @Override
    public Facture validerFacture(Long id) {
        Optional<Facture> factureOpt = findFactureById(id);
        if (factureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée avec l'ID: " + id);
        }

        Facture facture = factureOpt.get();
        facture.setStatut(StatutFacture.PAID); // Validate means mark as paid
        
        factureRepository.update(facture);
        Facture updatedFacture = facture; // Return the updated facture
        
        // Publication de l'événement temps réel
        publishFactureEvent("FACTURE_VALIDATED", updatedFacture);

        return updatedFacture;
    }

    @Override
    public BigDecimal calculerTotalFactures(LocalDate debut, LocalDate fin) {
        try {
            List<Facture> factures = findFacturesByPeriode(debut, fin);
            return factures.stream()
                .map(f -> f.getTotaleFacture() != null ? f.getTotaleFacture() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul du total des factures: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculerTotalPaiements(LocalDate debut, LocalDate fin) {
        try {
            List<Facture> factures = findFacturesByPeriode(debut, fin);
            return factures.stream()
                .map(f -> f.getTotalePaye() != null ? f.getTotalePaye() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul du total des paiements: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal calculerTotalCreances() {
        try {
            List<Facture> factures = findFacturesImpayees();
            return factures.stream()
                .map(f -> {
                    BigDecimal total = f.getTotaleFacture() != null ? f.getTotaleFacture() : BigDecimal.ZERO;
                    BigDecimal paye = f.getTotalePaye() != null ? f.getTotalePaye() : BigDecimal.ZERO;
                    return total.subtract(paye);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du calcul des créances: " + e.getMessage(), e);
        }
    }

    @Override
    public String genererNumeroFacture() {
        // Format: FAC-YYYYMMDD-XXXX
        LocalDate today = LocalDate.now();
        String prefix = "FAC-" + today.getYear() + String.format("%02d%02d", today.getMonthValue(), today.getDayOfMonth());
        
        // En pratique, on devrait compter les factures du jour et incrémenter
        // Pour l'instant, on génère un numéro simple
        return prefix + "-" + System.currentTimeMillis() % 10000;
    }

    @Override
    public byte[] exporterFacturePDF(Long factureId) {
        // Implémentation simple - en pratique, on utiliserait une bibliothèque comme iText
        String pdfContent = "Facture #" + factureId + "\n" +
                          "Générée le: " + LocalDate.now() + "\n" +
                          "Contenu de la facture à implémenter...";
        
        return pdfContent.getBytes();
    }

    @Override
    public String exporterFacturesCSV(List<Facture> factures) {
        StringBuilder csv = new StringBuilder();
        csv.append("Numéro,Patient,Date,Total,Payé,Reste,Statut,Notes\n");
        
        for (Facture facture : factures) {
            BigDecimal total = facture.getTotaleFacture() != null ? facture.getTotaleFacture() : BigDecimal.ZERO;
            BigDecimal paye = facture.getTotalePaye() != null ? facture.getTotalePaye() : BigDecimal.ZERO;
            BigDecimal reste = total.subtract(paye);
            
            csv.append("#" + facture.getIdEntite()).append(",");
            // Get patient name from situationFinanciere
            String patientNom = "N/A";
            if (facture.getSituationFinanciere() != null && facture.getSituationFinanciere().getDossierMedicale() != null && facture.getSituationFinanciere().getDossierMedicale().getPatient() != null) {
                patientNom = facture.getSituationFinanciere().getDossierMedicale().getPatient().getNom() + " " + facture.getSituationFinanciere().getDossierMedicale().getPatient().getPrenom();
            }
            csv.append(patientNom).append(",");
            csv.append(facture.getDateFacture().toLocalDate()).append(",");
            csv.append(total).append(",");
            csv.append(paye).append(",");
            csv.append(reste).append(",");
            csv.append(facture.getStatut() != null ? facture.getStatut() : "").append(",");
            csv.append("\""); // Notes field doesn't exist
            csv.append("\n");
        }
        
        return csv.toString();
    }

    @Override
    public List<Facture> rechercherFactures(String numero, Long patientId, String statut, LocalDate debut, LocalDate fin) {
        try {
            List<Facture> factures = findAllFactures();
            
            return factures.stream()
                .filter(f -> {
                    boolean matchesNumero = numero == null || numero.isEmpty(); // Numero field doesn't exist
                    
                    boolean matchesPatient = patientId == null || 
                        (f.getSituationFinanciere() != null && f.getSituationFinanciere().getDossierMedicale() != null && f.getSituationFinanciere().getDossierMedicale().getPatient() != null &&
                         patientId.equals(f.getSituationFinanciere().getDossierMedicale().getPatient().getIdEntite()));
                    
                    boolean matchesStatut = statut == null || statut.isEmpty() || 
                        (f.getStatut() != null && f.getStatut().equalsIgnoreCase(statut));
                    
                    boolean matchesPeriode = (debut == null || fin == null) ||
                        (f.getDateFacture() != null && 
                         !f.getDateFacture().isBefore(debut.atStartOfDay()) && !f.getDateFacture().isAfter(fin.atTime(23, 59, 59)));
                    
                    return matchesNumero && matchesPatient && matchesStatut && matchesPeriode;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des factures: " + e.getMessage(), e);
        }
    }

    @Override
    public long compterFacturesParStatut(String statut) {
        try {
            return findFacturesByStatut(statut).size();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du comptage des factures par statut: " + e.getMessage(), e);
        }
    }

    @Override
    public Facture appliquerRemise(Long factureId, BigDecimal remise, String motif) {
        Optional<Facture> factureOpt = findFactureById(factureId);
        if (factureOpt.isEmpty()) {
            throw new IllegalArgumentException("Facture non trouvée avec l'ID: " + factureId);
        }

        Facture facture = factureOpt.get();
        
        if (remise.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La remise doit être positive");
        }
        
        BigDecimal total = facture.getTotaleFacture() != null ? facture.getTotaleFacture() : BigDecimal.ZERO;
        BigDecimal nouveauTotal = total.subtract(remise);
        
        if (nouveauTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La remise ne peut pas dépasser le total de la facture");
        }

        try {
            facture.setTotaleFacture(nouveauTotal);
            
            // Notes field doesn't exist, skip notes update

            factureRepository.update(facture);
            Facture updatedFacture = facture; // Return the updated facture
            
            // Publication de l'événement temps réel
            publishFactureEvent("FACTURE_DISCOUNT_APPLIED", updatedFacture);

            return updatedFacture;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'application de la remise: " + e.getMessage(), e);
        }
    }

    @Override
    public FactureStats getFactureStats(LocalDate debut, LocalDate fin) {
        try {
            List<Facture> factures = findFacturesByPeriode(debut, fin);
            
            FactureStats stats = new FactureStats();
            stats.setPeriodeDebut(debut);
            stats.setPeriodeFin(fin);
            
            stats.setTotalFactures(factures.size());
            stats.setFacturesPayees(factures.stream().filter(f -> StatutFacture.PAID.equals(f.getStatutEnum())).count());
            stats.setFacturesImpayees(factures.stream().filter(f -> StatutFacture.PENDING.equals(f.getStatutEnum())).count());
            stats.setFacturesPartiellementPayees(0); // Repository doesn't have PARTIAL status
            
            stats.setTotalMontant(factures.stream()
                .map(f -> f.getTotaleFacture() != null ? f.getTotaleFacture() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            stats.setTotalPaye(factures.stream()
                .map(f -> f.getTotalePaye() != null ? f.getTotalePaye() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            stats.setTotalCreances(stats.getTotalMontant().subtract(stats.getTotalPaye()));
            
            if (stats.getTotalFactures() > 0) {
                stats.setMoyenneFacture(stats.getTotalMontant().divide(BigDecimal.valueOf(stats.getTotalFactures())));
            }
            
            return stats;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des statistiques: " + e.getMessage(), e);
        }
    }

    /**
     * Validation de la requête de création
     */
    private void validateCreateRequest(FactureCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de création est obligatoire");
        }
        
        if (request.getPatientId() == null) {
            throw new IllegalArgumentException("L'ID du patient est obligatoire");
        }
        
        if (request.getDateFacture() == null) {
            throw new IllegalArgumentException("La date de facture est obligatoire");
        }
        
        if (request.getDateFacture().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de facture ne peut pas être dans le passé");
        }
        
        if (request.getTotaleFacture() == null || request.getTotaleFacture().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le total de la facture doit être positif");
        }
    }

    /**
     * Validation de la requête de mise à jour
     */
    private void validateUpdateRequest(FactureUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de mise à jour est obligatoire");
        }
        
        if (request.getDateFacture() != null && request.getDateFacture().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La date de facture ne peut pas être dans le passé");
        }
        
        if (request.getTotaleFacture() != null && request.getTotaleFacture().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le total de la facture doit être positif");
        }
    }

    /**
     * Validation de la requête de paiement
     */
    private void validatePaiementRequest(PaiementRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La requête de paiement est obligatoire");
        }
        
        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant du paiement doit être positif");
        }
        
        if (request.getDatePaiement() == null) {
            request.setDatePaiement(LocalDate.now());
        }
        
        if (request.getDatePaiement().isAfter(LocalDate.now().plusDays(1))) {
            throw new IllegalArgumentException("La date de paiement ne peut pas être dans le futur");
        }
    }

    /**
     * Publication d'un événement temps réel pour les factures
     */
    private void publishFactureEvent(String eventType, Facture facture) {
        if (realTimeService != null) {
            try {
                RealTimeEvent event = new RealTimeEvent();
                event.setEventType(eventType);
                event.setSource("FACTURE_SERVICE");
                event.setData(java.util.Map.of(
                    "factureId", facture.getIdEntite(),
                    "patientId", facture.getSituationFinanciere() != null && facture.getSituationFinanciere().getDossierMedicale() != null && facture.getSituationFinanciere().getDossierMedicale().getPatient() != null ? facture.getSituationFinanciere().getDossierMedicale().getPatient().getIdEntite() : null,
                    "numero", facture.getNumero(), // Utiliser la méthode utilitaire
                    "dateFacture", facture.getDateFacture().toString(),
                    "total", facture.getTotaleFacture().toString(),
                    "statut", facture.getStatut() != null ? facture.getStatut() : ""
                ));
                
                realTimeService.publishEvent(event);
                
            } catch (Exception e) {
                System.err.println("Erreur lors de la publication de l'événement Facture: " + e.getMessage());
            }
        }
    }

    /**
     * Convertit une chaîne de caractères en enum StatutFacture
     */
    private StatutFacture convertStringToStatutFacture(String status) {
        if (status == null) return null;
        
        switch (status.toUpperCase()) {
            case "EN_ATTENTE":
            case "PENDING":
                return StatutFacture.PENDING;
            case "PAYEE":
            case "PAID":
                return StatutFacture.PAID;
            case "ANNULEE":
            case "CANCELLED":
                return StatutFacture.CANCELLED;
            case "EN_RETARD":
            case "OVERDUE":
                return StatutFacture.OVERDUE;
            default:
                return null;
        }
    }
}
