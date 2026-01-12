package ma.oralCare.service.modules.RDV.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Objet de transfert de données pour l'affichage dans le tableau de bord des rendez-vous.
 * Sépare la complexité des entités de la simplicité de la vue Swing.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RDVPanelDTO {

    private Long dossierId; // Vérifiez le nom ici
    private Long rdvId;             // Identifiant unique pour les actions (Démarrer/Annuler)
    private LocalTime heure;        // Stocké en LocalTime pour permettre le tri chronologique
    private String patientFullname; // Concaténation de Nom + Prénom faite par le service
    private String motif;           // Raison du rendez-vous
    private String statut;          // Statut actuel (ex: CONFIRMED, CANCELLED, COMPLETED)

    // Logique liée à la consultation
    private boolean dejaUneCons;    // Indique si une consultation est déjà rattachée
    private Long consultationId;    // ID de la consultation (null si non démarrée)

    /**
     * Formate l'heure pour un affichage propre dans le JTable.
     * @return String au format "HH:mm" (ex: 09:30)
     */
    public String getHeureFormattee() {
        return (heure != null) ? heure.format(DateTimeFormatter.ofPattern("HH:mm")) : "--:--";
    }

    /**
     * Retourne un libellé lisible pour la colonne Consultation.
     */
    public String getConsultationStatusLabel() {
        return dejaUneCons ? "En cours (ID: " + consultationId + ")" : "Non démarrée";
    }

    public Long getDossierId() {
        return dossierId;
    }
}