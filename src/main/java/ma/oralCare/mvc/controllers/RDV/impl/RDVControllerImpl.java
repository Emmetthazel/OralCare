package ma.oralCare.mvc.controllers.RDV.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.oralCare.mvc.controllers.RDV.api.RDVController;
import ma.oralCare.mvc.ui1.medecin.MainFrame;
import ma.oralCare.mvc.ui1.medecin.RDVPanel;
import ma.oralCare.service.modules.RDV.api.RDVService;
import ma.oralCare.service.modules.RDV.dto.RDVCreateRequest;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;
import ma.oralCare.service.modules.RDV.dto.RDVUpdateRequest;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RDVControllerImpl implements RDVController {

    private final RDVRepository rdvRepo;
    private final ConsultationRepository consultationRepo;
    private final RDVPanel view;
    private final Long medecinId;
    private LocalDate currentDateFilter;
    private List<RDVPanelDTO> lastLoadedData; // Cache pour le filtrage par statut

    public RDVControllerImpl(RDVRepository rdvRepo, RDVPanel view, Long medecinId) {
        this.rdvRepo = rdvRepo;
        this.consultationRepo = new ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl();
        this.view = view;
        this.medecinId = medecinId;
        this.currentDateFilter = LocalDate.now();
        refreshView();
    }

    // --- LOGIQUE DE L'AGENDA (DENTISTE) ---

    @Override
    public List<RDVPanelDTO> chargerPlanning(LocalDate date, Long medecinId) {
        List<RDV> rdvs = rdvRepo.findByDateAndMedecin(date, medecinId);
        return rdvs.stream().map(this::mapToPanelDTO).toList();
    }

    @Override
    public void demarrerSeance(Long rdvId) {
        RDV rdv = rdvRepo.findById(rdvId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable ID: " + rdvId));

        if (rdv.getConsultation() != null)
            throw new RuntimeException("Une consultation existe déjà pour ce RDV.");

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Créer la consultation
                Consultation cons = Consultation.builder()
                        .date(LocalDate.now())
                        .statut(StatutConsultation.IN_PROGRESS)
                        .dossierMedicale(rdv.getDossierMedicale())
                        .libelle("Consultation via RDV: " + rdv.getMotif())
                        .build();

                cons = consultationRepo.create(cons, conn);

                // 2. Mettre à jour le RDV
                rdv.setConsultation(cons);
                rdv.setStatut(StatutRDV.IN_PROGRESS);
                rdvRepo.update(rdv); // Utiliser la signature simple sans Connection

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors du démarrage de la séance", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion base de données", e);
        }
    }

    // --- LOGIQUE CRUD (SECRÉTAIRE) ---

    @Override
    public List<RDV> getRDVsByPeriode(LocalDate debut, LocalDate fin) {
        // Implémentation manuelle car la méthode n'existe pas dans RDVRepository
        List<RDV> allRDVs = new ArrayList<>();
        for (LocalDate date = debut; !date.isAfter(fin); date = date.plusDays(1)) {
            allRDVs.addAll(rdvRepo.findByDate(date));
        }
        return allRDVs;
    }

    @Override
    public RDV createRDV(RDVCreateRequest request) {
        // Validation : Vérifier si le créneau est libre
        if (!isSlotAvailable(request.getDate(), request.getHeureDebut(), request.getMedecinId())) {
            throw new RuntimeException("Le médecin a déjà un rendez-vous à cette heure.");
        }

        RDV newRDV = RDV.builder()
                .date(request.getDate())
                .heure(request.getHeureDebut())
                .motif(request.getMotif())
                .statut(StatutRDV.PENDING) // Utiliser PENDING au lieu de PLANIFIE
                // .dossierMedicale(...) Récupérer via un autre service si besoin
                .build();

        rdvRepo.create(newRDV);
        return newRDV; // Retourner l'objet créé (le repository ne retourne rien)
    }

    @Override
    public void annulerRendezVous(Long rdvId) {
        rdvRepo.updateStatut(rdvId, StatutRDV.CANCELLED);
    }

    @Override
    public void confirmerRendezVous(Long rdvId) {
        rdvRepo.updateStatut(rdvId, StatutRDV.CONFIRMED);
    }

    @Override
    public List<LocalTime> getAvailableTimeSlots(LocalDate date, Long medecinId) {
        List<LocalTime> allSlots = generateStandardSlots();
        List<RDV> booked = rdvRepo.findByDateAndMedecin(date, medecinId);
        List<LocalTime> bookedTimes = booked.stream().map(RDV::getHeure).toList();

        return allSlots.stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .toList();
    }

    // --- MÉTHODES PRIVÉES / HELPERS ---

    private boolean isSlotAvailable(LocalDate date, LocalTime time, Long medecinId) {
        return rdvRepo.findByDateAndMedecin(date, medecinId).stream()
                .noneMatch(r -> r.getHeure().equals(time) && r.getStatut() != StatutRDV.CANCELLED);
    }

    private List<LocalTime> generateStandardSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime time = LocalTime.of(9, 0); // Début 09:00
        while (time.isBefore(LocalTime.of(18, 0))) { // Fin 18:00
            slots.add(time);
            time = time.plusMinutes(30); // Créneaux de 30 min
        }
        return slots;
    }

    private RDVPanelDTO mapToPanelDTO(RDV rdv) {
        String patientName = "Inconnu";
        if (rdv.getDossierMedicale() != null && rdv.getDossierMedicale().getPatient() != null) {
            patientName = rdv.getDossierMedicale().getPatient().getNom().toUpperCase() + " " +
                    rdv.getDossierMedicale().getPatient().getPrenom();
        }

        return RDVPanelDTO.builder()
                .rdvId(rdv.getIdEntite())
                .heure(rdv.getHeure())
                .patientFullname(patientName)
                .motif(rdv.getMotif())
                .statut(rdv.getStatut().name())
                .dejaUneCons(rdv.getConsultation() != null)
                .build();
    }

    @Override
    public void refreshView() {
        if (view != null) {
            List<RDVPanelDTO> data = chargerPlanning(currentDateFilter, medecinId);
            view.updateTable(data);
        }
    }

    // --- MÉTHODES MANQUANTES POUR L'INTERFACE ---

    @Override
    public void handleConfirmerRDV(Long rdvId) {
        confirmerRendezVous(rdvId);
    }

    @Override
    public void deleteRDV(Long rdvId) {
        try {
            rdvRepo.deleteById(rdvId);
            refreshView();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la suppression du RDV: " + e.getMessage(), e);
        }
    }

    @Override
    public void cancelRDV(Long rdvId) {
        annulerRendezVous(rdvId);
    }

    @Override
    public void confirmRDV(Long rdvId) {
        confirmerRendezVous(rdvId);
    }

    @Override
    public RDV updateRDV(Long rdvId, RDVUpdateRequest request) {
        try {
            Optional<RDV> existingRDVOpt = rdvRepo.findById(rdvId);
            if (existingRDVOpt.isEmpty()) {
                throw new IllegalArgumentException("RDV non trouvé avec l'ID: " + rdvId);
            }
            
            RDV existingRDV = existingRDVOpt.get();
            
            // Mettre à jour les champs
            if (request.getDate() != null) existingRDV.setDate(request.getDate());
            if (request.getHeureDebut() != null) existingRDV.setHeure(request.getHeureDebut()); // Utiliser getHeureDebut()
            if (request.getMotif() != null) existingRDV.setMotif(request.getMotif());
            
            rdvRepo.update(existingRDV); // La méthode update() retourne void
            refreshView();
            return existingRDV; // Retourner l'objet mis à jour
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du RDV: " + e.getMessage(), e);
        }
    }

    // --- AUTRES MÉTHODES DE L'INTERFACE À IMPLÉMENTER ---

    @Override
    public void handleFilterWeek() {
        // Implémentation à ajouter
    }

    @Override
    public void handleFilterMonth() {
        // Implémentation à ajouter
    }

    @Override
    public void handleFilterStatut(String statut) {
        // Implémentation à ajouter
    }

    @Override
    public void handleDemarrerConsultation(Long rdvId) {
        demarrerSeance(rdvId);
    }

    @Override
    public void handleAnnulerRDV(Long rdvId) {
        annulerRendezVous(rdvId);
    }

    @Override
    public void handleDateChange(LocalDate date) {
        this.currentDateFilter = date;
        refreshView();
    }

    @Override
    public void handleOuvrirConsultation(Long rdvId) {
        // Implémentation à ajouter
    }
}