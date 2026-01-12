/*package ma.oralCare.service.modules.RDV.impl;

import ma.oralCare.conf.SessionFactory;
import ma.oralCare.entities.agenda.RDV;
import ma.oralCare.entities.dossierMedical.Consultation;
import ma.oralCare.entities.enums.StatutConsultation;
import ma.oralCare.entities.enums.StatutRDV;
import ma.oralCare.repository.modules.agenda.impl.RDVRepositoryImpl;
import ma.oralCare.repository.modules.dossierMedical.impl.ConsultationRepositoryImpl;
import ma.oralCare.service.modules.RDV.api.RDVService;
import ma.oralCare.service.modules.RDV.dto.RDVPanelDTO;
import ma.oralCare.repository.modules.agenda.api.RDVRepository;
import ma.oralCare.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RDVServiceImpl implements RDVService {
    private final RDVRepository rdvRepo;
    private final ConsultationRepository consultationRepo;

    public RDVServiceImpl() {
        this.rdvRepo = new RDVRepositoryImpl();
        this.consultationRepo = new ConsultationRepositoryImpl();
    }

    @Override
    public List<RDVPanelDTO> chargerPlanning(LocalDate date, Long medecinId) {
        List<RDV> rdvs = rdvRepo.findByDateAndMedecin(date, medecinId);

        return rdvs.stream()
                .map(rdv -> {
                    String fullname = "Patient inconnu";
                    if (rdv.getDossierMedicale() != null && rdv.getDossierMedicale().getPatient() != null) {
                        String nom = rdv.getDossierMedicale().getPatient().getNom();
                        String prenom = rdv.getDossierMedicale().getPatient().getPrenom();
                        fullname = (nom != null ? nom : "") + " " + (prenom != null ? prenom : "");
                        fullname = fullname.trim().isEmpty() ? "Sans Nom" : fullname.toUpperCase();
                    }

                    return RDVPanelDTO.builder()
                            .rdvId(rdv.getIdEntite())
                            .heure(rdv.getHeure())
                            .patientFullname(fullname)
                            .motif(rdv.getMotif() != null ? rdv.getMotif() : "Non spécifié")
                            .statut(rdv.getStatut() != null ? rdv.getStatut().name() : "INCONNU")
                            .dejaUneCons(rdv.getConsultation() != null)
                            .consultationId(rdv.getConsultation() != null ? rdv.getConsultation().getIdEntite() : null)
                            .build();
                })
                .toList();
    }

    @Override
    public void demarrerSeance(Long rdvId) {
        // 1. Charger le RDV
        RDV rdv = rdvRepo.findById(rdvId)
                .orElseThrow(() -> new RuntimeException("RDV #" + rdvId + " introuvable."));

        // 2. Vérifications métier
        if (rdv.getConsultation() != null) {
            throw new RuntimeException("Ce rendez-vous possède déjà une consultation active.");
        }
        if (rdv.getDossierMedicale() == null) {
            throw new RuntimeException("Erreur : Aucun dossier médical lié à ce RDV.");
        }

        // 3. Transaction
        try (Connection connection = SessionFactory.getInstance().getConnection()) {
            try {
                connection.setAutoCommit(false);

                Consultation c = Consultation.builder()
                        .date(LocalDate.now())
                        .statut(StatutConsultation.IN_PROGRESS)
                        .dossierMedicale(rdv.getDossierMedicale())
                        .libelle("Consultation : " + rdv.getMotif())
                        .build();

                // On force le cast pour utiliser la méthode JDBC.
                // Assurez-vous que l'interface ConsultationRepository retourne bien 'Consultation'
                c = ((ConsultationRepositoryImpl) consultationRepo).create(c, connection);

                if (c == null || c.getIdEntite() == null) {
                    throw new SQLException("L'ID de la consultation n'a pas été généré.");
                }

                rdv.setConsultation(c);
                rdv.setStatut(StatutRDV.IN_PROGRESS);

                ((RDVRepositoryImpl) rdvRepo).update(rdv, connection);

                connection.commit();
                System.out.println("[SERVICE] Séance démarrée avec succès.");

            } catch (Exception e) {
                if (connection != null) connection.rollback();
                e.printStackTrace();
                throw new RuntimeException("Échec du démarrage de la séance : " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion SQL", e);
        }
    }

    @Override
    public void annulerRendezVous(Long rdvId) {
        rdvRepo.updateStatut(rdvId, StatutRDV.CANCELLED);
    }
}
*/